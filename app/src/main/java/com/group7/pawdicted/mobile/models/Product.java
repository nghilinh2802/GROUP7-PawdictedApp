package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class Product {

    private String product_id;
    private String product_name;

    private int variant_id;
    private String variant_name;

    private double price;
    private String description;
    private String details;

    private double average_rating;
    private int rating_number;

    private int quantity;
    private Set<AnimalType> animal_class;
    private String child_category;

    private String product_image;
    private int category_id;

    private ArrayList<Integer> also_buy;
    private ArrayList<Integer> also_view;
    private ArrayList<Integer> similar_item;

    private int rank;
    private Date date_listed;

    private int discount;
    private double promotion_price;
    private int sold_quantity;


    public enum AnimalType {
        DOG,
        CAT
    }
//    private String categoryTop;
//    private String categorySide;

    public Product() {
    }

    public Product(String product_id, String product_name, int variant_id, String variant_name, double price, String description, String details, double average_rating, int rating_number, int quantity, Set<AnimalType> animal_class, String child_category, String product_image, int category_id, ArrayList<Integer> also_buy, ArrayList<Integer> also_view, ArrayList<Integer> similar_item, int rank, Date date_listed, int discount, double promotion_price, int sold_quantity) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.variant_id = variant_id;
        this.variant_name = variant_name;
        this.price = price;
        this.description = description;
        this.details = details;
        this.average_rating = average_rating;
        this.rating_number = rating_number;
        this.quantity = quantity;
        this.animal_class = animal_class;
        this.child_category = child_category;
        this.product_image = product_image;
        this.category_id = category_id;
        this.also_buy = also_buy;
        this.also_view = also_view;
        this.similar_item = similar_item;
        this.rank = rank;
        this.date_listed = date_listed;
        this.discount = discount;
        this.promotion_price = promotion_price;
        this.sold_quantity = sold_quantity;
//        this.categoryTop = categoryTop;
//        this.categorySide = categorySide;
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

    public int getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(int variant_id) {
        this.variant_id = variant_id;
    }

    public String getVariant_name() {
        return variant_name;
    }

    public void setVariant_name(String variant_name) {
        this.variant_name = variant_name;
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

    public Set<AnimalType> getAnimal_class() {
        return animal_class;
    }

    public void setAnimal_class(Set<AnimalType> animal_class) {
        this.animal_class = animal_class;
    }

    public String getChild_category() {
        return child_category;
    }

    public void setChild_category(String child_category) {
        this.child_category = child_category;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public ArrayList<Integer> getAlso_buy() {
        return also_buy;
    }

    public void setAlso_buy(ArrayList<Integer> also_buy) {
        this.also_buy = also_buy;
    }

    public ArrayList<Integer> getAlso_view() {
        return also_view;
    }

    public void setAlso_view(ArrayList<Integer> also_view) {
        this.also_view = also_view;
    }

    public ArrayList<Integer> getSimilar_item() {
        return similar_item;
    }

    public void setSimilar_item(ArrayList<Integer> similar_item) {
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

    public double getPromotion_price() {
        return promotion_price;
    }

    public void setPromotion_price(double promotion_price) {
        this.promotion_price = promotion_price;
    }

    public int getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(int sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

//    public void setCategoryTop(String categoryTop) {
//        this.categoryTop = categoryTop;
//    }
//
//    public void setCategorySide(String categorySide) {
//        this.categorySide = categorySide;
//    }

//    public String getCategoryTop() { return categoryTop; }
//    public String getCategorySide() { return categorySide; }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", product_name='" + product_name + '\'' +
                ", variant_name='" + variant_name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", average_rating=" + average_rating +
                ", quantity=" + quantity +
                '}';
    }

}
