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
        Admin admin = client.getAdminByName(username);
        if (admin != null && PasswordUtil.checkPassword(password, admin.getPassword())) {
            // Successful admin login
            HMSRunner.setCurrentUser(admin.getId(), "admin");
            return true;
        }

        // Try Doctor
        List<Doctor> doctors = client.searchDoctors(username);
        Doctor matchedDoctor = null;
        for (Doctor d : doctors) {
            if (d.getName().equalsIgnoreCase(username)) {
                matchedDoctor = d;
                break;
            }
        }
        if (matchedDoctor != null && PasswordUtil.checkPassword(password, matchedDoctor.getPassword())) {
            // Successful doctor login
            HMSRunner.setCurrentUser(matchedDoctor.getId(), "doctor");
            return true;
        }

        // Try Patient
        Patient patient = client.getPatientByName(username);
        if (patient != null && PasswordUtil.checkPassword(password, patient.getPassword())) {
            // Successful patient login
            HMSRunner.setCurrentUser(patient.getId(), "patient");
            return true;
        }

        // Username exists but password is wrong
        if (admin != null || matchedDoctor != null || patient != null) {
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
            return;
        } else if(!isValidPassword(password)) {
            errorLabel.setText("Invalid password.");
            return;
        }

        //TODO: (done, testing still left)  check if the account info matches with anything in doctor, patient or admin database
        if (authenticateUser(username, password)) {
            // Proceed to dashboard
            try {
                if( HMSRunner.getCurrentUserType().equals("admin")) {
                    SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/adminDashboard.fxml", client);
                } else if (HMSRunner.getCurrentUserType().equals("doctor")) {
                    SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/doctorDashboard.fxml", client);
                } else if (HMSRunner.getCurrentUserType().equals("patient")) {
                    SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/patientDashboard.fxml", client);
                }
            } catch (Exception e) {
                errorLabel.setText("Login failed: " + e.getMessage());
            }
        }
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
