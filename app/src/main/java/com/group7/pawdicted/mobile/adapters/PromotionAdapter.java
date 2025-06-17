package com.group7.pawdicted.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.PromotionNotification;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

    private List<PromotionNotification> promoList;

    public PromotionAdapter(List<PromotionNotification> promoList) {
        this.promoList = promoList;
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePromo;
        TextView textTitle, textDescription;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            imagePromo = itemView.findViewById(R.id.image_promo);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
        }
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion_notification, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {
        PromotionNotification promo = promoList.get(position);
        holder.imagePromo.setImageResource(promo.getImageResId());
        holder.textTitle.setText(promo.getTitle());
        holder.textDescription.setText(promo.getDescription());
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }
}

