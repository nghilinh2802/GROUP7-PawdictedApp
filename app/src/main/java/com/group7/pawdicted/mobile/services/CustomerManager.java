package com.group7.pawdicted.mobile.services;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.UUID;

public class CustomerManager {
    private static final String PREF_NAME = "customer_prefs";
    private static final String KEY_GUEST_ID = "guest_customer_id";
    private static CustomerManager instance;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private CustomerManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static CustomerManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomerManager(context.getApplicationContext());
        }
        return instance;
    }

    public String getCustomerId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return getGuestCustomerId();
        }
    }

    private String getGuestCustomerId() {
        String guestId = sharedPreferences.getString(KEY_GUEST_ID, null);
        if (guestId == null) {
            guestId = "guest_" + UUID.randomUUID().toString();
            saveGuestCustomerId(guestId);
        }
        return guestId;
    }

    private void saveGuestCustomerId(String guestId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_GUEST_ID, guestId);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getDisplayName() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                return displayName;
            }
            String email = user.getEmail();
            if (email != null) {
                return email.split("@")[0];
            }
        }
        return "Khách hàng";
    }

    public void getCustomerInfo(OnCustomerInfoListener listener) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onSuccess(null, getDisplayName());
            }
            return;
        }

        db.collection("customers")
                .whereEqualTo("customer_email", user.getEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        String customerName = doc.getString("customer_name");
                        if (listener != null) {
                            listener.onSuccess(doc, customerName != null ? customerName : getDisplayName());
                        }
                    } else {
                        if (listener != null) {
                            listener.onSuccess(null, getDisplayName());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    public void clearGuestData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_GUEST_ID);
        editor.apply();
    }

    public interface OnCustomerInfoListener {
        void onSuccess(DocumentSnapshot customerDoc, String displayName);
        void onFailure(String error);
    }
}
