package com.hms.myhospital;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AdminDash root = new AdminDash();
            Scene scene = new Scene(root);

            // Apply CSS if needed
            scene.getStylesheets().add(
                    getClass().getResource("/com/hms/myhospital/DashboardStyle.css").toExternalForm());

            stage.setTitle("Hospital Management System");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}