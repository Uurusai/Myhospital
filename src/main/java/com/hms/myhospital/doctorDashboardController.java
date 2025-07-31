package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.hms.utils.Validator.*;


public class doctorDashboardController {

    private final HMSClient client;
    public doctorDashboardController(HMSClient client) {
        this.client = client;

    }

    @FXML private StackPane doctorProfileBtn;
    @FXML private StackPane doctorAppointmentsBtn;
    @FXML private StackPane doctorInboxBtn;
    @FXML private StackPane doctorPrescriptionsBtn;

    @FXML private AnchorPane doctorAppointments;
    @FXML private AnchorPane doctorProfile;
    @FXML private AnchorPane doctorInbox;
    @FXML private AnchorPane doctorPrescriptions;


    private StackPane[] menuButtons;
    private AnchorPane[] screens;
    private StackPane currentlySelected;

    @FXML
    public void initialize() {
        // Initialize arrays for easy iteration
        menuButtons = new StackPane[]{
                doctorProfileBtn, doctorAppointmentsBtn,
                doctorInboxBtn, doctorPrescriptionsBtn
        };

        screens = new AnchorPane[]{
                doctorProfile, doctorAppointments, doctorInbox, doctorPrescriptions
        };

        // Set initial state
        selectMenuButton(doctorProfileBtn);

        for (StackPane button : menuButtons) {
            button.setOnMouseClicked(e -> selectMenuButton(button));
        }

        saveDoctorProfileBtn.setVisible(false);

        int currentDoctorId = HMSRunner.getCurrentUserId(); // TODO: replace with actual logic
        Doctor doctor = client.getDoctorById(currentDoctorId);
        if (doctor != null) {
            doctorName.setText(doctor.getName());
            doctorPhoneNumber.setText(String.valueOf(doctor.getContactNo()));
            doctorDateOfBirth.setValue(null); // If you have date_of_birth, set it here
            doctorGenderLabel.setText(doctor.getGender());
            specializationLabel.setText(doctor.getSpeciality());
            doctorPassword.setText(doctor.getPassword());
            // Save originals for edit/cancel
            originalName = doctor.getName();
            originalPhoneNumber = String.valueOf(doctor.getContactNo());
            originalPassword = doctor.getPassword();
            originalDateOfBirth = null; // Set if you have date_of_birth
            doctorGender = doctor.getGender();

        }
        setProfileFieldsEditable(false);
    }

    private AnchorPane getCorrespondingScreen(StackPane button) {
        if (button == doctorProfileBtn) return doctorProfile;
        if (button == doctorAppointmentsBtn) return doctorAppointments;
        if (button == doctorInboxBtn) return doctorInbox;
        if (button == doctorPrescriptionsBtn) return doctorPrescriptions;
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

    // Profile section stuff

    // Add these fields to store original values
    private String originalName;
    private String originalPhoneNumber;
    private String originalPassword;
    private String originalDateOfBirth;
    private String doctorGender;

    @FXML private Label doctorGenderLabel;
    @FXML private Label specializationLabel;
    //@FXML private Label doctorEmailLabel;

    @FXML private Button saveDoctorProfileBtn;
    @FXML private Button editDoctorProfileBtn;

    @FXML private TextField doctorName;
    @FXML private TextField doctorPhoneNumber;
    @FXML private DatePicker doctorDateOfBirth;
    @FXML private TextField doctorPassword;

    //TODO: Initialize the original values in the initialize method

    @FXML private void editDoctorProfile() {
        //there is an edit button, which, when clicked, sets its own text to cancel, the save button beside it to visible
        // and allows the user to edit the profile fields
        System.out.println("Editing profile...");

        if(saveDoctorProfileBtn.isVisible()) {
            // Cancel edit
            doctorName.setText(originalName);
            doctorPhoneNumber.setText(originalPhoneNumber);
            doctorPassword.setText(originalPassword);
            doctorDateOfBirth.setValue(originalDateOfBirth == null ? null : java.time.LocalDate.parse(originalDateOfBirth));

            setProfileFieldsEditable(false);
            saveDoctorProfileBtn.setVisible(false);
            editDoctorProfileBtn.setText("Edit");

        } else {
            //Edit profile
            originalName = doctorName.getText();
            originalPhoneNumber = doctorPhoneNumber.getText();
            originalPassword = doctorPassword.getText();
            originalDateOfBirth = doctorDateOfBirth.getValue() == null ? null : doctorDateOfBirth.getValue().toString();

            doctorName.clear();
            doctorPhoneNumber.clear();
            doctorPassword.clear();
            doctorDateOfBirth.setValue(null);

            setProfileFieldsEditable(true);
            saveDoctorProfileBtn.setVisible(true);
            editDoctorProfileBtn.setText("Cancel");
        }
    }

    @FXML
    private void saveDoctorProfile() {

        if(validateProfileFields()) {
            // TODO: Update the database with new values
            setProfileFieldsEditable(false);
            saveDoctorProfileBtn.setVisible(false);
            editDoctorProfileBtn.setText("Edit");

        } else {
            // Show error message to user
            System.out.println("Invalid profile data. Please check your input.");
            // You can also show a dialog or alert here
            editDoctorProfile(); //cancel edit
        }

        //TODO: show an error message if the field are invalid
    }

    private void setProfileFieldsEditable(boolean editable) {
        doctorName.setEditable(editable);
        doctorPhoneNumber.setEditable(editable);
        doctorPassword.setEditable(editable);
        doctorDateOfBirth.setDisable(!editable);
    }

    private boolean validateProfileFields() {
        return isValidName(doctorName.getText()) &&
               isValidPhoneNumber(doctorPhoneNumber.getText()) &&
               isValidDateOfBirth(doctorDateOfBirth.getValue().toString()) &&
                isValidPassword(doctorPassword.getText());
    }

    // Inbox section stuff
    @FXML private VBox doctorInboxMessagesVBox;
    @FXML private TextField sentTo;
    @FXML private TextField sentText;
    @FXML private Button sendMessageBtn;

    private void loadDoctorMessages() throws SQLException {
        doctorInboxMessagesVBox.getChildren().clear();
        // Get current doctor name or ID from context/session
        int currentDoctorId = HMSRunner.getCurrentUserId();
        List<Message> messages = client.getMessagesByRecipient(currentDoctorId);
        if (messages.isEmpty()) {
            System.out.println("No messages found for this doctor.");
            return;
        }

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
                doctorInboxMessagesVBox.getChildren().add(messageRow);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void sendPatientMessage() throws SQLException {

        String patientName = sentTo.getText();
        String text = sentText.getText();
        if (patientName.isEmpty() || text.isEmpty()) return;

        Message msg = new Message();
        msg.setRecipientId(client.getPatientByName(patientName).getId());
        msg.setSenderName(client.getDoctorById(HMSRunner.getCurrentUserId()).getName());
        msg.setContent(text);
        msg.setTimestamp(java.time.LocalDateTime.now());
        msg.setRead(false);
        client.createMessage(msg);
        sentText.clear();
        loadDoctorMessages();
    }

    //Appointments section stuff
    @FXML private VBox requestedAppointments;
    @FXML private VBox confirmedAppointments;

    // Helper to add an appointment row to a VBox
    private void addAppointmentRow(VBox targetVBox, Appointment appointment, boolean isConfirmed) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hms/myhospital/appointmentRow.fxml"));
            HBox row = loader.load();
            appointmentRowController controller = loader.getController();
            controller.setAppointmentData(
                appointment.getPatient().getName(),
                appointment.getDate_scheduled().toLocalDate().toString(),
                appointment.getDate_scheduled().toLocalTime().toString(),
                isConfirmed
            );
            // Set up button actions
            controller.getConfirmAppointmentBtn().setOnAction(e -> confirmAppointment(appointment, row));
            controller.getCancelAppointmentBtn().setOnAction(e -> cancelAppointment(appointment, row));
            targetVBox.getChildren().add(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDoctorAppointments() {
        requestedAppointments.getChildren().clear();
        confirmedAppointments.getChildren().clear();

        List<Appointment> allAppointments = client.getAppointmentsForDoctor(HMSRunner.getCurrentUserId());
        for (Appointment app : allAppointments) {
            if ("confirmed".equalsIgnoreCase(app.getStatus())) {
                addAppointmentRow(confirmedAppointments, app, true);
            } else if ("pending".equalsIgnoreCase(app.getStatus()) || "requested".equalsIgnoreCase(app.getStatus())) {
                addAppointmentRow(requestedAppointments, app, false);
            }
        }
    }

    private void confirmAppointment(Appointment appointment, HBox row) {
        boolean success = client.confirmAppointment(appointment.getId(), appointment.getDate_scheduled());
        if (success) {
            requestedAppointments.getChildren().remove(row);
            addAppointmentRow(confirmedAppointments, appointment, true);
        }
    }

    private void cancelAppointment(Appointment appointment, HBox row) {
        boolean success = client.rejectAppointment(appointment.getId());
        if (success) {
            requestedAppointments.getChildren().remove(row);
        }
    }

    private void autoscheduleNewAppointment(int patientId) {
        boolean scheduled = client.autoscheduleAppointment(patientId, HMSRunner.getCurrentUserId());
        if (scheduled) {
            loadDoctorAppointments();
        } else {
            System.out.println("No available slots for autoscheduling.");
        }
    }

    //prescriptions section stuff

    @FXML
    private void composePrescription() {
        System.out.println("Composing prescription...");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/prescription.fxml");
        } catch (Exception e) {
            System.out.println("Error switching to prescription scene.");
            e.printStackTrace();
        }
    } //done

    @FXML private void logOut() {
        System.out.println("Logging out...");
        try {
            HMSRunner.setCurrentUser(null, null); // Clear current user
            SceneSwitcher.switchScene("/com/hms/myhospital/welcome.fxml");
        } catch(IOException e) {
            System.out.println("Error switching to welcome scene.");
            e.printStackTrace();
        }
    }

}

