package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;
import com.hms.dao.AdminDAO;
import com.hms.dao.DoctorDAO;
import com.hms.dao.PatientDAO;
import com.hms.model.Admin;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import com.hms.utils.PasswordUtil;

import static com.hms.utils.Validator.*;


public class loginController {

    private boolean authenticateUser(String username, String password) {
        AdminDAO adminDAO = new AdminDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();

        // Try Admin
        Admin admin = adminDAO.getAdminByName(username);
        if (admin != null && PasswordUtil.checkPassword(password, admin.getPassword())) { //TODO: Implement this
            // Successful admin login
            return true;
        }

        // Try Doctor
        Doctor doctor = doctorDAO.getDoctorByName(username); //TODO: Implement this
        if (doctor != null && PasswordUtil.checkPassword(password, doctor.getPassword())) {
            // Successful doctor login
            return true;
        }

        // Try Patient
        Patient patient = patientDAO.getPatientByName(username);
        if (patient != null && PasswordUtil.checkPassword(password, patient.getPassword())) {
            // Successful patient login
            return true;
        }

        // Username exists but password is wrong
        if (admin != null || doctor != null || patient != null) {
            errorLabel.setText("Incorrect password.");
        } else {
            errorLabel.setText("Username does not exist.");
        }
        return false;
    }

    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private StackPane loginBtn;
    @FXML private StackPane loginCancelBtn;
    @FXML private Label errorLabel;
    @FXML private Hyperlink registerLink;


    @FXML
    private void handleLogin() {
        // Handle login button click
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
            errorLabel.setText("All fields are required.");
            return;
        } else if(!isValidUsername(username)) {
            errorLabel.setText("Username is not valid.");
        } else if(!isValidPassword(password)) {
            errorLabel.setText("Invalid password.");
        }

        //TODO: check if the account info matches with anything in doctor, patient or admin database
        if (authenticateUser(username, password)) {
            // Proceed to dashboard
            try {
                SceneSwitcher.switchScene("/fxml/patientDashboard.fxml");
            } catch (Exception e) {
                errorLabel.setText("Login failed: " + e.getMessage());
            }
        }

        System.out.println("Login button clicked with username: " + username);
        try {
            SceneSwitcher.switchScene("/fxml/patientDashboard.fxml"); //sample login
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoginCancel() {
        // Handle cancel button click
        System.out.println("Cancel button clicked");
        // Add your cancel logic here, e.g., clear fields or go back to the previous scene
        try {
            SceneSwitcher.switchScene("/fxml/welcome.fxml");
        } catch (Exception e) {
            System.out.println("Failed to switch scene: " + e.getMessage());
        }
    } //done

    @FXML
    private void handleRegisterLink() {
        // Handle register link click
        System.out.println("Register link clicked");

        try {
            SceneSwitcher.switchScene("/fxml/chooseAccount.fxml");
        } catch (Exception e) {
            System.out.println("Failed to switch to registration: " + e.getMessage());
        }
    } //done
}
