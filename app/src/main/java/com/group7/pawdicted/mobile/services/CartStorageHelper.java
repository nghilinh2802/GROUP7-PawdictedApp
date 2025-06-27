package com.group7.pawdicted.mobile.services;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.models.CartItem;
import java.lang.reflect.Type;
import java.util.*;

public class CartStorageHelper {
    public static void saveCart(Context context, String customerId, List<CartItem> cartItems) {
        SharedPreferences prefs = context.getSharedPreferences("cart_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = "cart_" + customerId;
        editor.putString(key, new Gson().toJson(cartItems));
        editor.apply();
    }

    public static List<CartItem> loadCart(Context context, String customerId) {
        SharedPreferences prefs = context.getSharedPreferences("cart_pref", Context.MODE_PRIVATE);
        String json = prefs.getString("cart_" + customerId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void clearCart(Context context, String customerId) {
        SharedPreferences prefs = context.getSharedPreferences("cart_pref", Context.MODE_PRIVATE);
        prefs.edit().remove("cart_" + customerId).apply();
    }
}