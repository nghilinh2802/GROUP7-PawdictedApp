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
import com.group7.pawdicted.mobile.models.PopularSearch;

import java.util.List;

public class PopularSearchAdapter extends RecyclerView.Adapter<PopularSearchAdapter.ViewHolder> {
    private Context context;
    private List<PopularSearch> popularSearches;
    private OnPopularSearchClickListener listener;

    public interface OnPopularSearchClickListener {
        void onPopularSearchClick(PopularSearch popularSearch);
    }

    public PopularSearchAdapter(Context context, List<PopularSearch> popularSearches) {
        this.context = context;
        this.popularSearches = popularSearches;
    }

    public void setOnPopularSearchClickListener(OnPopularSearchClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularSearch popularSearch = popularSearches.get(position);

        holder.txtSearchTerm.setText(popularSearch.getSearchTerm());
        holder.txtSearchCount.setText(popularSearch.getProductCount() + " sản phẩm");

        // Load product image
        Glide.with(context)
                .load(popularSearch.getProductImage())
                .placeholder(R.mipmap.ic_logo)
                .error(R.mipmap.ic_logo)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPopularSearchClick(popularSearch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularSearches.size();
    }

    public void updateData(List<PopularSearch> newData) {
        this.popularSearches.clear();
        this.popularSearches.addAll(newData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtSearchTerm;
        TextView txtSearchCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtSearchTerm = itemView.findViewById(R.id.txtSearchTerm);
            txtSearchCount = itemView.findViewById(R.id.txtSearchCount);
        }
    }
}