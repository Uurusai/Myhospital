package com.hms.model;

import java.util.ArrayList;
import java.util.List;

public class Admin {
    String name;
    int id;
    String gender;
    String email;
    int contactNo ;
    String address;
    List<Doctor> doctors = new ArrayList<Doctor>();
    List<Patient> patients = new ArrayList<Patient>();

    public Admin(String name, int id, String gender,String email, int contactNo, String address) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
    }
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
}
