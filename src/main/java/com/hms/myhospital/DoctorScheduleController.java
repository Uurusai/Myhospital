package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.dao.DoctorScheduleDAO;
import com.hms.model.DoctorSchedule;
import com.hms.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleController {

    @FXML
    private Spinner<Integer> startTimeSpinner;
    @FXML
    private Spinner<Integer> endTimeSpinner;
    @FXML
    private ListView<String> offDaysList;
    @FXML
    private Button submit_btn ;

    private HMSClient client;
    //private int doctorId;
    private DoctorSchedule ds ;

    public DoctorScheduleController(HMSClient client, int doctorId) {
        this.client = client;
        this.ds = new DoctorSchedule(doctorId);
    }

    @FXML
    public void initialize() {
        // Spinner setup (in 24hr format)
        startTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        endTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17));

        // ListView setup for off days
        offDaysList.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        offDaysList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        //submit action
        submit_btn.setOnAction(e-> {
            try {
                SceneSwitcher.switchScene("/com/hms/myhospital/doctorDashboard.fxml");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @FXML
    public void handleSubmit() {
        int startHour = startTimeSpinner.getValue();
        int endHour = endTimeSpinner.getValue();

        LocalTime start = LocalTime.of(startHour, 0);
        LocalTime end = LocalTime.of(endHour, 0);

        List<Integer> offDays = new ArrayList<>();
        for (String selected : offDaysList.getSelectionModel().getSelectedItems()) {
            offDays.add(dayStringToInt(selected));
        }
        client.setWorkingDays(this.ds,offDays,start,end);
    }

    private int dayStringToInt(String day) {
        return switch (day.toLowerCase()) {
            case "sunday" -> 0;
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            default -> -1;
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
