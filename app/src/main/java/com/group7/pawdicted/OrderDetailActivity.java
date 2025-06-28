package com.group7.pawdicted;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.view.LayoutInflater;
import android.widget.Toast;

public class OrderDetailActivity extends AppCompatActivity {

    ImageView btn_back;
    FirebaseFirestore db;
    Button btn_cancel, btn_contact, btn_evaluate, btn_returnrefund;
    private String orderId;
    private String orderItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận order_id và status_filter từ Intent
        orderId = getIntent().getStringExtra("order_id");
        Log.d("DEBUG", "📥  Nhận được orderId trong OrderDetailActivity: " + orderId);
        if (orderId == null || orderId.isEmpty()) {
            Log.e("ERROR", "❌ Order ID is null or empty! Cannot proceed with cancellation.");
            Toast.makeText(this, "Order ID is missing!", Toast.LENGTH_SHORT).show();
            finish();  // Kết thúc activity nếu không có orderId
            return;  // Dừng lại để không tiếp tục xử lý
        }

        // Lấy thông tin đơn hàng từ Firestore để lấy order_item_id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Lấy order_item_id từ Firestore
                            orderItemId = document.getString("order_item_id");
                            Log.d("DEBUG", "📥 Lấy được order_item_id: " + orderItemId);

                            // Nếu order_item_id hợp lệ, chuyển sang EvaluateActivity
                            if (orderItemId != null && !orderItemId.isEmpty()) {
                                // Khi nhấn nút Evaluate
                                Button btnEvaluate = findViewById(R.id.btn_evaluate);
                                btnEvaluate.setOnClickListener(v -> {
                                    Intent intent = new Intent(OrderDetailActivity.this, EvaluateActivity.class);
                                    intent.putExtra("order_item_id", orderItemId);  // Truyền order_item_id sang EvaluateActivity
                                    startActivity(intent);
                                });
                            } else {
                                Toast.makeText(this, "Order Item ID is missing!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("ERROR", "❌ No such order document!");
                            Toast.makeText(this, "No such order found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ERROR", "❌ Failed to fetch order details!");
                        Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                });

        String statusFilter = getIntent().getStringExtra("status_filter");
        if (statusFilter == null) {
            statusFilter = "Unknown";  // Hoặc sử dụng trạng thái mặc định khác
            Log.w("DEBUG", "status_filter is null, defaulting to Unknown.");
        }
        updateStatusBarForTab(statusFilter);

        // Load dữ liệu từ Firestore
        loadOrderStatus(orderId);
        loadOrderDetail(orderId);
    }

    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_contact = findViewById(R.id.btn_contact);
        btn_evaluate = findViewById(R.id.btn_evaluate);
        btn_returnrefund = findViewById(R.id.btn_returnrefund);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btn_cancel.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });
    }

    private void showCancelConfirmationDialog() {
        Log.d("DEBUG", "Displaying Cancel Confirmation Dialog");

        // Tạo một đối tượng AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to cancel this order?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    String cancelReason = "No longer need to buy";
                    cancelOrder(cancelReason);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        // Tạo AlertDialog từ builder
        AlertDialog dialog = builder.create();

        // Thiết lập sự kiện khi dialog được hiển thị
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            // Cập nhật màu sắc cho các nút
            positiveButton.setTextColor(Color.parseColor("#f8f1f1"));  // Màu chữ cho nút Yes
            negativeButton.setTextColor(Color.parseColor("#f8f1f1"));  // Màu chữ cho nút No

            // Đổi màu nền của các nút
            positiveButton.setBackgroundColor(Color.parseColor("#9c162c"));  // Màu nền nút Yes
            negativeButton.setBackgroundColor(Color.parseColor("#782421"));  // Màu nền nút No

            // Cập nhật màu sắc cho nội dung popup
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ec6a44")));  // Nền popup
        });

        // Hiển thị dialog
        dialog.show();
    }

    private void cancelOrder(String cancelReason) {
        // Kiểm tra nếu orderId là null hoặc rỗng
        if (orderId == null || orderId.isEmpty()) {
            Log.e("ERROR", "Order ID is null or empty! Cannot proceed with cancellation.");
            Toast.makeText(OrderDetailActivity.this, "Order ID is missing, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy reference đến Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy thời gian hiện tại
        Timestamp cancelRequestedAt = Timestamp.now();

        // Cập nhật đơn hàng trong Firestore
        db.collection("orders").document(orderId)
                .update(
                        "order_status", "Cancelled",
                        "cancel_requested_at", cancelRequestedAt,
                        "cancel_requested_by", "buyer",
                        "cancel_reason", cancelReason
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order has been cancelled", Toast.LENGTH_SHORT).show();
                    finish();  // Quay lại màn trước
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateStatusBarForTab(String tabStatus) {
        Log.d("DEBUG", "Tab status received: " + tabStatus);

        int activeColor = Color.parseColor("#9C162C");
        int inactiveColor = Color.parseColor("#BB8866");

        ImageView[] icons = {
                findViewById(R.id.icon_pending),
                findViewById(R.id.icon_shipped),
                findViewById(R.id.icon_out),
                findViewById(R.id.icon_completed)
        };

        int[] activeIcons = {
                R.mipmap.ic_to_confirm_red,
                R.mipmap.ic_to_pickup_red,
                R.mipmap.ic_to_ship_red,
                R.mipmap.ic_completed_red
        };

        int[] inactiveIcons = {
                R.mipmap.ic_to_confirm_gray,
                R.mipmap.ic_to_pickup_gray,
                R.mipmap.ic_to_ship_gray,
                R.mipmap.ic_completed_gray
        };

        TextView[] labels = {
                ((TextView)((LinearLayout) findViewById(R.id.step_pending)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_shipped)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_out)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_completed)).getChildAt(1))
        };

        boolean[] isStepActive = new boolean[4];

        // Cập nhật logic trạng thái theo đơn hàng
        if (tabStatus == null || tabStatus.isEmpty()) {
            Log.e("ERROR", "Tab status is null or empty!");
            return; // Tránh lỗi khi không có trạng thái
        }

        // Cập nhật trạng thái các bước dựa trên trạng thái của đơn hàng
        switch (tabStatus) {
            case "Pending Payment":
                isStepActive[0] = true;
                break;
            case "Shipped":
                isStepActive[0] = true;
                isStepActive[1] = true;
                break;
            case "Delivered":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                Log.d("DEBUG", "Handling Delivered status");
                break;
            case "Completed":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                isStepActive[3] = true;
                break;
            case "Cancelled":  // Trạng thái "Cancelled"
                // Ẩn các bước khác và chỉ hiển thị trạng thái đã hủy
                findViewById(R.id.status_bar).setVisibility(View.VISIBLE); // Hiển thị status bar đặc biệt cho "Cancelled"
                findViewById(R.id.icon_pending).setVisibility(View.GONE);   // Ẩn các icon trạng thái
                findViewById(R.id.icon_shipped).setVisibility(View.GONE);
                findViewById(R.id.icon_out).setVisibility(View.GONE);
                findViewById(R.id.icon_completed).setVisibility(View.GONE);
                // Ẩn phần tiêu đề và thanh tiến trình
                findViewById(R.id.tv_order_status).setVisibility(View.GONE);  // Ẩn tiêu đề
                findViewById(R.id.order_progress_bar).setVisibility(View.GONE);  // Ẩn thanh tiến trình
                break;
            default:
                Log.e("ERROR", "Unexpected status: " + tabStatus);
                return;
        }

        // Cập nhật các icon và nhãn nếu trạng thái không phải "Cancelled"
        if (!tabStatus.equals("Cancelled")) {
            for (int i = 0; i < 4; i++) {
                if (icons[i] != null && labels[i] != null) {
                    icons[i].setImageResource(isStepActive[i] ? activeIcons[i] : inactiveIcons[i]);
                    labels[i].setTextColor(isStepActive[i] ? activeColor : inactiveColor);
                    labels[i].setTypeface(null, isStepActive[i] ? Typeface.BOLD : Typeface.NORMAL);
                } else {
                    Log.e("DEBUG", "Icon or label is null for step " + i);
                }
            }
        }
    }

    private void loadOrderStatus(String orderId) {
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String actualStatus = document.getString("status");
                            if (actualStatus != null && !actualStatus.isEmpty()) {
                                updateStatusBarForTab(actualStatus);
                            }
                        }
                    }
                });
    }

    private void loadOrderDetail(String orderId) {
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Các thông tin khác của đơn hàng
                            String orderCode = document.getString("order_code");
                            String orderTime = formatTimestamp(document.getTimestamp("order_time"));
                            String paymentMethod = document.getString("payment_method");
                            int orderValue = document.getLong("order_value").intValue();
                            int shippingFee = document.getLong("shipping_fee").intValue();

                            // Hiển thị thông tin cơ bản của đơn hàng
                            ((TextView) findViewById(R.id.tv_order_code)).setText(orderCode);
                            ((TextView) findViewById(R.id.tv_order_time)).setText(orderTime);
                            ((TextView) findViewById(R.id.tv_payment_method)).setText(paymentMethod);
                            ((TextView) findViewById(R.id.tv_shipping_fee)).setText(formatCurrency(shippingFee) + " ₫");
                            ((TextView) findViewById(R.id.tv_final_price)).setText(formatCurrency(orderValue) + " ₫");

                            // Lấy thông tin customer_note
                            String customerNote = document.getString("customer_note");
                            TextView tvCustomerNote = findViewById(R.id.tv_customer_note);
                            // Đảm bảo ẩn TextView nếu không có dữ liệu
                            tvCustomerNote.setVisibility(View.GONE);  // Mặc định ẩn trước
                            if (customerNote != null && !customerNote.isEmpty()) {
                                Log.d("DEBUG", "Customer Note: " + customerNote); // Log để kiểm tra dữ liệu
                                tvCustomerNote.setText(customerNote);
                                tvCustomerNote.setVisibility(View.VISIBLE);  // Hiển thị nếu có dữ liệu
                            } else {
                                Log.d("DEBUG", "No Customer Note available.");
                                // Ở đây đã ẩn TextView rồi, không cần phải làm thêm gì
                            }

                            // Lấy thông tin hủy đơn
                            String cancelReason = document.getString("cancel_reason");
                            String cancelRequestedAt = formatOptionalTimestamp(document.get("cancel_requested_at"));
                            String cancelRequestedBy = document.getString("cancel_requested_by");

                            // Log để kiểm tra dữ liệu
                            Log.d("DEBUG", "Cancel Reason: " + cancelReason);
                            Log.d("DEBUG", "Cancel Requested At: " + cancelRequestedAt);
                            Log.d("DEBUG", "Cancel Requested By: " + cancelRequestedBy);

                            // Kiểm tra trạng thái đơn hàng
                            if ("Cancelled".equals(document.getString("status"))) {
                                // Hiển thị các thông tin hủy đơn
                                findViewById(R.id.layout_cancel_reason).setVisibility(View.VISIBLE);
                                findViewById(R.id.layout_cancel_requested_at).setVisibility(View.VISIBLE);
                                findViewById(R.id.layout_cancel_requested_by).setVisibility(View.VISIBLE);

                                // Cập nhật thông tin hủy đơn
                                TextView tvCancelReason = findViewById(R.id.tv_cancel_reason);
                                TextView tvCancelRequestedAt = findViewById(R.id.tv_cancel_requested_at);
                                TextView tvCancelRequestedBy = findViewById(R.id.tv_cancel_requested_by);

                                if (cancelReason != null && !cancelReason.isEmpty()) {
                                    tvCancelReason.setText(cancelReason);
                                    tvCancelReason.setVisibility(View.VISIBLE);
                                } else {
                                    tvCancelReason.setVisibility(View.GONE);
                                }

                                if (cancelRequestedAt != null && !cancelRequestedAt.equals("Unknown Time") && !cancelRequestedAt.isEmpty()) {
                                    tvCancelRequestedAt.setText(cancelRequestedAt);
                                    tvCancelRequestedAt.setVisibility(View.VISIBLE);
                                } else {
                                    tvCancelRequestedAt.setVisibility(View.GONE);
                                }

                                if (cancelRequestedBy != null && !cancelRequestedBy.isEmpty()) {
                                    tvCancelRequestedBy.setText(cancelRequestedBy);
                                    tvCancelRequestedBy.setVisibility(View.VISIBLE);
                                } else {
                                    tvCancelRequestedBy.setVisibility(View.GONE);
                                }
                            } else {
                                // Nếu không phải "Cancelled", ẩn các trường thông tin hủy đơn
                                findViewById(R.id.layout_cancel_reason).setVisibility(View.GONE);
                                findViewById(R.id.layout_cancel_requested_at).setVisibility(View.GONE);
                                findViewById(R.id.layout_cancel_requested_by).setVisibility(View.GONE);
                            }

                            // Lấy thông tin thời gian ship, payment, complete
                            String shipTime = formatOptionalTimestamp(document.get("ship_time"));
                            String paymentTime = formatOptionalTimestamp(document.get("payment_time"));
                            String completeTime = formatOptionalTimestamp(document.get("complete_time"));

                            // Ẩn các dòng thời gian nếu không có dữ liệu
                            handleVisibilityForTimeFields(shipTime, paymentTime, completeTime);

                            // Gọi loadDeliveryInfo và fetchOrderItems
                            loadDeliveryInfo(orderId);
                            fetchOrderItemIds(orderId);

                            // Lấy tabStatus từ Intent
                            String tabStatus = getIntent().getStringExtra("status_filter");
                            if (tabStatus == null || tabStatus.isEmpty()) {
                                tabStatus = "Unknown"; // Hoặc trạng thái mặc định khác
                            }

                            // Kiểm tra trạng thái tab
                            switch (tabStatus) {
                                case "Pending Payment":
                                case "Shipped":
                                case "Delivered":
                                    // Hiển thị nút "Contact Shop" và "Cancel Order"
                                    btn_contact.setVisibility(View.VISIBLE);
                                    btn_cancel.setVisibility(View.VISIBLE);
                                    break;

                                case "Completed":
                                    // Hiển thị nút "Contact Shop" và "Evaluate"
                                    btn_returnrefund.setVisibility(View.VISIBLE);
                                    btn_evaluate.setVisibility(View.VISIBLE);
                                    break;

                                case "Cancelled":
                                    // Không hiển thị nút nào
                                    btn_contact.setVisibility(View.GONE);
                                    btn_cancel.setVisibility(View.GONE);
                                    btn_evaluate.setVisibility(View.GONE);
                                    btn_returnrefund.setVisibility(View.GONE);
                                    break;

                                default:
                                    break;
                            }

                            // Thiết lập sự kiện click cho nút "Contact Shop"
                            btn_contact.setOnClickListener(v -> {
                                // Mở màn hình liên hệ với cửa hàng hoặc chat, gọi điện
                            });

                            // Thiết lập sự kiện click cho nút "Evaluate" nếu trạng thái là Completed
                            btn_evaluate.setOnClickListener(v -> {
                                if (orderItemId != null && !orderItemId.isEmpty()) {
                                    Intent intent = new Intent(OrderDetailActivity.this, EvaluateActivity.class);
                                    intent.putExtra("order_item_id", orderItemId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(OrderDetailActivity.this, "Order Item ID is missing!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Thiết lập sự kiện click cho nút "Cancel Order"
                            btn_cancel.setOnClickListener(v -> {
                                Log.d("DEBUG", "Đang xử lý hủy đơn hàng với orderId = " + orderId);
                                if (orderId == null || orderId.isEmpty()) {
                                    Log.e("ERROR", "Order ID is null or empty! Cannot proceed with cancellation.");
                                    return;
                                }
                                showCancelConfirmationDialog();
                            });

                            // Thiết lập sự kiện click cho nút "Return/Refund"
                            btn_returnrefund.setOnClickListener(v -> {
                                if (orderItemId != null && !orderItemId.isEmpty()) {
                                    Intent intent = new Intent(OrderDetailActivity.this, RefundReturnRequestActivity.class );
                                    intent.putExtra("order_item_id", orderItemId);
                                    intent.putExtra("order_id", orderId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(OrderDetailActivity.this, "Order Item ID is missing!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }

    private void handleVisibilityForTimeFields(String shipTime, String paymentTime, String completeTime) {
        // Handle Ship Time
        TextView tvShipTime = findViewById(R.id.tv_ship_time);
        LinearLayout layoutShipTime = findViewById(R.id.layout_ship_time);
        if (shipTime != null && !shipTime.equals("Unknown Time") && !shipTime.isEmpty()) {
            tvShipTime.setText(shipTime);
            layoutShipTime.setVisibility(View.VISIBLE);
        } else {
            layoutShipTime.setVisibility(View.GONE);  // Ẩn nếu không có dữ liệu
        }

        // Handle Payment Time
        TextView tvPaymentTime = findViewById(R.id.tv_payment_time);
        LinearLayout layoutPaymentTime = findViewById(R.id.layout_payment_time);
        if (paymentTime != null && !paymentTime.equals("Unknown Time") && !paymentTime.isEmpty()) {
            tvPaymentTime.setText(paymentTime);
            layoutPaymentTime.setVisibility(View.VISIBLE);
        } else {
            layoutPaymentTime.setVisibility(View.GONE);  // Ẩn nếu không có dữ liệu
        }

        // Handle Completed Time
        TextView tvCompleteTime = findViewById(R.id.tv_complete_time);
        LinearLayout layoutCompleteTime = findViewById(R.id.layout_completed_time);
        if (completeTime != null && !completeTime.equals("Unknown Time") && !completeTime.isEmpty()) {
            tvCompleteTime.setText(completeTime);
            layoutCompleteTime.setVisibility(View.VISIBLE);
        } else {
            layoutCompleteTime.setVisibility(View.GONE);  // Ẩn nếu không có dữ liệu
        }
    }

    private String formatOptionalTimestamp(Object timestampObj) {
        if (timestampObj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) timestampObj;
            return formatTimestamp(timestamp);
        } else if (timestampObj instanceof String) {
            return (String) timestampObj; // If it's already a string, return it as is
        }
        return "Unknown Time";  // Default if not available
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "Unknown Time";
    }

    private void loadDeliveryInfo(String orderId) {
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot orderDoc = task.getResult();
                        if (orderDoc.exists()) {
                            String customerId = orderDoc.getString("customer_id");

                            Log.d("DEBUG", "customerId: " + customerId);

                            if (customerId != null && !customerId.isEmpty()) {
                                // Fetch customer data
                                db.collection("customers").document(customerId)
                                        .get()
                                        .addOnCompleteListener(customerTask -> {
                                            if (customerTask.isSuccessful()) {
                                                DocumentSnapshot customerDoc = customerTask.getResult();
                                                if (customerDoc.exists()) {
                                                    String customerName = customerDoc.getString("customer_name");
                                                    String phone = customerDoc.getString("phone_number");
                                                    String address = customerDoc.getString("address");

                                                    // Hiển thị thông tin khách hàng
                                                    ((TextView) findViewById(R.id.tv_customer_name)).setText(customerName);
                                                    ((TextView) findViewById(R.id.tv_phone)).setText(phone);
                                                    ((TextView) findViewById(R.id.tv_address)).setText(address);
                                                } else {
                                                    Log.w("DEBUG", "Customer data not found.");
                                                }
                                            } else {
                                                Log.e("DEBUG", "Error fetching customer data: " + customerTask.getException());
                                            }
                                        });
                            } else {
                                Log.w("DEBUG", "No customer_id found in order: " + orderId);
                            }
                        } else {
                            Log.w("DEBUG", "No order document found for orderId: " + orderId);
                        }
                    } else {
                        Log.e("DEBUG", "Error fetching order data: " + task.getException());
                    }
                });
    }

    private void fetchOrderItemIds(String orderId) {
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String orderItemId = document.getString("order_item_id");
                            if (orderItemId != null && !orderItemId.isEmpty()) {
                                fetchOrderItems(orderItemId);
                            }
                        }
                    }
                });
    }

    private void fetchOrderItems(String orderItemId) {
        db.collection("order_items")
                .document(orderItemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot orderItemDoc = task.getResult();
                        if (orderItemDoc.exists()) {
                            LinearLayout productListLayout = findViewById(R.id.ll_products);
                            productListLayout.removeAllViews();  // Xóa các dữ liệu cũ

                            long totalCostOfGoods = 0;

                            // Lặp qua các sản phẩm
                            for (String productKey : orderItemDoc.getData().keySet()) {
                                if (productKey.startsWith("product")) {
                                    String productId = orderItemDoc.getString(productKey + ".product_id");
                                    int quantity = orderItemDoc.getLong(productKey + ".quantity").intValue();
                                    long productCost = orderItemDoc.getLong(productKey + ".total_cost_of_goods");

                                    totalCostOfGoods += productCost;

                                    // Fetch product details
                                    fetchProductDetails(productId, quantity, productListLayout);
                                }
                            }

                            // Cập nhật tổng chi phí
                            TextView totalCostTextView = findViewById(R.id.tv_total_cost);
                            String formattedTotalCost = String.format("%,d", totalCostOfGoods);
                            totalCostTextView.setText(formattedTotalCost + " ₫");
                        }
                    }
                });
    }

    private void fetchProductDetails(String productId, int quantity, LinearLayout productListLayout) {
        db.collection("products").document(productId)  // Get product details from "products" collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot productDoc = task.getResult();
                        if (productDoc.exists()) {
                            String productName = productDoc.getString("product_name");
                            int productPrice = productDoc.getLong("price").intValue();
                            String productImage = productDoc.getString("product_image");
                            String productColor = productDoc.getString("product_color");  // Assuming there is a color field

                            Log.d("DEBUG", "Fetched product: " + productName + ", Price: " + productPrice + ", Image: " + productImage);

                            // Inflate the checkout_item layout
                            View productView = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.checkout_item, productListLayout, false);

                            // Get references to the views
                            ImageView imgProduct = productView.findViewById(R.id.imgProduct);
                            TextView txtProductName = productView.findViewById(R.id.txtProductName);
                            TextView txtProductColor = productView.findViewById(R.id.txtProductColor);
                            TextView txtProductPrice = productView.findViewById(R.id.txtProductPrice);
                            TextView txtProductQuantity = productView.findViewById(R.id.txtProductQuantity);

                            // Set data to views
                            txtProductName.setText(productName);
                            txtProductColor.setText(productColor != null ? productColor : "No Color/Variant");
                            txtProductPrice.setText(formatCurrency(productPrice) + " ₫");
                            txtProductQuantity.setText("x" + quantity);

                            // Load product image with Glide
                            Glide.with(OrderDetailActivity.this)
                                    .load(productImage)
                                    .into(imgProduct);

                            // Add the inflated product view to the product list layout
                            productListLayout.addView(productView);
                        }
                    } else {
                        Log.e("DEBUG", "Error fetching product details: " + task.getException());
                    }
                });
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(amount);
        return formattedAmount;
    }
}
