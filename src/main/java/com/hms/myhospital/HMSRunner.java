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

    private static HMSClient client;

    public static HMSClient getClient() {
        return client;
    }

    @Override
    public void start(Stage primaryStage) {

        client = new HMSClient("localhost", 12345);

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