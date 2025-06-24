package com.group7.pawdicted.mobile.models;

import java.util.Date;
import java.util.List;

public class FlashSaleProduct extends Product {
    private FlashSale.FlashSaleProductInfo flashSaleInfo;
    private String flashSaleId;
    private String flashSaleName;
    private long flashSaleEndTime;

    public FlashSaleProduct() {
        super();
    }

    public FlashSaleProduct(Product product, FlashSale.FlashSaleProductInfo flashSaleInfo,
                            String flashSaleId, String flashSaleName, long flashSaleEndTime) {
        // Copy tất cả thuộc tính từ Product
        this.setProduct_id(product.getProduct_id());
        this.setProduct_name(product.getProduct_name());
        this.setVariant_id(product.getVariant_id());
        this.setPrice(product.getPrice());
        this.setDescription(product.getDescription());
        this.setDetails(product.getDetails());
        this.setAverage_rating(product.getAverage_rating());
        this.setRating_number(product.getRating_number());
        this.setQuantity(product.getQuantity());
        this.setProduct_image(product.getProduct_image());
        this.setAnimal_class_id(product.getAnimal_class_id());
        this.setCategory_id(product.getCategory_id());
        this.setChild_category_id(product.getChild_category_id());
        this.setAlso_buy(product.getAlso_buy());
        this.setAlso_view(product.getAlso_view());
        this.setSimilar_item(product.getSimilar_item());
        this.setRank(product.getRank());
        this.setDate_listed(product.getDate_listed());
        this.setDiscount(product.getDiscount());
        this.setSold_quantity(product.getSold_quantity());

        // Set flash sale info
        this.flashSaleInfo = flashSaleInfo;
        this.flashSaleId = flashSaleId;
        this.flashSaleName = flashSaleName;
        this.flashSaleEndTime = flashSaleEndTime;
    }

    // Getters and Setters
    public FlashSale.FlashSaleProductInfo getFlashSaleInfo() { return flashSaleInfo; }
    public void setFlashSaleInfo(FlashSale.FlashSaleProductInfo flashSaleInfo) { this.flashSaleInfo = flashSaleInfo; }

    public String getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(String flashSaleId) { this.flashSaleId = flashSaleId; }

    public String getFlashSaleName() { return flashSaleName; }
    public void setFlashSaleName(String flashSaleName) { this.flashSaleName = flashSaleName; }

    public long getFlashSaleEndTime() { return flashSaleEndTime; }
    public void setFlashSaleEndTime(long flashSaleEndTime) { this.flashSaleEndTime = flashSaleEndTime; }

    // Utility methods
    public double getFlashSalePrice() {
        if (flashSaleInfo != null) {
            return getPrice() * (100 - flashSaleInfo.getDiscountRate()) / 100.0;
        }
        return getPrice();
    }

    public int getFlashSaleDiscountRate() {
        return flashSaleInfo != null ? flashSaleInfo.getDiscountRate() : 0;
    }

    public int getFlashSaleUnitSold() {
        return flashSaleInfo != null ? flashSaleInfo.getUnitSold() : 0;
    }

    public int getFlashSaleMaxQuantity() {
        return flashSaleInfo != null ? flashSaleInfo.getMaxQuantity() : 0;
    }

    public int getFlashSaleRemainingQuantity() {
        return flashSaleInfo != null ? flashSaleInfo.getRemainingQuantity() : 0;
    }

    public double getFlashSaleSoldPercentage() {
        return flashSaleInfo != null ? flashSaleInfo.getSoldPercentage() : 0;
    }

    public boolean isFlashSaleAvailable() {
        return flashSaleInfo != null && flashSaleInfo.getRemainingQuantity() > 0;
    }
}