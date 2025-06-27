package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.adapters.PopularSearchAdapter;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.adapters.RecentSearchAdapter;
import com.group7.pawdicted.mobile.models.PopularSearch;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.RecentSearch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final String PREFS_RECENT_SEARCHES = "recent_searches";
    private static final int MAX_RECENT_SEARCHES = 10;

    // Views
    private EditText edtSearchInput;
    private ImageView btnBack, btnClearSearch;
    private TextView btnSearch, txtResultsCount, btnClearHistory;
    private LinearLayout layoutSuggestions, layoutSearchResults, layoutNoResults, layoutLoading, layoutRecentSearches;
    private RecyclerView rvPopularSearches, rvRecentSearches, rvSearchResults;

    // Adapters
    private PopularSearchAdapter popularSearchAdapter;
    private RecentSearchAdapter recentSearchAdapter;
    private ProductAdapter searchResultsAdapter;

    // Data
    private List<PopularSearch> popularSearches = new ArrayList<>();
    private List<RecentSearch> recentSearches = new ArrayList<>();
    private List<Product> searchResults = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;

    // Search handler
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500; // 500ms delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initFirebase();
        setupRecyclerViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        edtSearchInput = findViewById(R.id.edtSearchInput);
        btnBack = findViewById(R.id.btnBack);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnSearch = findViewById(R.id.btnSearch);
        txtResultsCount = findViewById(R.id.txtResultsCount);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        layoutSuggestions = findViewById(R.id.layoutSuggestions);
        layoutSearchResults = findViewById(R.id.layoutSearchResults);
        layoutNoResults = findViewById(R.id.layoutNoResults);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutRecentSearches = findViewById(R.id.layoutRecentSearches);

        rvPopularSearches = findViewById(R.id.rvPopularSearches);
        rvRecentSearches = findViewById(R.id.rvRecentSearches);
        rvSearchResults = findViewById(R.id.rvSearchResults);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerViews() {
        // Popular searches
        popularSearchAdapter = new PopularSearchAdapter(this, popularSearches);
        rvPopularSearches.setLayoutManager(new LinearLayoutManager(this));
        rvPopularSearches.setAdapter(popularSearchAdapter);

        // Recent searches
        recentSearchAdapter = new RecentSearchAdapter(this, recentSearches);
        rvRecentSearches.setLayoutManager(new LinearLayoutManager(this));
        rvRecentSearches.setAdapter(recentSearchAdapter);

        // Search results
        searchResultsAdapter = new ProductAdapter(this);
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Search input changes
        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // Show/hide clear button
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                if (query.isEmpty()) {
                    showSuggestions();
                } else {
                    // Delayed search
                    searchRunnable = () -> performSearch(query);
                    searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search button
        btnClearSearch.setOnClickListener(v -> {
            edtSearchInput.setText("");
            edtSearchInput.requestFocus();
        });

        // Search button
        btnSearch.setOnClickListener(v -> {
            String query = edtSearchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
                saveRecentSearch(query);
            }
        });

        // Clear history button
        btnClearHistory.setOnClickListener(v -> clearRecentSearches());

        // Popular search click
        popularSearchAdapter.setOnPopularSearchClickListener(popularSearch -> {
            edtSearchInput.setText(popularSearch.getSearchTerm());
            performSearch(popularSearch.getSearchTerm());
            saveRecentSearch(popularSearch.getSearchTerm());
        });

        // Recent search listeners
        recentSearchAdapter.setOnRecentSearchListener(new RecentSearchAdapter.OnRecentSearchListener() {
            @Override
            public void onRecentSearchClick(RecentSearch recentSearch) {
                edtSearchInput.setText(recentSearch.getSearchTerm());
                performSearch(recentSearch.getSearchTerm());
            }

            @Override
            public void onRemoveRecentSearch(RecentSearch recentSearch, int position) {
                recentSearches.remove(position);
                recentSearchAdapter.removeItem(position);
                saveRecentSearchesToPrefs();
                updateRecentSearchesVisibility();
            }
        });
    }

    private void loadData() {
        loadPopularSearches();
        loadRecentSearches();

        // Auto focus on search input
        edtSearchInput.requestFocus();
    }

    private void loadPopularSearches() {
        // Create some sample popular searches or load from Firestore
        popularSearches.clear();

        // You can either hardcode popular searches or calculate them from Firestore
        List<String> popularTerms = List.of("Thức ăn cho mèo", "Pate cho chó", "Đồ chơi cho thú cưng",
                "Cát vệ sinh", "Vitamin cho thú cưng");

        for (String term : popularTerms) {
            // Get a sample product image for each term
            getProductImageForTerm(term, (imageUrl, count) -> {
                PopularSearch popularSearch = new PopularSearch(term, imageUrl, count);
                popularSearches.add(popularSearch);
                popularSearchAdapter.notifyDataSetChanged();
            });
        }
    }

    private void getProductImageForTerm(String searchTerm, ProductImageCallback callback) {
        db.collection("products")
                .whereGreaterThanOrEqualTo("product_name", searchTerm)
                .whereLessThanOrEqualTo("product_name", searchTerm + "\uf8ff")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String imageUrl = ""; // Khởi tạo mặc định là empty string

                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String productImage = doc.getString("product_image");
                        // Kiểm tra null và gán giá trị an toàn
                        imageUrl = (productImage != null) ? productImage : "";
                    }

                    // Biến final để sử dụng trong lambda
                    final String finalImageUrl = imageUrl;

                    // Get total count for this search term
                    db.collection("products")
                            .whereGreaterThanOrEqualTo("product_name", searchTerm)
                            .whereLessThanOrEqualTo("product_name", searchTerm + "\uf8ff")
                            .get()
                            .addOnSuccessListener(countQuery -> {
                                callback.onResult(finalImageUrl, countQuery.size());
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error counting products for term: " + searchTerm, e);
                                callback.onResult(finalImageUrl, 0);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading product image for term: " + searchTerm, e);
                    callback.onResult("", 0);
                });
    }

    private interface ProductImageCallback {
        void onResult(String imageUrl, int count);
    }

    private void loadRecentSearches() {
        SharedPreferences prefs = getSharedPreferences(PREFS_RECENT_SEARCHES, MODE_PRIVATE);
        String json = prefs.getString("searches", "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<RecentSearch>>(){}.getType();
            List<RecentSearch> loaded = gson.fromJson(json, type);
            if (loaded != null) {
                recentSearches.clear();
                recentSearches.addAll(loaded);
                recentSearchAdapter.notifyDataSetChanged();
            }
        }

        updateRecentSearchesVisibility();
    }

    private void saveRecentSearch(String searchTerm) {
        // Remove if already exists
        recentSearches.removeIf(search -> search.getSearchTerm().equals(searchTerm));

        // Add to beginning
        recentSearches.add(0, new RecentSearch(searchTerm, System.currentTimeMillis()));

        // Keep only max items
        if (recentSearches.size() > MAX_RECENT_SEARCHES) {
            recentSearches = recentSearches.subList(0, MAX_RECENT_SEARCHES);
        }

        saveRecentSearchesToPrefs();
        recentSearchAdapter.notifyDataSetChanged();
        updateRecentSearchesVisibility();
    }

    private void saveRecentSearchesToPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_RECENT_SEARCHES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(recentSearches);
        prefs.edit().putString("searches", json).apply();
    }

    private void clearRecentSearches() {
        recentSearches.clear();
        recentSearchAdapter.clearAll();
        saveRecentSearchesToPrefs();
        updateRecentSearchesVisibility();
    }

    private void updateRecentSearchesVisibility() {
        layoutRecentSearches.setVisibility(recentSearches.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) return;

        showLoading();

        // Search in Firestore
        db.collection("products")
                .whereGreaterThanOrEqualTo("product_name", query)
                .whereLessThanOrEqualTo("product_name", query + "\uf8ff")
                .orderBy("product_name")
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    searchResults.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                searchResults.add(product);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product: " + document.getId(), e);
                        }
                    }

                    // Also search in description
                    searchInDescription(query);

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching products", e);
                    showNoResults();
                });
    }

    private void searchInDescription(String query) {
        db.collection("products")
                .whereGreaterThanOrEqualTo("description", query)
                .whereLessThanOrEqualTo("description", query + "\uf8ff")
                .limit(25)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            if (product != null && !containsProduct(product)) {
                                searchResults.add(product);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product from description search: " + document.getId(), e);
                        }
                    }

                    showSearchResults();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching in description", e);
                    showSearchResults(); // Show results from name search even if description search fails
                });
    }

    private boolean containsProduct(Product newProduct) {
        for (Product existing : searchResults) {
            if (existing.getProduct_id().equals(newProduct.getProduct_id())) {
                return true;
            }
        }
        return false;
    }

    private void showSuggestions() {
        layoutSuggestions.setVisibility(View.VISIBLE);
        layoutSearchResults.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
    }

    private void showLoading() {
        layoutSuggestions.setVisibility(View.GONE);
        layoutSearchResults.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
    }

    private void showSearchResults() {
        layoutSuggestions.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);

        if (searchResults.isEmpty()) {
            showNoResults();
        } else {
            layoutSearchResults.setVisibility(View.VISIBLE);
            layoutNoResults.setVisibility(View.GONE);

            txtResultsCount.setText("Tìm thấy " + searchResults.size() + " kết quả");

            // Convert to Object list for adapter
            List<Object> items = new ArrayList<>(searchResults);
            searchResultsAdapter.updateItems(items);
        }
    }

    private void showNoResults() {
        layoutSuggestions.setVisibility(View.GONE);
        layoutSearchResults.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}