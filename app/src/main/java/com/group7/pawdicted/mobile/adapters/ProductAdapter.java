package com.group7.pawdicted.mobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Product;

public class ProductAdapter extends ArrayAdapter<Product> {
    Activity context;
    int resource;
    public ProductAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View item = inflater.inflate(this.resource, null);
        ImageView imgCategoryProduct=item.findViewById(R.id.img_cate_product);
        TextView txtCategoryProduct=item.findViewById(R.id.txt_cate_product);
        TextView txtOriginalPrice=item.findViewById(R.id.txt_child_cate_original_price);
        RatingBar ratingBar = item.findViewById(R.id.rating_bar);
        TextView txtRating = item.findViewById(R.id.txt_rating);

        Product p = getItem(position);

        imgCategoryProduct.setImageResource(Integer.parseInt(p.getProduct_image()));

        txtCategoryProduct.setText(p.getProduct_name());

        String formattedOriginalPrice = String.format("%,.0fđ", p.getPrice());
        txtOriginalPrice.setText(formattedOriginalPrice);
        txtOriginalPrice.setPaintFlags(
                txtOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        );

        double rating = p.getAverage_rating(); // giả sử rating là kiểu double, ví dụ: 4.5
        ratingBar.setRating((float) rating);
        txtRating.setText(String.valueOf(rating));


        return item;
    }
}


