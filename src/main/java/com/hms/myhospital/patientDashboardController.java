package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Message;
import com.hms.model.Patient;
import com.hms.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.hms.utils.Validator.*;

public class patientDashboardController {

    private final HMSClient client;
    public patientDashboardController(HMSClient client) {
        this.client = client;
    }

        @FXML private StackPane patientProfileBtn;
        @FXML private StackPane patientAppointmentsBtn;
        @FXML private StackPane patientInboxBtn;
        @FXML private StackPane patientPrescriptionsBtn;

        @FXML private AnchorPane patientAppointments;
        @FXML private AnchorPane patientProfile;
        @FXML private AnchorPane patientInbox;
        @FXML private AnchorPane patientPrescriptions;

        private StackPane[] menuButtons;
        private AnchorPane[] screens;
        private StackPane currentlySelected;

        @FXML
        public void initialize() {
            // Initialize arrays for easy iteration
            menuButtons = new StackPane[]{
                    patientProfileBtn, patientAppointmentsBtn,
                    patientInboxBtn, patientPrescriptionsBtn
            };

            screens = new AnchorPane[]{
                    patientProfile, patientAppointments, patientInbox, patientPrescriptions
            };

            // Set initial state for profile fields
            selectMenuButton(patientProfileBtn);

            for (StackPane button : menuButtons) {
                button.setOnMouseClicked(e -> selectMenuButton(button));
            }

            savePatientProfileBtn.setVisible(false);

            int currentPatientId = HMSRunner.getCurrentUserId();
            Patient patient = client.getPatientbyId(currentPatientId);
            if (patient != null) {
                patientName.setText(patient.getName());
                patientPhoneNumber.setText(String.valueOf(patient.getContactNo()));
                String dobString = patient.getDate_of_birth(); // e.g., "2000-07-15 00:00:00.0"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDate dob = LocalDate.parse(dobString, formatter);
                patientDateOfBirth.setValue(dob);
                bloodGroup.setText(patient.getBlood_type());
                patientGenderLabel.setText(patient.getGender());

                // Set email and password if available
                patientPassword.setText(patient.getPassword());

                // Save originals for edit/cancel
                originalName = patient.getName();
                originalPhoneNumber = String.valueOf(patient.getContactNo());
                originalDateOfBirth = patient.getDate_of_birth();
                originalBloodGroup = patient.getBlood_type();
                originalPassword = patient.getPassword();
            } else {
                // Handle case where patient is not found
                System.out.println("Logged in patient not found in database!!!");
            }

            savePatientProfileBtn.setVisible(false);

            //initialize appoint section stuff
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("contactNo"));

            // Add button to each row
            requestColumn.setCellFactory(col -> new TableCell<Doctor, Void>() {
                private final Button requestBtn = new Button("Request");
                {
                    requestBtn.setOnAction(e -> {
                        Doctor doctor = getTableView().getItems().get(getIndex());
                        requestAppointment(doctor);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : requestBtn);
                }
            });

            doctorTableView.setItems(doctorList);

            // Populate specializations
            specializationChoiceBox.getItems().addAll("Cardiology", "Neurologist", "Pediatrics","bones", "General Medicine", "Dermatology", "Orthopedics", "Gynaecology");
            specializationChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadDoctorsBySpeciality(newVal);
                }
            });
        }

        private AnchorPane getCorrespondingScreen(StackPane button) {
            if (button == patientProfileBtn) return patientProfile;
            if (button == patientAppointmentsBtn) return patientAppointments;
            if (button == patientInboxBtn) return patientInbox;
            if (button == patientPrescriptionsBtn) return patientPrescriptions;
            return null;
        }

        private void selectMenuButton(StackPane button) {
            // Reset all buttons and screens
            for (StackPane btn : menuButtons) {
                btn.getStyleClass().removeAll("menu-button-selected", "menu-button-unselected");
                btn.getStyleClass().add("menu-button-unselected");
            }

            for (AnchorPane s : screens) {
                s.setVisible(false);
            }

            // Set selected state
            button.getStyleClass().removeAll("menu-button-unselected", "menu-button-selected");
            button.getStyleClass().add("menu-button-selected");
            AnchorPane screen = getCorrespondingScreen(button);
            assert screen != null;
            screen.setVisible(true);
            currentlySelected = button;
        }

        //profile section stuff

        private String originalName;
        private String originalPhoneNumber;
        private String originalPassword;
        private String originalDateOfBirth;
        private String originalBloodGroup;

        @FXML private Button savePatientProfileBtn;
        @FXML private Button editPatientProfileBtn;
        @FXML private TextField patientName;
        @FXML private TextField patientPhoneNumber;
        @FXML private DatePicker patientDateOfBirth;
        @FXML private TextField patientPassword;
        @FXML private TextField bloodGroup;
        @FXML private Label patientGenderLabel;
        @FXML private Label patientEmailLabel;


        @FXML
        private void editPatientProfile() {
            if (savePatientProfileBtn.isVisible()) {
                // Cancel edit: restore original values and make fields uneditable
                patientName.setText(originalName);
                patientPhoneNumber.setText(originalPhoneNumber);
                patientPassword.setText(originalPassword);
                patientDateOfBirth.setValue(originalDateOfBirth == null ? null : java.time.LocalDate.parse(originalDateOfBirth));
                bloodGroup.setText(originalBloodGroup);

                setPatientProfileFieldsEditable(false);
                savePatientProfileBtn.setVisible(false);
                editPatientProfileBtn.setText("Edit");

            } else {
                // Enter edit mode: store originals, clear fields, make editable
                originalName = patientName.getText();
                originalPhoneNumber = patientPhoneNumber.getText();
                originalPassword = patientPassword.getText();
                originalDateOfBirth = patientDateOfBirth.getValue() == null ? null : patientDateOfBirth.getValue().toString();
                originalBloodGroup = bloodGroup.getText();

                patientName.clear();
                patientPhoneNumber.clear();
                patientPassword.clear();
                patientDateOfBirth.setValue(null);
                bloodGroup.clear();

                setPatientProfileFieldsEditable(true);
                savePatientProfileBtn.setVisible(true);
                editPatientProfileBtn.setText("Cancel");
            }
        }
        @FXML
        private void savePatientProfile() {
        if (validatePatientProfileFields()) {
            try {

                int currentPatientId = HMSRunner.getCurrentUserId();

                // Create updated patient object
                Patient updatedPatient = new Patient();

                updatedPatient.setId(currentPatientId);
                updatedPatient.setName(patientName.getText());
                updatedPatient.setContactNo(Integer.parseInt(patientPhoneNumber.getText()));
                updatedPatient.setDate_of_birth(patientDateOfBirth.getValue().toString());
                updatedPatient.setBlood_type(bloodGroup.getText());
                updatedPatient.setPassword(patientPassword.getText());


                // Update patient via client
                boolean success = client.updatePatient(updatedPatient);

                if (success) {
                    // Update original values to new values
                    originalName = updatedPatient.getName();
                    originalPhoneNumber = String.valueOf(updatedPatient.getContactNo());
                    originalDateOfBirth = updatedPatient.getDate_of_birth();
                    originalBloodGroup = updatedPatient.getBlood_type();
                    originalPassword = updatedPatient.getPassword();

                } else {
                    // Revert to original values
                    editPatientProfile(); // This will cancel the edit
                }

                setPatientProfileFieldsEditable(false);
                savePatientProfileBtn.setVisible(false);
                editPatientProfileBtn.setText("Edit");

            } catch (Exception e) {
                e.printStackTrace();
                editPatientProfile(); // This will cancel the edit
            }
        }
    }
        private void setPatientProfileFieldsEditable(boolean editable) {
            patientName.setEditable(editable);
            patientPhoneNumber.setEditable(editable);
            patientPassword.setEditable(editable);
            patientDateOfBirth.setDisable(!editable);
            bloodGroup.setEditable(editable);
        }
        private boolean validatePatientProfileFields() {
            // Use your own validation logic or reuse from Validator
            return isValidName(patientName.getText()) &&
                   isValidPhoneNumber(patientPhoneNumber.getText()) &&
                   isValidDateOfBirth(patientDateOfBirth.getValue().toString()) &&
                   isValidPassword(patientPassword.getText()) &&
                   isValidBloodGroup(bloodGroup.getText());
        }


        //appointments section stuff

    //TODO: request an appointment to a specified doctor

        @FXML private TableView<Doctor> doctorTableView;
        @FXML private TableColumn<Doctor, String> nameColumn;
        @FXML private TableColumn<Doctor, Integer> phoneColumn;
        @FXML private TableColumn<Doctor, Void> requestColumn;
        @FXML private TableView<Appointment> appointmentTableView;
        @FXML private ChoiceBox<String> specializationChoiceBox;

        private ObservableList<Doctor> doctorList = FXCollections.observableArrayList();

        private void loadDoctorsBySpeciality(String speciality) {
            List<Doctor> doctors = client.getAllDoctors().stream()
                .filter(d -> d.getSpeciality().equalsIgnoreCase(speciality))
                .collect(Collectors.toList());
            doctorList.setAll(doctors);
        }

        private void requestAppointment(Doctor doctor) {
            int patientId = HMSRunner.getCurrentUserId();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("appointment-scheduler.fxml"));
                Parent root = loader.load();

                // Create a new stage for the scheduling window
                Stage schedulingStage = new Stage();

                // Get the controller and pass data
                AppointmentSchedulerController controller = loader.getController();
                controller.initData(patientId, doctor.getId(), client, schedulingStage);

                schedulingStage.setScene(new Scene(root));
                schedulingStage.setTitle("Schedule Appointment");

                // Set the owner of the new window to the main window
                schedulingStage.initOwner(getMainStage());

                schedulingStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not load scheduling window");
            }
            // Use autoschedule or request logic from AppointmentDAO via client
            AppointmentSchedulerController asc = new AppointmentSchedulerController();
        }
    private Stage getMainStage() {
        return (Stage) doctorTableView.getScene().getWindow();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

        //inbox section stuff

        @FXML private VBox inboxMessagesVBox;
        @FXML private TextField sentTo;
        @FXML private TextField sentText;
        @FXML private Button sendMessageBtn;

        private void loadMessages() throws SQLException {
            inboxMessagesVBox.getChildren().clear();
            List<Message> messages = client.getMessagesByRecipient(client.getPatientByName(patientName.getText()).getId()); // or similar
            for (Message msg : messages) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hms/myhospital/text.fxml"));
                    HBox messageRow = loader.load();
                    Label sender = (Label) loader.getNamespace().get("senderLabel");
                    Label recipient = (Label) loader.getNamespace().get("recipientLabel");
                    Label content = (Label) loader.getNamespace().get("messageLabel");
                    sender.setText(msg.getSenderName());
                    recipient.setText(String.valueOf(msg.getRecipientId()));
                    content.setText(msg.getContent());
                    inboxMessagesVBox.getChildren().add(messageRow);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    @FXML StackPane logOutBtn;
    @FXML
    private void logOut() {
        // Handle logout logic
        System.out.println("Logging out...");
        HMSRunner.setCurrentUser(null, null); // Clear current user
        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/welcome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching scene to welcome screen.");
        }
    }

}
