package com.group7.pawdicted;

import android.graphics.Color;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProductImage;
    private TextView txtDiscountPrice, txtSoldQuantity, txtProductPrice, txtDiscountRate;
    private TextView txtProductName, txtProductRatingCount, txtProductDescription;
    private TextView txtAverageRating, txtRatingCount;
    private android.widget.RatingBar productRatingBar, productRatingBar2;
    private ImageButton btnChat;
    private Button btnAddToCart, btnBuyNow;
    private LinearLayout lvVariation;
    private ListProduct listProduct;
    private String selectedVariantId; // Store selected variant ID
    private String selectedVariantName; // Store selected variant name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);

        // Fix View ID
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Clear Glide cache for debugging
        Glide.get(this).clearMemory();
        new Thread(() -> Glide.get(this).clearDiskCache()).start();

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
        productRatingBar2 = findViewById(R.id.product_rating_bar_2);
        txtAverageRating = findViewById(R.id.txt_avarage_rating);
        txtRatingCount = findViewById(R.id.txt_rating_count);
        btnChat = findViewById(R.id.btnChat);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        lvVariation = findViewById(R.id.lvVariation); // Initialize variation layout

        listProduct = new ListProduct();
        listProduct.generate_sample_dataset();
    }

    private void loadProductDetails() {
        String productId = getIntent().getStringExtra("product_id");
        Log.d("ProductDetailsActivity", "Received product_id: " + productId);
        if (productId == null) {
            Log.e("ProductDetailsActivity", "No product_id received");
            finish();
            return;
        }

        Product product = null;
        for (Product p : listProduct.getProducts()) {
            if (p.getProduct_id().equals(productId)) {
                product = p;
                break;
            }
        }

        if (product == null) {
            Log.e("ProductDetailsActivity", "Product not found for ID: " + productId);
            finish();
            return;
        }

        // Log product details
        Log.d("ProductDetailsActivity", "Product: " + product.getProduct_name() + ", Image URL: " + product.getProduct_image());

        // Load image with Glide
        Product finalProduct = product;
        Glide.with(this)
                .load(product.getProduct_image())
                .placeholder(R.mipmap.ic_ascend_arrows)
                .error(R.mipmap.ic_ascend_arrows)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        Log.e("ProductDetailsActivity", "Glide load failed for URL: " + finalProduct.getProduct_image(), e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        Log.d("ProductDetailsActivity", "Glide load success for URL: " + finalProduct.getProduct_image());
                        return false;
                    }
                })
                .into(imgProductImage);

        // Format prices
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        double discountPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
        txtDiscountPrice.setText(formatter.format(discountPrice));
        txtProductPrice.setText(formatter.format(product.getPrice()));

        // Sold quantity
        txtSoldQuantity.setText(product.getSold_quantity() + " sold  ");

        // Discount rate
        txtDiscountRate.setText(" -" + (int)product.getDiscount() + "% ");

        // Product name
        txtProductName.setText(product.getProduct_name());

        // Ratings
        productRatingBar.setRating((float) product.getAverage_rating());
        productRatingBar2.setRating((float) product.getAverage_rating());
        txtProductRatingCount.setText(product.getRating_number() + " Đánh giá");
        txtAverageRating.setText(String.format("%.1f", product.getAverage_rating()));
        txtRatingCount.setText("(" + product.getRating_number() + " Đánh giá)");

        // Description
        txtProductDescription.setText(product.getDescription());

        // Load variations
        List<String> variantIds = product.getVariant_id();
        List<String> variantNames = product.getVariant_name();
        Log.d("ProductDetailsActivity", "Variant IDs: " + variantIds + ", Variant Names: " + variantNames);
        loadVariations(variantIds, variantNames);

        Log.d("ProductDetailsActivity", "Loaded product: " + product.getProduct_name());
    }
    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void loadVariations(List<String> variantIds, List<String> variantNames) {
        // Clear existing variations
        lvVariation.removeAllViews();

        // Check for invalid or empty variant data
        if (variantIds == null || variantNames == null || variantIds.isEmpty() || variantIds.size() != variantNames.size()) {
            Log.w("ProductDetailsActivity", "Invalid or empty variant data");
            return;
        }

        // Set first variation as selected by default
        selectedVariantId = variantIds.get(0);
        selectedVariantName = variantNames.get(0);

        // Create a TextView for each variation
        for (int i = 0; i < variantIds.size(); i++) {
            String currentVariantId = variantIds.get(i);
            String currentVariantName = variantNames.get(i);

            TextView variationTextView = new TextView(this);
            variationTextView.setText(currentVariantName);
            variationTextView.setTextSize(14); // 14sp
            variationTextView.setGravity(android.view.Gravity.CENTER);

            // Set padding: 20dp horizontal, 4dp vertical
            int paddingHorizontal = dpToPx(20);
            int paddingVertical = dpToPx(4);
            variationTextView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

            // Set layout parameters with margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = dpToPx(10); // 10dp margin as per reference
            params.setMargins(margin, dpToPx(5), margin, dpToPx(5)); // Left: 10dp, Top: 5dp, Right: 10dp, Bottom: 5dp
            variationTextView.setLayoutParams(params);

            // Apply style based on selection
            updateVariationStyle(variationTextView, currentVariantName.equals(selectedVariantName));

            // Set click listener
            variationTextView.setOnClickListener(v -> {
                // Update selected variant
                selectedVariantId = currentVariantId;
                selectedVariantName = currentVariantName;
                Log.d("ProductDetailsActivity", "Selected variant: " + selectedVariantName + " (" + selectedVariantId + ")");

                // Update styles for all variations
                for (int j = 0; j < lvVariation.getChildCount(); j++) {
                    TextView tv = (TextView) lvVariation.getChildAt(j);
                    updateVariationStyle(tv, tv.getText().toString().equals(selectedVariantName));
                }
            });

            // Add to layout
            lvVariation.addView(variationTextView);
        }
    }

    private void updateVariationStyle(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.variation_button_red);
            textView.setTextColor(getColor(R.color.main_color));
        } else {
            textView.setBackgroundResource(R.drawable.variation_button_grey);
            textView.setTextColor(getColor(R.color.black));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}