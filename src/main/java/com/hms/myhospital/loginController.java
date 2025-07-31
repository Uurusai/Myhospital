package com.hms.myhospital;

import com.hms.client.HMSClient;
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

import java.util.List;

import static com.hms.utils.Validator.*;


public class loginController {

    private final HMSClient client;
    public loginController(HMSClient client) {
        this.client = client;
    }

    private boolean authenticateUser(String username, String password) {

        // Try Admin
//        Admin admin = client.getAdminByName(username);
//        if (admin != null && PasswordUtil.checkPassword(password, admin.getPassword())) {
//            // Successful admin login
//            HMSRunner.setCurrentUser(admin.getId(), "admin");
//            return true;
//        }

        // Try Doctor
       Doctor matchedDoctor = client.getDoctorByName(username);
        if (matchedDoctor != null /*&& PasswordUtil.checkPassword(password, matchedDoctor.getPassword())*/) {
            // Successful doctor login
            HMSRunner.setCurrentUser(matchedDoctor.getId(), "doctor");
            System.out.println("Found doc!");
            return true;
        }
        System.out.println("Didn't find Doctor");
        // Try Patient
        Patient patient = client.getPatientByName(username);
        if (patient != null /*&& PasswordUtil.checkPassword(password, patient.getPassword())*/) {
            // Successful patient login
            System.out.println("patient found!");
            HMSRunner.setCurrentUser(patient.getId(), "patient");
            return true;
        }

        // Username exists but password is wrong
        if (matchedDoctor != null || patient != null) {
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

        //TODO: (done, testing still left)  check if the account info matches with anything in doctor, patient or admin database
        if (authenticateUser(username, password)) {
            // Choose dashboard based on user type
            String userType = HMSRunner.getCurrentUserType();
            System.out.println("User type: " + userType);
            String dashboardFxml;
            switch (userType) {
                case "doctor" -> dashboardFxml = "/com/hms/myhospital/doctorDashboard.fxml";
                case "patient" -> dashboardFxml = "/com/hms/myhospital/patientDashboard.fxml";
                //case "admin" -> dashboardFxml = "/com/hms/myhospital/adminDashboard.fxml";
                default -> throw new IllegalStateException("Unknown user type: " + userType);
            }
            try {
                System.out.println("Switching scene to: " + dashboardFxml);
                SceneSwitcher.switchSceneWithClient(dashboardFxml,client);
            } catch (Exception e) {
                errorLabel.setText("Login failed: " + e.getMessage());
            }
            return;
        }

        System.out.println("Login button clicked with username: " + username);
    }

    @FXML
    private void handleLoginCancel() {
        // Handle cancel button click
        System.out.println("Cancel button clicked");
        // Add your cancel logic here, e.g., clear fields or go back to the previous scene
        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/welcome.fxml");
        } catch (Exception e) {
            System.out.println("Failed to switch scene: " + e.getMessage());
        }
    } //done

    @FXML
    private void handleRegisterLink() {
        // Handle register link click
        System.out.println("Register link clicked");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/chooseAccount.fxml");
        } catch (Exception e) {
            System.out.println("Failed to switch to registration: " + e.getMessage());
        }
    } //done
}
