package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import com.hms.utils.SceneSwitcher;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class welcomeController {
    @FXML private StackPane welcomeLoginBtn;
    @FXML private StackPane welcomeRegisterBtn;

    @FXML
    private void handleWelcomeLogin() {
        // Handle login button click
        System.out.println("Login button clicked");
        // Add your login logic here
        try {
            SceneSwitcher.switchScene("/fxml/login.fxml");
        } catch (IOException e) {
        }
    } //done

    @FXML
    private void handleWelcomeRegister() {
        // Handle register button click
        System.out.println("Register button clicked");

        boolean showButtons = !chooseDoctorBtn.isVisible();

        chooseDoctorBtn.setVisible(showButtons);
        chooseDoctorBtn.setManaged(showButtons);
        choosePatientBtn.setVisible(showButtons);
        choosePatientBtn.setManaged(showButtons);

    } //done

    @FXML private StackPane chooseDoctorBtn;
    @FXML private StackPane choosePatientBtn;

    @FXML
    private void initialize() {
        // Hide doctor/patient buttons initially
        chooseDoctorBtn.setVisible(false);
        chooseDoctorBtn.setManaged(false); // Remove from layout
        choosePatientBtn.setVisible(false);
        choosePatientBtn.setManaged(false);
    }

    @FXML private void handleChooseDoctor() {
        // Handle doctor selection button click
        System.out.println("Doctor button clicked");
        // Add your logic for choosing doctor here
        try {
            SceneSwitcher.switchScene("/fxml/registerDoctor.fxml");
        } catch (IOException e) {
        }
    }

    @FXML private void handleChoosePatient() {
        // Handle patient selection button click
        System.out.println("Patient button clicked");
        // Add your logic for choosing patient here
        try {
            SceneSwitcher.switchScene("/fxml/registerPatient.fxml");
        } catch (IOException e) {
        }
    }

}
