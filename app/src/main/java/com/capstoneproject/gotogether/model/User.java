package com.capstoneproject.gotogether.model;

import java.math.BigInteger;

/**
 * Created by MinhNL on 10/1/2016.
 */
public class User {
    BigInteger userId;
    String fullname;
    int gender;
    String birthday;
    String email;
    String address;
    String phonenumber;
    int isActive;
    float star;
    String date_created;

    public User(){}

    public User(BigInteger userId, String fullname, int gender, String phonenumber, float star){
        this.userId = userId;
        this.fullname = fullname;
        this.gender = gender;
        this.phonenumber = phonenumber;
        this.star = star;
    }

    public User(BigInteger userId, String fullname, int gender,String email, String address, String phonenumber){
        this.userId = userId;
        this.fullname = fullname;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.phonenumber = phonenumber;
    }

    public User(BigInteger userId, String fullname, int gender, String email){
        this.userId = userId;
        this.fullname = fullname;
        this.gender = gender;
        this.email = email;
    }

    public void setUserId(BigInteger userId){
        this.userId = userId;
    }

    public BigInteger getUserId(){
        return  this.userId;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }

    public String getFullname(){
        return this.fullname;
    }

    public void setGender(int gender){
        this.gender = gender;
    }

    public int getGender(){
        return this.gender;
    }

    public void setPhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber(){
        return  this.phonenumber;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return  this.email;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return  this.address;
    }
}
