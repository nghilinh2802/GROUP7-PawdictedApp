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

    public ChildCategoryAdapter(Context context, List<ChildCategory> childCategories) {
        this.context = context;
        this.childCategories = childCategories;
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
                .placeholder(R.mipmap.ic_phone)
                .error(R.mipmap.ic_phone)
                .into(holder.imgChildCategory);
    }

    @Override
    public int getItemCount() {
        return childCategories.size();
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

//package com.group7.pawdicted.mobile.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.group7.pawdicted.R;
//import com.group7.pawdicted.mobile.models.ChildCategory;
//
//import java.util.List;
//
//public class ChildCategoryAdapter extends RecyclerView.Adapter<ChildCategoryAdapter.ViewHolder> {
//
//    private Context context;
//    private List<ChildCategory> childCategories;
//
//    public ChildCategoryAdapter(Context context, List<ChildCategory> childCategories) {
//        this.context = context;
//        this.childCategories = childCategories;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_child_category, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ChildCategory childCategory = childCategories.get(position);
//        Glide.with(context)
//                .load(childCategory.getChildCategory_image())
//                .placeholder(R.mipmap.ic_phone)
//                .error(R.mipmap.ic_phone)
//                .into(holder.imageView);
//        holder.textView.setText(childCategory.getChildCategory_name());
//    }
//
//    @Override
//    public int getItemCount() {
//        return childCategories.size();
//    }
//
//    public void setOnItemClickListener(Object o) {
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView textView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.img_child_category);
//            textView = itemView.findViewById(R.id.txt_child_category);
//        }
//    }
//}