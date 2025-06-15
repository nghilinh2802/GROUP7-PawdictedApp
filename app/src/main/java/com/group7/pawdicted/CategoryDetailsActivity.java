package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.ChildCategoryAdapter;
import com.group7.pawdicted.mobile.adapters.ProductFilteredAdapter;
import com.group7.pawdicted.mobile.models.ChildCategory;
import com.group7.pawdicted.mobile.models.ListChildCategory;
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {

    private RecyclerView childCategoryRecyclerView;
    private LinearLayout filterCategoryLayout, productContainer;
    private TextView bestSellerText, newestText, ratingText, priceText;
    private ImageView priceArrow;
    private LinearLayout bestSellerLayout, newestLayout, ratingLayout, priceLayout;
    private ChildCategoryAdapter childCategoryAdapter;
    private ProductFilteredAdapter productAdapter;
    private ListChildCategory listChildCategory;
    private ListProduct listProduct;
    private String categoryId;
    private List<Product> filteredProducts;
    private boolean isPriceAscending = false; // Default to false for "none" state
    private String selectedFilter = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        // Initialize data sources
        listChildCategory = new ListChildCategory();
        try {
            listChildCategory.generate_sample_dataset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listProduct = new ListProduct();
        try {
            listProduct.generate_sample_dataset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get category_id from Intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("category_id");

        // Initialize views
        childCategoryRecyclerView = findViewById(R.id.child_category_container);
        filterCategoryLayout = findViewById(R.id.filter_category);
        productContainer = findViewById(R.id.product_filtered_category);
        bestSellerLayout = findViewById(R.id.layout_best_seller);
        newestLayout = findViewById(R.id.layout_newest);
        ratingLayout = findViewById(R.id.layout_rating);
        priceLayout = findViewById(R.id.layout_price);
        bestSellerText = findViewById(R.id.child_cate_best_seller);
        newestText = findViewById(R.id.child_cate_newest);
        ratingText = findViewById(R.id.child_care_rating);
        priceText = findViewById(R.id.child_cate_price);
        priceArrow = findViewById(R.id.img_ascend_arrows);

        // Setup child category RecyclerView
        childCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        childCategoryAdapter = new ChildCategoryAdapter(this, getChildCategoriesByCategoryId(categoryId));
        childCategoryRecyclerView.setAdapter(childCategoryAdapter);

        // Initialize filtered products
        filteredProducts = getProductsByCategoryId(categoryId);
        productAdapter = new ProductFilteredAdapter(this, filteredProducts);
        updateFilterUI(); // Set initial UI state
        updateProductContainer();

        // Setup filter click listeners
        bestSellerLayout.setOnClickListener(v -> {
            if (!selectedFilter.equals("best_seller")) {
                selectedFilter = "best_seller";
                updateFilterUI();
                sortProductsByBestSeller();
                updateProductContainer();
            } else {
                selectedFilter = "none";
                updateFilterUI();
                filteredProducts = getProductsByCategoryId(categoryId); // Reset sorting
                updateProductContainer();
            }
        });

        newestLayout.setOnClickListener(v -> {
            if (!selectedFilter.equals("newest")) {
                selectedFilter = "newest";
                updateFilterUI();
                sortProductsByNewest();
                updateProductContainer();
            } else {
                selectedFilter = "none";
                updateFilterUI();
                filteredProducts = getProductsByCategoryId(categoryId);
                updateProductContainer();
            }
        });

        ratingLayout.setOnClickListener(v -> {
            if (!selectedFilter.equals("rating")) {
                selectedFilter = "rating";
                updateFilterUI();
                sortProductsByRating();
                updateProductContainer();
            } else {
                selectedFilter = "none";
                updateFilterUI();
                filteredProducts = getProductsByCategoryId(categoryId);
                updateProductContainer();
            }
        });

        priceLayout.setOnClickListener(v -> {
            if (!selectedFilter.equals("price")) {
                selectedFilter = "price";
                isPriceAscending = true;
            } else if (isPriceAscending) {
                isPriceAscending = false;
            } else {
                selectedFilter = "none";
            }
            updateFilterUI();
            if (!selectedFilter.equals("none")) {
                sortProductsByPrice();
            } else {
                filteredProducts = getProductsByCategoryId(categoryId);
            }
            updateProductContainer();
        });
    }

    private void updateFilterUI() {
        // Reset all filters to default style
        bestSellerText.setTextColor(getResources().getColor(R.color.black));
        newestText.setTextColor(getResources().getColor(R.color.black));
        ratingText.setTextColor(getResources().getColor(R.color.black));
        priceText.setTextColor(getResources().getColor(R.color.black));
        priceArrow.setImageResource(R.mipmap.ic_ascend_arrows);

        // Highlight the selected filter
        switch (selectedFilter) {
            case "best_seller":
                bestSellerText.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case "newest":
                newestText.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case "rating":
                ratingText.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case "price":
                priceText.setTextColor(getResources().getColor(R.color.main_color));
                priceArrow.setImageResource(R.mipmap.ic_ascend_arrows_red);
                break;
        }
    }

    private List<ChildCategory> getChildCategoriesByCategoryId(String categoryId) {
        List<ChildCategory> childCategories = new ArrayList<>();
        List<ChildCategory> allChildCategories = listChildCategory.getChildCategories();
        if (allChildCategories != null) {
            for (ChildCategory childCategory : allChildCategories) {
                if (categoryId.equals("ALL") || childCategory.getCategory_id().equals(categoryId)) {
                    childCategories.add(childCategory);
                }
            }
        }
        return childCategories;
    }

    private List<Product> getProductsByCategoryId(String categoryId) {
        List<Product> products = new ArrayList<>();
        List<Product> allProducts = listProduct.getProducts();
        if (allProducts != null) {
            for (Product product : allProducts) {
                if (categoryId.equals("ALL") || product.getCategory_id().equals(categoryId)) {
                    products.add(product);
                }
            }
        }
        return products;
    }

    private void sortProductsByBestSeller() {
        Collections.sort(filteredProducts, (p1, p2) -> Integer.compare(p2.getSold_quantity(), p1.getSold_quantity()));
    }

    private void sortProductsByNewest() {
        Collections.sort(filteredProducts, (p1, p2) -> p2.getDate_listed().compareTo(p1.getDate_listed()));
    }

    private void sortProductsByRating() {
        Collections.sort(filteredProducts, (p1, p2) -> {
            int ratingComparison = Double.compare(p2.getAverage_rating(), p1.getAverage_rating());
            if (ratingComparison == 0) {
                return Integer.compare(p2.getRating_number(), p1.getRating_number());
            }
            return ratingComparison;
        });
    }

    private void sortProductsByPrice() {
        Collections.sort(filteredProducts, (p1, p2) -> {
            double price1 = p1.getPrice() * (1 - p1.getDiscount() / 100.0);
            double price2 = p2.getPrice() * (1 - p2.getDiscount() / 100.0);
            return isPriceAscending ? Double.compare(price1, price2) : Double.compare(price2, price1);
        });
    }

    private void updateProductContainer() {
        productContainer.removeAllViews();
        if (filteredProducts.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No products available");
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setTextColor(getResources().getColor(R.color.black));
            emptyView.setTextSize(16f);
            productContainer.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout rowLayout = null;

        for (int i = 0; i < filteredProducts.size(); i++) {
            if (i % 2 == 0) {
                rowLayout = new LinearLayout(this);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 8, 0, 8);
                rowLayout.setLayoutParams(rowParams);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setWeightSum(2f);
                productContainer.addView(rowLayout);
            }
            View productView = productAdapter.getView(i, null, rowLayout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(4, 0, 4, 0);
            productView.setLayoutParams(params);
            rowLayout.addView(productView);
        }
    }
}

//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//import com.group7.pawdicted.mobile.adapters.ProductAdapter;
//import com.group7.pawdicted.mobile.models.ListProduct;
//import com.group7.pawdicted.mobile.models.Product;
//
//import java.util.List;
//
//public class CategoryDetailsActivity extends AppCompatActivity {
//    TableLayout productFilteredCategory;
//    ProductAdapter adapter;
//    ListProduct listProduct;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_category_details);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        addViews();
//
//        List<Product> filteredProducts = getProductsByCategory(); // hoặc truyền từ intent
//        loadProductsToTableLayout(filteredProducts);
//
//
//
//
//    }
//
//    private List<Product> getProductsByCategory() {
//        if (listProduct==null)
//        {
//            listProduct= new ListProduct();
//            listProduct.generate_sample_dataset();
//        }
//        return listProduct.getProducts();
//    }
//
//    private void addViews() {
//        productFilteredCategory=findViewById(R.id.product_filtered_category);
//    }
//
//    private void loadProductsToTableLayout(List<Product> products) {
//        TableLayout tableLayout = findViewById(R.id.product_filtered_category);
//        tableLayout.removeAllViews(); // Xóa cũ nếu có
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        int columnCount = 2;
//        TableRow currentRow = null;
//
//        for (int i = 0; i < products.size(); i++) {
//            // Tạo dòng mới mỗi khi đủ cột
//            if (i % columnCount == 0) {
//                currentRow = new TableRow(this);
//                tableLayout.addView(currentRow);
//            }
//
//            // Inflate item layout
//            View itemView = inflater.inflate(R.layout.item_product, currentRow, false);
//
//            // Bind dữ liệu vào itemView
//            Product p = products.get(i);
//            ImageView img = itemView.findViewById(R.id.img_child_cate_product);
//            TextView name = itemView.findViewById(R.id.txt_child_cate_product_name);
//            TextView price = itemView.findViewById(R.id.txt_child_cate_product_price);
//            TextView discount = itemView.findViewById(R.id.txt_child_cate_product_discount);
//            TextView originalPrice = itemView.findViewById(R.id.txt_child_cate_original_price);
//            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//            TextView sold = itemView.findViewById(R.id.txt_child_cate_sold);
//            RatingBar ratingBar = itemView.findViewById(R.id.rating_bar);
//            TextView txtRating = itemView.findViewById(R.id.txt_rating);
//
//            // Gán dữ liệu
//            Glide.with(this)
//                    .load(p.getProduct_image())
//                    .placeholder(R.mipmap.ic_logo)  // ảnh tạm thời trong lúc loading
//                    .error(R.mipmap.ic_google_login)
//                    .into(img);
//
//            name.setText(p.getProduct_name());
//            price.setText(String.format("%,.0fđ", p.getPrice()));
//            discount.setText(p.getDiscount()+"%");
//            originalPrice.setText(String.format("%,.0fđ", p.getPrice()));
//            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            sold.setText(p.getSold_quantity() + " sold");
//            ratingBar.setRating((float) p.getAverage_rating());
//            txtRating.setText(String.valueOf(p.getAverage_rating()));
//
//            itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(this, ProductDetailsActivity.class);
//                intent.putExtra("product_id", p.getProduct_id());
//                startActivity(intent);
//            });
//
//            currentRow.addView(itemView);
//        }
//    }
//
//}