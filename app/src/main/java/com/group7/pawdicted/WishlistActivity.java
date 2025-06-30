package com.group7.pawdicted;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private FirebaseFirestore db;
    private ProductAdapter productAdapter;
    private static final String PREF_NAME = "RecentlyViewedPrefs";
    private static final String PREF_WISHLIST = "wishlist_products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_wishlist);

        // Initialize empty state layout
        emptyStateLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_nothing, null);
        TextView txtNothing = emptyStateLayout.findViewById(R.id.txt_nothing);
        txtNothing.setText(R.string.title_nothing_love); // Set to "You haven't anything in your heart yet"

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this);
        recyclerView.setAdapter(productAdapter);

        // Back button
        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        loadWishlistProducts();
    }

    private void loadWishlistProducts() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String wishlistProducts = prefs.getString(PREF_WISHLIST, "");
        if (wishlistProducts.isEmpty()) {
            showEmptyState();
            return;
        }

        List<String> productIds = new ArrayList<>();
        for (String id : wishlistProducts.split(",")) {
            if (!id.isEmpty()) {
                productIds.add(id);
            }
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
        productAdapter.updateItems(new ArrayList<Object>(products)); // Cast to List<Object>
    }
}