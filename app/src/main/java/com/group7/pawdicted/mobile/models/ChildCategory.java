package com.group7.pawdicted.mobile.models;

import androidx.annotation.NonNull;

public class ChildCategory {
    private String childCategory_id;
    private String childCategory_name;
    private String childCategory_description;
    private String childCategory_image;
    private int childCategory_rank;
    private String category_id;

    public ChildCategory() {
    }

    public ChildCategory(String childCategory_id, String childCategory_name, String childCategory_description, String childCategory_image, int childCategory_rank, String category_id) {
        this.childCategory_id = childCategory_id;
        this.childCategory_name = childCategory_name;
        this.childCategory_description = childCategory_description;
        this.childCategory_image = childCategory_image;
        this.childCategory_rank = childCategory_rank;
        this.category_id = category_id;
    }

    public String getChildCategory_id() {
        return childCategory_id;
    }

    public void setChildCategory_id(String childCategory_id) {
        this.childCategory_id = childCategory_id;
    }

    public String getChildCategory_name() {
        return childCategory_name;
    }

    public void setChildCategory_name(String childCategory_name) {
        this.childCategory_name = childCategory_name;
    }

    public String getChildCategory_description() {
        return childCategory_description;
    }

    public void setChildCategory_description(String childCategory_description) {
        this.childCategory_description = childCategory_description;
    }

    public String getChildCategory_image() {
        return childCategory_image;
    }

    public void setChildCategory_image(String childCategory_image) {
        this.childCategory_image = childCategory_image;
    }

    public int getChildCategory_rank() {
        return childCategory_rank;
    }

    public void setChildCategory_rank(int childCategory_rank) {
        this.childCategory_rank = childCategory_rank;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }


}

