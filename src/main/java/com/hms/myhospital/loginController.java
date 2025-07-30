package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;

import static com.hms.utils.Validator.*;


public class loginController {
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private StackPane loginBtn;
    @FXML private StackPane loginCancelBtn;
    @FXML private Label errorLabel;
//    @FXML private Hyperlink registerLink;

    @FXML
    private void handleLogin() {
        // Handle login button click
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        } else if(!isValidUsername(username)) {
            errorLabel.setText("Username is not valid.");
        } else if(!isValidPassword(password)) {
            errorLabel.setText("Invalid password.");
        }

        // Add your login logic here
        System.out.println("Login button clicked with username: " + username);
        try {
            SceneSwitcher.switchScene("/fxml/patientDashboard.fxml");
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
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

//    @FXML
//    private void handleRegisterLink() {
//        // Handle register link click
//        System.out.println("Register link clicked");
//
//        try {
//            SceneSwitcher.switchScene("/fxml/register.fxml");
//        } catch (Exception e) {
//            System.out.println("Failed to switch to registration: " + e.getMessage());
//        }
//    } //done
}
