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
            SceneSwitcher.switchScene("/com/hms/myhospital/login.fxml");
        } catch (IOException e) {
        }
    } //done

    @FXML
    private void handleWelcomeRegister() {
        // Handle register button click
        System.out.println("Register button clicked");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/chooseAccount.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    } //done

}
