package com.hms.myhospital;

import com.hms.server.HMSServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HMSRunner extends Application {
    @Override
    public void start(Stage stage) {
        try {
           // System.out.println("more testing!");
            AdminDash root = new AdminDash();
            Scene scene = new Scene(root);

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
        //new HMSServer().start();
        new Thread(()->new HMSServer().start()).start() ;
        launch(args);
    }
}