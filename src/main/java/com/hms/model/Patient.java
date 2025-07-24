package com.hms.model;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    enum Visitor_type{
        NEW,REVIEW,PAST
    }
    enum PaymentState{
        PENDING,DONE
    }
    String name;
    int id;
    String gender;
    int contactNo ;
    int age;
    String date_of_birth;
    String address;
    String vt ;
    String ps ;
    //List<Doctor> doctors = new ArrayList<Doctor>();
    //List<Appointment> appointments = new ArrayList<Appointment>();


    //constructor
    public Patient(String name,int id,String gender,int age,String date_of_birth,int contactNo,String address) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.contactNo = contactNo;
        this.address = address;
        this.age = age ;
        this.date_of_birth = date_of_birth;

    }

    public Patient(String name,int id,String gender,int age,String date_of_birth,int contactNo,String address,String payment_status,String visitor_type) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.contactNo = contactNo;
        this.address = address;
        this.age = age ;
        this.date_of_birth = date_of_birth;
        this.ps = payment_status;
        this.vt = visitor_type;

    }

    //getters
    public String getVt() {
        return vt;
    }
    public String getPs() {
        return ps;
    }
    public String getDate_of_birth() {
        return date_of_birth;
    }
    public int getAge() {
        return age;
    }
    public String getName() {
        return name;
    }
    public int getContactNo() {
        return contactNo;
    }
    public int getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
    public String getGender() {
        return gender;
    }
//    public List<Appointment> getAppointments() {
//        return appointments;
//    }
//    public List<Doctor> getDoctors() {
//        return doctors;
//    }
    //setters

    public void setVisitorType(String vt) {
        this.vt = vt;
    }
    public void setPaymentState(String ps) {
        this.ps = ps;
    }
    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setContactNo(int contactNo) {
        this.contactNo = contactNo ;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setName(String name) {
        this.name = name;
    }
//    public void setAppointments(List<Appointment> appointments) {
//        this.appointments = appointments;
//    }
//    public void setDoctors(List<Doctor> doctors) {
//        this.doctors = doctors;
//    }

}
