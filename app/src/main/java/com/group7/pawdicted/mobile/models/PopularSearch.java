package com.group7.pawdicted.mobile.models;

public class PopularSearch {
    private String searchTerm;
    private String productImage;
    private int productCount;

    public PopularSearch() {}

    public PopularSearch(String searchTerm, String productImage, int productCount) {
        this.searchTerm = searchTerm;
        this.productImage = productImage;
        this.productCount = productCount;
    }

    // Getters and Setters
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }

}
