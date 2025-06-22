package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.adapters.DividerItemDecoration;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.adapters.ReviewAdapter;
import com.group7.pawdicted.mobile.connectors.ProductConnector;
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.ListReview;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.Review;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RatingBar productRatingBarAgain;
    private TextView txtAverageRating, txtRatingCount;
    private RecyclerView rvReview, rvYouMay;
    private ListReview listReview;
    private LinearLayout relatedProductContainer;
    private ImageView imgScrollRelated;
    private HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating);
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
        productRatingBarAgain = findViewById(R.id.product_rating_bar_again);
        txtAverageRating = findViewById(R.id.txt_average_rating);
        txtRatingCount = findViewById(R.id.txt_rating_count);
        rvReview = findViewById(R.id.rv_review);
        relatedProductContainer = findViewById(R.id.lv_related_product);
        imgScrollRelated = findViewById(R.id.img_scroll_related_products);
        horizontalScrollView = findViewById(R.id.horizontalScrollView_related);
        rvYouMay = findViewById(R.id.rv_you_may);

        // Khởi tạo dữ liệu
        listReview = new ListReview();
        listReview.generate_sample_dataset();

        String productId = getIntent().getStringExtra("product_id");
        ListProduct listProduct = new ListProduct();
        listProduct.generate_sample_dataset();
        Product product = null;
        for (Product p : listProduct.getProducts()) {
            if (p.getProduct_id().equals(productId)) {
                product = p;
                break;
            }
        }

        if (product == null) {
            Log.e("RatingActivity", "Không tìm thấy sản phẩm với ID: " + productId);
            finish();
            return;
        }

        // Thiết lập rating và số lượng đánh giá
        if (productRatingBarAgain != null) {
            productRatingBarAgain.setRating((float) product.getAverage_rating());
        }
        if (txtAverageRating != null) {
            txtAverageRating.setText(String.format("%.1f", product.getAverage_rating()));
        }
        if (txtRatingCount != null) {
            txtRatingCount.setText("(" + product.getRating_number() + " Đánh giá)");
        }

        // Tải danh sách đánh giá
        List<Review> productReviews = new ArrayList<>();
        for (Review review : listReview.getReviews()) {
            if (review.getProduct_id().equals(productId)) {
                productReviews.add(review);
            }
        }
        if (rvReview != null) {
            rvReview.setLayoutManager(new LinearLayoutManager(this));
            rvReview.setAdapter(new ReviewAdapter(this, productReviews));
            rvReview.addItemDecoration(new DividerItemDecoration(this, 1, android.R.color.darker_gray, 16));
        }

        // Tải sản phẩm liên quan
        if (relatedProductContainer != null) {
            loadRelatedProducts(product.getCategory_id());
        } else {
            Log.e("RatingActivity", "relatedProductContainer là null");
        }
        loadYouMayAlsoLike();

        // Listener cho nút cuộn
        if (horizontalScrollView != null && imgScrollRelated != null) {
            imgScrollRelated.setOnClickListener(v -> horizontalScrollView.smoothScrollBy(150, 0));
        }
    }

    private void loadRelatedProducts(String categoryId) {
        ProductConnector productConnector = new ProductConnector();
        LayoutInflater inflater = LayoutInflater.from(this);

        ArrayList<Product> relatedProducts = productConnector.get_products_by_category(categoryId);
        relatedProducts.removeIf(p -> p.getProduct_id().equals(getIntent().getStringExtra("product_id")));

        if (relatedProductContainer != null) {
            relatedProductContainer.removeAllViews();
            DecimalFormat formatter = new DecimalFormat("#,###đ");
            for (Product product : relatedProducts) {
                View itemView = inflater.inflate(R.layout.item_product, relatedProductContainer, false);
                ImageView imgChildCateProduct = itemView.findViewById(R.id.img_child_cate_product);
                TextView txtChildCateProductName = itemView.findViewById(R.id.txt_child_cate_product_name);
                RatingBar ratingBar = itemView.findViewById(R.id.rating_bar);
                TextView txtRating = itemView.findViewById(R.id.txt_rating);
                TextView txtChildCateProductPrice = itemView.findViewById(R.id.txt_child_cate_product_price);
                TextView txtChildCateProductDiscount = itemView.findViewById(R.id.txt_child_cate_product_discount);
                TextView txtChildCateOriginalPrice = itemView.findViewById(R.id.txt_child_cate_original_price);
                TextView txtChildCateSold = itemView.findViewById(R.id.txt_child_cate_sold);

                LinearLayout productContainer = itemView.findViewById(R.id.item_product_container);
                if (productContainer != null) {
                    productContainer.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rounded_background));
                }

                if (imgChildCateProduct != null) {
                    Glide.with(this).load(product.getProduct_image()).into(imgChildCateProduct);
                }
                if (txtChildCateProductName != null) {
                    txtChildCateProductName.setText(product.getProduct_name());
                }
                if (ratingBar != null) {
                    ratingBar.setRating((float) product.getAverage_rating());
                }
                if (txtRating != null) {
                    txtRating.setText(String.format("%.1f", product.getAverage_rating()));
                }
                double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
                if (txtChildCateProductPrice != null) {
                    txtChildCateProductPrice.setText(formatter.format(discountedPrice));
                }
                if (txtChildCateProductDiscount != null) {
                    txtChildCateProductDiscount.setText("-" + product.getDiscount() + "%");
                }
                if (txtChildCateOriginalPrice != null) {
                    txtChildCateOriginalPrice.setText(formatter.format(product.getPrice()));
                }
                if (txtChildCateSold != null) {
                    txtChildCateSold.setText(product.getSold_quantity() + " sold");
                }

                // Thêm sự kiện click để mở ProductDetailsActivity
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(RatingActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("product_id", product.getProduct_id());
                    startActivity(intent);
                });

                relatedProductContainer.addView(itemView);
            }
        }
    }

    private void loadYouMayAlsoLike() {
        ProductConnector productConnector = new ProductConnector();
        ArrayList<Product> allProducts = productConnector.get_all_products();
        allProducts.sort((p1, p2) -> Double.compare(p2.getAverage_rating(), p1.getAverage_rating()));
        List<Product> top6Products = allProducts.subList(0, Math.min(6, allProducts.size()));

        if (rvYouMay != null) {
            rvYouMay.setLayoutManager(new GridLayoutManager(this, 2));
            ProductAdapter productAdapter = new ProductAdapter(this);
            productAdapter.updateItems(new ArrayList<>(top6Products));
            rvYouMay.setAdapter(productAdapter);

            rvYouMay.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    outRect.set(1, 8, 1, 8);
                }
            });
        }
    }
}