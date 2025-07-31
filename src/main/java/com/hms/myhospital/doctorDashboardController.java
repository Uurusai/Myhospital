package com.hms.myhospital;

import com.hms.client.HMSClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;

import static com.hms.utils.Validator.*;


public class doctorDashboardController {

    private final HMSClient client;
    public doctorDashboardController(HMSClient client) {
        this.client = client;

    }

    @FXML private StackPane doctorHomeBtn;
    @FXML private StackPane doctorProfileBtn;
    @FXML private StackPane doctorAppointmentsBtn;
    @FXML private StackPane doctorInboxBtn;
    @FXML private StackPane doctorPrescriptionsBtn;

    @FXML private AnchorPane doctorHome;
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
                doctorHomeBtn, doctorProfileBtn, doctorAppointmentsBtn,
                doctorInboxBtn, doctorPrescriptionsBtn
        };

        screens = new AnchorPane[]{
                doctorHome, doctorProfile, doctorAppointments, doctorInbox, doctorPrescriptions
        };

        // Set initial state
        selectMenuButton(doctorHomeBtn);

        for (StackPane button : menuButtons) {
            button.setOnMouseClicked(e -> selectMenuButton(button));
        }

        saveDoctorProfileBtn.setVisible(false);
        setProfileFieldsEditable(false);

    }

    private AnchorPane getCorrespondingScreen(StackPane button) {
        if (button == doctorHomeBtn) return doctorHome;
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
    private String originalFirstName;
    private String originalLastName;
    private String originalPhoneNumber;
    private String originalPassword;
    private String originalDateOfBirth;

    @FXML private Button saveDoctorProfileBtn;
    @FXML private Button editDoctorProfileBtn;
    @FXML private TextField doctorFirstName;
    @FXML private TextField doctorLastName;
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
            doctorFirstName.setText(originalFirstName);
            doctorLastName.setText(originalLastName);
            doctorPhoneNumber.setText(originalPhoneNumber);
            doctorPassword.setText(originalPassword);
            doctorDateOfBirth.setValue(originalDateOfBirth == null ? null : java.time.LocalDate.parse(originalDateOfBirth));

            setProfileFieldsEditable(false);
            saveDoctorProfileBtn.setVisible(false);
            editDoctorProfileBtn.setText("Edit");

        } else {
            //Edit profile
            originalFirstName = doctorFirstName.getText();
            originalLastName = doctorLastName.getText();
            originalPhoneNumber = doctorPhoneNumber.getText();
            originalPassword = doctorPassword.getText();
            originalDateOfBirth = doctorDateOfBirth.getValue() == null ? null : doctorDateOfBirth.getValue().toString();

            doctorFirstName.clear();
            doctorLastName.clear();
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
        // TODO: Update the database with new values
        if(validateProfileFields()) {
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
        doctorFirstName.setEditable(editable);
        doctorLastName.setEditable(editable);
        doctorPhoneNumber.setEditable(editable);
        doctorPassword.setEditable(editable);
        doctorDateOfBirth.setDisable(!editable);
    }

    private boolean validateProfileFields() {
        return isValidName(doctorFirstName.getText()) &&
               isValidName(doctorLastName.getText()) &&
               isValidPhoneNumber(doctorPhoneNumber.getText()) &&
               isValidDateOfBirth(doctorDateOfBirth.getValue().toString()) &&
                isValidPassword(doctorPassword.getText());
    }

    // Inbox section stuff
    private void sendMessage() {
        //TODO: send message to patient
    }

    //Appointments section stuff
    @FXML private VBox requestedAppointments;
    @FXML private VBox confirmedAppointments;

    public void addRequestedAppointment(String patientName, String date, String time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointmentRow.fxml"));
            HBox row = loader.load();
            appointmentRowController controller = loader.getController();
            controller.setAppointmentData(patientName, date, time, false); // false = not confirmed
            requestedAppointments.getChildren().add(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //to be used by appointment.java probably

    public void addConfirmedAppointment(String patientName, String date, String time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointmentRow.fxml"));
            HBox row = loader.load();
            appointmentRowController controller = loader.getController();
            controller.setAppointmentData(patientName, date, time, true); // true = confirmed
            confirmedAppointments.getChildren().add(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //to be used by appointment.java probably

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

}

