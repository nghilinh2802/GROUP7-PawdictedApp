package com.group7.pawdicted.mobile.models;

import java.util.Date;
import java.util.List;

public class Variant {
    private String variant_id;
    private String variant_name;
    private String variant_image;
    private String product_id;
    private double variant_price;

    private double variant_rating;
    private int variant_rating_number;

    private int variant_quantity;
    private Date variant_date_listed;
    private int variant_discount;
    private int variant_sold_quantity;

    public Variant() {
    }

    public Variant(String variant_id, String variant_name, String variant_image, String product_id, double variant_price, double variant_rating, int variant_rating_number, int variant_quantity, Date variant_date_listed, int variant_discount, int variant_sold_quantity) {
        this.variant_id = variant_id;
        this.variant_name = variant_name;
        this.variant_image = variant_image;
        this.product_id = product_id;
        this.variant_price = variant_price;
        this.variant_rating = variant_rating;
        this.variant_rating_number = variant_rating_number;
        this.variant_quantity = variant_quantity;
        this.variant_date_listed = variant_date_listed;
        this.variant_discount = variant_discount;
        this.variant_sold_quantity = variant_sold_quantity;
    }


    public String getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(String variant_id) {
        this.variant_id = variant_id;
    }

    public String getVariant_name() {
        return variant_name;
    }

    public void setVariant_name(String variant_name) {
        this.variant_name = variant_name;
    }

    public String getVariant_image() {
        return variant_image;
    }

    public void setVariant_image(String variant_image) {
        this.variant_image = variant_image;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public double getVariant_price() {
        return variant_price;
    }

    public void setVariant_price(double variant_price) {
        this.variant_price = variant_price;
    }

    public double getVariant_rating() {
        return variant_rating;
    }

    public void setVariant_rating(double variant_rating) {
        this.variant_rating = variant_rating;
    }

    public int getVariant_rating_number() {
        return variant_rating_number;
    }

    public void setVariant_rating_number(int variant_rating_number) {
        this.variant_rating_number = variant_rating_number;
    }

    public int getVariant_quantity() {
        return variant_quantity;
    }

    public void setVariant_quantity(int variant_quantity) {
        this.variant_quantity = variant_quantity;
    }

    public Date getVariant_date_listed() {
        return variant_date_listed;
    }

    public void setVariant_date_listed(Date variant_date_listed) {
        this.variant_date_listed = variant_date_listed;
    }

    public int getVariant_discount() {
        return variant_discount;
    }

    public void setVariant_discount(int variant_discount) {
        this.variant_discount = variant_discount;
    }

    public int getVariant_sold_quantity() {
        return variant_sold_quantity;
    }

    public void setVariant_sold_quantity(int variant_sold_quantity) {
        this.variant_sold_quantity = variant_sold_quantity;
    }
}
