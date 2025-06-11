package com.group7.pawdicted;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.connectors.SQLiteConnector;

import java.text.NumberFormat;
import java.util.Locale;

public class CancellationDetailActivity extends AppCompatActivity {
    ImageView btn_back;
    Button btn_buy_again;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Đặt layout cho activity này
        setContentView(R.layout.activity_cancellation_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

        // Nhận order_id từ Intent
        orderId = getIntent().getStringExtra("order_id");

        if (orderId != null && !orderId.isEmpty()) {
            loadCancellationDetails(orderId);
        }
    }

    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
        btn_buy_again = findViewById(R.id.btn_buy_again);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btn_buy_again.setOnClickListener(v -> {
            // TODO: Thêm logic cho nút "Mua lại"
            // Ví dụ: điều hướng đến trang chủ hoặc trang sản phẩm
            Toast.makeText(this, "Chức năng Mua lại đang được phát triển!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCancellationDetails(String orderId) {
        SQLiteConnector dbHelper = new SQLiteConnector(this);
        SQLiteDatabase db = dbHelper.getDatabase();

        loadCommonOrderInfo(db, orderId);

        Cursor cancelCursor = db.rawQuery(
                "SELECT o.cancel_reason, o.cancel_requested_by, o.cancel_requested_at, o.payment_method, o.order_code " +
                        "FROM orders o " +
                        "WHERE o.order_id = ? AND o.cancel_requested_by IS NOT NULL " +
                        "LIMIT 1",
                new String[]{orderId}
        );

        if (cancelCursor.moveToFirst()) {
            String reason = cancelCursor.getString(0);
            String cancelledBy = cancelCursor.getString(1);
            String cancellationTime = cancelCursor.getString(2);
            String paymentMethod = cancelCursor.getString(3);
            String orderCode = cancelCursor.getString(4);

            ((TextView) findViewById(R.id.tv_cancellation_reason)).setText(reason != null ? reason : "No reason provided");
            ((TextView) findViewById(R.id.tv_cancelled_by)).setText(cancelledBy != null ? cancelledBy : "N/A");
            ((TextView) findViewById(R.id.tv_cancellation_time)).setText(cancellationTime);
            ((TextView) findViewById(R.id.tv_payment_method)).setText(paymentMethod);
            ((TextView) findViewById(R.id.tv_order_code)).setText(orderCode);
        }
        cancelCursor.close();
        db.close();
    }

    private void loadCommonOrderInfo(SQLiteDatabase db, String orderId) {
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
        int totalCostOfGoods = 0;
        while (productCursor.moveToNext()) {
            totalCostOfGoods += productCursor.getInt(2);
            // ... (code thêm sản phẩm vào view giữ nguyên như cũ)
            String productName = productCursor.getString(0);
            int quantity = productCursor.getInt(1);
            int totalCost = productCursor.getInt(2);
            int unitPrice = (quantity > 0) ? totalCost / quantity : 0;

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

        // Tổng tiền
        Cursor orderCursor = db.rawQuery("SELECT shipping_fee FROM orders WHERE order_id = ?", new String[]{orderId});
        if (orderCursor.moveToFirst()) {
            int shipping = orderCursor.getInt(0);
            ((TextView) findViewById(R.id.tv_total_cost)).setText(formatCurrency(totalCostOfGoods) + " ₫");
            ((TextView) findViewById(R.id.tv_shipping_fee)).setText(formatCurrency(shipping) + " ₫");
            ((TextView) findViewById(R.id.tv_final_price)).setText(formatCurrency(totalCostOfGoods + shipping) + " ₫");
        }
        orderCursor.close();
    }

    // Hàm định dạng tiền tệ
    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}