package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppointmentSchedulerController {
    @FXML private VBox manualScheduleContainer;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextArea symptomsTextArea;
    @FXML private Button autoScheduleButton;
    @FXML private Button manualScheduleButton;
    @FXML private Button submitButton;

    private int patientId;
    private int doctorId;
    private HMSClient client;
    private Stage stage;

    public void initData(int patientId, int doctorId, HMSClient client, Stage stage) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.client = client;
        this.stage = stage;

        initializeTimeSlots();
        manualScheduleContainer.setVisible(false);
        autoScheduleButton.setOnAction(e-> handleAutoSchedule());
        manualScheduleButton.setOnAction(e -> handleManualSchedule());
        submitButton.setOnAction(e -> handleSubmit());

    }

    private void initializeTimeSlots() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.of(8, 0);
        while (time.isBefore(LocalTime.of(17, 30))) {
            timeComboBox.getItems().add(time.format(timeFormatter));
            time = time.plusMinutes(30);
        }
    }

    @FXML
    private void handleAutoSchedule() {
        // Auto-scheduling logic would go here
        String symptoms = symptomsTextArea.getText();
        boolean scheduled = client.autoscheduleAppointment(patientId, doctorId,symptoms);
        if(!scheduled) {
            showAlert("Error", "Scheduling failed. Please try again.");
            return;
        }
        processAppointment();
    }

    @FXML
    private void handleManualSchedule() {
        manualScheduleContainer.setVisible(true);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleSubmit() {
        if (datePicker.getValue() == null || timeComboBox.getValue() == null) {
            showAlert("Error", "Please select both date and time");
            return;
        }
        if (symptomsTextArea.getText().isEmpty()) {
            showAlert("Error", "Please enter symptoms");
            return;
        }
        try {
            LocalDate date = datePicker.getValue();
            int day = date.getDayOfWeek().ordinal(); // Convert to 1-7 range
            LocalTime time = LocalTime.parse(timeComboBox.getValue());
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);
            String symptoms = symptomsTextArea.getText();
            if(client.isAppointmentAvailable(appointmentDateTime) && client.isDoctorAvailable(client.getDoctorSchedule(client.getDoctorById(doctorId)),day,time)){
                Appointment a = new Appointment();
                a.setDoctor(client.getDoctorById(doctorId));
                a.setPatient(client.getPatientbyId(patientId));
                a.setDate_requested(appointmentDateTime);
                a.setSymptoms(symptoms);
                client.addAppointment(a);
                processAppointment();
            } else {
                showAlert("Error", "The selected date/time is not available");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid date/time selection");
        }
    }

    private void processAppointment() {
        // Here you would typically save to database
        String message = String.format(
                "Appointment scheduled!");
        showAlert("Success", message);
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
