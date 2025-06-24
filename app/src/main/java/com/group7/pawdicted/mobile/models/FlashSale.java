package com.group7.pawdicted.mobile.models;

import java.util.List;

public class FlashSale {
    private String flashSale_id;
    private String flashSale_name;
    private int discountRate;
    private long startTime;
    private long endTime;
    private List<FlashSaleProductInfo> products;

    public FlashSale() {}

    // Getters and Setters
    public String getFlashSale_id() { return flashSale_id; }
    public void setFlashSale_id(String flashSale_id) { this.flashSale_id = flashSale_id; }

    public String getFlashSale_name() { return flashSale_name; }
    public void setFlashSale_name(String flashSale_name) { this.flashSale_name = flashSale_name; }

    public int getDiscountRate() { return discountRate; }
    public void setDiscountRate(int discountRate) { this.discountRate = discountRate; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public List<FlashSaleProductInfo> getProducts() { return products; }
    public void setProducts(List<FlashSaleProductInfo> products) { this.products = products; }

    // Utility methods
    public boolean isActive() {
        long now = System.currentTimeMillis();
        return now >= startTime && now <= endTime;
    }

    public long getTimeRemaining() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    // Inner class cho product info trong flash sale
    public static class FlashSaleProductInfo {
        private String product_id;
        private int discountRate;
        private int maxQuantity;
        private int unitSold;

        public FlashSaleProductInfo() {}

        // Getters and Setters
        public String getProduct_id() { return product_id; }
        public void setProduct_id(String product_id) { this.product_id = product_id; }

        public int getDiscountRate() { return discountRate; }
        public void setDiscountRate(int discountRate) { this.discountRate = discountRate; }

        public int getMaxQuantity() { return maxQuantity; }
        public void setMaxQuantity(int maxQuantity) { this.maxQuantity = maxQuantity; }

        public int getUnitSold() { return unitSold; }
        public void setUnitSold(int unitSold) { this.unitSold = unitSold; }

        public int getRemainingQuantity() {
            return maxQuantity - unitSold;
        }

        public double getSoldPercentage() {
            return maxQuantity > 0 ? (unitSold * 100.0) / maxQuantity : 0;
        }
    }
}