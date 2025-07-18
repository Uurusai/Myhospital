package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class AdminDash extends StackPane {  // Must match fx:root type
    // Navigation Buttons
    @FXML private Button doctor_btn;
    @FXML private Button patient_btn;
    @FXML private Button appointment_btn;
    @FXML private Button db_btn;

    // Content Panes
    @FXML private AnchorPane patientsView;
    @FXML private AnchorPane doctorsView;
    @FXML private AnchorPane appointmentsView;
    @FXML private AnchorPane db_top;
    @FXML private AnchorPane db_btm;

    // Other UI elements
//    @FXML private Label top_username;
//    @FXML private Label date_time;
//    @FXML private Label current_form;
//    @FXML private Label admin_id;
//    @FXML private Label username;

    public AdminDash() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hms/myhospital/admin-dash.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML", e);
        }

        initialize();
    }

    @FXML
    private void initialize() {
        // Verify all injections
        if (doctor_btn == null || patientsView == null) {
            throw new RuntimeException("FXML injection failed - check your fx:id values");
        }

        // Set initial view
        setAllInvisible();
        db_top.setVisible(true);
        db_btm.setVisible(true);

        // Set up button actions
        doctor_btn.setOnAction(e -> switchToView(doctorsView));
        patient_btn.setOnAction(e -> switchToView(patientsView));
        appointment_btn.setOnAction(e -> switchToView(appointmentsView));
        db_btn.setOnAction(e -> {
            setAllInvisible();
            db_top.setVisible(true);
            db_btm.setVisible(true);
        });
    }

    private void switchToView(AnchorPane view) {
        setAllInvisible();
        view.setVisible(true);
    }

    private void setAllInvisible() {
        patientsView.setVisible(false);
        doctorsView.setVisible(false);
        appointmentsView.setVisible(false);
        db_top.setVisible(false);
        db_btm.setVisible(false);
    }
}