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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Customer;
import com.group7.pawdicted.mobile.models.ListVariant;
import com.group7.pawdicted.mobile.models.Review;
import com.group7.pawdicted.mobile.models.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final Context context;
    private final List<Review> reviews;
    private final ListVariant listVariant;
    private final Map<String, Customer> customerMap = new HashMap<>();
    private final Map<String, String> variantMap = new HashMap<>();
    private final FirebaseFirestore db;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.listVariant = new ListVariant();
        this.db = FirebaseFirestore.getInstance();
        listVariant.generate_sample_dataset();
        for (Variant variant : listVariant.getVariants()) {
            variantMap.put(variant.getVariant_id(), variant.getVariant_name());
        }

        // Load customer data from Firestore
        db.collection("customers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    customerMap.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String customerId = document.getId(); // document ID is customer_id
                        String customerName = document.getString("customer_name");
                        String avatarImg = document.getString("avatar_img");
                        String customerEmail = document.getString("customer_email");
                        String customerUsername = document.getString("customer_username");
                        // Create Customer with null for unused fields
                        Customer customer = new Customer(
                                customerId,
                                customerName,
                                customerEmail,
                                customerUsername,
                                null, // phone_number
                                null, // address
                                null, // gender
                                null, // dob
                                null, // date_joined
                                avatarImg,
                                null  // role
                        );
                        customerMap.put(customerId, customer);
                    }
                    notifyDataSetChanged(); // Refresh UI after loading customers
                    Log.d("ReviewAdapter", "Loaded " + customerMap.size() + " customers from Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("ReviewAdapter", "Failed to load customers from Firestore", e);
                    notifyDataSetChanged(); // Refresh UI even if failed, to show fallback data
                });
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
        Customer customer = customerMap.get(review.getCustomer_id());

        if (customer != null) {
            Glide.with(context)
                    .load(customer.getAvatar_img())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.imgAvatar);
            holder.txtCustomerName.setText(customer.getCustomer_name());
        } else {
            holder.txtCustomerName.setText("Unknown Customer");
            holder.imgAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.productRatingBar.setRating((float) review.getRating());
        String variantId = review.getProduct_variation();
        String variantName = variantMap.get(variantId);
        if (variantId == null || variantName == null) {
            holder.txtVariationName.setText("Variant: None");
        } else {
            holder.txtVariationName.setText("Variant: " + variantName);
        }
        holder.reviewPassage.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
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