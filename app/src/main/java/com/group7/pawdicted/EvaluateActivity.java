package com.group7.pawdicted;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluateActivity extends AppCompatActivity {
    ImageView btn_back;
    TextView txt_send;
    LinearLayout productsLayout;
    FirebaseFirestore db;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);

        addViews();
        addEvents();

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("order_id");  // Nhận orderId từ Intent

        // Kiểm tra nếu orderId hợp lệ, sau đó tải thông tin sản phẩm
        if (orderId != null && !orderId.isEmpty()) {
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addViews() {
        btn_back = findViewById(R.id.btnBack);
        txt_send = findViewById(R.id.txt_send);
        productsLayout = findViewById(R.id.products_layout);  // Layout chứa các sản phẩm đánh giá
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
        txt_send.setOnClickListener(v -> sendEvaluation());
    }

    private void loadOrderDetails(String orderId) {
        // Lấy thông tin đơn hàng từ Firestore theo orderId
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String orderItemId = document.getString("order_item_id");
                            loadOrderItems(orderItemId);  // Lấy thông tin sản phẩm của đơn hàng
                        } else {
                            Toast.makeText(this, "No such order!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOrderItems(String orderItemId) {
        db.collection("order_items").document(orderItemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Duyệt qua các sản phẩm trong đơn hàng
                            for (String key : document.getData().keySet()) {
                                if (key.startsWith("product")) {
                                    String productName = document.getString(key + ".product_name");
                                    String productImage = document.getString(key + ".product_image");
                                    // Thêm sản phẩm vào layout
                                    addProductToLayout(productName, productImage);
                                }
                            }
                        } else {
                            Toast.makeText(this, "No products found in order", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load order items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProductToLayout(String productName, String productImage) {
        // Nạp layout từ evaluate_item_layout.xml
        LinearLayout productLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.evaluate_item_layout, null);

        // Lấy các phần tử trong layout
        TextView productTextView = productLayout.findViewById(R.id.txt_product_name);
        RatingBar ratingBar = productLayout.findViewById(R.id.ratingBar_product);
        ImageView productImageView = productLayout.findViewById(R.id.img_product);

        // Cập nhật tên sản phẩm và ảnh
        productTextView.setText(productName);
        Glide.with(this).load(productImage).into(productImageView);

        // Thêm layout vào LinearLayout chứa các sản phẩm
        productsLayout.addView(productLayout);
    }

    private void sendEvaluation() {
        // Lấy thông tin đánh giá từ RatingBar và bình luận
        List<RatingBar> ratingBars = getRatingBarsFromLayout();
        EditText etComment = findViewById(R.id.edt_comment);
        String comment = etComment.getText().toString();

        if (ratingBars.isEmpty()) {
            Toast.makeText(this, "Please provide ratings for the products", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy order_item_id từ Intent (đảm bảo order_item_id được truyền đến Activity này)
        String orderItemId = getIntent().getStringExtra("order_item_id");

        if (orderItemId == null || orderItemId.isEmpty()) {
            Toast.makeText(this, "Order Item ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật đánh giá và bình luận cho từng sản phẩm
        for (int i = 0; i < ratingBars.size(); i++) {
            float rating = ratingBars.get(i).getRating();
            String productName = "Product " + (i + 1);  // Điều chỉnh theo tên sản phẩm thực tế nếu có

            // Chuẩn bị dữ liệu đánh giá
            Map<String, Object> productEvaluation = new HashMap<>();
            productEvaluation.put("comment", comment);
            productEvaluation.put("rating", rating);
            productEvaluation.put("product_id", "FT000" + (i + 1));  // Thực tế sử dụng product_id từ Firestore
            productEvaluation.put("quantity", 3);  // Sử dụng số lượng thực tế
            productEvaluation.put("total_cost_of_goods", 9000000);  // Sử dụng tổng chi phí thực tế

            // Cập nhật tài liệu Firestore cho sản phẩm tương ứng
            db.collection("order_items")
                    .document(orderItemId)  // Tài liệu đại diện cho order_item
                    .update("product" + (i + 1), productEvaluation)  // Cập nhật sản phẩm cụ thể (product1, product2, ...)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Evaluation sent successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Đóng Activity hoặc điều hướng tới màn hình khác
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to send evaluation", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private List<RatingBar> getRatingBarsFromLayout() {
        // Trả về danh sách các RatingBar từ layout sản phẩm
        List<RatingBar> ratingBars = new ArrayList<>();
        for (int i = 0; i < productsLayout.getChildCount(); i++) {
            LinearLayout productLayout = (LinearLayout) productsLayout.getChildAt(i);
            RatingBar ratingBar = productLayout.findViewById(R.id.ratingBar_product);
            ratingBars.add(ratingBar);
        }
        return ratingBars;
    }
}

