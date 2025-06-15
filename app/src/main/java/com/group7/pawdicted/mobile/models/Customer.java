package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private int customer_id;
    private String customer_name;
    private String customer_email;
    private String customer_username;
    private String password;
    private String phone_number;

    private String address;
    private Date date_joined;

    private ArrayList<Integer> wishlist;
    private ArrayList<Integer> cart;
    private ArrayList<Integer> recently_viewed;
    private ArrayList<Integer> purchased;
    private ArrayList<Review> reviews;

    private ArrayList<String> preferences;

    public Customer() {
    }

    public Customer(int customer_id, String customer_name, String customer_email, String customer_username, String password, String phone_number, String address, Date date_joined, ArrayList<Integer> wishlist, ArrayList<Integer> cart, ArrayList<Integer> recently_viewed, ArrayList<Integer> purchased, ArrayList<Review> reviews, ArrayList<String> preferences) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_username = customer_username;
        this.password = password;
        this.phone_number = phone_number;
        this.address = address;
        this.date_joined = date_joined;
        this.wishlist = wishlist;
        this.cart = cart;
        this.recently_viewed = recently_viewed;
        this.purchased = purchased;
        this.reviews = reviews;
        this.preferences = preferences;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    public ArrayList<Integer> getWishlist() {
        return wishlist;
    }

    public void setWishlist(ArrayList<Integer> wishlist) {
        this.wishlist = wishlist;
    }

    public ArrayList<Integer> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Integer> cart) {
        this.cart = cart;
    }

    public ArrayList<Integer> getRecently_viewed() {
        return recently_viewed;
    }

    public void setRecently_viewed(ArrayList<Integer> recently_viewed) {
        this.recently_viewed = recently_viewed;
    }

    public ArrayList<Integer> getPurchased() {
        return purchased;
    }

    public void setPurchased(ArrayList<Integer> purchased) {
        this.purchased = purchased;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customer_username='" + customer_username + '\'' +
                ", customer_id=" + customer_id +
                ", preferences=" + preferences +
                '}';
    }
}
