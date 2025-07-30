package com.hms.myhospital;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.hms.utils.Validator.*;


public class patientRegisterController {

    @FXML private TextField patientFirstName;
    @FXML private TextField patientLastName;
    @FXML private TextField patientUserName;
    @FXML private TextField patientEmail;
    @FXML private TextField patientPhoneNumber;
    @FXML private PasswordField patientSetPassword;
    @FXML private PasswordField patientConfirmPassword;
    @FXML private TextArea medicalHistory;
    @FXML private CheckBox patientIsMale;
    @FXML private CheckBox patientIsFemale;
    @FXML private DatePicker patientDateOfBirth;

    @FXML private ChoiceBox<String> patientBloodGroup;

    public void initialize() {
        // Initialize patientBloodGroup ChoiceBox with blood types
        ObservableList<String> bloodTypes = FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown"
        );
        patientBloodGroup.setItems(bloodTypes);
        patientBloodGroup.setValue("Unknown"); // Set default value
    }

    @FXML private Label patientPerInfoError;
    @FXML private Label patientAccInfoError;

    @FXML private StackPane patientRegisterBtn;
    @FXML private StackPane patientRegisterCancelBtn;

    //public boolean isMale = false;
    //public boolean isFemale = false;

    @FXML private void handleGenderSelection() {
        //TODO: Implement this
        if (patientIsMale.isSelected()) {
            patientIsFemale.setSelected(false);
        } else if (patientIsFemale.isSelected()) {
            patientIsMale.setSelected(false);
        }
    }

    private boolean validatePatientInfo() {

        if (isNullOrEmpty(patientFirstName.getText())) {
            patientPerInfoError.setText("First name required!");
            return false;
        }
        if (!isValidName(patientFirstName.getText())) {
            patientPerInfoError.setText("Invalid first name!");
            return false;
        }

        if (isNullOrEmpty(patientLastName.getText())) {
            patientPerInfoError.setText("Last name required!");
            return false;
        }
        if (!isValidName(patientLastName.getText())) {
            patientPerInfoError.setText("Invalid last name!");
            return false;
        }

        if(!patientIsMale.isSelected() && !patientIsFemale.isSelected()) {
            patientPerInfoError.setText("Gender selection is required.");
            return false;
        }

        if(patientDateOfBirth.getValue() == null) {
            patientPerInfoError.setText("Date of birth required!");
            return false;
        }
        if(!isValidDateOfBirth(patientDateOfBirth.getValue().toString())) {
            patientPerInfoError.setText("Invalid date of birth!");
            return false;
        }

        if (isNullOrEmpty(patientPhoneNumber.getText())) {
            patientPerInfoError.setText("Phone number required!");
            return false;
        }
        if (!isValidPhoneNumber(patientPhoneNumber.getText())) {
            patientPerInfoError.setText("Invalid phone number!");
            return false;
        }

        if (isNullOrEmpty(patientUserName.getText())) {
            patientAccInfoError.setText("User name required!");
            return false;
        }
        if(!isValidUsername(patientUserName.getText())) {
            patientAccInfoError.setText("Invalid user name!");
            return false;
        }

        if (isNullOrEmpty(patientEmail.getText())) {
            patientAccInfoError.setText("Email address required!");
            return false;
        }
        if(!isValidEmailAddress(patientEmail.getText())) {
            patientAccInfoError.setText("Invalid email address!");
            return false;
        }

        if (isNullOrEmpty(patientSetPassword.getText())) {
            patientAccInfoError.setText("Set your password!");
            return false;
        }
        if(!isValidPassword(patientSetPassword.getText())) {
            patientAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if (isNullOrEmpty(patientConfirmPassword.getText())) {
            patientAccInfoError.setText("Confirm your password!");
            return false;
        }
        if(!isValidPassword(patientConfirmPassword.getText())) {
            patientAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if(!patientSetPassword.equals(patientConfirmPassword)) {
            patientAccInfoError.setText("Passwords do not match!");
            return false;
        }

        return true;
    }

    @FXML private void handlePatientRegister() {
        // Handle patient selection button click
        System.out.println("Patient registration attempted!");

        if(!validatePatientInfo()) {
            return;
        }

        //TODO: Database logic handling

        try {
            SceneSwitcher.switchScene("/fxml/patientDashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching scene to patient profile.");
        }
    }

    @FXML private void handlePatientRegisterCancel(){
        // Handle patient selection button click
        System.out.println("Patient register cancelled!");

        try {
            SceneSwitcher.switchScene("/fxml/welcome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching scene to welcome screen.");
        }
    }

}