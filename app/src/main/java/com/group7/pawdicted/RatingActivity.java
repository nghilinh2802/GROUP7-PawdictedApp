package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Paint;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.DividerItemDecoration;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.adapters.ReviewAdapter;
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
    private FirebaseFirestore db;
    private String productId;
    private Product currentProduct;

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        productRatingBarAgain = findViewById(R.id.product_rating_bar_again);
        txtAverageRating = findViewById(R.id.txt_average_rating);
        txtRatingCount = findViewById(R.id.txt_rating_count);
        rvReview = findViewById(R.id.rv_review);
        relatedProductContainer = findViewById(R.id.lv_related_product);
        imgScrollRelated = findViewById(R.id.img_scroll_related_products);
        horizontalScrollView = findViewById(R.id.horizontalScrollView_related);
        rvYouMay = findViewById(R.id.rv_you_may);

        // Initialize review data (still using mock data for reviews)
        listReview = new ListReview();
        listReview.generate_sample_dataset();

        // Get product ID from intent
        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Log.e("RatingActivity", "No product_id received");
            finish();
            return;
        }

        // Fetch product details from Firestore
        fetchProductDetails();

        // Scroll button listener
        if (horizontalScrollView != null && imgScrollRelated != null) {
            imgScrollRelated.setOnClickListener(v -> horizontalScrollView.smoothScrollBy(150, 0));
        }
    }

    private void fetchProductDetails() {
        db.collection("products").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentProduct = documentSnapshot.toObject(Product.class);
                        if (currentProduct != null) {
                            displayProductDetails();
                            loadReviews();
                            loadRelatedProducts(currentProduct.getCategory_id());
                            loadYouMayAlsoLike();
                        } else {
                            Log.e("RatingActivity", "Failed to convert product data for ID: " + productId);
                            finish();
                        }
                    } else {
                        Log.e("RatingActivity", "Product not found for ID: " + productId);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RatingActivity", "Error fetching product data: " + e.getMessage());
                    finish();
                });
    }

    private void displayProductDetails() {
        if (productRatingBarAgain != null) {
            productRatingBarAgain.setRating((float) currentProduct.getAverage_rating());
        }
        if (txtAverageRating != null) {
            txtAverageRating.setText(String.format("%.1f", currentProduct.getAverage_rating()));
        }
        if (txtRatingCount != null) {
            txtRatingCount.setText("(" + currentProduct.getRating_number() + " Đánh giá)");
        }
    }

    private void loadReviews() {
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
    }

    private void loadRelatedProducts(String categoryId) {
        if (relatedProductContainer == null) {
            Log.e("RatingActivity", "relatedProductContainer is null");
            return;
        }

        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> relatedProducts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        if (!product.getProduct_id().equals(productId)) {
                            relatedProducts.add(product);
                        }
                    }

                    // Toggle visibility of img_scroll_related_products based on relatedProducts size
                    if (imgScrollRelated != null) {
                        imgScrollRelated.setVisibility(relatedProducts.size() > 2 ? View.VISIBLE : View.GONE);
                    }

                    relatedProductContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);
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
                            txtChildCateProductDiscount.setText(product.getDiscount() > 0 ? "-" + product.getDiscount() + "%" : "");
                            txtChildCateProductDiscount.setVisibility(product.getDiscount() > 0 ? View.VISIBLE : View.GONE);
                        }
                        if (txtChildCateOriginalPrice != null) {
                            txtChildCateOriginalPrice.setText(formatter.format(product.getPrice()));
                            if (product.getDiscount() > 0) {
                                txtChildCateOriginalPrice.setPaintFlags(txtChildCateOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                txtChildCateOriginalPrice.setPaintFlags(txtChildCateOriginalPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }
                        if (txtChildCateSold != null) {
                            txtChildCateSold.setText(product.getSold_quantity() + " sold");
                        }

                        itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(RatingActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("product_id", product.getProduct_id());
                            startActivity(intent);
                        });

                        relatedProductContainer.addView(itemView);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RatingActivity", "Error fetching related products: " + e.getMessage());
                    relatedProductContainer.removeAllViews();
                    if (imgScrollRelated != null) {
                        imgScrollRelated.setVisibility(View.GONE);
                    }
                });
    }

    private void loadYouMayAlsoLike() {
        if (rvYouMay == null) {
            Log.e("RatingActivity", "rvYouMay is null");
            return;
        }

        db.collection("products")
                .orderBy("average_rating", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> topProducts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        topProducts.add(product);
                    }

                    rvYouMay.setLayoutManager(new GridLayoutManager(this, 2));
                    ProductAdapter productAdapter = new ProductAdapter(this);
                    productAdapter.updateItems(new ArrayList<>(topProducts));
                    rvYouMay.setAdapter(productAdapter);

                    rvYouMay.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                            outRect.set(1, 8, 1, 8);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("RatingActivity", "Error fetching top-rated products: " + e.getMessage());
                    rvYouMay.setAdapter(null);
                });
    }
}