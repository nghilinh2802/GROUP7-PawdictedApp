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
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    // THÊM CÁC BIẾN FLASHSALE
    private Map<String, Map<String, Object>> flashsaleProductInfo = new HashMap<>();
    private boolean hasFlashsaleItems = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        Log.d("CheckoutActivity", "=== CHECKOUT ACTIVITY STARTED ===");

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
        Log.d("CheckoutActivity", "Cart JSON received: " + (cartJson != null ? "Yes" : "No"));

        if (cartJson != null && !cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(cartJson, type);
            Log.d("CheckoutActivity", "Cart items loaded: " + cartItems.size());
        } else {
            cartItems = new ArrayList<>();
            Log.e("CheckoutActivity", "No cart items received");
        }

        // Log cart items details
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            Log.d("CheckoutActivity", "Cart item " + i + ": " + item.name +
                    " | ProductID: " + item.productId +
                    " | Quantity: " + item.quantity +
                    " | Price: " + item.price +
                    " | Selected: " + item.isSelected);
        }

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, cartItems);
        recyclerViewOrderItems.setAdapter(orderItemAdapter);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        List<ShippingOption> shippingOptions = new ArrayList<>();
        shippingOptions.add(new ShippingOption("STANDARD DELIVERY", "Delivery fee 20K. Estimated delivery time is 2-5 days, excluding Sundays and holidays.", 20000));
        shippingOptions.add(new ShippingOption("EXPRESS DELIVERY", "Delivery fee 45K. Order after 5pm will be delivered the next day. ", 45000));
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
        selectedPaymentMethod = paymentMethods.get(0);

        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethods, method -> {
            selectedPaymentMethod = method;
        });
        recyclerViewPaymentMethods.setAdapter(paymentMethodAdapter);
        recyclerViewPaymentMethods.setLayoutManager(new LinearLayoutManager(this));

        calculateAndUpdateTotals();

        // THÊM KIỂM TRA FLASHSALE
        checkFlashsaleValidation();

        btnPlaceOrder.setOnClickListener(v -> {
            Log.d("CheckoutActivity", "=== PLACE ORDER CLICKED ===");

            if (finalAddressId == null) {
                Log.w("CheckoutActivity", "No address selected");
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

            // Tính tổng giá trị của các sản phẩm được chọn
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

            // Tính toán giảm giá cho merchandise và shipping
            int[] merchandiseDiscount = new int[1];
            int[] shippingDiscount = new int[1];
            Voucher voucherFromCart = (Voucher) getIntent().getSerializableExtra("selectedVoucher");
            if (voucherFromCart != null) {
                applyVoucher(voucherFromCart);
            }

            int totalValue = totalMerchandise + shippingFee;
            Log.d("CheckoutActivity", "Total merchandise: " + totalMerchandise);
            Log.d("CheckoutActivity", "Total order value: " + totalValue);

            if (appliedVoucher != null) {
                if ("merchandise".equals(appliedVoucher.getType()) && totalMerchandise >= appliedVoucher.getMinOrderValue()) {
                    merchandiseDiscount[0] = appliedVoucher.getDiscountValue(totalMerchandise);
                } else if ("shipping".equals(appliedVoucher.getType()) && shippingFee >= appliedVoucher.getMinOrderValue()) {
                    shippingDiscount[0] = appliedVoucher.getDiscountValue(shippingFee);
                }
            }

            int totalBeforeDiscount = totalMerchandise + shippingFee;
            int totalPayment = totalBeforeDiscount - merchandiseDiscount[0] - shippingDiscount[0];

            // Lưu các sản phẩm vào Firestore
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
                        orderData.put("order_value", totalPayment);
                        orderData.put("total_before_discount", totalBeforeDiscount);
                        orderData.put("order_status", "Pending Payment");
                        orderData.put("order_item_id", orderItemId);

                        // Thêm giảm giá vào dữ liệu đơn hàng nếu có
                        if (merchandiseDiscount[0] > 0) {
                            orderData.put("merchandise_discount", merchandiseDiscount[0]);
                        }
                        if (shippingDiscount[0] > 0) {
                            orderData.put("shipping_discount", shippingDiscount[0]);
                        }

                        if (appliedVoucher != null && appliedVoucher.getId() != null) {
                            orderData.put("voucher_id", appliedVoucher.getId());
                        }

                        // Lưu đơn hàng vào Firestore
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
        Log.d("CheckoutActivity", "Merchandise subtotal calculated: " + merchandiseSubtotal);
        return merchandiseSubtotal;
    }

    private void calculateAndUpdateTotals() {
        Log.d("CheckoutActivity", "=== CALCULATING TOTALS ===");

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

        Log.d("CheckoutActivity", "Merchandise: " + merchandiseSubtotal);
        Log.d("CheckoutActivity", "Shipping: " + shippingSubtotal);
        Log.d("CheckoutActivity", "Merchandise discount: " + merchandiseDiscountSubtotal);
        Log.d("CheckoutActivity", "Shipping discount: " + shippingDiscountSubtotal);
        Log.d("CheckoutActivity", "Total payment: " + totalPayment);
        Log.d("CheckoutActivity", "Saved amount: " + savedAmount);

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

    // THÊM METHOD KIỂM TRA FLASHSALE
    private void checkFlashsaleValidation() {
        Log.d("CheckoutActivity", "=== CHECKING FLASHSALE VALIDATION ===");

        List<String> productIds = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                productIds.add(item.productId);
                Log.d("CheckoutActivity", "Selected product ID: " + item.productId);
            }
        }

        Log.d("CheckoutActivity", "Total selected products: " + productIds.size());

        if (productIds.isEmpty()) {
            Log.d("CheckoutActivity", "No selected products found");
            return;
        }

        Log.d("CheckoutActivity", "Querying flashsales collection...");
        db.collection("flashsales").get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("CheckoutActivity", "Flashsales query successful - found " + querySnapshot.size() + " documents");

                    flashsaleProductInfo.clear();
                    for (QueryDocumentSnapshot flashsaleDoc : querySnapshot) {
                        String flashsaleId = flashsaleDoc.getString("flashSale_id");
                        Long startTime = flashsaleDoc.getLong("startTime");
                        Long endTime = flashsaleDoc.getLong("endTime");
                        List<Map<String, Object>> products = (List<Map<String, Object>>) flashsaleDoc.get("products");

                        Log.d("CheckoutActivity", "Checking flashsale: " + flashsaleId);
                        Log.d("CheckoutActivity", "Start time: " + startTime + ", End time: " + endTime);

                        long currentTime = System.currentTimeMillis();
                        Log.d("CheckoutActivity", "Current time: " + currentTime);

                        boolean isActive = startTime != null && endTime != null && currentTime >= startTime && currentTime <= endTime;
                        Log.d("CheckoutActivity", "Flashsale active: " + isActive);

                        if (isActive) {
                            if (products != null) {
                                Log.d("CheckoutActivity", "Found " + products.size() + " products in flashsale");
                                for (Map<String, Object> productMap : products) {
                                    String productId = (String) productMap.get("product_id");
                                    Log.d("CheckoutActivity", "Checking product: " + productId);

                                    if (productIds.contains(productId)) {
                                        Log.d("CheckoutActivity", "✅ Product " + productId + " is in cart and flashsale");
                                        Map<String, Object> info = new HashMap<>();
                                        info.put("flashsaleId", flashsaleId);
                                        info.put("discountRate", productMap.get("discountRate"));
                                        info.put("unitSold", productMap.get("unitSold"));
                                        info.put("maxQuantity", productMap.get("maxQuantity"));
                                        flashsaleProductInfo.put(productId, info);

                                        Log.d("CheckoutActivity", "Stored flashsale info: " + info.toString());
                                    }
                                }
                            } else {
                                Log.d("CheckoutActivity", "No products found in flashsale");
                            }
                        }
                    }

                    Log.d("CheckoutActivity", "Total flashsale products stored: " + flashsaleProductInfo.size());
                    validateFlashsaleQuantities();
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "❌ Error checking flashsales: " + e.getMessage());
                });
    }

    private void validateFlashsaleQuantities() {
        Log.d("CheckoutActivity", "=== STARTING FLASHSALE VALIDATION ===");
        Log.d("CheckoutActivity", "Total cart items: " + cartItems.size());
        Log.d("CheckoutActivity", "Flashsale products found: " + flashsaleProductInfo.size());

        boolean hasChanges = false;
        List<CartItem> itemsToAdd = new ArrayList<>();

        for (CartItem item : cartItems) {
            Log.d("CheckoutActivity", "--- Checking item: " + item.name + " ---");
            Log.d("CheckoutActivity", "Item selected: " + item.isSelected);
            Log.d("CheckoutActivity", "Item quantity: " + item.quantity);
            Log.d("CheckoutActivity", "Item productId: " + item.productId);

            if (!item.isSelected) {
                Log.d("CheckoutActivity", "Item not selected, skipping");
                continue;
            }

            Map<String, Object> flashsaleInfo = flashsaleProductInfo.get(item.productId);
            Log.d("CheckoutActivity", "Flashsale info found: " + (flashsaleInfo != null));

            if (flashsaleInfo != null) {
                Log.d("CheckoutActivity", "Flashsale info: " + flashsaleInfo.toString());

                int unitSold = ((Long) flashsaleInfo.get("unitSold")).intValue();
                int maxQuantity = ((Long) flashsaleInfo.get("maxQuantity")).intValue();
                int discountRate = ((Long) flashsaleInfo.get("discountRate")).intValue();

                Log.d("CheckoutActivity", "Current unitSold: " + unitSold);
                Log.d("CheckoutActivity", "Max quantity: " + maxQuantity);
                Log.d("CheckoutActivity", "Discount rate: " + discountRate + "%");
                Log.d("CheckoutActivity", "Want to buy: " + item.quantity);

                // KIỂM TRA TỔNG SỐ LƯỢNG SAU KHI MUA
                int totalAfterPurchase = unitSold + item.quantity;
                Log.d("CheckoutActivity", "Total after purchase: " + totalAfterPurchase);
                Log.d("CheckoutActivity", "Will exceed limit: " + (totalAfterPurchase > maxQuantity));

                if (unitSold >= maxQuantity) {
                    Log.d("CheckoutActivity", "❌ Flashsale completely sold out");
                    double originalPrice = item.price / (1 - discountRate / 100.0);
                    Log.d("CheckoutActivity", "Converting to original price: " + item.price + " -> " + originalPrice);
                    item.price = (int) originalPrice;
                    hasChanges = true;
                    Toast.makeText(this, "Flashsale cho " + item.name + " đã hết! Chuyển về giá gốc.", Toast.LENGTH_LONG).show();

                } else if (totalAfterPurchase > maxQuantity) {
                    Log.d("CheckoutActivity", "⚠️ Will exceed limit - splitting order");
                    int availableFlashsaleQuantity = maxQuantity - unitSold;
                    int regularQuantity = item.quantity - availableFlashsaleQuantity;

                    Log.d("CheckoutActivity", "Available flashsale quantity: " + availableFlashsaleQuantity);
                    Log.d("CheckoutActivity", "Regular price quantity: " + regularQuantity);

                    if (availableFlashsaleQuantity <= 0) {
                        Log.d("CheckoutActivity", "❌ No flashsale slots available");
                        double originalPrice = item.price / (1 - discountRate / 100.0);
                        item.price = (int) originalPrice;
                        Toast.makeText(this, "Flashsale cho " + item.name + " đã hết! Chuyển về giá gốc.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("CheckoutActivity", "✂️ Splitting into flashsale + regular price");
                        double originalPrice = item.price / (1 - discountRate / 100.0);

                        // Cập nhật item hiện tại thành phần flashsale
                        Log.d("CheckoutActivity", "Updating current item quantity: " + item.quantity + " -> " + availableFlashsaleQuantity);
                        item.quantity = availableFlashsaleQuantity;

                        // Tạo item mới cho phần giá gốc
                        CartItem regularPriceItem = new CartItem(
                                item.productId, item.name + " (Giá gốc)", (int) originalPrice,
                                item.imageUrl, item.options, item.selectedOption
                        );
                        regularPriceItem.quantity = regularQuantity;
                        regularPriceItem.isSelected = item.isSelected;
                        itemsToAdd.add(regularPriceItem);

                        Log.d("CheckoutActivity", "Created regular price item: " + regularPriceItem.name +
                                " x" + regularPriceItem.quantity + " at " + regularPriceItem.price);

                        Toast.makeText(this, "Chỉ còn " + availableFlashsaleQuantity + " sản phẩm " + item.name +
                                " với giá flashsale. " + regularQuantity + " sản phẩm còn lại tính giá gốc.", Toast.LENGTH_LONG).show();
                    }
                    hasChanges = true;
                } else {
                    Log.d("CheckoutActivity", "✅ Within flashsale limit - keeping flashsale price");
                }
            } else {
                Log.d("CheckoutActivity", "No flashsale info found for this product");
            }

            Log.d("CheckoutActivity", "--- End checking item: " + item.name + " ---");
        }

        Log.d("CheckoutActivity", "Items to add: " + itemsToAdd.size());
        cartItems.addAll(itemsToAdd);

        if (hasChanges) {
            Log.d("CheckoutActivity", "Changes detected - updating UI");
            // Cập nhật adapter
            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, cartItems);
            recyclerViewOrderItems.setAdapter(orderItemAdapter);
            calculateAndUpdateTotals();
        } else {
            Log.d("CheckoutActivity", "No changes needed");
        }

        Log.d("CheckoutActivity", "=== FLASHSALE VALIDATION COMPLETED ===");
    }

    private void updateFlashsaleUnitSold() {
        Log.d("CheckoutActivity", "=== UPDATING FLASHSALE UNIT SOLD ===");

        for (CartItem item : cartItems) {
            if (!item.isSelected) continue;

            Map<String, Object> flashsaleInfo = flashsaleProductInfo.get(item.productId);
            if (flashsaleInfo != null) {
                String flashsaleId = (String) flashsaleInfo.get("flashsaleId");

                Log.d("CheckoutActivity", "Updating unitSold for product: " + item.productId +
                        " in flashsale: " + flashsaleId +
                        " | Quantity purchased: " + item.quantity);

                db.collection("flashsales")
                        .whereEqualTo("flashSale_id", flashsaleId)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentReference flashsaleDoc = querySnapshot.getDocuments().get(0).getReference();

                                flashsaleDoc.get().addOnSuccessListener(documentSnapshot -> {
                                    List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");

                                    if (products != null) {
                                        for (Map<String, Object> productMap : products) {
                                            if (item.productId.equals(productMap.get("product_id"))) {
                                                int currentUnitSold = ((Long) productMap.get("unitSold")).intValue();
                                                int newUnitSold = currentUnitSold + item.quantity;
                                                productMap.put("unitSold", newUnitSold);

                                                Log.d("CheckoutActivity", "✅ Updated unitSold for " + item.productId +
                                                        " from " + currentUnitSold + " to " + newUnitSold);
                                                break;
                                            }
                                        }

                                        // Cập nhật lại document
                                        flashsaleDoc.update("products", products)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("CheckoutActivity", "✅ Flashsale document updated successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("CheckoutActivity", "❌ Error updating flashsale document: " + e.getMessage());
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("CheckoutActivity", "❌ Error finding flashsale document: " + e.getMessage());
                        });
            }
        }
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
