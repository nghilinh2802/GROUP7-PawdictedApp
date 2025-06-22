package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
        txtOriginalPrice.setText(formatter.format(product.getPrice()) + "đ");
        txtSold.setText(product.getSold_quantity() + " sold");

        if (product.getDiscount() > 0) {
            double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
            txtPrice.setText(formatter.format(discountedPrice) + "đ");
            txtPrice.setVisibility(View.VISIBLE);
            txtDiscount.setText("-" + (int) product.getDiscount() + "%");
            txtDiscount.setVisibility(View.VISIBLE);
            txtOriginalPrice.setPaintFlags(txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            txtPrice.setVisibility(View.GONE);
            txtDiscount.setVisibility(View.GONE);
            txtOriginalPrice.setPaintFlags(txtOriginalPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getProduct_id());
            context.startActivity(intent);
        });

        return convertView;
    }
}