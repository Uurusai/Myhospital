package com.hms.myhospital;

import com.hms.dao.PatientDAO;
import com.hms.model.Patient;
import com.hms.utils.PasswordUtil;
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

    @FXML private TextField patientName;
    @FXML private TextField patientEmail;
    @FXML private TextField patientPhoneNumber;
    @FXML private PasswordField patientSetPassword;
    @FXML private PasswordField patientConfirmPassword;
    @FXML private CheckBox patientIsMale;
    @FXML private CheckBox patientIsFemale;
    @FXML private DatePicker patientDateOfBirth;
    @FXML private TextArea patientAddress;

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

    @FXML private void handleGenderSelection() {
        if (patientIsMale.isSelected()) {
            patientIsFemale.setSelected(false);
        } else if (patientIsFemale.isSelected()) {
            patientIsMale.setSelected(false);
        }
    }

    private boolean validatePatientInfo() {

        if (isNullOrEmpty(patientName.getText())) {
            patientPerInfoError.setText("First name required!");
            return false;
        }
        if (!isValidName(patientName.getText())) {
            patientPerInfoError.setText("Invalid first name!");
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

        if(!patientSetPassword.getText().equals(patientConfirmPassword.getText())) {
            patientAccInfoError.setText("Passwords do not match!");
            return false;
        }

        return true;
    }

    private int calculateAgeFromDOB(String dob) {
        java.time.LocalDate birthDate = java.time.LocalDate.parse(dob); // safe if format is YYYY-MM-DD
        java.time.LocalDate today = java.time.LocalDate.now();
        return java.time.Period.between(birthDate, today).getYears();
    }

    @FXML private void handlePatientRegister() {
        // Handle patient selection button click
        System.out.println("Patient registration attempted!");

        if(!validatePatientInfo()) {
            System.out.println("Patient registration failed due to validation errors.");
            return;
        }

        //TODO: Database logic handling


        PatientDAO patientDAO = new PatientDAO();

        String name = patientName.getText().trim();
        String gender = patientIsMale.isSelected() ? "Male" : "Female";
        String dob = patientDateOfBirth.getValue().toString();
        int age = calculateAgeFromDOB(dob);
        String bloodType = patientBloodGroup.getValue();
        int contactNo;
        try {
            contactNo = Integer.parseInt(patientPhoneNumber.getText().trim());
        } catch (NumberFormatException e) {
            patientPerInfoError.setText("Invalid phone number!");
            return;
        }

        String address = patientAddress.getText(); // Add address field if present in FXML
        String password = patientSetPassword.getText();
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Check if patient already exists
        if (patientDAO.getPatientByName(name) != null) {
            patientAccInfoError.setText("Username already exists!");
            return;
        }

        Patient newPatient = new Patient(
                name, 0, gender, age, dob, contactNo, address, bloodType
        );

        newPatient.setPassword(hashedPassword);
        newPatient.setAccount_status("pending");

        boolean success = patientDAO.addPatient(newPatient);
        if (success) {
            try {
                SceneSwitcher.switchScene("/com/hms/myhospital/patientDashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error switching scene to patient profile.");
            }
        } else {
            patientAccInfoError.setText("Registration failed. Try again.");
        }
    }

    @FXML private void handlePatientRegisterCancel(){
        // Handle patient selection button click
        System.out.println("Patient register cancelled!");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/welcome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching scene to welcome screen.");
        }
    }

}