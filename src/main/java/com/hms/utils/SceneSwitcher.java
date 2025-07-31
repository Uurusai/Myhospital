package com.hms.utils;

import com.hms.client.HMSClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath) throws IOException  {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set! Call setPrimaryStage() first.");
        }

        // Load new scene
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Apply new scene
        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);
    }

//    public static <T> T switchSceneWithController(String fxmlPath) throws IOException {
//        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
//        Parent root = loader.load();
//        T controller = loader.getController();
//
//        primaryStage.setScene(new Scene(root));
//        return controller;
//    }

    // Add this method to SceneSwitcher.java
    public static void switchSceneWithClient(String fxmlPath, HMSClient client) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set! Call setPrimaryStage() first.");
        }
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        setControllerFactory(loader, client);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
    }

    public static void setControllerFactory(FXMLLoader loader, HMSClient client) {
        loader.setControllerFactory(param -> {
            if (param == com.hms.myhospital.patientRegisterController.class) {
                return new com.hms.myhospital.patientRegisterController(client);
            }
            if (param == com.hms.myhospital.doctorRegisterController.class) {
                return new com.hms.myhospital.doctorRegisterController(client);
            }
            if (param == com.hms.myhospital.patientDashboardController.class) {
                return new com.hms.myhospital.patientDashboardController(client);
            }
            if (param == com.hms.myhospital.doctorDashboardController.class) {
                return new com.hms.myhospital.doctorDashboardController(client);
            }
            if (param == com.hms.myhospital.prescriptionController.class) {
                return new com.hms.myhospital.prescriptionController(client);
            }
            if (param == com.hms.myhospital.loginController.class) {
                return new com.hms.myhospital.loginController(client);
            }
            // Controllers that do not need HMSClient
            if (param == com.hms.myhospital.welcomeController.class) {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (param == com.hms.myhospital.chooseAccountController.class) {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            // Default fallback
            try {
                return param.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}

