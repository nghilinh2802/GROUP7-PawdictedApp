package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;


public class PurchaseOrderActivity extends AppCompatActivity {

    Button btn_confirm, btn_to_pickup, btn_received, btn_completed, btn_cancelled, btn_returnrefund;
    ImageView btn_back, btn_search;
    LinearLayout emptyView;

    ScrollView orderScroll;
    LinearLayout orderListContainer;

    FirebaseFirestore db;
    CollectionReference ordersRef, orderItemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        ordersRef = db.collection("orders");
        orderItemsRef = db.collection("order_items");

        String status = getIntent().getStringExtra("order_status");
        if (status == null) status = "Pending Payment";

        loadOrdersByStatus(status);
    }

    private void addViews() {
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_to_pickup = findViewById(R.id.btn_to_pickup);
        btn_received = findViewById(R.id.btn_received);
        btn_completed = findViewById(R.id.btn_completed);
        btn_cancelled = findViewById(R.id.btn_cancelled);
        btn_returnrefund = findViewById(R.id.btn_returnrefund);
        btn_back = findViewById(R.id.btn_back);
//        btn_search = findViewById(R.id.btn_search);

        emptyView = findViewById(R.id.empty_view);
        orderScroll = findViewById(R.id.order_scroll);
        orderListContainer = findViewById(R.id.order_list_container);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btn_confirm.setOnClickListener(v -> loadOrdersByStatus("Pending Payment"));
        btn_to_pickup.setOnClickListener(v -> loadOrdersByStatus("Shipped"));
        btn_received.setOnClickListener(v -> loadOrdersByStatus("Delivered"));
        btn_completed.setOnClickListener(v -> loadOrdersByStatus("Completed"));
        btn_cancelled.setOnClickListener(v -> loadOrdersByStatus("Cancelled"));
        btn_returnrefund.setOnClickListener(v -> loadOrdersByStatus("Return/Refund"));
    }

    private void loadOrdersByStatus(String status) {
        highlightSelectedStatus(status);
        orderListContainer.removeAllViews();  // Clear existing views

        ordersRef.whereEqualTo("order_status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            boolean foundAny = false;
                            emptyView.setVisibility(View.GONE);  // Hide empty view when orders are present
                            orderScroll.setVisibility(View.VISIBLE);  // Show ScrollView

                            for (QueryDocumentSnapshot orderSnap : snapshot) {
                                String orderId = orderSnap.getId();
                                String orderTime = formatOrderTime(orderSnap);  // Lấy thời gian đơn hàng và định dạng

                                Integer orderValue = orderSnap.getLong("order_value").intValue();
                                String itemGroupId = orderSnap.getString("order_item_id");

                                if (orderTime == null) orderTime = "Unknown Time";
                                if (orderValue == null) orderValue = 0;

                                // Load the order items (products) based on the order_item_id
                                loadOrderItems(orderId, itemGroupId, orderTime, orderValue, status);

                                foundAny = true;
                            }

                            if (!foundAny) {
                                Log.w("DEBUG_ORDER", "⚠️ No orders found for status = [" + status + "]");
                                emptyView.setVisibility(View.VISIBLE);  // Show empty view when no orders are found
                                orderScroll.setVisibility(View.GONE);  // Hide ScrollView
                            }
                        } else {
                            Log.w("DEBUG_ORDER", "⚠️ No orders found for status = [" + status + "]");
                            emptyView.setVisibility(View.VISIBLE);  // Show empty view when no orders are found
                            orderScroll.setVisibility(View.GONE);  // Hide ScrollView
                        }
                    } else {
                        Log.e("DEBUG_ORDER", "Error getting orders: " + task.getException());
                        emptyView.setVisibility(View.VISIBLE);  // Show empty view when an error occurs
                        orderScroll.setVisibility(View.GONE);  // Hide ScrollView on error
                    }
                });
    }

    // Thêm phương thức formatOrderTime để trích xuất và định dạng thời gian đơn hàng
    private String formatOrderTime(QueryDocumentSnapshot orderSnap) {
        String orderTime = "";
        if (orderSnap.contains("order_time")) {
            Object orderTimeObj = orderSnap.get("order_time");
            if (orderTimeObj instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) orderTimeObj;
                orderTime = formatTimestamp(timestamp);  // Format Timestamp
            } else {
                orderTime = orderSnap.getString("order_time");  // If it's a String, take it directly
            }
        }
        return orderTime;
    }

    // Thêm phương thức formatTimestamp để định dạng Timestamp thành String
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(timestamp.toDate());  // Convert Timestamp to Date and format it
        }
        return "Unknown Time";  // Return default value if Timestamp is null
    }

    private void loadOrderItems(String orderId, String itemGroupId, String orderTime, int totalPrice, String status) {
        Log.d("DEBUG_ORDER", "Truy vấn với order_item_id: " + itemGroupId);

        if (status.equalsIgnoreCase("Return/Refund")) {
            db.collection("orders").document(orderId).get().addOnSuccessListener(orderDoc -> {
                if (orderDoc.exists()) {
                    Integer returnAmount = orderDoc.getLong("return_amount") != null
                            ? orderDoc.getLong("return_amount").intValue()
                            : 0;

                    List<Map<String, String>> productReturnList = (List<Map<String, String>>) orderDoc.get("product_return");

                    View orderView = createOrderView(orderId, orderTime, 0, status, null, null, returnAmount, productReturnList);
                    orderListContainer.addView(orderView);
                } else {
                    Log.w("DEBUG_ORDER", "⚠️ Không tìm thấy đơn hàng với ID: " + orderId);
                }
            });
        } else {
            // Trường hợp đơn hàng bình thường
            orderItemsRef.document(itemGroupId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot productSnap = task.getResult();
                            if (productSnap.exists()) {
                                LinearLayout productList = new LinearLayout(PurchaseOrderActivity.this);
                                productList.setOrientation(LinearLayout.VERTICAL);

                                Map<String, Object> data = productSnap.getData();
                                if (data != null) {
                                    for (String productKey : data.keySet()) {
                                        if (productKey.startsWith("product")) {
                                            String productId = productSnap.getString(productKey + ".product_id");
                                            Long quantityLong = productSnap.getLong(productKey + ".quantity");
                                            int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                                            db.collection("products")
                                                    .document(productId)
                                                    .get()
                                                    .addOnCompleteListener(productTask -> {
                                                        if (productTask.isSuccessful()) {
                                                            DocumentSnapshot productDoc = productTask.getResult();
                                                            if (productDoc.exists()) {
                                                                String productName = productDoc.getString("product_name");

                                                                LinearLayout row = new LinearLayout(PurchaseOrderActivity.this);
                                                                row.setOrientation(LinearLayout.HORIZONTAL);
                                                                row.setLayoutParams(new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT));

                                                                TextView tvQty = new TextView(PurchaseOrderActivity.this);
                                                                tvQty.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
                                                                tvQty.setText(quantity + "x");
                                                                tvQty.setTextSize(14f);
                                                                tvQty.setTextColor(Color.BLACK);

                                                                TextView tvName = new TextView(PurchaseOrderActivity.this);
                                                                tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                                                                tvName.setText("Product: " + productName);
                                                                tvName.setTextSize(14f);
                                                                tvName.setTextColor(Color.BLACK);

                                                                row.addView(tvQty);
                                                                row.addView(tvName);
                                                                productList.addView(row);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }

                                View orderView = createOrderView(orderId, orderTime, totalPrice, status, itemGroupId, productList, null, null);
                                orderListContainer.addView(orderView);
                            } else {
                                Log.w("DEBUG_ORDER", "⚠️ No order items found for itemGroupId: " + itemGroupId);
                            }
                        }
                    });
        }
    }

    private View createOrderView(String orderId, String orderTime, int totalPrice, String status,
                                 String itemGroupId, LinearLayout productList,
                                 @Nullable Integer returnAmount, @Nullable List<Map<String, String>> productReturnList) {
        // Inflate the layout
        View view = getLayoutInflater().inflate(R.layout.order_item, orderListContainer, false);

        // View bindings
        TextView tvTime = view.findViewById(R.id.tv_order_time);
        TextView tvTotal = view.findViewById(R.id.tv_total_price);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        LinearLayout productListContainer = view.findViewById(R.id.product_list);

        // Set values
        tvTime.setText(orderTime);
        tvStatus.setText(getStatusLabel(status));

        if (status.equalsIgnoreCase("Return/Refund")) {
            // Sử dụng return_amount và đổi text thành Return Amount
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedAmount = formatter.format(returnAmount != null ? returnAmount : 0);
            tvTotal.setText("Return Amount: " + formattedAmount + " ₫");

            // Chỉ hiển thị product_name từ productReturnList
            productListContainer.removeAllViews();
            if (productReturnList != null) {
                for (Map<String, String> product : productReturnList) {
                    String productName = product.get("product_name");

                    TextView tv = new TextView(this);
                    tv.setText(" " + productName);
                    tv.setTextSize(14);
                    tv.setTextColor(getResources().getColor(android.R.color.black));
                    tv.setPadding(0, 4, 0, 4);
                    productListContainer.addView(tv);
                }
            }

        } else {
            // Trường hợp còn lại xử lý như cũ
            tvTotal.setText("Total: " + formatCurrency(totalPrice) + " ₫");

            productListContainer.removeAllViews();
            productListContainer.addView(productList);
        }

        // Click → Mở chi tiết
        view.setOnClickListener(v -> {
            if (orderId == null || orderId.isEmpty()) {
                Log.e("ERROR", "Order ID is null or empty in PurchaseOrderActivity");
                Toast.makeText(PurchaseOrderActivity.this, "Order ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("DEBUG", "📤 Gửi sang OrderDetailActivity với orderId = " + orderId);

            Intent intent = status.equalsIgnoreCase("Return/Refund")
                    ? new Intent(PurchaseOrderActivity.this, RefundReturnDetailActivity.class)
                    : new Intent(PurchaseOrderActivity.this, OrderDetailActivity.class);

            intent.putExtra("order_id", orderId);
            intent.putExtra("status_filter", status);
            startActivity(intent);
        });

        return view;
    }

    private String getTabNameFromStatus(String status) {
        switch (status) {
            case "Pending Payment": return "To Confirm";
            case "Shipped": return "To Pickup";
            case "Delivered": return "To Ship";
            case "Completed": return "Completed";
            default: return "";
        }
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    private String getStatusLabel(String status) {
        if (status.equalsIgnoreCase("Pending Payment")) return "To Pay";
        if (status.equalsIgnoreCase("Shipped")) return "To Ship";
        if (status.equalsIgnoreCase("Out for Delivery")) return "To Receive";
        if (status.equalsIgnoreCase("Refund Credited") || status.equalsIgnoreCase("Refund Requested") || status.equalsIgnoreCase("Return Approved")) return "Return/Refund";
        return status;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void highlightSelectedStatus(String selectedStatus) {
        MaterialButton btnToPickup = findViewById(R.id.btn_to_pickup);
        MaterialButton btnReceived = findViewById(R.id.btn_received);
        MaterialButton btnCompleted = findViewById(R.id.btn_completed);
        MaterialButton btnCancelled = findViewById(R.id.btn_cancelled);
        MaterialButton btnReturnRefund = findViewById(R.id.btn_returnrefund);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        resetButtonStyle(btnConfirm);
        resetButtonStyle(btnToPickup);
        resetButtonStyle(btnReceived);
        resetButtonStyle(btnCompleted);
        resetButtonStyle(btnCancelled);
        resetButtonStyle(btnReturnRefund);

        switch (selectedStatus) {
            case "Pending Payment": highlightButton(btnConfirm); break;
            case "Shipped": highlightButton(btnToPickup); break;
            case "Delivered": highlightButton(btnReceived); break;
            case "Completed": highlightButton(btnCompleted); break;
            case "Cancelled": highlightButton(btnCancelled); break;
            case "Return/Refund": highlightButton(btnReturnRefund); break;
        }
    }

    private void resetButtonStyle(View button) {
        if (button instanceof MaterialButton) {
            MaterialButton btn = (MaterialButton) button;
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
            btn.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9C162C")));
            btn.setStrokeWidth(1);
            btn.setTextColor(Color.parseColor("#9C162C"));
        } else if (button instanceof Button) {
            Button btn = (Button) button;
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
            btn.setTextColor(Color.parseColor("#9C162C"));
        }
    }

    private void highlightButton(View button) {
        if (button instanceof MaterialButton) {
            MaterialButton btn = (MaterialButton) button;
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9C162C")));
            btn.setStrokeWidth(0);
            btn.setTextColor(Color.WHITE);
        } else if (button instanceof Button) {
            Button btn = (Button) button;
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9C162C")));
            btn.setTextColor(Color.WHITE);
        }
    }
}
