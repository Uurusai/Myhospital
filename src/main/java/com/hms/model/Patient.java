package com.hms.model;

import com.hms.utils.PasswordUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient implements Serializable {

    String name;
    int id;
    String gender;
    int contactNo ;
    int age;
    String date_of_birth;
    String address;
    String blood_type ;
    String password;
    String account_status ;

    //constructor
    public Patient(String name,int id,String gender,int age,String date_of_birth,int contactNo,String address,String blood_type) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.contactNo = contactNo;
        this.address = address;
        this.age = age ;
        this.date_of_birth = date_of_birth;
        this.blood_type = blood_type;
    }

    public Patient(String name,int id,String gender,int age,String date_of_birth,int contactNo,String address,String blood_type,String password,String account_status) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.contactNo = contactNo;
        this.address = address;
        this.age = age ;
        this.date_of_birth = date_of_birth;
        this.blood_type = blood_type ;
        this.password = password;
        this.account_status = account_status ;
    }

    public Patient(String name,String gender,int age,String date_of_birth,int contactNo,String address,String blood_type){
        this.name = name;
        this.gender = gender;
        this.contactNo = contactNo;
        this.address = address;
        this.age = age ;
        this.date_of_birth = date_of_birth;
        this.blood_type = blood_type;

    }

    public Patient() {

    }
    //getters

    public String getAccount_status() {
        return account_status;
    }
    public String getPassword(){ return PasswordUtil.hashPassword(password); }
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
    public String getBlood_type(){  return blood_type;}

    //setters


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
    public void setBlood_type(String blood_type) {  this.blood_type = blood_type;   }
    public void setPassword(String password){  this.password = password; }
    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }


}
