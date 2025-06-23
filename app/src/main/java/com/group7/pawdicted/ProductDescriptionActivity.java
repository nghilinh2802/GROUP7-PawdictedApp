package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductDescriptionActivity extends AppCompatActivity {

    private TextView txtDescriptionName, txtDescription;
    private TableLayout tableDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_description);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập nút back
        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        // Khởi tạo views
        txtDescriptionName = findViewById(R.id.txt_description_name);
        txtDescription = findViewById(R.id.txt_description);
        tableDetails = findViewById(R.id.table_details);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String productId = intent.getStringExtra("product_id");
        String productName = intent.getStringExtra("product_name");
        String description = intent.getStringExtra("description");
        String details = intent.getStringExtra("details");

        // Điền dữ liệu vào views
        if (productName != null) {
            txtDescriptionName.setText(productName);
        } else {
            Log.w("ProductDescriptionActivity", "productName is null");
            txtDescriptionName.setText("Không có tên sản phẩm");
        }

        if (description != null) {
            txtDescription.setText(description);
        } else {
            Log.w("ProductDescriptionActivity", "description is null");
            txtDescription.setText("Không có mô tả");
        }

        // Tạo bảng cho details
        if (details != null && !details.isEmpty()) {
            populateDetailsTable(details);
        } else {
            Log.w("ProductDescriptionActivity", "details is null or empty");
            TableRow row = new TableRow(this);
            TextView textView = new TextView(this, null, 0, R.style.TableTextViewNormal);
            textView.setText("Không có chi tiết sản phẩm");
            row.addView(textView);
            tableDetails.addView(row);
        }
    }

    private void populateDetailsTable(String details) {
        // Lấy chiều rộng màn hình để giới hạn maxWidth
        int maxWidth = getResources().getDisplayMetrics().widthPixels - dpToPx(32); // Trừ padding 16dp mỗi bên

        // Chia details thành các dòng
        String[] lines = details.split("\n");
        for (String line : lines) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if (line.contains(":")) {
                // Hàng có dấu hai chấm: tạo 2 ô
                String[] parts = line.split(":", 2); // Chia thành tối đa 2 phần
                TextView leftCell = new TextView(this, null, 0, R.style.TableTextViewBold);
                TextView rightCell = new TextView(this, null, 0, R.style.TableTextViewNormal);

                // Điều chỉnh chiều rộng: cột trái 40%, cột phải 60%
                leftCell.setMaxWidth((int) (maxWidth * 0.4)); // 40% chiều rộng bảng
                rightCell.setMaxWidth((int) (maxWidth * 0.6)); // 60% chiều rộng bảng

                leftCell.setText(parts[0].trim());
                rightCell.setText(parts.length > 1 ? parts[1].trim() : "");

                row.addView(leftCell);
                row.addView(rightCell);
            } else {
                // Hàng không có dấu hai chấm: tạo 1 ô span 2 cột
                TextView cell = new TextView(this, null, 0, R.style.TableTextViewNormal);
                cell.setMaxWidth(maxWidth); // Giới hạn chiều rộng bằng cả bảng
                cell.setText(line.trim());

                // Thiết lập span 2 cột
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                params.span = 2; // Span qua 2 cột
                cell.setLayoutParams(params);

                row.addView(cell);
            }

            // Thêm hàng vào bảng
            tableDetails.addView(row);

            // Thêm đường phân cách giữa các hàng
            View divider = new View(this);
            divider.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            tableDetails.addView(divider);
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}