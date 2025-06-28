package com.group7.pawdicted.mobile.models;

import java.util.Date;

public class Customer {
    private String customer_id;
    private String customer_name;
    private String customer_email;
    private String customer_username;
    private String phone_number;
    private String address;
    private String gender;
    private Date dob;
    private Date date_joined;
    private String avatar_img;
    private String role;

    public Customer() {}

    public Customer(String customer_id, String customer_name, String customer_email, String customer_username,
                    String phone_number, String address, String gender, Date dob, Date date_joined,
                    String avatar_img, String role) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_username = customer_username;
        this.phone_number = phone_number;
        this.address = address;
        this.gender = gender;
        this.dob = dob;
        this.date_joined = date_joined;
        this.avatar_img = avatar_img;
        this.role = role;
    }



    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_username() {
        return customer_username;
    }

    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Date getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    public String getAvatar_img() {
        return avatar_img;
    }

    public void setAvatar_img(String avatar_img) {
        this.avatar_img = avatar_img;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customer_id='" + customer_id + '\'' +
                ", customer_username='" + customer_username + '\'' +
                ", customer_email='" + customer_email + '\'' +
                '}';
    }
}
