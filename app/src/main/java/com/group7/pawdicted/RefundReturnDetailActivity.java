package com.group7.pawdicted;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class RefundReturnDetailActivity extends AppCompatActivity {

    ImageView btn_back;
    TextView tv_status, tv_refund_amount, tv_refund_method;
    TextView tv_detail_requested_at, tv_return_situation, tv_return_reason;
    LinearLayout ll_items;

    FirebaseFirestore db;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_return_detail);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("order_id");

        addViews();
        addEvents();

        if (orderId != null && !orderId.isEmpty()) {
            loadRefundDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
        tv_status = findViewById(R.id.tv_status);
        tv_refund_amount = findViewById(R.id.tv_refund_amount);
        tv_refund_method = findViewById(R.id.tv_refund_method);
        tv_detail_requested_at = findViewById(R.id.tv_detail_requested_at);
        tv_return_situation = findViewById(R.id.tv_return_situation);
        tv_return_reason = findViewById(R.id.tv_return_reason);
        ll_items = findViewById(R.id.ll_items);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private void loadRefundDetails(String orderId) {
        db.collection("orders").document(orderId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                tv_status.setText("Successfully requested a return!");
                Number refundAmount = document.getDouble("return_amount");
                String returnReason = document.getString("return_reason");
                String refundMethod = document.getString("refund_method");
                List<Map<String, String>> productReturn = (List<Map<String, String>>) document.get("product_return");
                List<String> returnSituation = (List<String>) document.get("return_situation");
                Date requestedAt = document.getDate("return_requested_at");

                if (refundAmount != null) {
                    java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
                    tv_refund_amount.setText(formatter.format(refundAmount) + "₫");
                }
                if (refundMethod != null) tv_refund_method.setText(refundMethod);
                if (returnReason != null) tv_return_reason.setText(returnReason);
                if (returnSituation != null) tv_return_situation.setText(String.join(", ", returnSituation));
                if (requestedAt != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    tv_detail_requested_at.setText(sdf.format(requestedAt));
                }

                if (productReturn != null) {
                    for (Map<String, String> item : productReturn) {
                        String productId = item.get("product_id");
                        String productName = item.get("product_name");

                        db.collection("products").document(productId).get().addOnSuccessListener(productDoc -> {
                            if (productDoc.exists()) {
                                String imageUrl = productDoc.getString("product_image");

                                // Tạo container nằm ngang
                                LinearLayout row = new LinearLayout(this);
                                row.setOrientation(LinearLayout.HORIZONTAL);
                                row.setPadding(0, 16, 0, 16);

                                // Tạo ImageView
                                ImageView imageView = new ImageView(this);
                                int imageSize = (int) getResources().getDisplayMetrics().density * 64;
                                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                                imageParams.setMargins(0, 0, 16, 0);
                                imageView.setLayoutParams(imageParams);
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .error(R.mipmap.ic_launcher)
                                        .into(imageView);

                                // Tạo TextView cho tên sản phẩm
                                TextView tvName = new TextView(this);
                                tvName.setText(productName);
                                tvName.setTextSize(15);
                                tvName.setTextColor(getResources().getColor(android.R.color.black));
                                tvName.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                // Thêm ImageView và TextView vào dòng
                                row.addView(imageView);
                                row.addView(tvName);

                                // Thêm dòng vào layout cha
                                ll_items.addView(row);
                            }
                        });
                    }
                }

            } else {
                Toast.makeText(this, "Refund data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
