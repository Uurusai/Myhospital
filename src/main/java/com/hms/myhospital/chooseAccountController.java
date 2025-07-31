package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;

public class chooseAccountController {
    @FXML private StackPane chooseDoctorBtn;
    @FXML private StackPane choosePatientBtn;

    @FXML private void handleChooseDoctor() {
        // Handle doctor selection button click
        System.out.println("Doctor button clicked");
        // Add your logic for choosing doctor here
        try {
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/registerDoctor.fxml", HMSRunner.getClient());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to switch scene: " + e.getMessage());
        }
    }

    @FXML private void handleChoosePatient() {
        // Handle patient selection button click
        System.out.println("Patient button clicked");
        // Add your logic for choosing patient here
        try {
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/registerPatient.fxml", HMSRunner.getClient());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to switch scene: " + e.getMessage());
        }
    }
}
