package com.group7.pawdicted;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.connectors.SQLiteConnector;

import java.text.NumberFormat;
import java.util.Locale;

public class RefundReturnDetailActivity extends AppCompatActivity {
    TextView tvRefundAmount;
    TextView tvDetailRefundAmount, tvDetailRequestedAt, tvDetailRefundTo, tvDetailApproved, tvDetailProcessed, tvDetailReason;
    LinearLayout llRefundProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_refund_return_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        addViews();

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) {
            loadRefundInfo(orderId);
        }
    }

    private void addViews() {
        tvDetailRefundAmount = findViewById(R.id.tv_detail_refund_amount);
        tvDetailRequestedAt  = findViewById(R.id.tv_detail_requested_at);
        tvDetailRefundTo     = findViewById(R.id.tv_detail_refund_to);
        tvDetailApproved     = findViewById(R.id.tv_detail_approved);
        tvDetailProcessed    = findViewById(R.id.tv_detail_processed);
        tvDetailReason       = findViewById(R.id.tv_detail_reason);
        llRefundProducts     = findViewById(R.id.ll_refund_products);
        tvRefundAmount = findViewById(R.id.tv_refund_amount);

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }

    private void loadRefundInfo(String orderId) {
        SQLiteConnector dbHelper = new SQLiteConnector(this);
        SQLiteDatabase db = dbHelper.getDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT i.product_name, i.quantity, o.refund_amount, o.refund_reason, " +
                        "o.refund_requested_at, o.date_approved, o.date_processed " +
                        "FROM order_items i JOIN orders o ON i.order_id = o.order_id " +
                        "WHERE i.order_id = ?",
                new String[]{orderId}
        );

        llRefundProducts.removeAllViews();

        if (cursor.moveToFirst()) {
            int refundAmount     = cursor.getInt(2);
            String refundReason  = cursor.getString(3);
            String requestedAt   = cursor.getString(4);
            String dateApproved  = cursor.getString(5);
            String dateProcessed = cursor.getString(6);

            tvDetailRefundAmount.setText(formatCurrency(refundAmount) + " ₫");
            tvRefundAmount.setText(formatCurrency(refundAmount) + " ₫");
            tvDetailRequestedAt.setText(requestedAt);
            tvDetailRefundTo.setText("Linked Smart Banking");
            tvDetailApproved.setText(dateApproved);
            tvDetailProcessed.setText(dateProcessed);
            tvDetailReason.setText(refundReason);

            do {
                String productName = cursor.getString(0);
                int quantity = cursor.getInt(1);

                LinearLayout item = new LinearLayout(this);
                item.setOrientation(LinearLayout.HORIZONTAL);
                item.setPadding(0, 16, 0, 16);

                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(100, 100);
                img.setLayoutParams(imgParams);

                TextView tvInfo = new TextView(this);
                tvInfo.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                tvInfo.setPadding(16, 0, 0, 0);
                tvInfo.setText(Html.fromHtml("<b>" + productName + "</b><br>x" + quantity));
                tvInfo.setTextColor(Color.BLACK);

                item.addView(img);
                item.addView(tvInfo);

                Cursor imgCursor = db.rawQuery(
                        "SELECT ImageLink FROM products WHERE product_name = ? LIMIT 1",
                        new String[]{productName}
                );
                if (imgCursor.moveToFirst()) {
                    String imageUrl = imgCursor.getString(0);
                    Glide.with(this).load(imageUrl).into(img);
                }
                imgCursor.close();

                llRefundProducts.addView(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
