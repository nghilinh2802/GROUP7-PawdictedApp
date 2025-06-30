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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.adapters.OrderItemAdapter;
import com.group7.pawdicted.mobile.adapters.ShippingOptionAdapter;
import com.group7.pawdicted.mobile.adapters.PaymentMethodAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.ShippingOption;
import com.group7.pawdicted.mobile.models.PaymentMethod;
import com.group7.pawdicted.mobile.models.Voucher;

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
    private AddressItem currentAddress;
    private Voucher appliedVoucher;
    private TextView txtVoucherDetails;

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
        txtVoucherDetails = findViewById(R.id.txtVoucherDetails);

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
        selectedPaymentMethod = paymentMethods.get(3); // Default to MoMo Wallet

        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethods, method -> {
            selectedPaymentMethod = method;
            Log.d("CheckoutActivity", "Selected payment method: " + method.getName());
        });
        recyclerViewPaymentMethods.setAdapter(paymentMethodAdapter);
        recyclerViewPaymentMethods.setLayoutManager(new LinearLayoutManager(this));

        // Calculate and update total payment
        calculateAndUpdateTotals();

        btnPlaceOrder.setOnClickListener(v -> {
            Toast.makeText(CheckoutActivity.this, "Order Placed with " + selectedPaymentMethod.getName() + "!", Toast.LENGTH_SHORT).show();
        });

        // Always load the default address on create
        loadDefaultAddressFromFirestore();
    }


    private void applyVoucher(Voucher voucher) {
        this.appliedVoucher = voucher;

        txtVoucherDetails.setText(voucher.getCode()); // Hiển thị voucher đã chọn

        calculateAndUpdateTotals(); // Cập nhật lại tổng tiền
    }

    private void calculateAndUpdateTotals() {
        int merchandiseSubtotal = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                merchandiseSubtotal += item.price * item.quantity;
            }
        }

        int shippingSubtotal = selectedShippingOption != null ? selectedShippingOption.getCost() : 0;
        int merchandiseDiscountSubtotal = 0;
        int shippingDiscountSubtotal = 0;

        if (appliedVoucher != null) {
            if ("merchandise".equals(appliedVoucher.getType())) {
                if (merchandiseSubtotal >= appliedVoucher.getMinOrderValue()) {
                    merchandiseDiscountSubtotal = appliedVoucher.getDiscountValue(merchandiseSubtotal);
                }
            } else if ("shipping".equals(appliedVoucher.getType())) {
                if (shippingSubtotal >= appliedVoucher.getMinOrderValue()) {
                    shippingDiscountSubtotal = appliedVoucher.getDiscountValue(shippingSubtotal);
                }
            }
        }

        int totalPayment = merchandiseSubtotal + shippingSubtotal
                - merchandiseDiscountSubtotal - shippingDiscountSubtotal;
        int savedAmount = merchandiseDiscountSubtotal + shippingDiscountSubtotal;

        // Hiển thị lên UI
        txtMerchandiseSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseSubtotal)));
        txtShippingSubtotal.setText(String.format("đ%s", formatCurrency(shippingSubtotal)));
        txtShippingDiscountSubtotal.setText(String.format("đ%s", formatCurrency(shippingDiscountSubtotal)));
        txtMerchandiseDiscountSubtotal.setText(String.format("đ%s", formatCurrency(merchandiseDiscountSubtotal)));
        txtTotalPayment.setText(String.format("đ%s", formatCurrency(totalPayment)));

        txtTotalFooter.setText(String.format("Total đ%s", formatCurrency(totalPayment)));
        txtSavedFooter.setText(String.format("Saved đ%s", formatCurrency(savedAmount)));
        txtSavedFooter.setVisibility(savedAmount > 0 ? View.VISIBLE : View.GONE);
    }


    private String formatCurrency(int value) {
        return String.format("%,d", value);
    }

    public void open_voucher_activity(View view) {
        Intent intent = new Intent(this, VoucherManagementActivity.class);
        startActivityForResult(intent, 100);
    }

    public void open_address_selection_activity(View view) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        if (currentAddress != null) {
            intent.putExtra("selectedAddressId", currentAddress.getId());
        }
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            AddressItem selectedAddress = data.getParcelableExtra("selectedAddress");
            if (selectedAddress != null) {
                currentAddress = selectedAddress; // Cập nhật currentAddress
                updateAddressUI(selectedAddress);
            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Voucher selectedVoucher = (Voucher) data.getSerializableExtra("selectedVoucher");
            if (selectedVoucher != null) {
                applyVoucher(selectedVoucher);
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            AddressItem selectedAddress = data.getParcelableExtra("selectedAddress");
            if (selectedAddress != null) {
                currentAddress = selectedAddress;
                updateAddressUI(selectedAddress);
            }
        }
    }

    private void loadDefaultAddressFromFirestore() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (customerId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("addresses")
                .document(customerId)
                .collection("items")
                .whereEqualTo("isDefault", true)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        AddressItem defaultAddress = querySnapshot.getDocuments().get(0).toObject(AddressItem.class);
                        defaultAddress.setId(querySnapshot.getDocuments().get(0).getId()); // Đảm bảo set ID
                        if (defaultAddress != null) {
                            currentAddress = defaultAddress; // Lưu vào currentAddress
                            updateAddressUI(defaultAddress);
                        }
                    } else {
                        FirebaseFirestore.getInstance()
                                .collection("addresses")
                                .document(customerId)
                                .collection("items")
                                .orderBy("time")
                                .limit(1)
                                .get()
                                .addOnSuccessListener(fallbackSnapshot -> {
                                    if (!fallbackSnapshot.isEmpty()) {
                                        AddressItem firstAddress = fallbackSnapshot.getDocuments().get(0).toObject(AddressItem.class);
                                        firstAddress.setId(fallbackSnapshot.getDocuments().get(0).getId()); // Đảm bảo set ID
                                        if (firstAddress != null) {
                                            currentAddress = firstAddress; // Lưu vào currentAddress
                                            updateAddressUI(firstAddress);
                                        }
                                    } else {
                                        Toast.makeText(this, "No addresses found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CheckoutActivity", "Error loading fallback address", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "Error loading default address", e);
                });
    }

    private void updateDefaultAddressInFirestore(AddressItem address) {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("addresses")
                .document(customerId)
                .collection("items")
                .document(address.getId())
                .set(address)
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating default address", e));
    }

    private void updateAddressUI(AddressItem address) {
        TextView nameTextView = findViewById(R.id.addressNameTextView);
        TextView phoneTextView = findViewById(R.id.addressPhoneTextView);
        TextView addressTextView = findViewById(R.id.addressDetailTextView);

        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        prefs.edit().putString("selectedAddressId", address.getId()).apply();

        if (nameTextView != null && phoneTextView != null && addressTextView != null) {
            nameTextView.setText(address.getName());
            phoneTextView.setText(address.getPhone());
            addressTextView.setText(address.getAddress());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear any temporary address selection (optional, ensures reset on next entry)
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("selectedAddressId");
        editor.apply();
    }
}