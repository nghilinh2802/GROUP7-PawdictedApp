package com.group7.pawdicted.mobile.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFirestoreService {
    private static final String TAG = "CartFirestoreService";

    public static void syncCartToFirestore(String customerId, List<CartItem> cartItems) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("carts").document(customerId).collection("items");

        // Bước 1: Lấy danh sách các document IDs hiện có trên Firestore
        itemsRef.get().addOnSuccessListener(querySnapshot -> {
            List<String> existingDocIds = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                existingDocIds.add(doc.getId());
            }

            // Bước 2: Tạo danh sách document IDs từ cartItems hiện tại
            List<String> currentDocIds = new ArrayList<>();
            for (CartItem item : cartItems) {
                String docId = item.productId + "_" + item.selectedOption;
                currentDocIds.add(docId);
            }

            // Bước 3: Xóa các document không còn trong cartItems
            for (String docId : existingDocIds) {
                if (!currentDocIds.contains(docId)) {
                    itemsRef.document(docId).delete()
                            .addOnSuccessListener(unused -> Log.d(TAG, "Đã xóa item: " + docId))
                            .addOnFailureListener(e -> Log.e(TAG, "Lỗi khi xóa item: " + docId, e));
                }
            }

            // Bước 4: Ghi đè các item hiện tại trong cartItems
            for (CartItem item : cartItems) {
                String docId = item.productId + "_" + item.selectedOption;
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productId", item.productId);
                itemMap.put("name", item.name);
                itemMap.put("price", item.price);
                itemMap.put("imageUrl", item.imageUrl);
                itemMap.put("options", item.options);
                itemMap.put("selectedOption", item.selectedOption);
                itemMap.put("quantity", item.quantity);
                itemMap.put("isSelected", item.isSelected);
                itemMap.put("optionPrices", item.optionPrices);

                itemsRef.document(docId).set(itemMap)
                        .addOnSuccessListener(unused -> Log.d(TAG, "Đã lưu item: " + item.name))
                        .addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lưu item: " + item.name, e));
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi khi lấy danh sách item hiện có trên Firestore", e);
        });
    }

    public static void fetchCartFromFirestore(Context context, String customerId, Runnable onComplete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("carts")
                .document(customerId)
                .collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CartItem> cartItems = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);
                        if (item != null) cartItems.add(item);
                    }

                    // ✅ sửa lỗi gọi static sai: dùng getInstance()
                    CartManager.getInstance().setCartItems(cartItems);
                    CartManager.getInstance().setCustomerId(customerId);

                    // ✅ sửa lỗi thiếu customerId khi lưu local
                    CartStorageHelper.saveCart(context, customerId, cartItems);

                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartLoad", "Failed to fetch cart", e);
                    if (onComplete != null) onComplete.run();
                });
    }
}
