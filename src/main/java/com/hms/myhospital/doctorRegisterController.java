package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;

import static com.hms.utils.Validator.*;

public class doctorRegisterController {

    @FXML private TextField doctorFirstName;
    @FXML private TextField doctorLastName;
    @FXML private DatePicker doctorDateOfBirth;
    @FXML private TextField specialization;
    @FXML private TextField doctorPhoneNumber;
    @FXML private TextArea doctorAddress;

    @FXML private TextField doctorUserName;
    @FXML private TextField doctorEmail;
    @FXML private PasswordField doctorSetPassword;
    @FXML private PasswordField doctorConfirmPassword;

    @FXML private Label doctorPerInfoError;
    @FXML private Label doctorAccInfoError;


    @FXML private StackPane doctorRegisterBtn;
    @FXML private StackPane doctorRegisterCancelBtn;

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

        if (doctorDateOfBirth.getValue() == null) {
            doctorPerInfoError.setText("Date of birth required!");
            return false;
        }
        if(!isValidDateOfBirth(doctorDateOfBirth.getValue().toString())) {
            doctorPerInfoError.setText("Invalid date of birth!");
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

    @FXML private void handleDoctorRegister() {

        System.out.println("Doctor registration attempted!");

        if(!validateDoctorInfo()) {
            return;
        }

        //TODO: Database logic handling

        try {
            SceneSwitcher.switchScene("/fxml/doctorDashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching to doctor dashboard scene!");
        }
    }

    @FXML private void handleDoctorRegisterCancel() {

        System.out.println("Doctor register cancelled!");

        try {
            SceneSwitcher.switchScene("/fxml/welcome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching to welcome scene!");
        }
    }

}
