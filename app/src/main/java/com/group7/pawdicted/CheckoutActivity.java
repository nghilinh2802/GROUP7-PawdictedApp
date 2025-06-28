package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.group7.pawdicted.mobile.adapters.PaymentMethodAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.ShippingOption;
import com.group7.pawdicted.mobile.models.PaymentMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private RecyclerView recyclerViewShippingOptions;
    private RecyclerView recyclerViewPaymentMethods;
    private List<CartItem> cartItems;
    private TextView txtMerchandiseSubtotal, txtShippingSubtotal, txtShippingDiscountSubtotal, txtMerchandiseDiscountSubtotal, txtTotalPayment;
    private TextView txtTotalFooter, txtSavedFooter;
    private Button btnPlaceOrder;
    private ShippingOption selectedShippingOption;
    private PaymentMethod selectedPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        recyclerViewShippingOptions = findViewById(R.id.recyclerViewShippingOptions);
        recyclerViewPaymentMethods = findViewById(R.id.recyclerViewPaymentMethods);
        txtMerchandiseSubtotal = findViewById(R.id.txtMerchandiseSubtotal);
        txtShippingSubtotal = findViewById(R.id.txtShippingSubtotal);
        txtShippingDiscountSubtotal = findViewById(R.id.txtShippingDiscountSubtotal);
        txtMerchandiseDiscountSubtotal = findViewById(R.id.txtMerchandiseDiscountSubtotal);
        txtTotalFooter = findViewById(R.id.txtTotalFooter);
        txtSavedFooter = findViewById(R.id.txtSavedFooter);
        txtTotalPayment = findViewById(R.id.txtTotalPayment);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Get the selected cart items from the intent
        String cartJson = getIntent().getStringExtra("cartItems");
        if (cartJson != null && !cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(cartJson, type);
        } else {
            cartItems = new ArrayList<>();
            Log.e("CheckoutActivity", "No cart items received");
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
                "Delivery fee 45K (only available in HCMC); order before 5pm will be delivered the same day.",
                45000
        ));
        Log.d("CheckoutActivity", "Shipping options size: " + shippingOptions.size());

        selectedShippingOption = shippingOptions.get(0); // Default to Standard Delivery

        ShippingOptionAdapter shippingOptionAdapter = new ShippingOptionAdapter(this, shippingOptions, option -> {
            selectedShippingOption = option;
            calculateAndUpdateTotals();
        });
        recyclerViewShippingOptions.setAdapter(shippingOptionAdapter);
        recyclerViewShippingOptions.setLayoutManager(new LinearLayoutManager(this));

        // Set up the RecyclerView for payment methods
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("Cash on Delivery", R.mipmap.ic_cod));
        paymentMethods.add(new PaymentMethod("QR Code - VNPay", R.mipmap.ic_vnpay));
        paymentMethods.add(new PaymentMethod("ZaloPay", R.mipmap.ic_zalopay));
        paymentMethods.add(new PaymentMethod("MoMo Wallet", R.mipmap.ic_momo));
        Log.d("CheckoutActivity", "Payment methods size: " + paymentMethods.size());

        selectedPaymentMethod = paymentMethods.get(3); // Default to MoMo Wallet

        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethods, method -> {
            selectedPaymentMethod = method;
            // Optionally update UI or logic based on selected payment method
            Log.d("CheckoutActivity", "Selected payment method: " + method.getName());
        });
        recyclerViewPaymentMethods.setAdapter(paymentMethodAdapter);
        recyclerViewPaymentMethods.setLayoutManager(new LinearLayoutManager(this));

        // Calculate and update total payment
        calculateAndUpdateTotals();

        btnPlaceOrder.setOnClickListener(v -> {
            Toast.makeText(CheckoutActivity.this, "Order Placed with " + selectedPaymentMethod.getName() + "!", Toast.LENGTH_SHORT).show();
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

        // Merchandise Discount Subtotal (currently set to 0, update as needed)
        int merchandiseDiscountSubtotal = 0;

        // Shipping Discount Subtotal (currently set to 0, update as needed)
        int shippingDiscountSubtotal = 0;

        // Total Payment Calculation
        int totalPayment = merchandiseSubtotal + shippingSubtotal - shippingDiscountSubtotal - merchandiseDiscountSubtotal;

        // Saved Amount Calculation
        int savedAmount = merchandiseDiscountSubtotal + shippingDiscountSubtotal;

        // Update the UI with calculated values
        txtMerchandiseSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseSubtotal)));
        txtShippingSubtotal.setText(String.format("đ%s", formatCurrency(shippingSubtotal)));
        txtShippingDiscountSubtotal.setText(String.format("đ%s", formatCurrency(shippingDiscountSubtotal)));
        txtMerchandiseDiscountSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseDiscountSubtotal)));
        txtTotalPayment.setText(String.format("đ%s", formatCurrency(totalPayment)));

        // Update footer
        txtTotalFooter.setText(String.format("Total đ%s", formatCurrency(totalPayment)));
        txtSavedFooter.setText(String.format("Saved đ%s", formatCurrency(savedAmount)));
        txtSavedFooter.setVisibility(savedAmount > 0 ? View.VISIBLE : View.GONE);
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