package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Message;
import com.hms.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


        //profile section stuff
        saveDoctorProfileBtn.setVisible(false);

        int currentDoctorId = HMSRunner.getCurrentUserId();
        Doctor doctor = client.getDoctorById(currentDoctorId);

        if (doctor != null) {
            doctorName.setText(doctor.getName());
            doctorPhoneNumber.setText(String.valueOf(doctor.getContactNo()));
            doctorDateOfBirth.setValue(null);
            doctorGenderLabel.setText(doctor.getGender());
            specializationLabel.setText(doctor.getSpeciality());
            doctorPassword.setText(doctor.getPassword());

            originalName = doctor.getName();
            originalPhoneNumber = String.valueOf(doctor.getContactNo());
            originalPassword = doctor.getPassword();
            originalDateOfBirth = null;
        } else {
            System.out.println("Logged in doctor ID was not found!!!");
        }
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

    private String originalName;
    private String originalPhoneNumber;
    private String originalPassword;
    private String originalDateOfBirth;

    @FXML private Label doctorGenderLabel;
    @FXML private Label specializationLabel;

    @FXML private Button saveDoctorProfileBtn;
    @FXML private Button editDoctorProfileBtn;

    @FXML private TextField doctorName;
    @FXML private TextField doctorPhoneNumber;
    @FXML private DatePicker doctorDateOfBirth;
    @FXML private TextField doctorPassword;

    @FXML
    private void editDoctorProfile() {

        System.out.println("Editing profile...");

        if(saveDoctorProfileBtn.isVisible()) {
            // Cancel editting
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
            // TODO: (done) Update the database with new values
            Doctor updatedDoctor = client.getDoctorById(HMSRunner.getCurrentUserId());
            updatedDoctor.setName(doctorName.getText());
            updatedDoctor.setContactNo(Integer.parseInt(doctorPhoneNumber.getText()));
            updatedDoctor.setPassword(doctorPassword.getText());
            client.updateDoctor(updatedDoctor);
            // updatedDoctor.setAddress(doctorAddress.getText());

            setProfileFieldsEditable(false);
            saveDoctorProfileBtn.setVisible(false);
            editDoctorProfileBtn.setText("Edit");

        } else {
            System.out.println("Invalid profile data. Please check your input.");
            editDoctorProfile(); //cancel edit
        }

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

    //Appointments section stuff
    @FXML private VBox requestedAppointments;
    @FXML private VBox confirmedAppointments;

    // Helper to add an appointment row to a VBox
    public void addAppointmentRow(VBox targetVBox, Appointment appointment, boolean isConfirmed) {
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
            if (requestedAppointments.getChildren().contains(row)) {
                requestedAppointments.getChildren().remove(row);
            } else {
                confirmedAppointments.getChildren().remove(row);
            }
        }
    }

    //Currently Not being used
    private void autoscheduleNewAppointment(int patientId) {
        boolean scheduled = client.autoscheduleAppointment(patientId, HMSRunner.getCurrentUserId(),"symptoms");
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
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/prescription.fxml", client);
        } catch (Exception e) {
            System.out.println("Error switching to prescription scene.");
            e.printStackTrace();
        }
    } //done

    @FXML private StackPane logOutBtn;
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

