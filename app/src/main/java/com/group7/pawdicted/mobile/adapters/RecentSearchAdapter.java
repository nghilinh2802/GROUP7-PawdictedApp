package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.RecentSearch;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
    private Context context;
    private List<RecentSearch> recentSearches;
    private OnRecentSearchListener listener;

    public interface OnRecentSearchListener {
        void onRecentSearchClick(RecentSearch recentSearch);
        void onRemoveRecentSearch(RecentSearch recentSearch, int position);
    }

    public RecentSearchAdapter(Context context, List<RecentSearch> recentSearches) {
        this.context = context;
        this.recentSearches = recentSearches;
    }

    public void setOnRecentSearchListener(OnRecentSearchListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentSearch recentSearch = recentSearches.get(position);

        holder.txtRecentSearchTerm.setText(recentSearch.getSearchTerm());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecentSearchClick(recentSearch);
            }
        });

        holder.btnRemoveRecentSearch.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveRecentSearch(recentSearch, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    public void removeItem(int position) {
        recentSearches.remove(position);
        notifyItemRemoved(position);
    }

    public void updateData(List<RecentSearch> newData) {
        this.recentSearches.clear();
        this.recentSearches.addAll(newData);
        notifyDataSetChanged();
    }

    public void clearAll() {
        recentSearches.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRecentSearchTerm;
        ImageView btnRemoveRecentSearch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRecentSearchTerm = itemView.findViewById(R.id.txtRecentSearchTerm);
            btnRemoveRecentSearch = itemView.findViewById(R.id.btnRemoveRecentSearch);
        }
    }
}