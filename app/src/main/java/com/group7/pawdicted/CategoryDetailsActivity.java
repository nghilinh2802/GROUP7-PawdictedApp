package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {
    TableLayout productFilteredCategory;
    ProductAdapter adapter;
    ListProduct listProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();

        List<Product> filteredProducts = getProductsByCategory(); // hoặc truyền từ intent
        loadProductsToTableLayout(filteredProducts);




    }

    private List<Product> getProductsByCategory() {
        if (listProduct==null)
        {
            listProduct= new ListProduct();
            listProduct.generate_sample_dataset();
        }
        return listProduct.getProducts();
    }

    private void addViews() {
        productFilteredCategory=findViewById(R.id.product_filtered_category);
    }

    private void loadProductsToTableLayout(List<Product> products) {
        TableLayout tableLayout = findViewById(R.id.product_filtered_category);
        tableLayout.removeAllViews(); // Xóa cũ nếu có

        LayoutInflater inflater = LayoutInflater.from(this);
        int columnCount = 2;
        TableRow currentRow = null;

        for (int i = 0; i < products.size(); i++) {
            // Tạo dòng mới mỗi khi đủ cột
            if (i % columnCount == 0) {
                currentRow = new TableRow(this);
                tableLayout.addView(currentRow);
            }

            // Inflate item layout
            View itemView = inflater.inflate(R.layout.item_product, currentRow, false);

            // Bind dữ liệu vào itemView
            Product p = products.get(i);
            ImageView img = itemView.findViewById(R.id.img_child_cate_product);
            TextView name = itemView.findViewById(R.id.txt_child_cate_product_name);
            TextView price = itemView.findViewById(R.id.txt_child_cate_product_price);
            TextView discount = itemView.findViewById(R.id.txt_child_cate_product_discount);
            TextView originalPrice = itemView.findViewById(R.id.txt_child_cate_original_price);
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            TextView sold = itemView.findViewById(R.id.txt_child_cate_sold);
            RatingBar ratingBar = itemView.findViewById(R.id.rating_bar);
            TextView txtRating = itemView.findViewById(R.id.txt_rating);

            // Gán dữ liệu
            Glide.with(this)
                    .load(p.getProduct_image())
                    .placeholder(R.mipmap.ic_logo)  // ảnh tạm thời trong lúc loading
                    .error(R.mipmap.ic_google_login)
                    .into(img);

            name.setText(p.getProduct_name());
            price.setText(String.format("%,.0fđ", p.getPrice()));
            discount.setText(p.getDiscount()+"%");
            originalPrice.setText(String.format("%,.0fđ", p.getPrice()));
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            sold.setText(p.getSold_quantity() + " sold");
            ratingBar.setRating((float) p.getAverage_rating());
            txtRating.setText(String.valueOf(p.getAverage_rating()));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailsActivity.class);
                intent.putExtra("product_id", p.getProduct_id());
                startActivity(intent);
            });

            currentRow.addView(itemView);
        }
    }

}