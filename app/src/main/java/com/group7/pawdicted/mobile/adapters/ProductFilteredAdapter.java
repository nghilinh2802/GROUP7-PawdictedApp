package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Product;

import java.util.List;

public class ProductFilteredAdapter extends RecyclerView.Adapter<ProductFilteredAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;

    public ProductFilteredAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getProduct_image())
                .placeholder(R.mipmap.ic_logo)
                .error(R.mipmap.ic_logo)
                .into(holder.imageView);
        holder.nameText.setText(product.getProduct_name());
        holder.ratingBar.setRating((float) product.getAverage_rating());
        holder.ratingText.setText(String.format("%.1f", product.getAverage_rating()));
        double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
        holder.priceText.setText(String.format("%.0fđ", discountedPrice));
        holder.discountText.setText("-" + product.getDiscount() + "%");
        holder.originalPriceText.setText(String.format("%.0fđ", product.getPrice()));
        holder.soldText.setText(product.getSold_quantity() + " sold");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        onBindViewHolder(holder, position);
        return convertView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, ratingText, priceText, discountText, originalPriceText, soldText;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_child_cate_product);
            nameText = itemView.findViewById(R.id.txt_child_cate_product_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            ratingText = itemView.findViewById(R.id.txt_rating);
            priceText = itemView.findViewById(R.id.txt_child_cate_product_price);
            discountText = itemView.findViewById(R.id.txt_child_cate_product_discount);
            originalPriceText = itemView.findViewById(R.id.txt_child_cate_original_price);
            soldText = itemView.findViewById(R.id.txt_child_cate_sold);
        }
    }
}