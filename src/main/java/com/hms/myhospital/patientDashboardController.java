package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.Doctor;
import com.hms.model.Message;
import com.hms.model.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

            // Set initial state
            selectMenuButton(patientProfileBtn);

            for (StackPane button : menuButtons) {
                button.setOnMouseClicked(e -> selectMenuButton(button));
            }

            savePatientProfileBtn.setVisible(false);

            int currentPatientId = HMSRunner.getCurrentUserId();
            Patient patient = client.getPatientById(currentPatientId);
            if (patient != null) {
                patientName.setText(patient.getName());
                patientPhoneNumber.setText(String.valueOf(patient.getContactNo()));
                patientDateOfBirth.setValue(LocalDate.parse(patient.getDate_of_birth()));
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
            }
            savePatientProfileBtn.setVisible(false);

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
        @FXML private ChoiceBox<String> specializationChoiceBox;

        private void requestAppointment() {
            // TODO: request an appointment to a specified doctor

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

        @FXML
        private void sendMessage() throws SQLException {
            String doctorName = sentTo.getText();
            String text = sentText.getText();
            if (doctorName.isEmpty() || text.isEmpty()) return;
            List<Doctor> doctors = client.searchDoctors(doctorName);

            if (doctors.isEmpty()) {
                System.out.println("No doctor found with that name.");
                return;
            }
            int doctorId = doctors.get(0).getId();

            Message msg = new Message();
            msg.setRecipientId(doctorId);
            msg.setSenderName(client.getPatientByName(patientName.getText()).getName());
            msg.setContent(text);
            msg.setTimestamp(java.time.LocalDateTime.now());
            msg.setRead(false);
            client.createMessage(msg);
            sentText.clear();
            loadMessages();
        }

        //prescriptions section stuff


//        @FXML private void viewPrescription() {
//            try {
//                //TODO: get the prescription file as composed by doctor, except all the fields editable for doctor
//                //is set to un-editable for patient
//                SceneSwitcher.switchScene("/com/hms/myhospital/prescription.fxml");
//
//            } catch(IOException e) {
//                e.printStackTrace();
//                System.out.println("Error loading prescription view.");
//            }
//        }

}
