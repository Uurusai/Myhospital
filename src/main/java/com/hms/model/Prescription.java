package com.hms.model;

import java.util.List;

public class Prescription {
    Patient patient ;
    Doctor doctor;
    Appointment appointment;
    String date_made;
    List<String> directions;
    List<String> medicines ;
    List<String> tests ;
    //setters
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    public void setDate_made(String date_made) {
        this.date_made = date_made;
    }

    //getters
    public Appointment getAppointment() {
        return appointment;
    }
    public Doctor getDoctor() {
        return doctor;
    }
    public Patient getPatient() {
        return patient;
    }
    public String getDate_made() {
        return date_made;
    }
}

