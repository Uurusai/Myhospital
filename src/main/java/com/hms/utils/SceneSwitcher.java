package com.hms.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    private static Stage primaryStage; // Reference to your single stage

    // Call this once when your app starts (in Main.java)
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

    public static <T> T switchSceneWithController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        primaryStage.setScene(new Scene(root));
        return controller;
    }

}

