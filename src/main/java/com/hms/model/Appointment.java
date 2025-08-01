package com.hms.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
//    enum Status{
//        PENDING,APPROVED,REJECTED,NOSHOW,COMPLETED
//    }
    private int id ;
    private LocalDateTime date_made ;
    private LocalDateTime  date_requested ;
    private LocalDateTime  date_scheduled;
    private String diagnosis ;
    Patient p ;
    Doctor d ;
    String status ;
    String isComplete ;
    String symptoms ;

    //constructor
    public Appointment(int id, LocalDateTime date_made, LocalDateTime date_requested, LocalDateTime date_scheduled, Patient  p, Doctor d, String status){
        this.id = id;
        this.date_made = date_made;
        this.date_requested = date_requested ;
        this.date_scheduled = date_scheduled;
        this.p = p;
        this.d = d;
        this.status = status ;
    }

    public Appointment() {

    }
    //setters
    public void setDiagnosis(String diagnosis){     this.diagnosis = diagnosis ; }
    public void setComplete(String complete) { isComplete = complete;  }
    public void setSymptoms(String symptoms) {  this.symptoms = symptoms;   }
    public void setStatus(String status) {  this.status = status;   }
    public void setDate_scheduled(LocalDateTime date_scheduled) {  this.date_scheduled = date_scheduled;   }
    public void setId(int id) {
        this.id = id;
    }
    public void setDoctor(Doctor d) {
        this.d = d;
    }
    public void setPatient(Patient p){
        this.p = p ;
    }
    public void setDate_requested(LocalDateTime  date_seen) {
        this.date_requested = date_seen;
    }
    public void setDate_made(LocalDateTime  date_made) {
        this.date_made = date_made;
    }
    //getters
    public String getDiagnosis(){   return diagnosis ;}
    public String isComplete() {   return isComplete;  }
    public LocalDateTime  getDate_scheduled(){  return date_scheduled;   }
    public int getId() {
        return id;
    }
    public Patient getPatient() {
        return p;
    }
    public Doctor getDoctor() {
        return d;
    }
    public LocalDateTime  getDate_made() {
        return date_made;
    }
    public LocalDateTime  getDate_requested() {
        return date_requested;
    }
    public String getStatus() {
        return status;
    }
    public String getSymptoms(){    return symptoms ;}
}
