package com.group7.pawdicted.mobile.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class AddressItem implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String address;
    private boolean isDefault;
    private Timestamp time;

    public AddressItem() {} // Required by Firestore

    // Full constructor
    public AddressItem(String id, String name, String phone, String address, boolean isDefault, Timestamp time) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isDefault = isDefault;
        this.time = time;
    }

    // Convenient constructor
    public AddressItem(String name, String phone, String address, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isDefault = isDefault;
        this.time = Timestamp.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }
}
