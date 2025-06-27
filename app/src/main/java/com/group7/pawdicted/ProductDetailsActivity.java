package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.Variant;
import com.group7.pawdicted.mobile.services.CartStorageHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private FirebaseAuth mAuth;
    private boolean isFlashsale = false;
    private int flashsaleDiscountRate = 0;
    private double flashsalePrice = 0;
    private String flashsaleId = "";
    private String flashsaleName = "";
    private long flashsaleEndTime = 0;

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
        mAuth = FirebaseAuth.getInstance();
        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        initViews();
        receiveFlashsaleData();
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
                                    Intent intent = new Intent(this, ProductDescriptionActivity.class);
                                    intent.putExtra("product_id", productId);
                                    intent.putExtra("product_name", product.getProduct_name());
                                    intent.putExtra("description", product.getDescription());
                                    intent.putExtra("details", product.getDetails());
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

        viewAllRating.setOnClickListener(v -> {
            String productId = getIntent().getStringExtra("product_id");
            Intent intent = new Intent(this, RatingActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) return;

            db.collection("variants").whereEqualTo("product_id", currentProduct.getProduct_id()).get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<String> variantNames = new ArrayList<>();
                        Map<String, Integer> variantPriceMap = new HashMap<>();
                        String selectedVariantName = "Default";
                        String imageUrl = currentProduct.getProduct_image();
                        double selectedPrice = currentProduct.getPrice() * (1 - currentProduct.getDiscount() / 100.0);

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Variant var = doc.toObject(Variant.class);
                            variantNames.add(var.getVariant_name());
                            int variantPrice = (int) (var.getVariant_price() * (1 - var.getVariant_discount() / 100.0));
                            variantPriceMap.put(var.getVariant_name(), variantPrice);

                            if (var.getVariant_id().equals(selectedVariantId)) {
                                selectedVariantName = var.getVariant_name();
                                imageUrl = (var.getVariant_image() != null) ? var.getVariant_image() : imageUrl;
                                selectedPrice = variantPrice;
                            }
                        }

                        List<CartItem> cartItems = CartManager.getInstance().getCartItems(); // ✅ dùng getInstance
                        boolean alreadyExists = false;
                        for (CartItem item : cartItems) {
                            if (item.productId.equals(currentProduct.getProduct_id()) &&
                                    item.selectedOption.equals(selectedVariantName)) {
                                item.quantity += 1;
                                alreadyExists = true;
                                break;
                            }
                        }

                        if (!alreadyExists) {
                            CartItem newItem = new CartItem(
                                    currentProduct.getProduct_id(),
                                    currentProduct.getProduct_name(),
                                    (int) selectedPrice,
                                    imageUrl,
                                    variantNames,
                                    selectedVariantName
                            );
                            newItem.optionPrices = variantPriceMap;
                            CartManager.getInstance().addToCart(newItem);
                        }

                        // Lưu theo customer_id nếu cần
                        String customerId = CartManager.getInstance().getCustomerId(); // đảm bảo đã set khi login
                        if (customerId != null && !customerId.isEmpty()) {
                            CartStorageHelper.saveCart(this, customerId, CartManager.getInstance().getCartItems());
                        }

                        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void loadProductDetails() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            finish();
            return;
        }

        db.collection("products").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            currentProduct = product;
                            defaultProductImage = product.getProduct_image();
                            displayProductDetails(product, null);
                            loadVariations(product.getVariant_id(), productId);
                        }
                    } else {
                        finish();
                    }
                });
    }


    private void displayProductDetails(Product product, Variant variant) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        if (variant != null) {
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
            if (variant.getVariant_discount() > 0) {
                txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (isFlashsale) {
                displayFlashsalePrice(variant.getVariant_price());
            }
        } else {
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
            if (product.getDiscount() > 0) {
                txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (isFlashsale) {
                displayFlashsalePrice(product.getPrice());
            }
        }

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
                    Variant firstVariant = variants.get(0);

                    for (Variant variant : variants) {
                        TextView variationTextView = new TextView(this);
                        variationTextView.setText(variant.getVariant_name());
                        variationTextView.setTextSize(14);
                        variationTextView.setGravity(Gravity.CENTER);
                        variationTextView.setPadding(dpToPx(20), dpToPx(4), dpToPx(20), dpToPx(4));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, dpToPx(5), dpToPx(10), dpToPx(5));
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

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void receiveFlashsaleData() {
        Intent intent = getIntent();
        if (intent != null) {
            isFlashsale = intent.getBooleanExtra("IS_FLASHSALE", false);
            flashsaleDiscountRate = intent.getIntExtra("FLASHSALE_DISCOUNT_RATE", 0);
            flashsaleId = intent.getStringExtra("FLASHSALE_ID");
            flashsaleName = intent.getStringExtra("FLASHSALE_NAME");
            flashsaleEndTime = intent.getLongExtra("FLASHSALE_END_TIME", 0);
        }
    }

    private void calculateFlashsalePrice(double originalPrice) {
        if (isFlashsale && flashsaleDiscountRate > 0) {
            flashsalePrice = originalPrice * (1 - flashsaleDiscountRate / 100.0);
        }
    }

    private void displayFlashsalePrice(double originalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        if (isFlashsale) {
            calculateFlashsalePrice(originalPrice);
            txtDiscountPrice.setText(formatter.format(flashsalePrice));
            txtProductPrice.setText(formatter.format(originalPrice));
            txtDiscountRate.setText("-" + flashsaleDiscountRate + "%");
            txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtDiscountPrice.setTextColor(getColor(R.color.main_color));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
