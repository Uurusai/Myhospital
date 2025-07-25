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


public class registerController implements Initializable{

    private boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    @FXML private TextField doctorFirstName;
    @FXML private TextField doctorLastName;
    @FXML private TextField doctorUserName;
    @FXML private TextField doctorEmail;
    @FXML private TextField doctorPhoneNumber;
    @FXML private PasswordField doctorSetPassword;
    @FXML private PasswordField doctorConfirmPassword;
    @FXML private DatePicker doctorDateOfBirth;
    @FXML private TextField specialization;
    @FXML private Label doctorPerInfoError;
    @FXML private Label doctorAccInfoError;
    @FXML private StackPane doctorRegisterBtn;
    @FXML private StackPane doctorRegisterCancelBtn;

    @FXML private void handleGenderSelection() {
        //TODO: Implement this
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

    //added doctor info validation 
    private boolean validateDoctorInfo() {
        // Personal Information Validation
        if (isNullOrEmpty(doctorFirstName.getText())) {
            doctorPerInfoError.setText("First name required!");
            return false;
        }
        if (!isValidName(doctorFirstName.getText())) {
            doctorPerInfoError.setText("Invalid first name!");
            return false;
        }

        if (isNullOrEmpty(doctorLastName.getText())) {
            doctorPerInfoError.setText("Last name required!");
            return false;
        }
        if (!isValidName(doctorLastName.getText())) {
            doctorPerInfoError.setText("Invalid last name!");
            return false;
        }

        if (isNullOrEmpty(specialization.getText())) {
            doctorPerInfoError.setText("Specialization required!");
            return false;
        }

        if (isNullOrEmpty(doctorAddress.getText())) {
            doctorPerInfoError.setText("Address required!");
            return false;
        }

        if (isNullOrEmpty(doctorPhoneNumber.getText())) {
            doctorPerInfoError.setText("Phone number required!");
            return false;
        }
        if (!isValidPhoneNumber(doctorPhoneNumber.getText())) {
            doctorPerInfoError.setText("Invalid phone number!");
            return false;
        }

        // Account Information Validation
        if (isNullOrEmpty(doctorUserName.getText())) {
            doctorAccInfoError.setText("User name required!");
            return false;
        }
        if (!isValidUsername(doctorUserName.getText())) {
            doctorAccInfoError.setText("Invalid user name!");
            return false;
        }

        if (isNullOrEmpty(doctorEmail.getText())) {
            doctorAccInfoError.setText("Email address required!");
            return false;
        }
        if (!isValidEmailAddress(doctorEmail.getText())) {
            doctorAccInfoError.setText("Invalid email address!");
            return false;
        }

        if (isNullOrEmpty(doctorSetPassword.getText())) {
            doctorAccInfoError.setText("Set your password!");
            return false;
        }
        if (!isValidPassword(doctorSetPassword.getText())) {
            doctorAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if (isNullOrEmpty(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Confirm your password!");
            return false;
        }
        if (!isValidPassword(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if (!doctorSetPassword.getText().equals(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Passwords do not match!");
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

        //TODO: (for ADIBA) Implement the taking the data input into database
        
        try {
            SceneSwitcher.switchScene("/fxml/patientProfile.fxml");
        } catch (IOException e) {
        }
    }

    
    
    @FXML private void handlePatientRegisterCancel(){
        // Handle patient selection button click
        System.out.println("Patient register cancelled!");

        try {
            SceneSwitcher.switchScene("/fxml/welcome.fxml");
        } catch (IOException e) {
        }
    }

    @FXML private void handleDoctorRegister() {

        System.out.println("Doctor registration attempted!");

        if(!validateDoctorInfo()) {
            return;
        }

        //TODO: (for ADIBA) Implement the taking the data input into database
        
        try {
            SceneSwitcher.switchScene("/fxml/doctorProfile.fxml");
        } catch (IOException e) {
        }
    }

    @FXML private void handleDoctorRegisterCancel() {

        System.out.println("Doctor register cancelled!");

        try {
            SceneSwitcher.switchScene("/fxml/welcome.fxml");
        } catch (IOException e) {
        }
    }

}
