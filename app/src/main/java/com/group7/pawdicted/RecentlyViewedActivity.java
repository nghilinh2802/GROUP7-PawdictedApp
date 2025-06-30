package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentlyViewedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProductAdapter productAdapter;
    private static final String PREF_NAME = "RecentlyViewedPrefs";
    private static final String PREF_PRODUCTS = "viewed_products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recently_viewed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycler_view_recently_viewed);

        // Initialize empty state layout
        emptyStateLayout = new LinearLayout(this);
        emptyStateLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyStateLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView emptyImage = new ImageView(this);
        emptyImage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyImage.setImageResource(R.mipmap.ic_dog);

        TextView emptyText = new TextView(this);
        emptyText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyText.setText(R.string.title_nothing_history);
        emptyText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyText.setTextSize(16);
        emptyText.setTypeface(emptyText.getTypeface(), android.graphics.Typeface.ITALIC);
        emptyText.setPadding(0, 16, 0, 0);

        emptyStateLayout.addView(emptyImage);
        emptyStateLayout.addView(emptyText);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this);
        recyclerView.setAdapter(productAdapter);

        // Back button
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        loadRecentlyViewedProducts();
    }

    private void loadRecentlyViewedProducts() {
        String customerId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (customerId == null) {
            showEmptyState();
            return;
        }

        // Read viewed products from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String viewedProductsStr = prefs.getString(PREF_PRODUCTS, "");
        if (viewedProductsStr.isEmpty()) {
            showEmptyState();
            return;
        }

        // Parse viewed products
        List<String[]> viewedProducts = new ArrayList<>();
        for (String entry : viewedProductsStr.split(",")) {
            if (!entry.isEmpty() && entry.contains(":")) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    viewedProducts.add(parts); // [productId, timestamp]
                }
            }
        }

        // Sort by timestamp (descending)
        Collections.sort(viewedProducts, (o1, o2) -> Long.compare(Long.parseLong(o2[1]), Long.parseLong(o1[1])));

        // Extract product IDs
        List<String> productIds = new ArrayList<>();
        for (String[] entry : viewedProducts) {
            productIds.add(entry[0]);
        }

        if (productIds.isEmpty()) {
            showEmptyState();
            return;
        }

        // Fetch product details from Firestore
        db.collection("products")
                .whereIn("product_id", productIds.subList(0, Math.min(productIds.size(), 10))) // Firestore 'in' limit
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        products.add(product);
                    }

                    // Sort products based on SharedPreferences order
                    List<Product> sortedProducts = new ArrayList<>();
                    for (String productId : productIds) {
                        for (Product product : products) {
                            if (product.getProduct_id().equals(productId)) {
                                sortedProducts.add(product);
                                break;
                            }
                        }
                    }

                    if (sortedProducts.isEmpty()) {
                        showEmptyState();
                    } else {
                        showProducts(sortedProducts);
                    }
                })
                .addOnFailureListener(e -> showEmptyState());
    }

    private void showEmptyState() {
        LinearLayout mainLayout = findViewById(R.id.main);
        mainLayout.removeView(recyclerView);
        if (emptyStateLayout.getParent() == null) {
            mainLayout.addView(emptyStateLayout);
        }
    }

    private void showProducts(List<Product> products) {
        LinearLayout mainLayout = findViewById(R.id.main);
        if (emptyStateLayout.getParent() != null) {
            mainLayout.removeView(emptyStateLayout);
        }
        if (recyclerView.getParent() == null) {
            mainLayout.addView(recyclerView);
        }
        productAdapter.updateItems(new ArrayList<>(products));
    }
}