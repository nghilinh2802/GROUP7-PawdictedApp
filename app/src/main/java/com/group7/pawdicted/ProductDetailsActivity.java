package com.group7.pawdicted;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProductImage;
    private TextView txtDiscountPrice, txtSoldQuantity, txtProductPrice, txtDiscountRate;
    private TextView txtProductName, txtProductRatingCount, txtProductDescription;
    private TextView txtAverageRating, txtRatingCount;
    private android.widget.RatingBar productRatingBar, productRatingBar2;
    private ImageButton btnChat;
    private Button btnAddToCart, btnBuyNow;
    private ListProduct listProduct;

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
        txtDiscountRate.setText("-" + (int)product.getDiscount() + "%");

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

        Log.d("ProductDetailsActivity", "Loaded product: " + product.getProduct_name());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}