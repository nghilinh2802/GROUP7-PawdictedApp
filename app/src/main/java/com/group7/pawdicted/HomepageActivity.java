package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomepageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String customerId;
    private LinearLayout productList;
    private Map<String, Integer> layoutClickCounts;
    private SharedPreferences prefs;
    private Map<String, LinearLayout> layoutMap;
    private ProgressBar progressBar; // Add ProgressBar for loading
    private static final String PREFS_NAME = "LayoutPrefs";
    private static final String[] LAYOUT_IDS = {
            "layout_suggest", "layout_recommendation", "layout_ft", "layout_pc",
            "layout_toy", "layout_acc", "layout_ck", "layout_furniture"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayoutmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        productList = findViewById(R.id.productList);
        layoutClickCounts = new HashMap<>();
        layoutMap = new HashMap<>();
        progressBar = new ProgressBar(this); // Initialize ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        productList.addView(progressBar); // Add to layout temporarily

        // Adjust banner height dynamically
        adjustBannerHeight();

        initializeLayoutMap();
        loadLayoutClickCounts();

        // Get customer ID
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("customers")
                    .whereEqualTo("customer_email", user.getEmail())
                    .limit(1)
                    .get(Source.CACHE) // Try cache first for faster loading
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            customerId = snapshot.getDocuments().get(0).getId();
                            loadDynamicLayouts();
                        } else {
                            // Fallback to server if cache fails
                            db.collection("customers")
                                    .whereEqualTo("customer_email", user.getEmail())
                                    .limit(1)
                                    .get(Source.SERVER)
                                    .addOnSuccessListener(serverSnapshot -> {
                                        if (!serverSnapshot.isEmpty()) {
                                            customerId = serverSnapshot.getDocuments().get(0).getId();
                                            loadDynamicLayouts();
                                        } else {
                                            Toast.makeText(this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                                            customerId = null;
                                            loadDynamicLayouts();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        customerId = null;
                                        loadDynamicLayouts();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        customerId = null;
                        loadDynamicLayouts();
                    });
        } else {
            customerId = null;
            loadDynamicLayouts();
        }

        FooterManager footerManager = new FooterManager(this);
    }

    private void adjustBannerHeight() {
        ImageView banner = findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        banner.getLayoutParams().height = screenHeight / 3; // Set to 1/3 of screen height
        banner.requestLayout();
    }

    private void initializeLayoutMap() {
        for (String layoutId : LAYOUT_IDS) {
            int resId = getResources().getIdentifier(layoutId, "id", getPackageName());
            LinearLayout layout = findViewById(resId);
            if (layout != null) {
                layoutMap.put(layoutId, layout);
            } else {
                Toast.makeText(this, "Không tìm thấy layout: " + layoutId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadLayoutClickCounts() {
        for (String layoutId : LAYOUT_IDS) {
            layoutClickCounts.put(layoutId, prefs.getInt(layoutId, 0));
        }
    }

    private void saveLayoutClickCount(String layoutId) {
        int count = layoutClickCounts.getOrDefault(layoutId, 0) + 1;
        layoutClickCounts.put(layoutId, count);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(layoutId, count);
        editor.apply();
    }

    private void loadDynamicLayouts() {
        // Remove ProgressBar and clear existing layouts
        productList.removeAllViews();

        // Sort layouts by click count (descending)
        List<String> sortedLayoutIds = new ArrayList<>(Arrays.asList(LAYOUT_IDS));
        Collections.sort(sortedLayoutIds, (a, b) -> layoutClickCounts.getOrDefault(b, 0) - layoutClickCounts.getOrDefault(a, 0));

        // Re-add layouts in sorted order
        for (String layoutId : sortedLayoutIds) {
            LinearLayout layout = layoutMap.get(layoutId);
            if (layout != null) {
                productList.addView(layout);
                loadProductsForLayout(layoutId);
            } else {
                Toast.makeText(this, "Bỏ qua layout không tìm thấy: " + layoutId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductsForLayout(String layoutId) {
        LinearLayout productContainer = findViewById(
                getResources().getIdentifier("lv_" + layoutId.replace("layout_", "") + "_product", "id", getPackageName())
        );
        if (productContainer == null) {
            Toast.makeText(this, "Không tìm thấy container cho layout: " + layoutId, Toast.LENGTH_SHORT).show();
            return;
        }
        productContainer.removeAllViews();

        if (layoutId.equals("layout_suggest")) {
            loadTopSoldProducts(productContainer, 10);
        } else if (layoutId.equals("layout_recommendation")) {
            loadRandomProducts(productContainer, 10);
        } else {
            String categoryId = getCategoryIdForLayout(layoutId);
            loadCategoryProducts(productContainer, categoryId);
        }
    }

    private String getCategoryIdForLayout(String layoutId) {
        switch (layoutId) {
            case "layout_ft": return "FT";
            case "layout_pc": return "PC";
            case "layout_toy": return "TO";
            case "layout_acc": return "AC";
            case "layout_ck": return "CK";
            case "layout_furniture": return "FU";
            default: return "";
        }
    }

    private void loadTopSoldProducts(LinearLayout container, int limit) {
        db.collection("products")
                .orderBy("sold_quantity", Query.Direction.DESCENDING)
                .limit(limit)
                .get(Source.CACHE) // Try cache first
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        // Fallback to server
                        db.collection("products")
                                .orderBy("sold_quantity", Query.Direction.DESCENDING)
                                .limit(limit)
                                .get(Source.SERVER)
                                .addOnSuccessListener(serverSnapshot -> {
                                    for (DocumentSnapshot doc : serverSnapshot.getDocuments()) {
                                        addProductToContainer(container, doc, "layout_suggest");
                                    }
                                    progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi tải sản phẩm bán chạy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    } else {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            addProductToContainer(container, doc, "layout_suggest");
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sản phẩm bán chạy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void loadRandomProducts(LinearLayout container, int limit) {
        db.collection("products")
                .get(Source.CACHE)
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        db.collection("products")
                                .get(Source.SERVER)
                                .addOnSuccessListener(serverSnapshot -> {
                                    List<DocumentSnapshot> products = serverSnapshot.getDocuments();
                                    Collections.shuffle(products, new Random());
                                    for (int i = 0; i < Math.min(limit, products.size()); i++) {
                                        addProductToContainer(container, products.get(i), "layout_recommendation");
                                    }
                                    progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi tải sản phẩm đề xuất: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    } else {
                        List<DocumentSnapshot> products = snapshot.getDocuments();
                        Collections.shuffle(products, new Random());
                        for (int i = 0; i < Math.min(limit, products.size()); i++) {
                            addProductToContainer(container, products.get(i), "layout_recommendation");
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sản phẩm đề xuất: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void loadCategoryProducts(LinearLayout container, String categoryId) {
        List<DocumentSnapshot> selectedProducts = new ArrayList<>();
        if (customerId != null) {
            db.collection("products")
                    .whereEqualTo("category_id", "FT")
                    .whereArrayContains("also_view", customerId)
                    .get(Source.CACHE)
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.isEmpty()) {
                            db.collection("products")
                                    .whereEqualTo("category_id", "FT")
                                    .whereArrayContains("also_view", customerId)
                                    .get(Source.SERVER)
                                    .addOnSuccessListener(serverSnapshot -> {
                                        List<DocumentSnapshot> viewedNotBought = new ArrayList<>();
                                        for (DocumentSnapshot doc : serverSnapshot.getDocuments()) {
                                            List<String> alsoBuy = (List<String>) doc.get("also_buy");
                                            if (alsoBuy == null || !alsoBuy.contains(customerId)) {
                                                viewedNotBought.add(doc);
                                            }
                                        }
                                        for (int i = 0; i < Math.min(5, viewedNotBought.size()); i++) {
                                            selectedProducts.add(viewedNotBought.get(i));
                                        }
                                        loadRemainingCategoryProducts(container, categoryId, selectedProducts);
                                    })
                                    .addOnFailureListener(e -> {
                                        loadRemainingCategoryProducts(container, categoryId, selectedProducts);
                                    });
                        } else {
                            List<DocumentSnapshot> viewedNotBought = new ArrayList<>();
                            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                List<String> alsoBuy = (List<String>) doc.get("also_buy");
                                if (alsoBuy == null || !alsoBuy.contains(customerId)) {
                                    viewedNotBought.add(doc);
                                }
                            }
                            for (int i = 0; i < Math.min(5, viewedNotBought.size()); i++) {
                                selectedProducts.add(viewedNotBought.get(i));
                            }
                            loadRemainingCategoryProducts(container, categoryId, selectedProducts);
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadRemainingCategoryProducts(container, categoryId, selectedProducts);
                    });
        } else {
            loadRemainingCategoryProducts(container, categoryId, selectedProducts);
        }
    }

    private void loadRemainingCategoryProducts(LinearLayout container, String categoryId, List<DocumentSnapshot> selectedProducts) {
        int remaining = 10 - selectedProducts.size();
        if (remaining <= 0) {
            for (DocumentSnapshot doc : selectedProducts) {
                addProductToContainer(container, doc, "layout_" + categoryId.toLowerCase());
            }
            progressBar.setVisibility(View.GONE);
            return;
        }

        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .orderBy("sold_quantity", Query.Direction.DESCENDING)
                .limit(remaining)
                .get(Source.CACHE)
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        db.collection("products")
                                .whereEqualTo("category_id", categoryId)
                                .orderBy("sold_quantity", Query.Direction.DESCENDING)
                                .limit(remaining)
                                .get(Source.SERVER)
                                .addOnSuccessListener(serverSnapshot -> {
                                    for (DocumentSnapshot doc : serverSnapshot.getDocuments()) {
                                        if (!selectedProducts.contains(doc)) {
                                            selectedProducts.add(doc);
                                        }
                                    }
                                    for (DocumentSnapshot doc : selectedProducts) {
                                        addProductToContainer(container, doc, "layout_" + categoryId.toLowerCase());
                                    }
                                    progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi tải sản phẩm danh mục " + categoryId + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    for (DocumentSnapshot doc : selectedProducts) {
                                        addProductToContainer(container, doc, "layout_" + categoryId.toLowerCase());
                                    }
                                    progressBar.setVisibility(View.GONE);
                                });
                    } else {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            if (!selectedProducts.contains(doc)) {
                                selectedProducts.add(doc);
                            }
                        }
                        for (DocumentSnapshot doc : selectedProducts) {
                            addProductToContainer(container, doc, "layout_" + categoryId.toLowerCase());
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sản phẩm danh mục " + categoryId + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    for (DocumentSnapshot doc : selectedProducts) {
                        addProductToContainer(container, doc, "layout_" + categoryId.toLowerCase());
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void addProductToContainer(LinearLayout container, DocumentSnapshot doc, String layoutId) {
        View productView = LayoutInflater.from(this).inflate(R.layout.item_product, container, false);
        ImageView productImage = productView.findViewById(R.id.img_child_cate_product);
        TextView productName = productView.findViewById(R.id.txt_child_cate_product_name);
        TextView productPrice = productView.findViewById(R.id.txt_child_cate_product_price);
        TextView productDiscount = productView.findViewById(R.id.txt_child_cate_product_discount);
        TextView originalPrice = productView.findViewById(R.id.txt_child_cate_original_price);
        TextView soldQuantity = productView.findViewById(R.id.txt_child_cate_sold);
        RatingBar ratingBar = productView.findViewById(R.id.rating_bar);
        TextView ratingText = productView.findViewById(R.id.txt_rating);

        String imageUrl = doc.getString("product_image");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(productImage);
        } else {
            productImage.setImageResource(R.mipmap.ic_logo);
        }
        productName.setText(doc.getString("product_name") != null ? doc.getString("product_name") : "Không có tên");
        Double price = doc.getDouble("price");
        if (price == null) price = 0.0;
        Long discount = doc.getLong("discount");
        if (discount == null) discount = 0L;
        double discountedPrice = price * (1 - discount / 100.0);
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        productPrice.setText(formatter.format(discountedPrice) + "đ");
        productDiscount.setText("-" + discount + "%");
        originalPrice.setText(formatter.format(price) + "đ");
        Long sold = doc.getLong("sold_quantity");
        if (sold == null) sold = 0L;
        soldQuantity.setText(sold + " đã bán");
        Double rating = doc.getDouble("average_rating");
        if (rating == null) rating = 0.0;
        ratingBar.setRating(rating.floatValue());
        ratingText.setText(String.format(Locale.getDefault(), "%.1f", rating));

        productView.setOnClickListener(v -> {
            saveLayoutClickCount(layoutId);
            loadDynamicLayouts();
            Intent intent = new Intent(HomepageActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_id", doc.getId());
            startActivity(intent);
        });

        container.addView(productView);
    }

    public void open_blogs(View view) {
        Intent intent = new Intent(HomepageActivity.this, BlogActivity.class);
        startActivity(intent);
    }

    public void open_policy(View view) {
        Intent intent = new Intent(HomepageActivity.this, PolicynSecurityActivity.class);
        startActivity(intent);
    }

    public void open_faq(View view) {
        Intent intent = new Intent(HomepageActivity.this, FAQActivity.class);
        startActivity(intent);
    }

    public void open_category(View view) {
        Intent intent = new Intent(HomepageActivity.this, CategoryActivity.class);
        startActivity(intent);
    }

    public void open_newArrival(View view) {
        Intent intent = new Intent(HomepageActivity.this, NewArrivalActivity.class);
        startActivity(intent);
    }

    public void open_flashsale(View view) {
        Intent intent = new Intent(HomepageActivity.this, FlashSaleActivity.class);
        startActivity(intent);
    }

    public void open_chat(View view) {
        Intent intent = new Intent(HomepageActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    public void open_search(View view) {
        Intent intent=new Intent(HomepageActivity.this,SearchActivity.class);
        startActivity(intent);
    }
    public void open_cart(View view) {
        Intent intent=new Intent(HomepageActivity.this,CartActivity.class);
        startActivity(intent);
    }
}