package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.connectors.ProductConnector;
import com.group7.pawdicted.mobile.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NewArrivalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewArrivalAdapter adapter;
    private List<Product> allProducts;
    private List<Product> displayedProducts;
    private TextView txtStatus;
    private TextView txtLoadMore;
    private LinearLayout dividerLayout;
    private int productsPerPage = 10;
    private int currentPage = 1;
    private int totalProducts;
    private static final String TAG = "NewArrivalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_arrival);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_new_arrivals);
        txtStatus = findViewById(R.id.txt_status);
        txtLoadMore = findViewById(R.id.txt_load_more);
        dividerLayout = findViewById(R.id.divider_layout);

        // Initialize data
        ProductConnector connector = new ProductConnector();
        allProducts = connector.get_all_products();
        displayedProducts = new ArrayList<>();

        // Log product count
        Log.d(TAG, "Total products fetched: " + allProducts.size());

        // Sort products by date_listed (newest to oldest)
        Collections.sort(allProducts, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return p2.getDate_listed().compareTo(p1.getDate_listed());
            }
        });

        totalProducts = allProducts.size();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new NewArrivalAdapter(displayedProducts);
        recyclerView.setAdapter(adapter);

        // Load initial products
        loadMoreProducts();

        // Set Load More click listener
        txtLoadMore.setOnClickListener(v -> loadMoreProducts());
    }

    private void loadMoreProducts() {
        int startIndex = (currentPage - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, totalProducts);

        // Log loading details
        Log.d(TAG, "Loading products: startIndex=" + startIndex + ", endIndex=" + endIndex + ", currentPage=" + currentPage);

        // Add products to displayed list
        for (int i = startIndex; i < endIndex; i++) {
            displayedProducts.add(allProducts.get(i));
        }

        // Log displayed products count
        Log.d(TAG, "Displayed products count: " + displayedProducts.size());

        adapter.notifyDataSetChanged();

        // Update status text and visibility
        if (totalProducts > 10) {
            dividerLayout.setVisibility(View.VISIBLE);
            if (endIndex < totalProducts) {
                txtStatus.setText(getString(R.string.title_viewing_result, displayedProducts.size(), totalProducts));
                txtLoadMore.setVisibility(View.VISIBLE);
            } else {
                txtStatus.setText("You're viewing all products");
                txtLoadMore.setVisibility(View.GONE);
            }
        } else {
            dividerLayout.setVisibility(View.GONE);
            txtLoadMore.setVisibility(View.GONE);
            txtStatus.setText(getString(R.string.title_viewing_result, displayedProducts.size(), totalProducts));
        }

        currentPage++;
    }

    private class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ProductViewHolder> {

        private List<Product> products;

        public NewArrivalAdapter(List<Product> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = products.get(position);

            // Bind data to views
            Glide.with(NewArrivalActivity.this)
                    .load(product.getProduct_image())
                    .placeholder(R.mipmap.ic_logo)
                    .error(R.mipmap.ic_logo)
                    .into(holder.imgProduct);

            holder.txtProductName.setText(product.getProduct_name());
            holder.ratingBar.setRating((float) product.getAverage_rating());
            holder.txtRating.setText(String.format(Locale.getDefault(), "%.1f", product.getAverage_rating()));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            holder.txtOriginalPrice.setText(formatter.format(product.getPrice()));
            holder.txtSold.setText(product.getSold_quantity() + " sold");

            if (product.getDiscount() > 0) {
                double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
                holder.txtPrice.setText(formatter.format(discountedPrice));
                holder.txtPrice.setVisibility(View.VISIBLE);
                holder.txtDiscount.setText("-" + (int) product.getDiscount() + "%");
                holder.txtDiscount.setVisibility(View.VISIBLE);
                holder.txtOriginalPrice.setPaintFlags(
                        holder.txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.txtPrice.setVisibility(View.GONE);
                holder.txtDiscount.setVisibility(View.GONE);
                holder.txtOriginalPrice.setPaintFlags(
                        holder.txtOriginalPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Handle item click
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(NewArrivalActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProduct;
            TextView txtProductName;
            RatingBar ratingBar;
            TextView txtRating;
            TextView txtPrice;
            TextView txtDiscount;
            TextView txtOriginalPrice;
            TextView txtSold;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProduct = itemView.findViewById(R.id.img_child_cate_product);
                txtProductName = itemView.findViewById(R.id.txt_child_cate_product_name);
                ratingBar = itemView.findViewById(R.id.rating_bar);
                txtRating = itemView.findViewById(R.id.txt_rating);
                txtPrice = itemView.findViewById(R.id.txt_child_cate_product_price);
                txtDiscount = itemView.findViewById(R.id.txt_child_cate_product_discount);
                txtOriginalPrice = itemView.findViewById(R.id.txt_child_cate_original_price);
                txtSold = itemView.findViewById(R.id.txt_child_cate_sold);
            }
        }
    }
}