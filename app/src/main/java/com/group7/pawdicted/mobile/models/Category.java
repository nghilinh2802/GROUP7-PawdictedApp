package com.group7.pawdicted.mobile.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private String category_id;
    private String category_name;
    private String category_description;
    private String category_image_url;
    private int category_rank;
    private List<String> category_keywords;

    private ArrayList<Product>products;


    public Category() {

    }

    public Category(String category_id, String category_name, String category_description, String category_image_url, int category_rank, List<String> category_keywords) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_description = category_description;
        this.category_image_url = category_image_url;
        this.category_rank = category_rank;
        this.category_keywords = category_keywords;

        this.products = new ArrayList<>();
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_description() {
        return category_description;
    }

    public void setCategory_description(String category_description) {
        this.category_description = category_description;
    }

    public String getCategory_image_url() {
        return category_image_url;
    }

    public void setCategory_image_url(String category_image_url) {
        this.category_image_url = category_image_url;
    }

    public int getCategory_rank() {
        return category_rank;
    }

    public void setCategory_rank(int category_rank) {
        this.category_rank = category_rank;
    }

    public List<String> getCategory_keywords() {
        return category_keywords;
    }

    public void setCategory_keywords(List<String> category_keywords) {
        this.category_keywords = category_keywords;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public String toString() {
        return category_id+"\t"+category_name;
    }

    public void addProduct (Product p){
        products.add(p);
    }

}
