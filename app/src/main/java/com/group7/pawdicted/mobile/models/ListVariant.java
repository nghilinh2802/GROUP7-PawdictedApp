package com.group7.pawdicted.mobile.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListVariant {
    private ArrayList<Variant> variants;

    public ListVariant() {
        variants = new ArrayList<>();
    }

    public ArrayList<Variant> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<Variant> variants) {
        this.variants = variants;
    }

    public void generate_sample_dataset() {
        variants.add(new Variant("FT00011", "20kg", "https://ik.imagekit.io/0kl6tkq9q/Image/FT00031.png?updatedAt=1750211546448", "FT0001",
                300000, 3.6, 80, 10, getDate("2025-17-06"), 5, 200));
        variants.add(new Variant("FT00012", "15kg", "https://ik.imagekit.io/0kl6tkq9q/Image/FT00032.jpg?updatedAt=1750211546547", "FT0001",
                250000, 3.4, 87, 40, getDate("2025-17-06"), 5, 798));
    }

    public List<Variant> getVariantsByProductId(String productId) {
        List<Variant> result = new ArrayList<>();
        if (productId == null) {
            Log.w("ListVariant", "productId is null, returning empty list");
            return result;
        }
        for (Variant variant : variants) {
            if (variant != null) {
                if (variant.getProduct_id() != null && variant.getProduct_id().equals(productId)) {
                    result.add(variant);
                } else if (variant.getProduct_id() == null) {
                    Log.w("ListVariant", "Variant " + (variant.getVariant_id() != null ? variant.getVariant_id() : "null") + " has null product_id");
                }
            } else {
                Log.w("ListVariant", "Found null variant in variants list");
            }
        }
        Log.d("ListVariant", "Found " + result.size() + " variants for productId: " + productId);
        return result;
    }

    private Date getDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            Log.e("ListVariant", "Date parsing error: " + e.getMessage());
            return new Date();
        }
    }
}