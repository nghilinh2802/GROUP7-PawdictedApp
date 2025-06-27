package com.group7.pawdicted.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.PromotionNotification;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

    private List<PromotionNotification> promoList;

    public PromotionAdapter(List<PromotionNotification> promoList) {
        this.promoList = promoList;
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePromo;
        TextView textTitle, textDescription, textTime;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            imagePromo = itemView.findViewById(R.id.image_promo);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion_notification, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {
        PromotionNotification promo = promoList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(promo.getImageUrl())
                .placeholder(R.mipmap.fofos)
                .error(R.mipmap.fofos)
                .into(holder.imagePromo);

        holder.textTitle.setText(promo.getTitle());
        holder.textDescription.setText(promo.getDescription());

        // Định dạng time từ Firestore Timestamp
        Timestamp timestamp = promo.getTime();
        String formattedTime = "";
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            formattedTime = outputFormat.format(date);
        }
        holder.textTime.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }
}
