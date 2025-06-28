package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.group7.pawdicted.mobile.models.ChildCategory;
import com.group7.pawdicted.mobile.models.ListChildCategory;
import com.group7.pawdicted.mobile.models.PopularSearch;
import com.group7.pawdicted.mobile.models.Product;
import com.group7.pawdicted.mobile.models.RecentSearch;
import com.group7.pawdicted.mobile.services.SearchService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final String PREFS_RECENT_SEARCHES = "recent_searches";
    private static final String PREFS_POPULAR_CATEGORIES = "popular_categories";
    private static final int MAX_RECENT_SEARCHES = 10;
    private static final int NUM_POPULAR_SEARCHES = 5;

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

    // Smart Search Service
    private SearchService searchService;

    // Search handler
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 800; // 800ms delay để tránh gọi API quá nhiều

    // Search state
    private boolean isSearching = false;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initServices();
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

    private void initServices() {
        db = FirebaseFirestore.getInstance();
        searchService = new SearchService();
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
                    currentQuery = "";
                    showSuggestions();
                } else if (!query.equals(currentQuery)) {
                    // Delayed search chỉ khi query thay đổi
                    searchRunnable = () -> performSmartSearch(query);
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
                performSmartSearch(query);
                saveRecentSearch(query);
            }
        });

        // Clear history button
        btnClearHistory.setOnClickListener(v -> clearRecentSearches());

        // Popular search click
        popularSearchAdapter.setOnPopularSearchClickListener(popularSearch -> {
            edtSearchInput.setText(popularSearch.getSearchTerm());
            performSmartSearch(popularSearch.getSearchTerm());
            saveRecentSearch(popularSearch.getSearchTerm());
        });

        // Recent search listeners
        recentSearchAdapter.setOnRecentSearchListener(new RecentSearchAdapter.OnRecentSearchListener() {
            @Override
            public void onRecentSearchClick(RecentSearch recentSearch) {
                edtSearchInput.setText(recentSearch.getSearchTerm());
                performSmartSearch(recentSearch.getSearchTerm());
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
        popularSearches.clear();
        layoutLoading.setVisibility(View.VISIBLE);

        // Initialize ListChildCategory and load sample dataset
        ListChildCategory listChildCategory = new ListChildCategory();
        listChildCategory.generate_sample_dataset();
        List<ChildCategory> allCategories = listChildCategory.getChildCategories();

        // Get previously shown category IDs to avoid repetition
        SharedPreferences prefs = getSharedPreferences(PREFS_POPULAR_CATEGORIES, MODE_PRIVATE);
        String lastShownJson = prefs.getString("last_shown_categories", "");
        Set<String> lastShownIds = new HashSet<>();
        if (!lastShownJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<Set<String>>(){}.getType();
            lastShownIds = gson.fromJson(lastShownJson, type);
        }

        // Filter out previously shown categories
        List<ChildCategory> availableCategories = new ArrayList<>();
        for (ChildCategory category : allCategories) {
            if (!lastShownIds.contains(category.getChildCategory_id())) {
                availableCategories.add(category);
            }
        }

        // If not enough categories, use all categories
        if (availableCategories.size() < NUM_POPULAR_SEARCHES) {
            availableCategories = new ArrayList<>(allCategories);
        }

        // Randomly select categories
        List<ChildCategory> selectedCategories = getRandomCategories(availableCategories, NUM_POPULAR_SEARCHES);

        // Save selected category IDs
        Set<String> selectedIds = new HashSet<>();
        for (ChildCategory category : selectedCategories) {
            selectedIds.add(category.getChildCategory_id());
        }
        prefs.edit().putString("last_shown_categories", new Gson().toJson(selectedIds)).apply();

        // Counter to track completed Firestore queries
        final int[] completedQueries = {0};

        for (ChildCategory category : selectedCategories) {
            // Query Firestore only for product count
            getProductCountForCategory(category.getChildCategory_id(), category.getChildCategory_name(), (count) -> {
                PopularSearch popularSearch = new PopularSearch(
                        category.getChildCategory_name(),
                        category.getChildCategory_image(),
                        count
                );
                popularSearches.add(popularSearch);

                completedQueries[0]++;
                if (completedQueries[0] == selectedCategories.size()) {
                    runOnUiThread(() -> {
                        popularSearchAdapter.notifyDataSetChanged();
                        rvPopularSearches.setVisibility(View.VISIBLE);
                        layoutLoading.setVisibility(View.GONE);
                    });
                }
            });
        }
    }

    private List<ChildCategory> getRandomCategories(List<ChildCategory> allCategories, int count) {
        List<ChildCategory> shuffledCategories = new ArrayList<>(allCategories);
        Collections.shuffle(shuffledCategories);
        return shuffledCategories.subList(0, Math.min(count, shuffledCategories.size()));
    }

    private void getProductCountForCategory(String childCategoryId, String categoryName, ProductCountCallback callback) {
        db.collection("products")
                .whereEqualTo("child_category_id", childCategoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    callback.onResult(queryDocumentSnapshots.size());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error counting products for category: " + categoryName, e);
                    runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Không thể tải số lượng sản phẩm cho " + categoryName, Toast.LENGTH_SHORT).show());
                    callback.onResult(0);
                });
    }

    private interface ProductCountCallback {
        void onResult(int count);
    }

    private void getProductImageForTerm(String searchTerm, ProductImageCallback callback) {
        db.collection("products")
                .whereGreaterThanOrEqualTo("product_name", searchTerm)
                .whereLessThanOrEqualTo("product_name", searchTerm + "\uf8ff")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String imageUrl = "";

                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String productImage = doc.getString("product_image");
                        imageUrl = (productImage != null) ? productImage : "";
                    }

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

    private void performSmartSearch(String query) {
        if (query.trim().isEmpty() || isSearching) return;

        currentQuery = query.trim();
        isSearching = true;
        showLoading();

        Log.d(TAG, "Starting smart search for: " + query);

        // First try Smart Search API
        searchService.searchProducts(currentQuery, new SearchService.SearchCallback() {
            @Override
            public void onSuccess(List<String> productIds) {
                Log.d(TAG, "Smart search API returned " + productIds.size() + " product IDs");

                if (!productIds.isEmpty()) {
                    // Get products from Firestore using the AI-recommended IDs
                    getProductsByIds(productIds, true);
                } else {
                    // Fallback to traditional search
                    Log.d(TAG, "No results from smart search, falling back to traditional search");
                    performTraditionalSearch(currentQuery);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Smart search API error: " + error);
                runOnUiThread(() -> {
                    // Show toast for API error but don't stop the search
                    Toast.makeText(SearchActivity.this, "Tìm kiếm thông minh không khả dụng, sử dụng tìm kiếm cơ bản", Toast.LENGTH_SHORT).show();

                    // Fallback to traditional search
                    performTraditionalSearch(currentQuery);
                });
            }
        });
    }

    private void getProductsByIds(List<String> productIds, boolean isFromSmartSearch) {
        if (productIds.isEmpty()) {
            runOnUiThread(() -> {
                isSearching = false;
                showNoResults();
            });
            return;
        }

        Log.d(TAG, "Getting products by IDs: " + productIds.size() + " items");

        // Split productIds into chunks of 10 (Firestore's 'in' query limit)
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i += 10) {
            chunks.add(productIds.subList(i, Math.min(i + 10, productIds.size())));
        }

        List<Product> allProducts = new ArrayList<>();
        final int[] completedChunks = {0};

        for (List<String> chunk : chunks) {
            db.collection("products")
                    .whereIn("product_id", chunk)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Product product = document.toObject(Product.class);
                                if (product != null) {
                                    allProducts.add(product);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing product: " + document.getId(), e);
                            }
                        }

                        completedChunks[0]++;

                        // When all chunks are processed
                        if (completedChunks[0] == chunks.size()) {
                            runOnUiThread(() -> {
                                isSearching = false;

                                if (!allProducts.isEmpty()) {
                                    // Sort by the order of productIds (maintain AI ranking)
                                    if (isFromSmartSearch) {
                                        allProducts.sort((p1, p2) -> {
                                            int index1 = productIds.indexOf(p1.getProduct_id());
                                            int index2 = productIds.indexOf(p2.getProduct_id());
                                            return Integer.compare(index1, index2);
                                        });
                                    }

                                    searchResults.clear();
                                    searchResults.addAll(allProducts);
                                    showSearchResults();

                                    Log.d(TAG, "Successfully loaded " + allProducts.size() + " products");
                                } else {
                                    // If smart search found IDs but Firestore has no matching products
                                    // fallback to traditional search
                                    if (isFromSmartSearch) {
                                        Log.d(TAG, "Smart search IDs not found in Firestore, falling back to traditional search");
                                        performTraditionalSearch(currentQuery);
                                    } else {
                                        showNoResults();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting products by IDs", e);
                        completedChunks[0]++;

                        if (completedChunks[0] == chunks.size()) {
                            runOnUiThread(() -> {
                                isSearching = false;
                                if (isFromSmartSearch) {
                                    // Fallback to traditional search
                                    performTraditionalSearch(currentQuery);
                                } else {
                                    showNoResults();
                                }
                            });
                        }
                    });
        }
    }

    private void performTraditionalSearch(String query) {
        if (query.trim().isEmpty()) return;

        Log.d(TAG, "Performing traditional search for: " + query);

        // Search in product names
        db.collection("products")
                .whereGreaterThanOrEqualTo("product_name", query)
                .whereLessThanOrEqualTo("product_name", query + "\uf8ff")
                .orderBy("product_name")
                .limit(25)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> nameSearchResults = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                nameSearchResults.add(product);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product: " + document.getId(), e);
                        }
                    }

                    // Also search in description
                    searchInDescription(query, nameSearchResults);

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error in traditional search", e);
                    runOnUiThread(() -> {
                        isSearching = false;
                        showNoResults();
                    });
                });
    }

    private void searchInDescription(String query, List<Product> nameSearchResults) {
        db.collection("products")
                .whereGreaterThanOrEqualTo("description", query)
                .whereLessThanOrEqualTo("description", query + "\uf8ff")
                .limit(25)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> existingIds = new HashSet<>();

                    // Add existing products from name search
                    for (Product product : nameSearchResults) {
                        existingIds.add(product.getProduct_id());
                    }

                    // Add products from description search (avoid duplicates)
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            if (product != null && !existingIds.contains(product.getProduct_id())) {
                                nameSearchResults.add(product);
                                existingIds.add(product.getProduct_id());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product from description search: " + document.getId(), e);
                        }
                    }

                    runOnUiThread(() -> {
                        isSearching = false;
                        searchResults.clear();
                        searchResults.addAll(nameSearchResults);
                        showSearchResults();

                        Log.d(TAG, "Traditional search completed with " + nameSearchResults.size() + " results");
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching in description", e);
                    runOnUiThread(() -> {
                        isSearching = false;
                        // Show results from name search even if description search fails
                        searchResults.clear();
                        searchResults.addAll(nameSearchResults);
                        showSearchResults();
                    });
                });
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

            String resultText = "Tìm thấy " + searchResults.size() + " kết quả";
            txtResultsCount.setText(resultText);

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
        if (searchService != null) {
            searchService.shutdown();
        }
    }
}