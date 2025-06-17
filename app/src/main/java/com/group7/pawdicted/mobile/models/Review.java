package com.group7.pawdicted.mobile.models;

public class Review {
    private String review_id;
    private String customer_id;
    private int rating;
    private String product_id;
    private String product_variation;
    private String comment;
    private long timestamp;

    public Review() {
    }

    public Review(String review_id, String customer_id, int rating, String product_id, String product_variation, String comment, long timestamp) {
        this.review_id = review_id;
        this.customer_id = customer_id;
        this.rating = rating;
        this.product_id = product_id;
        this.product_variation = product_variation;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_variation() {
        return product_variation;
    }

    public void setProduct_variation(String product_variation) {
        this.product_variation = product_variation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
