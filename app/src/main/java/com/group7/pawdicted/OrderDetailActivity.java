package com.group7.pawdicted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.connectors.SQLiteConnector;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    ImageView btn_back;

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

        // Nhận order_id từ Intent
        String orderId = getIntent().getStringExtra("order_id");
        String statusFilter = getIntent().getStringExtra("status_filter");
        if (statusFilter != null) {
            updateStatusBarForTab(statusFilter);
        }

        // Load dữ liệu theo order
        loadOrderStatus(orderId);
        loadOrderDetail(orderId);
    }

    private void updateStatusBarForTab(String tab) {
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

        switch (tab) {
            case "To Confirm":
                isStepActive[0] = true;
                break;
            case "To Pickup":
                isStepActive[0] = true;
                isStepActive[1] = true;
                break;
            case "To Ship":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                break;
            case "Completed":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                isStepActive[3] = true;
                break;
        }

        for (int i = 0; i < 4; i++) {
            icons[i].setImageResource(isStepActive[i] ? activeIcons[i] : inactiveIcons[i]);
            labels[i].setTextColor(isStepActive[i] ? activeColor : inactiveColor);
            labels[i].setTypeface(null, isStepActive[i] ? Typeface.BOLD : Typeface.NORMAL);
        }
    }


    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private void loadOrderStatus(String orderId) {
        SQLiteConnector dbHelper = new SQLiteConnector(this);
        SQLiteDatabase db = dbHelper.getDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT status FROM order_status WHERE order_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{orderId}
        );

        if (cursor.moveToFirst()) {
            String actualStatus = cursor.getString(0);

            // Lấy tên tab từ PurchaseOrderActivity (ví dụ: "To Confirm", "To Ship",...)
            String tabStatus = getIntent().getStringExtra("status_filter");
            if (tabStatus == null) tabStatus = actualStatus;  // fallback nếu không có

            // Gọi status bar theo tab ban đầu
            updateStatusBar(tabStatus);

            // Nếu trạng thái thật là Completed → hiển thị các nút
            if ("Completed".equalsIgnoreCase(actualStatus)) {
                Button btnCancel = findViewById(R.id.btn_cancel);
                LinearLayout layoutBottomActions = findViewById(R.id.layout_bottom_actions);

                layoutBottomActions.setVisibility(View.VISIBLE);
                btnCancel.setText("Buy Again");

                findViewById(R.id.img_back).setOnClickListener(v -> {
                    Intent intent = new Intent(OrderDetailActivity.this, RefundReturnRequestActivity.class);
                    startActivity(intent);
                });

                findViewById(R.id.btn_evaluate).setOnClickListener(v -> {
                    Intent intent = new Intent(OrderDetailActivity.this, EvaluateActivity.class);
                    startActivity(intent);
                });
            }
        }

        cursor.close();
        db.close();
    }


    private void updateStatusBar(String tabStatus) {
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

        // Active từng bước tùy theo tên tab
        switch (tabStatus) {
            case "To Confirm":
                isStepActive[0] = true; // chỉ Pending Payment
                break;
            case "To Pickup":
                isStepActive[0] = true;
                isStepActive[1] = true;
                break;
            case "To Ship":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                break;
            case "Completed":
                isStepActive[0] = true;
                isStepActive[1] = true;
                isStepActive[2] = true;
                isStepActive[3] = true;
                break;
        }

        for (int i = 0; i < 4; i++) {
            icons[i].setImageResource(isStepActive[i] ? activeIcons[i] : inactiveIcons[i]);
            labels[i].setTextColor(isStepActive[i] ? activeColor : inactiveColor);
            labels[i].setTypeface(null, isStepActive[i] ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }


    private void loadOrderDetail(String orderId) {
        SQLiteConnector dbHelper = new SQLiteConnector(this);
        SQLiteDatabase db = dbHelper.getDatabase();

        // Địa chỉ giao hàng
        Cursor addressCursor = db.rawQuery(
                "SELECT recipient_name, phone, address FROM delivery_address WHERE order_id = ?",
                new String[]{orderId}
        );
        if (addressCursor.moveToFirst()) {
            ((TextView) findViewById(R.id.tv_customer_name)).setText(addressCursor.getString(0));
            ((TextView) findViewById(R.id.tv_phone)).setText(addressCursor.getString(1));
            ((TextView) findViewById(R.id.tv_address)).setText(addressCursor.getString(2));
        }
        addressCursor.close();

        // Danh sách sản phẩm
        LinearLayout productList = findViewById(R.id.ll_products);
        Cursor productCursor = db.rawQuery(
                "SELECT product_name, quantity, unit_price FROM order_items WHERE order_id = ?",
                new String[]{orderId}
        );
        productList.removeAllViews();
        while (productCursor.moveToNext()) {
            String productName = productCursor.getString(0);
            int quantity = productCursor.getInt(1);
            int unitPrice = productCursor.getInt(2);

            View itemView = getLayoutInflater().inflate(R.layout.checkout_item, productList, false);
            ((TextView) itemView.findViewById(R.id.txtProductName)).setText(productName);
            ((TextView) itemView.findViewById(R.id.txtProductPrice)).setText(formatCurrency(unitPrice) + " ₫");
            ((TextView) itemView.findViewById(R.id.txtProductQuantity)).setText("x" + quantity);

            Cursor imgCursor = db.rawQuery(
                    "SELECT ImageLink FROM products WHERE product_name = ? LIMIT 1",
                    new String[]{productName}
            );
            if (imgCursor.moveToFirst()) {
                String imgUrl = imgCursor.getString(0);
                ImageView imageView = itemView.findViewById(R.id.imgProduct);
                Glide.with(this).load(imgUrl).into(imageView);
            }
            imgCursor.close();

            productList.addView(itemView);
        }
        productCursor.close();

        // Chi phi + Order info
        Cursor orderCursor = db.rawQuery(
                "SELECT order_code, order_value, shipping_fee, order_time, payment_method, ship_time, payment_time, completed_time " +
                        "FROM orders WHERE order_id = ?",
                new String[]{orderId}
        );

        String shipTime = null;
        String paymentTime = null;
        String completedTime = null;
        String paymentMethod = null;

        if (orderCursor.moveToFirst()) {
            String code = orderCursor.getString(0);
            int total = orderCursor.getInt(1);
            int shipping = orderCursor.getInt(2);
            String time = orderCursor.getString(3);
            paymentMethod = orderCursor.getString(4);
            shipTime = orderCursor.getString(5);
            paymentTime = orderCursor.getString(6);
            completedTime = orderCursor.getString(7);

            int totalCostOfGoods = 0;
            Cursor itemTotalCursor = db.rawQuery(
                    "SELECT SUM(total_cost_of_goods) FROM order_items WHERE order_id = ?",
                    new String[]{orderId}
            );
            if (itemTotalCursor.moveToFirst()) {
                totalCostOfGoods = itemTotalCursor.getInt(0);
            }
            itemTotalCursor.close();

            ((TextView) findViewById(R.id.tv_total_cost)).setText(formatCurrency(totalCostOfGoods) + " ₫");
            ((TextView) findViewById(R.id.tv_shipping_fee)).setText(formatCurrency(shipping) + " ₫");
            ((TextView) findViewById(R.id.tv_final_price)).setText(formatCurrency(totalCostOfGoods + shipping) + " ₫");

            ((TextView) findViewById(R.id.tv_order_code)).setText(code);
            ((TextView) findViewById(R.id.tv_order_time)).setText(time);
            ((TextView) findViewById(R.id.tv_payment_method)).setText(paymentMethod);
        }
        orderCursor.close();

        // Trạng thái đơn hàng
        Cursor statusCursor = db.rawQuery(
                "SELECT status FROM order_status WHERE order_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{orderId}
        );

        if (statusCursor.moveToFirst()) {
            String status = statusCursor.getString(0);

            LinearLayout layoutShipTime = findViewById(R.id.layout_ship_time);
            LinearLayout layoutPaymentTime = findViewById(R.id.layout_payment_time);
            LinearLayout layoutCompletedTime = findViewById(R.id.layout_completed_time);

            TextView tvShipTime = findViewById(R.id.tv_ship_time);
            TextView tvPaymentTime = findViewById(R.id.tv_payment_time);
            TextView tvCompletedTime = findViewById(R.id.tv_completed_time);

            // Ship time
            if (shipTime != null && !shipTime.isEmpty() &&
                    (status.equalsIgnoreCase("Shipped")
                            || status.equalsIgnoreCase("Out for Delivery")
                            || status.equalsIgnoreCase("Completed"))) {
                layoutShipTime.setVisibility(View.VISIBLE);
                tvShipTime.setText(shipTime);
            }

            // Payment time
            if (paymentTime != null && !paymentTime.isEmpty()) {
                boolean isSmartBanking = "Smart Banking".equalsIgnoreCase(paymentMethod);
                boolean shouldShow = isSmartBanking || status.equalsIgnoreCase("Out for Delivery")
                        || status.equalsIgnoreCase("Completed")
                        || status.equalsIgnoreCase("Shipped")
                        || status.equalsIgnoreCase("Pending Payment");
                if (shouldShow) {
                    layoutPaymentTime.setVisibility(View.VISIBLE);
                    tvPaymentTime.setText(paymentTime);
                }
            }

            // Completed time
            if (completedTime != null && !completedTime.isEmpty()
                    && status.equalsIgnoreCase("Completed")) {
                layoutCompletedTime.setVisibility(View.VISIBLE);
                tvCompletedTime.setText(completedTime);
            }
        }
        statusCursor.close();
        db.close();
    }

    // Hàm định dạng tiền tệ kiểu Việt Nam
    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
