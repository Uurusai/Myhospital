package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;

import static com.hms.utils.Validator.*;

public class patientDashboardController {

        @FXML private StackPane patientHomeBtn;
        @FXML private StackPane patientProfileBtn;
        @FXML private StackPane patientAppointmentsBtn;
        @FXML private StackPane patientInboxBtn;
        @FXML private StackPane patientPrescriptionsBtn;

        @FXML private AnchorPane patientHome;
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
                    patientHomeBtn, patientProfileBtn, patientAppointmentsBtn,
                    patientInboxBtn, patientPrescriptionsBtn
            };

            screens = new AnchorPane[]{
                    patientHome, patientProfile, patientAppointments, patientInbox, patientPrescriptions
            };

            // Set initial state
            selectMenuButton(patientHomeBtn);

            for (StackPane button : menuButtons) {
                button.setOnMouseClicked(e -> selectMenuButton(button));
            }

            savePatientProfileBtn.setVisible(false);
        }

        private AnchorPane getCorrespondingScreen(StackPane button) {
            if (button == patientHomeBtn) return patientHome;
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

        private String originalFirstName;
        private String originalLastName;
        private String originalPhoneNumber;
        private String originalPassword;
        private String originalDateOfBirth;
        private String originalBloodGroup;

        @FXML private Button savePatientProfileBtn;
        @FXML private Button editPatientProfileBtn;
        @FXML private TextField patientFirstName;
        @FXML private TextField patientLastName;
        @FXML private TextField patientPhoneNumber;
        @FXML private DatePicker patientDateOfBirth;
        @FXML private TextField patientPassword;
        @FXML private TextField bloodGroup;
        @FXML private TextField medicalHistory;

        //TODO: initialize these fields with the current patient's data from the database

        @FXML
        private void editPatientProfile() {
            if (savePatientProfileBtn.isVisible()) {
                // Cancel edit: restore original values and make fields uneditable
                patientFirstName.setText(originalFirstName);
                patientLastName.setText(originalLastName);
                patientPhoneNumber.setText(originalPhoneNumber);
                patientPassword.setText(originalPassword);
                patientDateOfBirth.setValue(originalDateOfBirth == null ? null : java.time.LocalDate.parse(originalDateOfBirth));
                bloodGroup.setText(originalBloodGroup);

                setPatientProfileFieldsEditable(false);
                savePatientProfileBtn.setVisible(false);
                editPatientProfileBtn.setText("Edit");

            } else {
                // Enter edit mode: store originals, clear fields, make editable
                originalFirstName = patientFirstName.getText();
                originalLastName = patientLastName.getText();
                originalPhoneNumber = patientPhoneNumber.getText();
                originalPassword = patientPassword.getText();
                originalDateOfBirth = patientDateOfBirth.getValue() == null ? null : patientDateOfBirth.getValue().toString();
                originalBloodGroup = bloodGroup.getText();

                patientFirstName.clear();
                patientLastName.clear();
                patientPhoneNumber.clear();
                patientPassword.clear();
                patientDateOfBirth.setValue(null);
                bloodGroup.clear();
                medicalHistory.clear();

                setPatientProfileFieldsEditable(true);
                savePatientProfileBtn.setVisible(true);
                editPatientProfileBtn.setText("Cancel");
            }
        }

        @FXML
        private void savePatientProfile() {
            // TODO: Update the database with new values
            if (validatePatientProfileFields()) {
                setPatientProfileFieldsEditable(false);
                savePatientProfileBtn.setVisible(false);
                editPatientProfileBtn.setText("Edit");
            } else {
                System.out.println("Invalid profile data. Please check your input.");
                editPatientProfile(); // cancel edit
            }
            //TODO: show error message if invalid input

        }

        private void setPatientProfileFieldsEditable(boolean editable) {
            patientFirstName.setEditable(editable);
            patientLastName.setEditable(editable);
            patientPhoneNumber.setEditable(editable);
            patientPassword.setEditable(editable);
            patientDateOfBirth.setDisable(!editable);
            bloodGroup.setEditable(editable);
            medicalHistory.setEditable(editable);
        }

        private boolean validatePatientProfileFields() {
            // Use your own validation logic or reuse from Validator
            return isValidName(patientFirstName.getText()) &&
                   isValidName(patientLastName.getText()) &&
                   isValidPhoneNumber(patientPhoneNumber.getText()) &&
                   isValidDateOfBirth(patientDateOfBirth.getValue().toString()) &&
                   isValidPassword(patientPassword.getText()) &&
                   isValidBloodGroup(bloodGroup.getText());
        }

        //appointments section stuff
        @FXML private ChoiceBox<String> specializationChoiceBox;

        private void requestAppointment() {
            // TODO: request an appointment to a specified doctor
        }

        private void saveResults() {
            //TODO: save the results/prescriptions uploaded by the doctor
        }

        private void sendMessage() {
            //TODO: send messages to the doctor
        }

        //prescriptions section stuff

        @FXML private void viewPrescription() {
            try {
                //TODO: get the prescription file as composed by doctor, except all the fields editable for doctor
                //is set to un-editable for patient
                SceneSwitcher.switchScene("/fxml/prescription.fxml");

            } catch(IOException e) {
                e.printStackTrace();
                System.out.println("Error loading prescription view.");
            }
        }
}
