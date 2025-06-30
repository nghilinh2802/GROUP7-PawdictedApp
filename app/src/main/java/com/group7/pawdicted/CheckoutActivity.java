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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FirebaseFirestore db;
    private TextView txtMessage;
    private String finalAddressId = null;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        addViews();
        addEvents();

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
        txtMessage = findViewById(R.id.txtMessage);

        db = FirebaseFirestore.getInstance();

        String cartJson = getIntent().getStringExtra("cartItems");
        if (cartJson != null && !cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(cartJson, type);
        } else {
            cartItems = new ArrayList<>();
            Log.e("CheckoutActivity", "No cart items received");
        }

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, cartItems);
        recyclerViewOrderItems.setAdapter(orderItemAdapter);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        List<ShippingOption> shippingOptions = new ArrayList<>();
        shippingOptions.add(new ShippingOption("STANDARD DELIVERY", "Delivery fee 20K...", 20000));
        shippingOptions.add(new ShippingOption("EXPRESS DELIVERY", "Delivery fee 45K...", 45000));
        selectedShippingOption = shippingOptions.get(0);

        ShippingOptionAdapter shippingOptionAdapter = new ShippingOptionAdapter(this, shippingOptions, option -> {
            selectedShippingOption = option;
            calculateAndUpdateTotals();
        });
        recyclerViewShippingOptions.setAdapter(shippingOptionAdapter);
        recyclerViewShippingOptions.setLayoutManager(new LinearLayoutManager(this));

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("Cash on Delivery", R.mipmap.ic_cod));
        paymentMethods.add(new PaymentMethod("QR Code - VNPay", R.mipmap.ic_vnpay));
        paymentMethods.add(new PaymentMethod("ZaloPay", R.mipmap.ic_zalopay));
        paymentMethods.add(new PaymentMethod("MoMo Wallet", R.mipmap.ic_momo));
        selectedPaymentMethod = paymentMethods.get(3);

        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethods, method -> {
            selectedPaymentMethod = method;
        });
        recyclerViewPaymentMethods.setAdapter(paymentMethodAdapter);
        recyclerViewPaymentMethods.setLayoutManager(new LinearLayoutManager(this));

        calculateAndUpdateTotals();

        btnPlaceOrder.setOnClickListener(v -> {
            if (finalAddressId == null) {
                Toast.makeText(this, "Please choose the delivery address!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String customerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";
            String customerNote = txtMessage.getText().toString().trim();
            int shippingFee = selectedShippingOption != null ? selectedShippingOption.getCost() : 0;
            String paymentMethod = selectedPaymentMethod != null ? selectedPaymentMethod.getName() : "Unknown";
            Timestamp orderTime = Timestamp.now();

            Map<String, Object> productMap = new HashMap<>();
            int totalMerchandise = 0;
            int index = 1;

            for (CartItem item : cartItems) {
                if (item.isSelected) {
                    int cost = item.price * item.quantity;
                    totalMerchandise += cost;

                    Map<String, Object> productDetail = new HashMap<>();
                    productDetail.put("product_id", item.productId);
                    productDetail.put("quantity", item.quantity);
                    productDetail.put("total_cost_of_goods", cost);
                    if (item.selectedOption != null && !item.selectedOption.isEmpty()) {
                        productDetail.put("selected_option", item.selectedOption);
                    }
                    productMap.put("product" + index, productDetail);
                    index++;
                }
            }

            int totalValue = totalMerchandise + shippingFee;

            DocumentReference orderItemRef = db.collection("order_items").document();
            String orderItemId = orderItemRef.getId();

            orderItemRef.set(productMap)
                    .addOnSuccessListener(unused -> {
                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("customer_id", customerId);
                        orderData.put("address_id", finalAddressId);
                        orderData.put("customer_note", customerNote);
                        orderData.put("shipping_fee", shippingFee);
                        orderData.put("payment_method", paymentMethod);
                        orderData.put("order_time", orderTime);
                        orderData.put("order_value", totalValue);
                        orderData.put("order_status", "Pending Payment");
                        orderData.put("order_item_id", orderItemId);

                        db.collection("orders").add(orderData)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CheckoutActivity.this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
                    });
        });

        String selectedAddressId = getSelectedAddressId();
        if (selectedAddressId != null) {
            loadSelectedAddressFromFirestore(selectedAddressId);
        } else {
            loadDefaultAddressFromFirestore();
        }
    }

    private void addEvents() {
        imgBack.setOnClickListener(v -> finish());
    }

    private void addViews() {
        imgBack = findViewById(R.id.imgBack);
    }

    private void applyVoucher(Voucher voucher) {
        if (voucher != null) {
            int baseAmount = "merchandise".equals(voucher.getType()) ? calculateMerchandiseSubtotal() : selectedShippingOption != null ? selectedShippingOption.getCost() : 0;
            if (baseAmount >= voucher.getMinOrderValue()) {
                this.appliedVoucher = voucher;
                txtVoucherDetails.setText(voucher.getCode());
                calculateAndUpdateTotals();
            } else {
                Toast.makeText(this, "Minimum order value not met", Toast.LENGTH_SHORT).show();
                this.appliedVoucher = null;
                txtVoucherDetails.setText("Enter Code");
                calculateAndUpdateTotals();
            }
        } else {
            this.appliedVoucher = null;
            txtVoucherDetails.setText("Enter Code");
            calculateAndUpdateTotals();
        }
    }

    private int calculateMerchandiseSubtotal() {
        int merchandiseSubtotal = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                merchandiseSubtotal += item.price * item.quantity;
            }
        }
        return merchandiseSubtotal;
    }

    private void calculateAndUpdateTotals() {
        int merchandiseSubtotal = calculateMerchandiseSubtotal();
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

        int totalPayment = merchandiseSubtotal + shippingSubtotal - merchandiseDiscountSubtotal - shippingDiscountSubtotal;
        int savedAmount = merchandiseDiscountSubtotal + shippingDiscountSubtotal;

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
        String selectedAddressId = getSelectedAddressId();
        if (selectedAddressId != null) {
            intent.putExtra("selectedAddressId", selectedAddressId);
        }
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            AddressItem selectedAddress = (AddressItem) data.getParcelableExtra("selectedAddress");
            String selectedAddressId = data.getStringExtra("selectedAddressId");
            if (selectedAddress != null && selectedAddressId != null) {
                updateAddressUI(selectedAddress);
                saveSelectedAddressId(selectedAddressId);
                finalAddressId = selectedAddressId;
            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Voucher selectedVoucher = (Voucher) data.getSerializableExtra("selectedVoucher");
            applyVoucher(selectedVoucher);
        }
    }

    private void loadSelectedAddressFromFirestore(String addressId) {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("addresses")
                .document(customerId)
                .collection("items")
                .document(addressId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AddressItem address = documentSnapshot.toObject(AddressItem.class);
                        if (address != null) {
                            updateAddressUI(address);
                            finalAddressId = addressId;
                        }
                    } else {
                        loadDefaultAddressFromFirestore();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "Error loading selected address", e);
                    loadDefaultAddressFromFirestore();
                });
    }

    private void loadDefaultAddressFromFirestore() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                        if (defaultAddress != null) {
                            updateAddressUI(defaultAddress);
                            saveSelectedAddressId(defaultAddress.getId());
                            finalAddressId = defaultAddress.getId();
                        }
                    } else {
                        Toast.makeText(this, "No default address found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "Error loading default address", e);
                    Toast.makeText(this, "Error loading default address", Toast.LENGTH_SHORT).show();
                });
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

    private String getSelectedAddressId() {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        return prefs.getString("selectedAddressId", null);
    }

    private void saveSelectedAddressId(String addressId) {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selectedAddressId", addressId);
        editor.apply();
    }
}