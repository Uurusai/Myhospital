package com.hms.model;

import com.hms.utils.PasswordUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Admin implements Serializable {
    String name;
    int id;
    String email;
    String contactNo;
    String address;
    String password ;

    public Admin(String name, int id, String email, String contactNo, String address) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPassword(){ return PasswordUtil.hashPassword(password); }
    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNo() {
        return this.contactNo ;
    }

    public void setPassword(String password){  this.password = password; }
}
