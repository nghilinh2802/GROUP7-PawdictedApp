package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.Variant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProductImage;
    private TextView txtDiscountPrice, txtSoldQuantity, txtProductPrice, txtDiscountRate;
    private TextView txtProductName, txtProductRatingCount, txtProductDescription;
    private TextView txtAverageRating, txtRatingCount, txtNoVariants;
    private android.widget.RatingBar productRatingBar, productRatingBar2;
    private ImageButton btnChat;
    private Button btnAddToCart, btnBuyNow;
    private LinearLayout lvVariation, viewAllDescription, viewAllRating;
    private String selectedVariantId;
    private String defaultProductImage;
    private Product currentProduct;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        initViews();
        loadProductDetails();
    }

    private void initViews() {
        imgProductImage = findViewById(R.id.img_product_image);
        txtDiscountPrice = findViewById(R.id.txt_discount_price);
        txtSoldQuantity = findViewById(R.id.txt_sold_quantity);
        txtProductPrice = findViewById(R.id.txt_product_price);
        txtDiscountRate = findViewById(R.id.txt_discount_rate);
        txtProductName = findViewById(R.id.txt_product_name);
        productRatingBar = findViewById(R.id.product_rating_bar);
        txtProductRatingCount = findViewById(R.id.txt_product_rating_count);
        txtProductDescription = findViewById(R.id.txt_product_description);
        productRatingBar2 = findViewById(R.id.product_rating_bar_again);
        txtAverageRating = findViewById(R.id.txt_average_rating);
        txtRatingCount = findViewById(R.id.txt_rating_count);
        btnChat = findViewById(R.id.img_avatar);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        lvVariation = findViewById(R.id.lvVariation);
        txtNoVariants = findViewById(R.id.txt_no_variants);
        viewAllRating = findViewById(R.id.view_all_rating);
        viewAllDescription = findViewById(R.id.view_all_description);

        viewAllDescription.setOnClickListener(v -> {
            String productId = getIntent().getStringExtra("product_id");
            if (productId != null) {
                db.collection("products").document(productId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Product product = documentSnapshot.toObject(Product.class);
                                if (product != null) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, ProductDescriptionActivity.class);
                                    intent.putExtra("product_id", productId);
                                    intent.putExtra("product_name", product.getProduct_name());
                                    intent.putExtra("description", product.getDescription());
                                    intent.putExtra("details", product.getDetails());
                                    startActivity(intent);
                                }
                            } else {
                                Log.e("ProductDetailsActivity", "Product not found for ID: " + productId);
                            }
                        })
                        .addOnFailureListener(e -> Log.e("ProductDetailsActivity", "Error fetching product data: " + e.getMessage()));
            }
        });

        viewAllRating.setOnClickListener(v -> {
            String productId = getIntent().getStringExtra("product_id");
            Intent intent = new Intent(ProductDetailsActivity.this, RatingActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });
    }

    private void loadProductDetails() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Log.e("ProductDetailsActivity", "No product_id received");
            finish();
            return;
        }

        db.collection("products").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            currentProduct = product;
                            displayProductDetails(product, null);
                            loadVariations(product.getVariant_id(), productId);
                        } else {
                            Log.e("ProductDetailsActivity", "Failed to convert product data for ID: " + productId);
                            finish();
                        }
                    } else {
                        Log.e("ProductDetailsActivity", "Product not found for ID: " + productId);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Error fetching product data: " + e.getMessage());
                    finish();
                });
    }

    private void displayProductDetails(Product product, Variant variant) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");

        if (variant != null) {
            // Display variant-specific details
            double discountPrice = variant.getVariant_price() * (1 - variant.getVariant_discount() / 100.0);
            txtDiscountPrice.setText(formatter.format(discountPrice));
            txtProductPrice.setText(formatter.format(variant.getVariant_price()));
            txtDiscountRate.setText(variant.getVariant_discount() > 0 ? "-" + variant.getVariant_discount() + "%" : "");
            txtSoldQuantity.setText(variant.getVariant_sold_quantity() + " sold");
            productRatingBar.setRating((float) variant.getVariant_rating());
            productRatingBar2.setRating((float) variant.getVariant_rating());
            txtAverageRating.setText(String.format("%.1f", variant.getVariant_rating()));
            txtRatingCount.setText("(" + variant.getVariant_rating_number() + " Reviews)");
            txtProductRatingCount.setText(variant.getVariant_rating_number() + " Reviews");
            loadImage(variant.getVariant_image() != null ? variant.getVariant_image() : defaultProductImage);
        } else {
            // Display default product details
            defaultProductImage = product.getProduct_image();
            double discountPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
            txtDiscountPrice.setText(formatter.format(discountPrice));
            txtProductPrice.setText(formatter.format(product.getPrice()));
            txtDiscountRate.setText(product.getDiscount() > 0 ? "-" + product.getDiscount() + "%" : "");
            txtSoldQuantity.setText(product.getSold_quantity() + " sold");
            productRatingBar.setRating((float) product.getAverage_rating());
            productRatingBar2.setRating((float) product.getAverage_rating());
            txtAverageRating.setText(String.format("%.1f", product.getAverage_rating()));
            txtRatingCount.setText("(" + product.getRating_number() + " Reviews)");
            txtProductRatingCount.setText(product.getRating_number() + " Reviews");
            loadImage(defaultProductImage);
        }

        txtProductPrice.setPaintFlags(product.getDiscount() > 0 ? txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : txtProductPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        txtProductName.setText(product.getProduct_name());
        txtProductDescription.setText(product.getDescription());
    }

    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_ascend_arrows)
                .error(R.mipmap.ic_ascend_arrows)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProductImage);
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void loadVariations(List<String> variantIds, String productId) {
        lvVariation.removeAllViews();

        if (variantIds == null || variantIds.isEmpty()) {
            txtNoVariants.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("variants").whereEqualTo("product_id", productId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Variant> variants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Variant variant = document.toObject(Variant.class);
                        if (variant != null && variantIds.contains(variant.getVariant_id())) {
                            variants.add(variant);
                        }
                    }

                    if (variants.isEmpty()) {
                        txtNoVariants.setVisibility(View.VISIBLE);
                        return;
                    }

                    txtNoVariants.setVisibility(View.GONE);
                    selectedVariantId = variantIds.get(0);
                    Variant firstVariant = variants.stream()
                            .filter(v -> v.getVariant_id().equals(selectedVariantId))
                            .findFirst()
                            .orElse(null);

                    for (Variant variant : variants) {
                        if (variant == null || variant.getVariant_id() == null) {
                            continue;
                        }
                        TextView variationTextView = new TextView(this);
                        variationTextView.setText(variant.getVariant_name());
                        variationTextView.setTextSize(14);
                        variationTextView.setGravity(android.view.Gravity.CENTER);

                        int paddingHorizontal = dpToPx(20);
                        int paddingVertical = dpToPx(4);
                        variationTextView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        int margin = dpToPx(10);
                        params.setMargins(0, dpToPx(5), margin, dpToPx(5));
                        variationTextView.setLayoutParams(params);

                        updateVariationStyle(variationTextView, variant.getVariant_id().equals(selectedVariantId));

                        variationTextView.setOnClickListener(v -> {
                            selectedVariantId = variant.getVariant_id();
                            displayProductDetails(currentProduct, variant);
                            for (int j = 0; j < lvVariation.getChildCount(); j++) {
                                TextView tv = (TextView) lvVariation.getChildAt(j);
                                updateVariationStyle(tv, tv.getText().toString().equals(variant.getVariant_name()));
                            }
                        });

                        lvVariation.addView(variationTextView);
                    }

                    if (firstVariant != null) {
                        displayProductDetails(currentProduct, firstVariant);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Error fetching variants: " + e.getMessage());
                    txtNoVariants.setVisibility(View.VISIBLE);
                });
    }

    private void updateVariationStyle(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.red_rounded_background);
            textView.setTextColor(getColor(R.color.main_color));
        } else {
            textView.setBackgroundResource(R.drawable.gray_rounded_background);
            textView.setTextColor(getColor(R.color.black));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}