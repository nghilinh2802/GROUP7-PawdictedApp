package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Product {

    private String product_id;
    private String product_name;

    private List<String> variant_id;

    private double price;
    private String description;
    private String details;

    private double average_rating;
    private int rating_number;

    private int quantity;
    private String product_image;

    private int animal_class_id; //0 là cat, 1 là dog
    private String category_id;

    private String child_category_id;



    private List<String> also_buy;
    private List<String> also_view;
    private List<String> similar_item;

    private int rank;
    private Date date_listed;

    private int discount;
    private int sold_quantity;




    public Product() {
    }

    public Product(String product_id, String product_name, List<String> variant_id, double price, String description, String details, double average_rating, int rating_number, int quantity, String product_image, int animal_class_id, String category_id, String child_category_id,  List<String> also_buy, List<String> also_view, List<String> similar_item, int rank, Date date_listed, int discount, int sold_quantity) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.variant_id = variant_id;
        this.price = price;
        this.description = description;
        this.details = details;
        this.average_rating = average_rating;
        this.rating_number = rating_number;
        this.quantity = quantity;
        this.product_image = product_image;
        this.animal_class_id = animal_class_id;
        this.category_id = category_id;
        this.child_category_id = child_category_id;
        this.also_buy = also_buy;
        this.also_view = also_view;
        this.similar_item = similar_item;
        this.rank = rank;
        this.date_listed = date_listed;
        this.discount = discount;
        this.sold_quantity = sold_quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public List<String> getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(List<String> variant_id) {
        this.variant_id = variant_id;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public int getRating_number() {
        return rating_number;
    }

    public void setRating_number(int rating_number) {
        this.rating_number = rating_number;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public int getAnimal_class_id() {
        return animal_class_id;
    }

    public void setAnimal_class_id(int animal_class_id) {
        this.animal_class_id = animal_class_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getChild_category_id() {
        return child_category_id;
    }

    public void setChild_category_id(String child_category_id) {
        this.child_category_id = child_category_id;
    }


    public List<String> getAlso_buy() {
        return also_buy;
    }

    public void setAlso_buy(List<String> also_buy) {
        this.also_buy = also_buy;
    }

    public List<String> getAlso_view() {
        return also_view;
    }

    public void setAlso_view(List<String> also_view) {
        this.also_view = also_view;
    }

    public List<String> getSimilar_item() {
        return similar_item;
    }

    public void setSimilar_item(List<String> similar_item) {
        this.similar_item = similar_item;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getDate_listed() {
        return date_listed;
    }

    public void setDate_listed(Date date_listed) {
        this.date_listed = date_listed;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(int sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", product_name='" + product_name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", average_rating=" + average_rating +
                ", quantity=" + quantity +
                '}';
    }

}
