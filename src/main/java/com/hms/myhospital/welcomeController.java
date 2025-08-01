package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
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
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/login.fxml", HMSRunner.getClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //done

    @FXML
    private void handleWelcomeRegister() {
        // Handle register button click
        System.out.println("Register button clicked");

        try {
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/chooseAccount.fxml", HMSRunner.getClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //done

}
