package com.group7.pawdicted.mobile.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.*;
import com.group7.pawdicted.mobile.services.CartStorageHelper;

import java.util.*;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems = new ArrayList<>();
    private String currentCustomerId;

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> items) {
        cartItems = items;
    }

    public void setCustomerId(String id) {
        currentCustomerId = id;
    }

    public String getCustomerId() {
        return currentCustomerId;
    }

    public void addToCart(CartItem newItem) {
        if (newItem == null || newItem.productId == null || newItem.selectedOption == null) {
            return;
        }

        for (CartItem item : cartItems) {
            if (newItem.productId.equals(item.productId) &&
                    newItem.selectedOption.equals(item.selectedOption)) {
                item.quantity += newItem.quantity;
                return;
            }
        }
        cartItems.add(newItem);
    }

    public void loadCartFromFirestore(Context context, String customerId, Runnable onComplete) {
        FirebaseFirestore.getInstance()
                .collection("carts")
                .document(customerId)
                .collection("items")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<CartItem> loadedCart = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);
                        if (item != null) loadedCart.add(item);
                    }
                    this.setCartItems(loadedCart);
                    this.setCustomerId(customerId);
                    CartStorageHelper.saveCart(context, customerId, loadedCart);
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartManager", "Lỗi khi load Firestore: " + e.getMessage());
                    if (onComplete != null) onComplete.run();
                });
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }
}
