package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hms.dao.DatabaseConnection.getConnection;

public class AdminDash extends StackPane {  // Must match fx:root type
    // Navigation Buttons
    @FXML private Button doctor_btn;
    @FXML private Button patient_btn;
    @FXML private Button appointment_btn;
    @FXML private Button db_btn;

    // Content Panes
    @FXML private AnchorPane patientsView;
    @FXML private AnchorPane doctorsView;
    @FXML private AnchorPane appointmentsView;
    @FXML private AnchorPane db_top;
    @FXML private AnchorPane db_btm;

    @FXML private Label activeDoctorsLabel;
    @FXML private Label totalPatientsLabel;
    @FXML private Label activePatientsLabel;
    @FXML private AreaChart<String, Number> weeklyPatientsChart;
    @FXML private AreaChart<String, Number> weeklyDoctorsChart;

    // Doctors table components
    @FXML private TableView<Doctor> doctorsTable;
    @FXML private TableColumn<Doctor, Integer> doctorIdColumn;
    @FXML private TableColumn<Doctor, String> doctorNameColumn;
    @FXML private TableColumn<Doctor, String> doctorAdressColumn;
    @FXML private TableColumn<Doctor, String> doctorContactColumn;
    @FXML private TableColumn<Doctor, String> doctorEmailColumn;
    @FXML private TableColumn<Doctor, String> doctorSpecializationColumn;

    // Patients table components
    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, Integer> patientIdColumn;
    @FXML private TableColumn<Patient, String> patientNameColumn;
    @FXML private TableColumn<Patient, String> patientGenderColumn;
    @FXML private TableColumn<Patient, String> patientContactColumn;
    @FXML private TableColumn<Patient, String> patientDoctorColumn;
    @FXML private TableColumn<Patient, String> patientAddressColumn;
    @FXML private TableColumn<Patient, String> patientStatusColumn;

    private final HMSClient client;


    public AdminDash(HMSClient client) {
        this.client = client ;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hms/myhospital/admin-dash.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML", e);
        }

        initialize();
    }

    @FXML
    private void initialize() {
        // Verify all injections
        if (doctor_btn == null || patientsView == null) {
            throw new RuntimeException("FXML injection failed - check your fx:id values");
        }
        // Set initial view
        setAllInvisible();
        db_top.setVisible(true);
        db_btm.setVisible(true);
        // Set up button actions
        doctor_btn.setOnAction(e -> switchToView(doctorsView));
        patient_btn.setOnAction(e -> switchToView(patientsView));
        appointment_btn.setOnAction(e -> switchToView(appointmentsView));
        db_btn.setOnAction(e -> {
            setAllInvisible();
            db_top.setVisible(true);
            db_btm.setVisible(true);
        });
        setupDoctorsTable();
        setupPatientsTable();
        setupCharts();
        loadDashboardDataAsync();
    }

    private void switchToView(AnchorPane view) {
        setAllInvisible();
        view.setVisible(true);
    }

    private void setAllInvisible() {
        patientsView.setVisible(false);
        doctorsView.setVisible(false);
        appointmentsView.setVisible(false);
        db_top.setVisible(false);
        db_btm.setVisible(false);
    }

    private void setupDoctorsTable() {
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        doctorContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        doctorEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        doctorSpecializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        doctorAdressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Add action buttons
        TableColumn<Doctor, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button deleteBtn = new Button("Delete");

            {
                approveBtn.setOnAction(event -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    approveDoctor(doctor.getId());
                });

                deleteBtn.setOnAction(event -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    deleteDoctor(doctor.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, approveBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        doctorsTable.getColumns().add(actionsColumn);
    }

    private void setupPatientsTable() {
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patientGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        patientContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        patientDoctorColumn.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getId();
            List<Doctor> doctors = client.getDoctorsForPatient(patientId);
            for(Doctor d : doctors){
                try{
                    return new SimpleStringProperty(d.getName());
                }catch(Exception e){
                    return new SimpleStringProperty("Error");
                }
            }
            return new SimpleStringProperty("N/A");
        });
        patientStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action button
        TableColumn<Patient, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");

            {
                viewBtn.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showPatientDetails(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });

        patientsTable.getColumns().add(actionColumn);
    }
    private void setupCharts() {
        weeklyPatientsChart.setTitle("Patients This Week");
        weeklyDoctorsChart.setTitle("Active Doctors This Week");
    }
    private void loadDashboardDataAsync() {
        Task<Map<String, Object>> task = new Task<>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                // Get active doctors count
                List<Doctor> activeDoctors = client.getAllDoctors();
                int activeDoctorsCount = activeDoctors != null ? activeDoctors.size() : 0;

                // Get total patients count
                List<Patient> allPatients = client.getAllPatients();
                List<Appointment> apps = new ArrayList<>();
                for(Patient patient : allPatients){
                    List<Appointment> a = (client.getPendingAppointmentsForPatient(patient.getId()));
                    for(Appointment appointment : a){
                        apps.add(appointment);
                    }
                }
                int totalPatientsCount = allPatients != null ? allPatients.size() : 0;

                // Get active patients count
                int activePatientsCount = 0;
                if (allPatients != null) {
                    activePatientsCount = (int) apps.stream().count();
                }

                List<Integer> weeklyPatients = List.of(10, 15, 8, 12, 9, 14, 11);
                List<Integer> weeklyDoctors = List.of(5, 6, 5, 7, 6, 7, 8);

                return Map.of(
                        "activeDoctors", activeDoctorsCount,
                        "totalPatients", totalPatientsCount,
                        "activePatients", activePatientsCount,
                        "weeklyPatients", weeklyPatients,
                        "weeklyDoctors", weeklyDoctors
                );
            }
        };

        task.setOnSucceeded(e -> {
            Map<String, Object> results = task.getValue();

            // Update stats
            activeDoctorsLabel.setText(String.valueOf(results.get("activeDoctors")));
            totalPatientsLabel.setText(String.valueOf(results.get("totalPatients")));
            activePatientsLabel.setText(String.valueOf(results.get("activePatients")));

            // Update charts
            updateWeeklyPatientsChart((List<Integer>) results.get("weeklyPatients"));
            updateWeeklyDoctorsChart((List<Integer>) results.get("weeklyDoctors"));
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Failed to load dashboard data", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void updateWeeklyPatientsChart(List<Integer> weeklyData) {
        weeklyPatientsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Patients");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String day = date.format(formatter);
            int count = weeklyData.get(6 - i); // Get count for this day
            series.getData().add(new XYChart.Data<>(day, count));
        }

        weeklyPatientsChart.getData().add(series);
    }

    private void updateWeeklyDoctorsChart(List<Integer> weeklyData) {
        weeklyDoctorsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Active Doctors");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String day = date.format(formatter);
            int count = weeklyData.get(6 - i);
            series.getData().add(new XYChart.Data<>(day, count));
        }

        weeklyDoctorsChart.getData().add(series);
    }

    private void loadDoctorsDataAsync() {
        Task<List<Doctor>> task = new Task<>() {
            @Override
            protected List<Doctor> call() throws Exception {
                return client.getAllDoctors();
            }
        };

        task.setOnSucceeded(e -> {
            doctorsTable.getItems().setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Failed to load doctors", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void loadPatientsDataAsync() {
        Task<List<Patient>> task = new Task<>() {
            @Override
            protected List<Patient> call() throws Exception {
                return client.getAllPatients();
            }
        };

        task.setOnSucceeded(e -> {
            patientsTable.getItems().setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Failed to load patients", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void approveDoctor(int doctorId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Approval");
        alert.setHeaderText("Approve Doctor");
        alert.setContentText("Are you sure you want to approve this doctor?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Boolean> task = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        Doctor doctor = client.getDoctorById(doctorId);
                        if (doctor != null) {
                            doctor.setAccount_status("Active");
                            return client.updateDoctor(doctor);
                        }
                        return false;
                    }
                };
                task.setOnSucceeded(e -> {
                    if (task.getValue()) {
                        loadDoctorsDataAsync();
                        loadDashboardDataAsync();
                    } else {
                        showAlert("Error", "Failed to approve doctor", "Server returned failure");
                    }
                });

                task.setOnFailed(e -> {
                    showAlert("Error", "Failed to approve doctor", task.getException().getMessage());
                });

                new Thread(task).start();
            }
        });
    }

    private void deleteDoctor(int doctorId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Doctor");
        alert.setContentText("Are you sure you want to delete this doctor?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Boolean> task = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return client.deleteDoctor(doctorId);
                    }
                };
                task.setOnSucceeded(e -> {
                    if (task.getValue()) {
                        loadDoctorsDataAsync();
                        loadDashboardDataAsync();
                    } else {
                        showAlert("Error", "Failed to delete doctor", "Server returned failure");
                    }
                });

                task.setOnFailed(e -> {
                    showAlert("Error", "Failed to delete doctor", task.getException().getMessage());
                });

                new Thread(task).start();
            }
        });
    }


    private void showPatientDetails(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Patient Details");
        alert.setHeaderText("Details for " + patient.getName());
        alert.setContentText(
                "ID: " + patient.getId() + "\n" +
                        "Name: " + patient.getName() + "\n" +
                        "Gender: " + patient.getGender() + "\n" +
                        "Contact: " + patient.getContactNo()
        );
        alert.showAndWait();
    }

    private int getCountFromDatabase(String query) throws SQLException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }


    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}