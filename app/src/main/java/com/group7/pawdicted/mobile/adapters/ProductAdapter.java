package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.ProductDetailsActivity;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.ChildCategory;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_CHILD_CATEGORY = 1;
    private static final int TYPE_SEE_ALL = 2;

    private List<Object> items;
    private Context context;
    private OnChildCategoryClickListener childCategoryClickListener;

    public ProductAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void setOnChildCategoryClickListener(OnChildCategoryClickListener listener) {
        this.childCategoryClickListener = listener;
    }

    public void updateItems(List<Object> newItems) {
        Log.d("ProductAdapter", "Updating items, new size: " + newItems.size());
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Product) {
            return TYPE_PRODUCT;
        } else if (item instanceof ChildCategory) {
            return TYPE_CHILD_CATEGORY;
        } else if (item instanceof SeeAllItem) {
            return TYPE_SEE_ALL;
        }
        Log.e("ProductAdapter", "Invalid item type at position: " + position);
        return TYPE_PRODUCT;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    return viewType == TYPE_SEE_ALL ? 2 : 1;
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PRODUCT) {
            View view = inflater.inflate(R.layout.item_category, parent, false);
            return new ProductViewHolder(view);
        } else if (viewType == TYPE_CHILD_CATEGORY) {
            View view = inflater.inflate(R.layout.item_child_category, parent, false);
            return new ChildCategoryViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.see_all_item, parent, false);
            return new SeeAllViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("ProductAdapter", "Binding position: " + position + ", Item: " + items.get(position).getClass().getSimpleName());
        if (holder instanceof ProductViewHolder) {
            Product product = (Product) items.get(position);
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.txtCateProduct.setText(product.getProduct_name());
            Glide.with(context)
                    .load(product.getProduct_image())
                    .placeholder(R.mipmap.ic_ascend_arrows)
                    .error(R.mipmap.ic_ascend_arrows)
                    .into(productHolder.imgCateProduct);

            productHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                context.startActivity(intent);
            });
        } else if (holder instanceof ChildCategoryViewHolder) {
            ChildCategory childCategory = (ChildCategory) items.get(position);
            ChildCategoryViewHolder childHolder = (ChildCategoryViewHolder) holder;
            childHolder.txtChildCategory.setText(childCategory.getChildCategory_name());
            Glide.with(context)
                    .load(childCategory.getChildCategory_image())
                    .placeholder(R.mipmap.ic_phone)
                    .error(R.mipmap.ic_phone)
                    .into(childHolder.imgChildCategory);

            childHolder.itemView.setOnClickListener(v -> {
                if (childCategoryClickListener != null) {
                    childCategoryClickListener.onChildCategoryClick(childCategory);
                }
            });
        } else if (holder instanceof SeeAllViewHolder) {
            SeeAllViewHolder seeAllHolder = (SeeAllViewHolder) holder;
            seeAllHolder.txtEtc.setText("...");
            seeAllHolder.txtSeeAll.setText(context.getString(R.string.see_all));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCateProduct;
        TextView txtCateProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCateProduct = itemView.findViewById(R.id.img_cate_product);
            txtCateProduct = itemView.findViewById(R.id.txt_cate_product);
        }
    }

    static class ChildCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChildCategory;
        TextView txtChildCategory;

        public ChildCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChildCategory = itemView.findViewById(R.id.img_child_category);
            txtChildCategory = itemView.findViewById(R.id.txt_child_category);
        }
    }

    static class SeeAllViewHolder extends RecyclerView.ViewHolder {
        TextView txtEtc;
        TextView txtSeeAll;

        public SeeAllViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEtc = itemView.findViewById(R.id.txtEtc);
            txtSeeAll = itemView.findViewById(R.id.txtSeeAll);
        }
    }

    public static class SeeAllItem {
    }

    public interface OnChildCategoryClickListener {
        void onChildCategoryClick(ChildCategory childCategory);
    }
}

//package com.group7.pawdicted.mobile.adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.group7.pawdicted.ProductDetailsActivity;
//import com.group7.pawdicted.R;
//import com.group7.pawdicted.mobile.models.ChildCategory;
//import com.group7.pawdicted.mobile.models.Product;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private static final int TYPE_PRODUCT = 0;
//    private static final int TYPE_HEADER = 1;
//    private static final int TYPE_SEE_ALL = 2;
//
//    private List<Object> items;
//    private Context context;
//
//    public ProductAdapter(Context context) {
//        this.context = context;
//        this.items = new ArrayList<>();
//    }
//
//    public void updateItems(List<Object> newItems) {
//        Log.d("ProductAdapter", "Updating items, new size: " + newItems.size());
//        this.items.clear();
//        this.items.addAll(newItems);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Object item = items.get(position);
//        if (item instanceof Product) {
//            return TYPE_PRODUCT;
//        } else if (item instanceof ChildCategory) {
//            return TYPE_HEADER;
//        } else if (item instanceof SeeAllItem) {
//            return TYPE_SEE_ALL;
//        }
//        Log.e("ProductAdapter", "Invalid item type at position: " + position);
//        return TYPE_PRODUCT;
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int viewType = getItemViewType(position);
//                    return viewType == TYPE_HEADER ? 2 : viewType == TYPE_SEE_ALL ? 2 : 1;
//                }
//            });
//        }
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        if (viewType == TYPE_PRODUCT) {
//            View view = inflater.inflate(R.layout.item_category, parent, false);
//            return new ProductViewHolder(view);
//        } else if (viewType == TYPE_HEADER) {
//            View view = inflater.inflate(R.layout.item_child_category_header, parent, false);
//            return new HeaderViewHolder(view);
//        } else {
//            View view = inflater.inflate(R.layout.see_all_item, parent, false);
//            return new SeeAllViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Log.d("ProductAdapter", "Binding position: " + position + ", Item: " + items.get(position).getClass().getSimpleName());
//        if (holder instanceof ProductViewHolder) {
//            Product product = (Product) items.get(position);
//            ProductViewHolder productHolder = (ProductViewHolder) holder;
//            productHolder.txtCateProduct.setText(product.getProduct_name());
//            Log.d("ProductAdapter", "Binding Product: " + product.getProduct_name());
//            Glide.with(context)
//                    .load(product.getProduct_image())
//                    .placeholder(R.mipmap.ic_ascend_arrows)
//                    .error(R.mipmap.ic_ascend_arrows)
//                    .into(productHolder.imgCateProduct);
//
//            // Sự kiện nhấn sản phẩm
//            productHolder.itemView.setOnClickListener(v -> {
//                Log.d("ProductAdapter", "Clicked product: " + product.getProduct_id());
//                Intent intent = new Intent(context, ProductDetailsActivity.class);
//                intent.putExtra("product_id", product.getProduct_id());
//                context.startActivity(intent);
//            });
//        } else if (holder instanceof HeaderViewHolder) {
//            ChildCategory childCategory = (ChildCategory) items.get(position);
//            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
//            headerHolder.txtChildCategoryName.setText(childCategory.getChildCategory_name());
//            Log.d("ProductAdapter", "Binding ChildCategory: " + childCategory.getChildCategory_name());
//        } else if (holder instanceof SeeAllViewHolder) {
//            SeeAllViewHolder seeAllHolder = (SeeAllViewHolder) holder;
//            seeAllHolder.txtEtc.setText("...");
//            seeAllHolder.txtSeeAll.setText(context.getString(R.string.see_all));
//            Log.d("ProductAdapter", "Binding SeeAll");
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public void setOnChildCategoryClickListener(Object o) {
//    }
//
//    static class ProductViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgCateProduct;
//        TextView txtCateProduct;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgCateProduct = itemView.findViewById(R.id.img_cate_product);
//            txtCateProduct = itemView.findViewById(R.id.txt_cate_product);
//        }
//    }
//
//    static class HeaderViewHolder extends RecyclerView.ViewHolder {
//        TextView txtChildCategoryName;
//
//        public HeaderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtChildCategoryName = itemView.findViewById(R.id.txt_child_category_name);
//        }
//    }
//
//    static class SeeAllViewHolder extends RecyclerView.ViewHolder {
//        TextView txtEtc;
//        TextView txtSeeAll;
//
//        public SeeAllViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtEtc = itemView.findViewById(R.id.txtEtc);
//            txtSeeAll = itemView.findViewById(R.id.txtSeeAll);
//        }
//    }
//
//    public static class SeeAllItem {
//    }
//}