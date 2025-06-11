package com.group7.pawdicted;

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

        // Load dữ liệu theo order
        loadOrderStatus(orderId);
        loadOrderDetail(orderId);
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

        int currentStep = 0;

        Cursor cursor = db.rawQuery(
                "SELECT status FROM order_status WHERE order_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{orderId}
        );

        if (cursor.moveToFirst()) {
            String status = cursor.getString(0);
            switch (status) {
                case "Pending Payment": currentStep = 0; break;
                case "Shipped": currentStep = 1; break;
                case "Out for Delivery": currentStep = 2; break;
                case "Completed": currentStep = 3; break;
            }

            Button btnCancel = findViewById(R.id.btn_cancel);
            LinearLayout layoutBottomActions = findViewById(R.id.layout_bottom_actions);

            // Khi trạng thái là "Completed"
            if ("Completed".equalsIgnoreCase(status)) {
                findViewById(R.id.layout_bottom_actions).setVisibility(View.VISIBLE);

                // Đổi text nút Cancel thành Buy Again
                btnCancel.setText("Buy Again");

                // Hiện hai nút Return/Refund & Evaluate bên dưới
                layoutBottomActions.setVisibility(View.VISIBLE);

                // Gán hành động nếu cần
                findViewById(R.id.btn_return).setOnClickListener(v -> {
                    // TODO: mở giao diện Return/Refund
                });

                findViewById(R.id.btn_evaluate).setOnClickListener(v -> {
                    // TODO: mở giao diện đánh giá
                });
            }
        }
        cursor.close();
        db.close();

        updateStatusBar(currentStep);
    }

    private void updateStatusBar(int currentStep) {
        int activeColor = Color.parseColor("#9C162C");
        int inactiveColor = Color.parseColor("#BB8866");

        ImageView[] icons = {
                findViewById(R.id.icon_pending),
                findViewById(R.id.icon_shipped),
                findViewById(R.id.icon_out),
                findViewById(R.id.icon_completed)
        };

        int[] activeIcons = {
                R.drawable.ic_to_confirm_red,
                R.drawable.ic_to_pickup_red,
                R.drawable.ic_to_ship_red,
                R.drawable.ic_completed_red
        };

        int[] inactiveIcons = {
                R.drawable.ic_to_confirm_gray,
                R.drawable.ic_to_pickup_gray,
                R.drawable.ic_to_ship_gray,
                R.drawable.ic_completed_gray
        };

        TextView[] labels = {
                ((TextView)((LinearLayout) findViewById(R.id.step_pending)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_shipped)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_out)).getChildAt(1)),
                ((TextView)((LinearLayout) findViewById(R.id.step_completed)).getChildAt(1))
        };

        for (int i = 0; i < 4; i++) {
            icons[i].setImageResource(i == currentStep ? activeIcons[i] : inactiveIcons[i]);
            labels[i].setTextColor(i == currentStep ? activeColor : inactiveColor);
            labels[i].setTypeface(null, i == currentStep ? Typeface.BOLD : Typeface.NORMAL);
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
                "SELECT product_name, quantity, total_cost_of_goods FROM order_items WHERE order_id = ?",
                new String[]{orderId}
        );
        productList.removeAllViews();
        while (productCursor.moveToNext()) {
            String productName = productCursor.getString(0);
            int quantity = productCursor.getInt(1);
            int totalCost = productCursor.getInt(2);
            int unitPrice = totalCost / quantity;

            View itemView = getLayoutInflater().inflate(R.layout.layout_product_item, productList, false);
            ((TextView) itemView.findViewById(R.id.tv_product_name)).setText(productName);
            ((TextView) itemView.findViewById(R.id.tv_unit_price)).setText(formatCurrency(unitPrice) + " ₫");
            ((TextView) itemView.findViewById(R.id.tv_quantity)).setText("x" + quantity);
            ((TextView) itemView.findViewById(R.id.tv_total)).setText(formatCurrency(totalCost) + " ₫");

            Cursor imgCursor = db.rawQuery(
                    "SELECT ImageLink FROM products WHERE product_name = ? LIMIT 1",
                    new String[]{productName}
            );
            if (imgCursor.moveToFirst()) {
                String imgUrl = imgCursor.getString(0);
                ImageView imageView = itemView.findViewById(R.id.img_product);
                Glide.with(this).load(imgUrl).into(imageView);
            }
            imgCursor.close();

            productList.addView(itemView);
        }
        productCursor.close();

        // Tổng tiền + order code + thời gian + phương thức thanh toán + thời gian liên quan
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
