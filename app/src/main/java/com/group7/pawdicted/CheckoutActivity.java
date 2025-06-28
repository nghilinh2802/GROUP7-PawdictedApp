package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.adapters.OrderItemAdapter;
import com.group7.pawdicted.mobile.adapters.ShippingOptionAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.ShippingOption;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private RecyclerView recyclerViewShippingOptions;
    private List<CartItem> cartItems;
    private TextView txtMerchandiseSubtotal, txtShippingSubtotal, txtShippingDiscountSubtotal, txtMerchandiseDiscountSubtotal, txtTotalPayment;
    private Button btnPlaceOrder;
    private ShippingOption selectedShippingOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        recyclerViewShippingOptions = findViewById(R.id.recyclerViewShippingOptions);
        txtMerchandiseSubtotal = findViewById(R.id.txtMerchandiseSubtotal);
        txtShippingSubtotal = findViewById(R.id.txtShippingSubtotal);
        txtShippingDiscountSubtotal = findViewById(R.id.txtShippingDiscountSubtotal);
        txtMerchandiseDiscountSubtotal = findViewById(R.id.txtMerchandiseDiscountSubtotal);
        txtTotalPayment = findViewById(R.id.txtTotalPayment);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Get the selected cart items from the intent
        String cartJson = getIntent().getStringExtra("cartItems");
        if (cartJson != null && !cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(cartJson, type);
        }

        // Set up the RecyclerView for order items
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, cartItems);
        recyclerViewOrderItems.setAdapter(orderItemAdapter);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Set up the RecyclerView for shipping options
        List<ShippingOption> shippingOptions = new ArrayList<>();
        shippingOptions.add(new ShippingOption(
                "STANDARD DELIVERY",
                "Delivery fee 20K. Estimated delivery time is 2–5 days, excluding Sundays and holidays.",
                20000
        ));
        shippingOptions.add(new ShippingOption(
                "EXPRESS DELIVERY",
                "Delivery fee 45K (only available in HCMC); order before 5pm will be delivered the same day, after 5pm will be delivered the next day.",
                45000
        ));

//        selectedShippingOption = shippingOptions.get(0); // Default to Standard Delivery

        ShippingOptionAdapter shippingOptionAdapter = new ShippingOptionAdapter(this, shippingOptions, option -> {
            selectedShippingOption = option;
            calculateAndUpdateTotals();
        });
        recyclerViewShippingOptions.setAdapter(shippingOptionAdapter);
        recyclerViewShippingOptions.setLayoutManager(new LinearLayoutManager(this));

        // Calculate and update total payment
        calculateAndUpdateTotals();

        btnPlaceOrder.setOnClickListener(v -> {
            Toast.makeText(CheckoutActivity.this, "Order Placed!", Toast.LENGTH_SHORT).show();
        });

        // Hiển thị địa chỉ mặc định khi khởi tạo
        AddressItem defaultAddress = getDefaultAddress();
        if (defaultAddress != null) {
            updateAddressUI(defaultAddress);
        }
    }

    private void calculateAndUpdateTotals() {
        // Merchandise Subtotal
        int merchandiseSubtotal = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                merchandiseSubtotal += item.price * item.quantity;
            }
        }

        // Shipping Subtotal
        int shippingSubtotal = selectedShippingOption != null ? selectedShippingOption.getCost() : 0;

        // Merchandise Discount Subtotal (currently set to 0)
        int merchandiseDiscountSubtotal = 0;

        // Shipping Discount Subtotal (currently set to 0)
        int shippingDiscountSubtotal = 0;

        // Total Payment Calculation
        int totalPayment = merchandiseSubtotal + shippingSubtotal - shippingDiscountSubtotal - merchandiseDiscountSubtotal;

        // Update the UI with calculated values
        txtMerchandiseSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseSubtotal)));
        txtShippingSubtotal.setText(String.format("đ%s", formatCurrency(shippingSubtotal)));
        txtShippingDiscountSubtotal.setText(String.format("đ%s", formatCurrency(shippingDiscountSubtotal)));
        txtMerchandiseDiscountSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseDiscountSubtotal)));
        txtTotalPayment.setText(String.format("đ%s", formatCurrency(totalPayment)));
    }

    private String formatCurrency(int value) {
        return String.format("%,d", value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastSelectedPosition(-1);
    }

    public void open_voucher_activity(View view) {
        Intent intent = new Intent(this, VoucherManagementActivity.class);
        startActivity(intent);
    }

    public void open_address_selection_activity(View view) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        int lastSelected = getLastSelectedPosition();
        if (lastSelected != -1) {
            intent.putExtra("lastSelectedPosition", lastSelected);
        }
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            AddressItem selectedAddress = (AddressItem) data.getSerializableExtra("selectedAddress");
            if (selectedAddress != null) {
                updateAddressUI(selectedAddress);
                int lastSelected = data.getIntExtra("lastSelectedPosition", -1);
                if (lastSelected != -1) {
                    saveLastSelectedPosition(lastSelected);
                }
            }
        }
    }

    private AddressItem getDefaultAddress() {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        String json = prefs.getString("addressList", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<AddressItem>>(){}.getType();
        List<AddressItem> addressList = gson.fromJson(json, type);
        if (addressList != null && !addressList.isEmpty()) {
            for (AddressItem address : addressList) {
                if (address.isDefault()) {
                    return address;
                }
            }
            if (addressList.size() == 1) {
                addressList.get(0).setDefault(true);
                saveAddressList(addressList);
                return addressList.get(0);
            }
        }
        return null;
    }

    private void saveAddressList(List<AddressItem> addressList) {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressList);
        editor.putString("addressList", json);
        editor.commit();
    }

    private void updateAddressUI(AddressItem address) {
        TextView nameTextView = findViewById(R.id.addressNameTextView);
        TextView phoneTextView = findViewById(R.id.addressPhoneTextView);
        TextView addressTextView = findViewById(R.id.addressDetailTextView);
        if (nameTextView != null && phoneTextView != null && addressTextView != null) {
            nameTextView.setText(address.getName());
            phoneTextView.setText(address.getPhone());
            addressTextView.setText(address.getAddress());
        }
    }

    private int getLastSelectedPosition() {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        return prefs.getInt("lastSelectedPosition", -1);
    }

    private void saveLastSelectedPosition(int position) {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("lastSelectedPosition", position);
        editor.apply();
    }
}