package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.ProductDetailsActivity;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.FlashSaleProduct;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.ViewHolder> {
    private List<FlashSaleProduct> productList;
    private Context context;

    public FlashSaleAdapter(List<FlashSaleProduct> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flash_sale_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FlashSaleProduct product = productList.get(position);

        holder.tvProductName.setText(product.getProduct_name());

        // Sử dụng giá flash sale
        double originalPrice = product.getPrice();
        double flashSalePrice = product.getFlashSalePrice();

        // Format giá tiền
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvSalePrice.setText(formatter.format(flashSalePrice) + "đ");
        holder.tvOriginalPrice.setText(formatter.format(originalPrice) + "đ");

        // Gạch ngang giá gốc
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tvDiscount.setText("-" + product.getFlashSaleDiscountRate() + "%");
        holder.tvUnitSold.setText(product.getFlashSaleUnitSold() + " đã bán");

        // Progress bar cho flash sale
        double soldPercentage = product.getFlashSaleSoldPercentage();
        holder.progressBar.setProgress((int) soldPercentage);

        // Hiển thị số lượng còn lại
        int remaining = product.getFlashSaleRemainingQuantity();
        holder.tvRemaining.setText("Còn " + remaining + " sản phẩm");

        // Load hình ảnh
        Glide.with(context)
                .load(product.getProduct_image())
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .into(holder.ivProduct);

        // Rating
        holder.tvRating.setText(String.format("%.1f", product.getAverage_rating()));
        holder.tvRatingCount.setText("(" + product.getRating_number() + ")");

        // Disable button nếu hết hàng
        if (!product.isFlashSaleAvailable()) {
            holder.btnBuyNow.setText("Hết hàng");
            holder.btnBuyNow.setEnabled(false);
            holder.btnBuyNow.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        } else {
            holder.btnBuyNow.setText("Mua ngay");
            holder.btnBuyNow.setEnabled(true);
            holder.btnBuyNow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A01B1B")));
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getProduct_id());
            intent.putExtra("isFlashSale", true);
            intent.putExtra("flashSaleId", product.getFlashSaleId());
            context.startActivity(intent);
        });

        holder.btnBuyNow.setOnClickListener(v -> {
            if (product.isFlashSaleAvailable()) {
                addToCart(product);
            }
        });
    }

    private void addToCart(FlashSaleProduct product) {
        Toast.makeText(context, "Đã thêm " + product.getProduct_name() +
                        " vào giỏ hàng với giá " +
                        String.format("%.0f", product.getFlashSalePrice()) + "đ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName, tvSalePrice, tvOriginalPrice, tvDiscount, tvUnitSold;
        TextView tvRating, tvRatingCount, tvRemaining;
        ProgressBar progressBar;
        Button btnBuyNow;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvUnitSold = itemView.findViewById(R.id.tvUnitSold);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
            tvRemaining = itemView.findViewById(R.id.tvRemaining);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
        }
    }
}
