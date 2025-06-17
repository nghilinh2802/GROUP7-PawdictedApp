package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.ProductDetailsActivity;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductFilteredAdapter extends BaseAdapter {

    private Context context;
    private List<Product> products;

    public ProductFilteredAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        Product product = products.get(position);

        ImageView imgProduct = convertView.findViewById(R.id.img_child_cate_product);
        TextView txtName = convertView.findViewById(R.id.txt_child_cate_product_name);
        RatingBar ratingBar = convertView.findViewById(R.id.rating_bar);
        TextView txtRating = convertView.findViewById(R.id.txt_rating);
        TextView txtPrice = convertView.findViewById(R.id.txt_child_cate_product_price);
        TextView txtDiscount = convertView.findViewById(R.id.txt_child_cate_product_discount);
        TextView txtOriginalPrice = convertView.findViewById(R.id.txt_child_cate_original_price);
        TextView txtSold = convertView.findViewById(R.id.txt_child_cate_sold);

        txtName.setText(product.getProduct_name());
        Glide.with(context)
                .load(product.getProduct_image())
                .placeholder(R.mipmap.ic_ascend_arrows)
                .error(R.mipmap.ic_ascend_arrows)
                .into(imgProduct);

        ratingBar.setRating((float) product.getAverage_rating());
        txtRating.setText(String.format("%.1f", product.getAverage_rating()));

        DecimalFormat formatter = new DecimalFormat("#,###");
        double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
        txtPrice.setText(formatter.format(discountedPrice) + "đ");
        txtDiscount.setText("-" + product.getDiscount() + "%");
        txtOriginalPrice.setText(formatter.format(product.getPrice()) + "đ");
        txtSold.setText(product.getSold_quantity() + " sold");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                context.startActivity(intent);
            }
        });



        return convertView;
    }
}

//package com.group7.pawdicted.mobile.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.group7.pawdicted.R;
//import com.group7.pawdicted.mobile.models.Product;
//
//import java.util.List;
//
//public class ProductFilteredAdapter extends RecyclerView.Adapter<ProductFilteredAdapter.ViewHolder> {
//
//    private Context context;
//    private List<Product> products;
//
//    public ProductFilteredAdapter(Context context, List<Product> products) {
//        this.context = context;
//        this.products = products;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Product product = products.get(position);
//        Glide.with(context)
//                .load(product.getProduct_image())
//                .placeholder(R.mipmap.ic_logo)
//                .error(R.mipmap.ic_logo)
//                .into(holder.imageView);
//        holder.nameText.setText(product.getProduct_name());
//        holder.ratingBar.setRating((float) product.getAverage_rating());
//        holder.ratingText.setText(String.format("%.1f", product.getAverage_rating()));
//        double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
//        holder.priceText.setText(String.format("%.0fđ", discountedPrice));
//        holder.discountText.setText("-" + product.getDiscount() + "%");
//        holder.originalPriceText.setText(String.format("%.0fđ", product.getPrice()));
//        holder.soldText.setText(product.getSold_quantity() + " sold");
//    }
//
//    @Override
//    public int getItemCount() {
//        return products.size();
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
//        }
//        ViewHolder holder = new ViewHolder(convertView);
//        onBindViewHolder(holder, position);
//        return convertView;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView nameText, ratingText, priceText, discountText, originalPriceText, soldText;
//        RatingBar ratingBar;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.img_child_cate_product);
//            nameText = itemView.findViewById(R.id.txt_child_cate_product_name);
//            ratingBar = itemView.findViewById(R.id.rating_bar);
//            ratingText = itemView.findViewById(R.id.txt_rating);
//            priceText = itemView.findViewById(R.id.txt_child_cate_product_price);
//            discountText = itemView.findViewById(R.id.txt_child_cate_product_discount);
//            originalPriceText = itemView.findViewById(R.id.txt_child_cate_original_price);
//            soldText = itemView.findViewById(R.id.txt_child_cate_sold);
//        }
//    }
//}