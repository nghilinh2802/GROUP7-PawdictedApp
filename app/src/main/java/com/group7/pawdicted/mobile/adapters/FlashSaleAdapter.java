package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
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

        Log.d("ProgressBar", "=== BINDING PRODUCT " + position + " ===");
        Log.d("ProgressBar", "Product: " + product.getProduct_name());

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

        // Lấy thông tin flash sale
        int unitSold = product.getFlashSaleUnitSold();
        int maxQuantity = product.getFlashSaleMaxQuantity();
        int remaining = maxQuantity - unitSold;

        Log.d("ProgressBar", "Unit sold: " + unitSold);
        Log.d("ProgressBar", "Max quantity: " + maxQuantity);
        Log.d("ProgressBar", "Remaining: " + remaining);

        // Kiểm tra ProgressBar có tồn tại không
        if (holder.progressBar == null) {
            Log.e("ProgressBar", "❌ ProgressBar is NULL!");
        } else {
            Log.d("ProgressBar", "✅ ProgressBar found");

            // Cập nhật progress bar
            if (maxQuantity > 0) {
                int progressPercentage = (int) ((unitSold * 100.0) / maxQuantity);
                Log.d("ProgressBar", "Setting progress: " + progressPercentage + "%");

                // Set progress với log
                holder.progressBar.setMax(100);
                holder.progressBar.setProgress(progressPercentage);

                // Force refresh
                holder.progressBar.invalidate();

                Log.d("ProgressBar", "Progress after set: " + holder.progressBar.getProgress());
            } else {
                Log.w("ProgressBar", "Max quantity is 0, setting progress to 0");
                holder.progressBar.setProgress(0);
            }

            // Đảm bảo visible
            holder.progressBar.setVisibility(View.VISIBLE);
            Log.d("ProgressBar", "Visibility: " + holder.progressBar.getVisibility());
        }

        // Cập nhật text hiển thị
        holder.tvUnitSold.setText(unitSold + " đã bán");
        holder.tvRemaining.setText("Còn " + remaining);

        // Thay đổi màu text dựa vào số lượng còn lại
        if (remaining <= 0) {
            holder.tvRemaining.setText("Hết hàng");
            holder.tvRemaining.setTextColor(Color.RED);
        } else if (remaining <= 10) {
            holder.tvRemaining.setTextColor(Color.parseColor("#FF5722")); // Cam đỏ
        } else {
            holder.tvRemaining.setTextColor(Color.parseColor("#E53935")); // Đỏ bình thường
        }

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
        if (remaining <= 0) {
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

        // CHỈ SỬA PHẦN btnBuyNow - KHÔNG ĐỘNG VÀO GÌ KHÁC
        holder.btnBuyNow.setOnClickListener(v -> {
            if (remaining > 0) {
                // Chuyển sang ProductDetails với thông tin flashsale
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                intent.putExtra("IS_FLASHSALE", true);
                intent.putExtra("FLASHSALE_DISCOUNT_RATE", product.getFlashSaleDiscountRate());
                intent.putExtra("FLASHSALE_ID", product.getFlashSaleId());
                intent.putExtra("FLASHSALE_NAME", "Flash Sale");
                intent.putExtra("FLASHSALE_END_TIME", System.currentTimeMillis() + 3600000);
                context.startActivity(intent);
            }
        });
        ;
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

            // Debug log
            if (progressBar == null) {
                Log.e("ViewHolder", "❌ ProgressBar not found in layout!");
            } else {
                Log.d("ViewHolder", "✅ ProgressBar found successfully");
                Log.d("ViewHolder", "ProgressBar class: " + progressBar.getClass().getSimpleName());
            }
        }
    }
}
