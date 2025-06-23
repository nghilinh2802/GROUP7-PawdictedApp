package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.ChildCategory;

import java.util.List;

public class ChildCategoryAdapter extends RecyclerView.Adapter<ChildCategoryAdapter.ViewHolder> {

    private Context context;
    private List<ChildCategory> childCategories;
    private OnChildCategoryClickListener clickListener;

    // Interface for click callback
    public interface OnChildCategoryClickListener {
        void onChildCategoryClick(String childCategoryId);
    }

    public ChildCategoryAdapter(Context context, List<ChildCategory> childCategories, OnChildCategoryClickListener clickListener) {
        this.context = context;
        this.childCategories = childCategories;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildCategory childCategory = childCategories.get(position);
        holder.txtChildCategory.setText(childCategory.getChildCategory_name());
        Glide.with(context)
                .load(childCategory.getChildCategory_image())
                .placeholder(R.mipmap.ic_logo)
                .error(R.mipmap.ic_logo)
                .into(holder.imgChildCategory);

        // Set click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onChildCategoryClick(childCategory.getChildCategory_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childCategories != null ? childCategories.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChildCategory;
        TextView txtChildCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChildCategory = itemView.findViewById(R.id.img_child_category);
            txtChildCategory = itemView.findViewById(R.id.txt_child_category);
        }
    }
}