package com.group7.pawdicted.mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem implements Parcelable {
    public String productId;
    public String name;
    public int price;
    public String imageUrl;
    public List<String> options;
    public String selectedOption;
    public int quantity;
    public Map<String, Integer> optionPrices;
    public Map<String, String> optionImageUrls;
    public boolean isSelected;

    // Constructor mặc định (không tham số) yêu cầu bởi Firestore
    public CartItem() {
        // Firestore sẽ gọi constructor này để tạo đối tượng CartItem từ dữ liệu trong Firestore
    }

    // Constructor có tham số
    public CartItem(String productId, String name, int price, String imageUrl, List<String> options, String selectedOption) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = options != null ? options : new ArrayList<>();
        this.selectedOption = selectedOption;
        this.quantity = 1;
        this.optionPrices = new HashMap<>();
        this.optionImageUrls = new HashMap<>();
        this.isSelected = true; // Default to selected when adding to cart
    }

    // Parcelable constructor
    protected CartItem(Parcel in) {
        productId = in.readString();
        name = in.readString();
        price = in.readInt();
        imageUrl = in.readString();
        options = in.createStringArrayList();
        selectedOption = in.readString();
        quantity = in.readInt();

        // Read optionPrices (Map<String, Integer>)
        int optionPricesSize = in.readInt();
        optionPrices = new HashMap<>();
        for (int i = 0; i < optionPricesSize; i++) {
            String key = in.readString();
            Integer value = in.readInt();
            optionPrices.put(key, value);
        }

        // Read optionImageUrls (Map<String, String>)
        int optionImageUrlsSize = in.readInt();
        optionImageUrls = new HashMap<>();
        for (int i = 0; i < optionImageUrlsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            optionImageUrls.put(key, value);
        }

        isSelected = in.readByte() != 0;
    }

    // Parcelable creator
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(imageUrl);
        dest.writeStringList(options);
        dest.writeString(selectedOption);
        dest.writeInt(quantity);

        // Write optionPrices (Map<String, Integer>)
        dest.writeInt(optionPrices.size());
        for (Map.Entry<String, Integer> entry : optionPrices.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue());
        }

        // Write optionImageUrls (Map<String, String>)
        dest.writeInt(optionImageUrls.size());
        for (Map.Entry<String, String> entry : optionImageUrls.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }

        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
