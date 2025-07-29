package com.hms.model;

import com.hms.utils.PasswordUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor implements Serializable {
    String name;
    int id;
    String gender;
    String email;
    String speciality ;
    int contactNo ;
    String address;
    List<Patient> patients = new ArrayList<Patient>();
    List<Appointment> appointments = new ArrayList<Appointment>();
    String password ;
    String account_status ;


    //constructor
    public Doctor(String name, int id, String gender, String email, String speciality, int contactNo, String address) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.email = email;
        this.speciality = speciality;
        this.contactNo = contactNo;
        this.address = address;
    }
    public Doctor(String name, String gender, String email, String speciality, int contactNo, String address) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.speciality = speciality;
        this.contactNo = contactNo;
        this.address = address;
    }


    //setter
    public void setAccount_status(String account_status){  this.account_status = account_status;}
    public void setPassword(){ this.password = password ;}
    public void setName(String name) {
        this.name = name;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setContactNo(int contactNo) {
        this.contactNo = contactNo;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    //getters
    public String getAccount_status(){ return account_status;   }
    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public String getAddress() {
        return address;
    }
    public int getId() {
        return id;
    }
    public int getContactNo() {
        return contactNo;
    }
    public String getEmail() {
        return email;
    }
    public String getSpeciality() {
        return speciality;
    }
    public List<Appointment> getAppointments() {
        return appointments;
    }
    public List<Patient> getPatients() {
        return patients;
    }
    public String getPassword(){ return PasswordUtil.hashPassword(password); }
}
