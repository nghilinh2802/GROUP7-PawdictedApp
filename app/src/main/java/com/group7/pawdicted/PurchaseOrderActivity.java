package com.group7.pawdicted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.group7.pawdicted.mobile.connectors.SQLiteConnector;

import java.text.NumberFormat;
import java.util.Locale;

public class PurchaseOrderActivity extends AppCompatActivity {

    Button btn_confirm, btn_to_pickup, btn_received, btn_completed, btn_cancelled, btn_returnrefund;
    ImageView btn_back, btn_search;
    LinearLayout emptyView;

    SQLiteConnector dbHelper;
    SQLiteDatabase db;

    ScrollView orderScroll;
    LinearLayout orderListContainer;

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

        // Lấy trạng thái từ Intent
        String status = getIntent().getStringExtra("order_status");
        if (status == null) {
            status = "Pending Payment"; // fallback mặc định nếu không có dữ liệu
        }

        loadOrdersByStatus(status); // Load đơn hàng theo trạng thái
    }

    private void addViews() {
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_to_pickup = findViewById(R.id.btn_to_pickup);
        btn_received = findViewById(R.id.btn_received);
        btn_completed = findViewById(R.id.btn_completed);
        btn_cancelled = findViewById(R.id.btn_cancelled);
        btn_returnrefund = findViewById(R.id.btn_returnrefund);
        btn_back = findViewById(R.id.btn_back);
        btn_search = findViewById(R.id.btn_search);

        emptyView = findViewById(R.id.empty_view); // layout không có dữ liệu cho status đó  -text "You have no orders yet"

        dbHelper = new SQLiteConnector(this);
        db = dbHelper.getDatabase();

        orderScroll = findViewById(R.id.order_scroll);
        orderListContainer = findViewById(R.id.order_list_container);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btn_confirm.setOnClickListener(v -> loadOrdersByStatus("Pending Payment"));
        btn_to_pickup.setOnClickListener(v -> loadOrdersByStatus("Shipped"));
        btn_received.setOnClickListener(v -> loadOrdersByStatus("Out for Delivery"));
        btn_completed.setOnClickListener(v -> loadOrdersByStatus("Completed"));
        btn_cancelled.setOnClickListener(v -> loadOrdersByStatus("Cancelled"));
        btn_returnrefund.setOnClickListener(v -> loadOrdersByStatus("Return/Refund"));
    }

    private void loadOrdersByStatus(String status) {
        highlightSelectedStatus(status);
        orderListContainer.removeAllViews();

        Cursor cursor;

        if ("Cancelled".equalsIgnoreCase(status)) {
            cursor = db.rawQuery(
                    "SELECT order_id, order_code, order_time FROM orders " +
                            "WHERE cancel_requested_by IS NOT NULL " +
                            "ORDER BY order_time DESC", null
            );
        } else if ("Return/Refund".equalsIgnoreCase(status)) {
            cursor = db.rawQuery(
                    "SELECT DISTINCT o.order_id, o.order_code, o.order_time " +
                            "FROM orders o JOIN order_status s ON o.order_id = s.order_id " +
                            "WHERE s.status IN ('Refund Requested', 'Refund Credited', 'Return Approved') " +
                            "ORDER BY o.order_time DESC",
                    null
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT DISTINCT o.order_id, o.order_code, o.order_time " +
                            "FROM orders o JOIN order_status s ON o.order_id = s.order_id " +
                            "WHERE s.status = ? " +
                            "ORDER BY o.order_time DESC",
                    new String[]{status}
            );
        }

        if (cursor.moveToFirst()) {
            emptyView.setVisibility(View.GONE);
            orderScroll.setVisibility(View.VISIBLE);

            String lastMonthYear = "";

            do {
                String orderId = cursor.getString(0);
                String orderCode = cursor.getString(1);
                String orderTime = cursor.getString(2);

                // Định dạng tháng-năm nhóm đơn hàng
                String monthYear = getMonthYearFormatted(orderTime);
                if (!monthYear.equals(lastMonthYear)) {
                    TextView monthView = new TextView(this);
                    monthView.setText(monthYear);
                    monthView.setTextSize(16f);
                    monthView.setTypeface(null, android.graphics.Typeface.BOLD);
                    monthView.setTextColor(Color.parseColor("#782421"));
                    monthView.setPadding(0, 16, 0, 8);
                    orderListContainer.addView(monthView);
                    lastMonthYear = monthYear;
                }

                int totalCostOfGoods = 0;
                int shippingFee = 0;

                Cursor costCursor = db.rawQuery(
                        "SELECT SUM(total_cost_of_goods) FROM order_items WHERE order_id = ?",
                        new String[]{orderId}
                );
                if (costCursor.moveToFirst()) totalCostOfGoods = costCursor.getInt(0);
                costCursor.close();

                Cursor shipCursor = db.rawQuery(
                        "SELECT shipping_fee FROM orders WHERE order_id = ?",
                        new String[]{orderId}
                );
                if (shipCursor.moveToFirst()) shippingFee = shipCursor.getInt(0);
                shipCursor.close();

                int finalPrice = totalCostOfGoods + shippingFee;

                orderListContainer.addView(
                        createOrderView(orderId, orderTime, finalPrice, status)
                );

            } while (cursor.moveToNext());

        } else {
            emptyView.setVisibility(View.VISIBLE);
            orderScroll.setVisibility(View.GONE);
        }

        cursor.close();
    }


    private String getMonthYearFormatted(String orderTime) {
        try {
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date date = parser.parse(orderTime);

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MMMM yyyy");
            return formatter.format(date);
        } catch (Exception e) {
            return ""; // fallback nếu lỗi
        }
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    private View createOrderView(String orderId, String orderTime, int totalPrice, String status) {
        View view = getLayoutInflater().inflate(R.layout.order_item, orderListContainer, false);

        TextView tvTime = view.findViewById(R.id.tv_order_time);
        TextView tvTotal = view.findViewById(R.id.tv_total_price);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        LinearLayout productList = view.findViewById(R.id.product_list);

        LinearLayout layoutContact = view.findViewById(R.id.layout_contact_shop);
        LinearLayout layoutCompleted = view.findViewById(R.id.layout_completed_actions);
        MaterialButton btnReturnRefund = view.findViewById(R.id.btn_return_refund);
        MaterialButton btnEvaluate = view.findViewById(R.id.btn_evaluate);

        tvTime.setText(orderTime);
        tvTotal.setText("Total: " + formatCurrency(totalPrice) + " ₫");
        tvStatus.setText(getStatusLabel(status));

        // Load sản phẩm của đơn hàng
        Cursor itemCursor = db.rawQuery(
                "SELECT product_name, quantity, total_cost_of_goods FROM order_items WHERE order_id = ?",
                new String[]{orderId}
        );

        if (itemCursor.moveToFirst()) {
            do {
                String productName = itemCursor.getString(0);
                int quantity = itemCursor.getInt(1);

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView tvQty = new TextView(this);
                tvQty.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
                tvQty.setText(quantity + "x");
                tvQty.setTextSize(14f);
                tvQty.setTextColor(Color.BLACK);

                TextView tvName = new TextView(this);
                tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                tvName.setText(productName);
                tvName.setTextSize(14f);
                tvName.setTextColor(Color.BLACK);

                row.addView(tvQty);
                row.addView(tvName);
                productList.addView(row);

            } while (itemCursor.moveToNext());
        }
        itemCursor.close();

        view.setOnClickListener(v -> {
            Intent intent;

            if ("Cancelled".equalsIgnoreCase(status)) {
                intent = new Intent(PurchaseOrderActivity.this, CancellationDetailActivity.class);
            } else if (
                    status.equalsIgnoreCase("Refund Requested") ||
                            status.equalsIgnoreCase("Refund Credited") ||
                            status.equalsIgnoreCase("Return Approved") ||
                            status.equalsIgnoreCase("Return/Refund")
            ) {
                intent = new Intent(PurchaseOrderActivity.this, RefundReturnDetailActivity.class);
            } else {
                intent = new Intent(PurchaseOrderActivity.this, OrderDetailActivity.class);
                intent.putExtra("status_filter", getTabNameFromStatus(status));
            }

            intent.putExtra("order_id", orderId);
            startActivity(intent);
        });

        // Gán sự kiện click nếu trạng thái là Completed
        if (status.equalsIgnoreCase("Completed")) {
            layoutContact.setVisibility(View.GONE);
            layoutCompleted.setVisibility(View.VISIBLE);
            btnReturnRefund.setOnClickListener(v -> {
                // TODO: Mở giao diện Return/Refund
            });
            btnEvaluate.setOnClickListener(v -> {
                // TODO: Mở giao diện Evaluate
            });

        } else if ( // Trạng thái Refund/Return → ẩn cả 2 nhóm nút
                status.equalsIgnoreCase("Refund Requested") ||
                        status.equalsIgnoreCase("Refund Credited") ||
                        status.equalsIgnoreCase("Return Approved") ||
                        status.equalsIgnoreCase("Return/Refund")
        ) {
            layoutContact.setVisibility(View.GONE);
            layoutCompleted.setVisibility(View.GONE);

        } else {
            // Các trạng thái còn lại
            layoutContact.setVisibility(View.VISIBLE);
            layoutCompleted.setVisibility(View.GONE);
        }

        return view;
    }

    private String getTabNameFromStatus(String status) {
        switch (status) {
            case "Pending Payment":
                return "To Confirm";
            case "Shipped":
                return "To Pickup";
            case "Out for Delivery":
                return "To Ship";
            case "Completed":
                return "Completed";
            default:
                return ""; // Trường hợp khác như "Cancelled", "Return/Refund"
        }
    }

    private String getStatusLabel(String status) {
        if (status.equalsIgnoreCase("Pending Payment")) return "To Pay";
        if (status.equalsIgnoreCase("Shipped")) return "To Ship";
        if (status.equalsIgnoreCase("Out for Delivery")) return "To Receive";

        if (status.equalsIgnoreCase("Refund Credited") ||
                status.equalsIgnoreCase("Refund Requested") ||
                status.equalsIgnoreCase("Return Approved")) return "Return/Refund";

        return status;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Status Buttons
    private void highlightSelectedStatus(String selectedStatus) {
        MaterialButton btnToPickup = findViewById(R.id.btn_to_pickup);
        MaterialButton btnReceived = findViewById(R.id.btn_received);
        MaterialButton btnCompleted = findViewById(R.id.btn_completed);
        MaterialButton btnCancelled = findViewById(R.id.btn_cancelled);
        MaterialButton btnReturnRefund = findViewById(R.id.btn_returnrefund);
        Button btnConfirm = findViewById(R.id.btn_confirm); // kiểu Button thường

        // Reset tất cả về style mặc định
        resetButtonStyle(btnConfirm);
        resetButtonStyle(btnToPickup);
        resetButtonStyle(btnReceived);
        resetButtonStyle(btnCompleted);
        resetButtonStyle(btnCancelled);
        resetButtonStyle(btnReturnRefund);

        // Xác định nút tương ứng với trạng thái và làm nổi bật
        switch (selectedStatus) {
            case "Pending Payment":
                highlightButton(btnConfirm);
                break;
            case "Shipped":
                highlightButton(btnToPickup);
                break;
            case "Out for Delivery":
                highlightButton(btnReceived);
                break;
            case "Completed":
                highlightButton(btnCompleted);
                break;
            case "Cancelled":
                highlightButton(btnCancelled);
                break;
            case "Return/Refund":
                highlightButton(btnReturnRefund);
                break;
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
