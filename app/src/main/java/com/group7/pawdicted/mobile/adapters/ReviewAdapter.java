package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Customer;
import com.group7.pawdicted.mobile.models.ListCustomer;
import com.group7.pawdicted.mobile.models.ListVariant;
import com.group7.pawdicted.mobile.models.Review;
import com.group7.pawdicted.mobile.models.Variant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final Context context;
    private final List<Review> reviews;
    private final ListCustomer listCustomer;
    private final ListVariant listVariant;
    private final Map<String, Customer> customerMap = new HashMap<>();
    private final Map<String, String> variantMap = new HashMap<>();

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.listCustomer = new ListCustomer();
        this.listVariant = new ListVariant();
        listCustomer.generate_sample_dataset();
        listVariant.generate_sample_dataset();
        for (Customer customer : listCustomer.getCustomers()) {
            customerMap.put(customer.getCustomer_id(), customer);
        }
        for (Variant variant : listVariant.getVariants()) {
            variantMap.put(variant.getVariant_id(), variant.getVariant_name());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (context == null) {
            Log.e("ReviewAdapter", "Context is null in onBindViewHolder");
            return;
        }
        Review review = reviews.get(position);
        Customer customer = findCustomerById(review.getCustomer_id());

        if (customer != null) {
            Glide.with(context).load(customer.getAvatar_img()).into(holder.imgAvatar);
            holder.txtCustomerName.setText(customer.getCustomer_name());
        }
        holder.productRatingBar.setRating((float) review.getRating());
        String variantId = review.getProduct_variation();
        String variantName = variantMap.get(variantId);
        if (variantId == null || variantName == null) {
            holder.txtVariationName.setText("Variant: Null");
        } else {
            holder.txtVariationName.setText("Variant: " + variantName);
        }
        holder.reviewPassage.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    private Customer findCustomerById(String customerId) {
        return customerMap.get(customerId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imgAvatar;
        TextView txtCustomerName;
        RatingBar productRatingBar;
        TextView txtVariationName;
        TextView reviewPassage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            productRatingBar = itemView.findViewById(R.id.product_rating_bar_again);
            txtVariationName = itemView.findViewById(R.id.txt_variation_name);
            reviewPassage = itemView.findViewById(R.id.review_passage);
        }
    }
}