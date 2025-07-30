package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class appointmentRowController {
    @FXML private Label patientName;
    @FXML private Label appointmentDate;
    @FXML private Label appointmentTime;
    @FXML private Button viewPatientBtn;
    @FXML private Button confirmAppointmentBtn;
    @FXML private Button cancelAppointmentBtn;

    @FXML
    public void setAppointmentData(String name, String date, String time, boolean confirmed) {
        patientName.setText(name);
        appointmentDate.setText(date);
        appointmentTime.setText(time);
        if(confirmed) {
            confirmAppointmentBtn.setVisible(false);
        } else {
            confirmAppointmentBtn.setVisible(true);
        }
        cancelAppointmentBtn.setVisible(true);
        viewPatientBtn.setVisible(true);
    }


}
