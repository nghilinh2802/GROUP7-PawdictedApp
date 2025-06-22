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
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.ListVariant;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.Variant;

import java.text.DecimalFormat;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProductImage;
    private TextView txtDiscountPrice, txtSoldQuantity, txtProductPrice, txtDiscountRate;
    private TextView txtProductName, txtProductRatingCount, txtProductDescription;
    private TextView txtAverageRating, txtRatingCount, txtNoVariants;
    private android.widget.RatingBar productRatingBar, productRatingBar2;
    private ImageButton btnChat;
    private Button btnAddToCart, btnBuyNow;
    private LinearLayout lvVariation, viewAllDescription;
    private LinearLayout viewAllRating;

    private ListProduct listProduct;
    private ListVariant listVariant;
    private String selectedVariantId;
    private String defaultProductImage;

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

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
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

        // Thiết lập click cho view_all_description
        viewAllDescription.setOnClickListener(v -> {
            String productId = getIntent().getStringExtra("product_id");
            Product product = null;
            for (Product p : listProduct.getProducts()) {
                if (p.getProduct_id().equals(productId)) {
                    product = p;
                    break;
                }
            }
            if (product != null) {
                Intent intent = new Intent(ProductDetailsActivity.this, ProductDescriptionActivity.class);
                intent.putExtra("product_id", productId);
                intent.putExtra("product_name", product.getProduct_name());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("details", product.getDetails());
                startActivity(intent);
            } else {
                Log.e("ProductDetailsActivity", "Không tìm thấy sản phẩm với ID: " + productId);
            }
        });

        viewAllRating.setOnClickListener(v -> {
            String productId = getIntent().getStringExtra("product_id");
            Intent intent = new Intent(ProductDetailsActivity.this, RatingActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });

        listProduct = new ListProduct();
        listProduct.generate_sample_dataset();
        listVariant = new ListVariant();
        listVariant.generate_sample_dataset();
    }

    private void loadProductDetails() {
        String productId = getIntent().getStringExtra("product_id");
        Log.d("ProductDetailsActivity", "Received product_id: " + (productId != null ? productId : "null"));
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

        Log.d("ProductDetailsActivity", "Product: " + product.getProduct_name() + ", Image URL: " + product.getProduct_image());
        defaultProductImage = product.getProduct_image();

        loadImage(defaultProductImage, product);

        DecimalFormat formatter = new DecimalFormat("#,###đ");
        double discountPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
        txtDiscountPrice.setText(formatter.format(discountPrice));
        txtProductPrice.setText(formatter.format(product.getPrice()));
        // Thêm gạch ngang nếu có giảm giá
        if (product.getDiscount() > 0) {
            txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        txtSoldQuantity.setText(product.getSold_quantity() + " sold  ");
        txtDiscountRate.setText(" -" + (int)product.getDiscount() + "% ");
        txtProductName.setText(product.getProduct_name());
        productRatingBar.setRating((float) product.getAverage_rating());
        productRatingBar2.setRating((float) product.getAverage_rating());
        txtProductRatingCount.setText(product.getRating_number() + " Đánh giá");
        txtAverageRating.setText(String.format("%.1f", product.getAverage_rating()));
        txtRatingCount.setText("(" + product.getRating_number() + " Đánh giá)");
        txtProductDescription.setText(product.getDescription());

        List<String> variantIds = product.getVariant_id();
        Log.d("ProductDetailsActivity", "Variant IDs from product: " + (variantIds != null ? variantIds.toString() : "null"));
        loadVariations(variantIds, productId);
    }

    private void loadImage(String imageUrl, Product product) {
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

        if (productId == null) {
            Log.e("ProductDetailsActivity", "productId is null, cannot load variations");
            txtNoVariants.setVisibility(View.VISIBLE);
            return;
        }
        if (variantIds == null || variantIds.isEmpty()) {
            Log.w("ProductDetailsActivity", "No variants for product ID: " + productId);
            txtNoVariants.setVisibility(View.VISIBLE);
            return;
        }

        List<Variant> variants = listVariant.getVariantsByProductId(productId);
        Log.d("ProductDetailsActivity", "Variants found for productId " + productId + ": " + (variants != null ? variants.size() : 0));
        if (variants == null || variants.isEmpty()) {
            Log.w("ProductDetailsActivity", "No variants found for product ID: " + productId);
            txtNoVariants.setVisibility(View.VISIBLE);
            return;
        }

        txtNoVariants.setVisibility(View.GONE);
        selectedVariantId = variantIds.get(0);
        Variant firstVariant = null;
        for (Variant v : variants) {
            if (v != null && v.getVariant_id() != null && v.getVariant_id().equals(selectedVariantId)) {
                firstVariant = v;
                break;
            }
        }

        for (Variant variant : variants) {
            if (variant == null || variant.getVariant_id() == null) {
                Log.w("ProductDetailsActivity", "Skipping null variant or variant_id");
                continue;
            }
            Log.d("ProductDetailsActivity", "Processing variant: " + variant.getVariant_id() + ", name: " + variant.getVariant_name());
            if (!variantIds.contains(variant.getVariant_id())) {
                Log.w("ProductDetailsActivity", "Variant " + variant.getVariant_id() + " not in variantIds, skipping");
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
                Log.d("ProductDetailsActivity", "Selected variant: " + variant.getVariant_name() + " (" + selectedVariantId + ")");
                loadImage(variant.getVariant_image() != null ? variant.getVariant_image() : defaultProductImage, null);
                for (int j = 0; j < lvVariation.getChildCount(); j++) {
                    TextView tv = (TextView) lvVariation.getChildAt(j);
                    updateVariationStyle(tv, tv.getText().toString().equals(variant.getVariant_name()));
                }
            });

            lvVariation.addView(variationTextView);
            Log.d("ProductDetailsActivity", "Added variant: " + variant.getVariant_name());
        }

        if (firstVariant != null) {
            loadImage(firstVariant.getVariant_image() != null ? firstVariant.getVariant_image() : defaultProductImage, null);
        } else if (defaultProductImage != null) {
            loadImage(defaultProductImage, null);
        } else {
            Log.w("ProductDetailsActivity", "No image to load for default or first variant");
        }
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