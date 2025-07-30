package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.server.HMSServer;
import com.hms.utils.SceneSwitcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HMSRunner extends Application {
    @Override
    public void start(Stage primaryStage) {
//        try {
//           // System.out.println("more testing!");
//            HMSClient client = new HMSClient("localhost", 12345);
//            AdminDash root = new AdminDash(client);
//            Scene scene = new Scene(root);
//
//            scene.getStylesheets().add(
//                    getClass().getResource("/com/hms/myhospital/DashboardStyle.css").toExternalForm());
//
//            stage.setTitle("Hospital Management System");
//            stage.setScene(scene);
//            stage.show();
//            SceneSwitcher.setPrimaryStage(primaryStage);
//
//            Parent root = FXMLLoader.load(getClass().getResource("com/hms/myhospital/welcome.fxml"));
//            primaryStage.setTitle("Duckland Hospital");
//            primaryStage.setScene(new Scene(root, 1100, 600));
//            primaryStage.show();
//            primaryStage.setResizable(false);


//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(1);
//        }

        try {
            SceneSwitcher.setPrimaryStage(primaryStage);
            Parent root = FXMLLoader.load(getClass().getResource("/com/hms/myhospital/welcome.fxml"));
            primaryStage.setTitle("Duckland Hospital");
            primaryStage.setScene(new Scene(root, 1100, 600));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void main(String[] args) {

        new Thread(()->new HMSServer().start()).start() ;
        launch(args);
    }
}