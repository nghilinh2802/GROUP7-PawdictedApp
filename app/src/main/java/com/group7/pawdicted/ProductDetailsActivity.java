package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.DividerItemDecoration;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.adapters.ReviewAdapter;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;
import com.group7.pawdicted.mobile.models.ListReview;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.Recommendation;
import com.group7.pawdicted.mobile.models.Review;
import com.group7.pawdicted.mobile.models.Variant;
import com.group7.pawdicted.mobile.services.CartStorageHelper;
import com.group7.pawdicted.mobile.services.SimilarService;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProductImage, imgHeart;
    private TextView txtDiscountPrice, txtSoldQuantity, txtProductPrice, txtDiscountRate;
    private TextView txtProductName, txtProductRatingCount, txtProductDescription;
    private TextView txtAverageRating, txtRatingCount, txtNoVariants;
    private RatingBar productRatingBar, productRatingBar2;
    private ImageButton btnChat;
    private Button btnAddToCart, btnBuyNow;
    private LinearLayout lvVariation, viewAllDescription, viewAllRating;
    private RecyclerView rvReview, rvYouMay;
    private LinearLayout relatedProductContainer;
    private HorizontalScrollView horizontalScrollView;
    private String selectedVariantId;
    private String defaultProductImage;
    private Product currentProduct;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SimilarService similarService;
    private static final String PREF_NAME = "RecentlyViewedPrefs";
    private static final String PREF_PRODUCTS = "viewed_products";
    private static final String PREF_WISHLIST = "wishlist_products";

    // FLASHSALE VARIABLES
    private boolean isFlashsale = false;
    private int flashsaleDiscountRate = 0;
    private double flashsalePrice = 0;
    private String flashsaleId = "";
    private String flashsaleName = "";
    private long flashsaleEndTime = 0;
    private double finalPrice = 0;

    private ListReview listReview;

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
        similarService = new SimilarService();

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
        imgHeart = findViewById(R.id.img_heart);
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
        viewAllDescription = findViewById(R.id.view_all_description);
        viewAllRating = findViewById(R.id.view_all_rating);
        rvReview = findViewById(R.id.rv_review);
        relatedProductContainer = findViewById(R.id.lv_related_product);
        horizontalScrollView = findViewById(R.id.horizontalScrollView_related);
        rvYouMay = findViewById(R.id.rv_you_may);

        // Initialize review data
        listReview = new ListReview();

        // Heart icon click listener
        imgHeart.setOnClickListener(v -> toggleWishlist());

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

        // Hide view all rating since RatingActivity is no longer needed
        viewAllRating.setVisibility(View.GONE);

        // THÊM ADD TO CART LOGIC
        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) return;

            db.collection("variants").whereEqualTo("product_id", currentProduct.getProduct_id()).get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<String> variantNames = new ArrayList<>();
                        Map<String, Integer> variantPriceMap = new HashMap<>();
                        Map<String, String> variantImageMap = new HashMap<>();
                        String selectedVariantName = "Default";
                        String imageUrl = currentProduct.getProduct_image();

                        // SỬ DỤNG finalPrice ĐÃ ĐƯỢC TÍNH
                        double selectedPrice = finalPrice;
                        Log.d("Cart", "Using finalPrice: " + finalPrice + " (isFlashsale: " + isFlashsale + ")");

                        // Lấy dữ liệu từ Firestore
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Variant var = doc.toObject(Variant.class);
                            variantNames.add(var.getVariant_name());

                            // Tính giá cho variant options
                            int variantPrice;
                            if (isFlashsale) {
                                variantPrice = (int) (var.getVariant_price() * (1 - flashsaleDiscountRate / 100.0));
                            } else {
                                variantPrice = (int) (var.getVariant_price() * (1 - var.getVariant_discount() / 100.0));
                            }
                            variantPriceMap.put(var.getVariant_name(), variantPrice);

                            variantImageMap.put(var.getVariant_name(),
                                    var.getVariant_image() != null ? var.getVariant_image() : currentProduct.getProduct_image());

                            if (var.getVariant_id().equals(selectedVariantId)) {
                                selectedVariantName = var.getVariant_name();
                                imageUrl = var.getVariant_image() != null ? var.getVariant_image() : imageUrl;
                                selectedPrice = variantPrice;
                            }
                        }

                        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
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
                            newItem.optionImageUrls = variantImageMap;
                            CartManager.getInstance().addToCart(newItem);
                        }

                        String customerId = CartManager.getInstance().getCustomerId();
                        if (customerId != null && !customerId.isEmpty()) {
                            CartStorageHelper.saveCart(this, customerId, CartManager.getInstance().getCartItems());
                        }

                        int toastMessage = isFlashsale ? R.string.title_noti_add_cart_flashsale : R.string.title_noti_add_cart;
                        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void toggleWishlist() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) return;

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String wishlistProducts = prefs.getString(PREF_WISHLIST, "");
        List<String> wishlist = new ArrayList<>();
        if (!wishlistProducts.isEmpty()) {
            for (String id : wishlistProducts.split(",")) {
                if (!id.isEmpty()) {
                    wishlist.add(id);
                }
            }
        }

        if (wishlist.contains(productId)) {
            // Remove from wishlist
            wishlist.remove(productId);
            imgHeart.setImageResource(R.mipmap.ic_heart);
            Toast.makeText(this, R.string.title_noti_remove_wishlist, Toast.LENGTH_SHORT).show();
        } else {
            // Add to wishlist
            wishlist.add(productId);
            imgHeart.setImageResource(R.mipmap.ic_red_heart);
            Toast.makeText(this, R.string.title_noti_add_wishlist, Toast.LENGTH_SHORT).show();
        }

        editor.putString(PREF_WISHLIST, String.join(",", wishlist));
        editor.apply();
    }

    private void updateHeartIcon() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) return;

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String wishlistProducts = prefs.getString(PREF_WISHLIST, "");
        if (wishlistProducts.contains(productId)) {
            imgHeart.setImageResource(R.mipmap.ic_red_heart);
        } else {
            imgHeart.setImageResource(R.mipmap.ic_heart);
        }
    }

    private void saveViewToPreferences(String productId) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String existingViews = prefs.getString(PREF_PRODUCTS, "");
        StringBuilder newViews = new StringBuilder();
        String[] viewEntries = existingViews.isEmpty() ? new String[0] : existingViews.split(",");
        boolean updated = false;

        for (String entry : viewEntries) {
            if (entry.startsWith(productId + ":")) {
                newViews.append(productId).append(":").append(System.currentTimeMillis()).append(",");
                updated = true;
            } else if (!entry.isEmpty()) {
                newViews.append(entry).append(",");
            }
        }
        if (!updated) {
            newViews.append(productId).append(":").append(System.currentTimeMillis()).append(",");
        }

        String[] updatedEntries = newViews.toString().split(",");
        if (updatedEntries.length > 50) {
            newViews = new StringBuilder();
            for (int i = 0; i < 50; i++) {
                newViews.append(updatedEntries[i]).append(",");
            }
        }

        editor.putString(PREF_PRODUCTS, newViews.toString());
        editor.apply();
    }

    private void loadProductDetails() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            finish();
            return;
        }

        String customerId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Update Firestore also_view array and SharedPreferences
        if (customerId != null) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("also_view", com.google.firebase.firestore.FieldValue.arrayUnion(customerId));
            db.collection("products").document(productId)
                    .update(updateData)
                    .addOnSuccessListener(aVoid -> Log.d("ProductDetailsActivity", "Added customer to also_view"))
                    .addOnFailureListener(e -> Log.e("ProductDetailsActivity", "Error updating also_view", e));

            saveViewToPreferences(productId);
        }

        // Load product details
        db.collection("products").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            currentProduct = product;
                            defaultProductImage = product.getProduct_image();
                            displayProductDetails(product, null);
                            loadVariations(product.getVariant_id(), productId);
                            loadReviews();
                            loadRelatedProducts(productId);
                            loadYouMayAlsoLike();
                            updateHeartIcon();
                        }
                    } else {
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Error loading product details", e);
                    finish();
                });
    }

    private void displayProductDetails(Product product, Variant variant) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        if (variant != null) {
            if (isFlashsale) {
                double flashsalePrice = variant.getVariant_price() * (1 - flashsaleDiscountRate / 100.0);
                txtDiscountPrice.setText(formatter.format(flashsalePrice));
                txtProductPrice.setText(formatter.format(variant.getVariant_price()));
                txtDiscountRate.setText("  -" + flashsaleDiscountRate + "%  ");
                txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtDiscountPrice.setTextColor(getColor(R.color.main_color));
                finalPrice = flashsalePrice;
            } else {
                double discountPrice = variant.getVariant_price() * (1 - variant.getVariant_discount() / 100.0);
                txtDiscountPrice.setText(formatter.format(discountPrice));
                txtProductPrice.setText(formatter.format(variant.getVariant_price()));
                txtDiscountRate.setText(variant.getVariant_discount() > 0 ? "  -" + variant.getVariant_discount() + "%  " : "");
                if (variant.getVariant_discount() > 0) {
                    txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                finalPrice = discountPrice;
            }

            txtSoldQuantity.setText(variant.getVariant_sold_quantity() + " sold  ");
            productRatingBar.setRating((float) variant.getVariant_rating());
            productRatingBar2.setRating((float) variant.getVariant_rating());
            txtAverageRating.setText(String.format("%.1f", variant.getVariant_rating()));
            txtRatingCount.setText("(" + variant.getVariant_rating_number() + " Reviews)");
            txtProductRatingCount.setText(variant.getVariant_rating_number() + " Reviews");
            loadImage(variant.getVariant_image() != null ? variant.getVariant_image() : defaultProductImage);
        } else {
            if (isFlashsale) {
                double flashsalePrice = product.getPrice() * (1 - flashsaleDiscountRate / 100.0);
                txtDiscountPrice.setText(formatter.format(flashsalePrice));
                txtProductPrice.setText(formatter.format(product.getPrice()));
                txtDiscountRate.setText("  -" + flashsaleDiscountRate + "%  ");
                txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtDiscountPrice.setTextColor(getColor(R.color.main_color));
                finalPrice = flashsalePrice;
            } else {
                double discountPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
                txtDiscountPrice.setText(formatter.format(discountPrice));
                txtProductPrice.setText(formatter.format(product.getPrice()));
                txtDiscountRate.setText(product.getDiscount() > 0 ? "  -" + product.getDiscount() + "%  " : "");
                if (product.getDiscount() > 0) {
                    txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                finalPrice = discountPrice;
            }

            defaultProductImage = product.getProduct_image();
            txtSoldQuantity.setText(product.getSold_quantity() + " sold  ");
            productRatingBar.setRating((float) product.getAverage_rating());
            productRatingBar2.setRating((float) product.getAverage_rating());
            txtAverageRating.setText(String.format("%.1f", product.getAverage_rating()));
            txtRatingCount.setText("(" + product.getRating_number() + " Reviews)");
            txtProductRatingCount.setText(product.getRating_number() + " Reviews");
            loadImage(defaultProductImage);
        }

        Log.d("ProductDetailsActivity", "💰 Final price set to: " + finalPrice);
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

            if (isFlashsale) {
                Log.d("ProductDetailsActivity", "=== FLASHSALE MODE ACTIVATED ===");
                Log.d("ProductDetailsActivity", "Flashsale: " + flashsaleName);
                Log.d("ProductDetailsActivity", "Discount: " + flashsaleDiscountRate + "%");
            }
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
            txtDiscountRate.setText("  -" + flashsaleDiscountRate + "%  ");
            txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtDiscountPrice.setTextColor(getColor(R.color.main_color));
        }
    }

    private void loadReviews() {
        db.collection("reviews")
                .whereEqualTo("product_id", currentProduct.getProduct_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> productReviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String reviewId = document.getString("review_id");
                        String customerId = document.getString("customer_id");
                        Long ratingLong = document.getLong("rating");
                        int rating = ratingLong != null ? ratingLong.intValue() : 0;
                        String productId = document.getString("product_id");
                        String productVariation = document.getString("product_variation");
                        String comment = document.getString("comment");
                        Long timestamp = document.getLong("timestamp");

                        Review review = new Review(reviewId, customerId, rating, productId, productVariation, comment, timestamp);
                        productReviews.add(review);
                    }

                    if (rvReview != null) {
                        rvReview.setLayoutManager(new LinearLayoutManager(this));
                        rvReview.setAdapter(new ReviewAdapter(this, productReviews));
                        rvReview.addItemDecoration(new DividerItemDecoration(this, 1, android.R.color.darker_gray, 16));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Error fetching reviews: " + e.getMessage());
                    if (rvReview != null) {
                        rvReview.setAdapter(new ReviewAdapter(this, new ArrayList<>()));
                    }
                });
    }

    private void loadRelatedProducts(String productId) {
        if (relatedProductContainer == null) {
            Log.e("ProductDetailsActivity", "relatedProductContainer is null");
            return;
        }

        similarService.getSimilarProducts(productId, 3, new SimilarService.SimilarCallback() {
            @Override
            public void onSuccess(List<Recommendation> recommendations) {
                List<String> productIds = new ArrayList<>();
                for (Recommendation rec : recommendations) {
                    productIds.add(rec.getProductId());
                }

                db.collection("products")
                        .whereIn("product_id", productIds)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            relatedProductContainer.removeAllViews();
                            LayoutInflater inflater = LayoutInflater.from(ProductDetailsActivity.this);
                            DecimalFormat formatter = new DecimalFormat("#,###đ");

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Product product = document.toObject(Product.class);
                                String imageUrl = product.getProduct_image();
                                for (Recommendation rec : recommendations) {
                                    if (rec.getProductId().equals(product.getProduct_id())) {
                                        imageUrl = rec.getImageUrl();
                                        break;
                                    }
                                }

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
                                    productContainer.setBackground(ContextCompat.getDrawable(ProductDetailsActivity.this, R.drawable.gray_rounded_background));
                                }

                                if (imgChildCateProduct != null) {
                                    Glide.with(ProductDetailsActivity.this).load(imageUrl).into(imgChildCateProduct);
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
                                    txtChildCateSold.setText(product.getSold_quantity() + " sold  ");
                                }

                                itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("product_id", product.getProduct_id());
                                    startActivity(intent);
                                });

                                relatedProductContainer.addView(itemView);
                            }
                        })
                        .addOnFailureListener(e -> {
                            relatedProductContainer.removeAllViews();
                            runOnUiThread(() -> Toast.makeText(ProductDetailsActivity.this, R.string.title_noti_related_products_error, Toast.LENGTH_LONG).show());
                        });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(ProductDetailsActivity.this, R.string.title_noti_related_products_error, Toast.LENGTH_LONG).show());
                relatedProductContainer.removeAllViews();
            }
        });
    }

    private void loadYouMayAlsoLike() {
        if (rvYouMay == null) {
            Log.e("ProductDetailsActivity", "rvYouMay is null");
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
                    Log.e("ProductDetailsActivity", "Error fetching top-rated products: " + e.getMessage());
                    rvYouMay.setAdapter(null);
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}