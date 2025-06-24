package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.ProductDetailsActivity;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.ChildCategory;
import com.group7.pawdicted.mobile.models.Product;

import java.text.DecimalFormat;
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
            View view = inflater.inflate(R.layout.item_product, parent, false);
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
            DecimalFormat formatter = new DecimalFormat("#,###đ");

            // Set background cho item_product_container
            if (productHolder.productContainer != null) {
                productHolder.productContainer.setBackground(context.getResources().getDrawable(R.drawable.gray_rounded_background));
            }

            if (productHolder.imgChildCateProduct != null) {
                Glide.with(context)
                        .load(product.getProduct_image())
                        .placeholder(R.mipmap.ic_ascend_arrows)
                        .error(R.mipmap.ic_ascend_arrows)
                        .into(productHolder.imgChildCateProduct);
            }
            if (productHolder.txtChildCateProductName != null) {
                productHolder.txtChildCateProductName.setText(product.getProduct_name());
            }
            if (productHolder.ratingBar != null) {
                productHolder.ratingBar.setRating((float) product.getAverage_rating());
            }
            if (productHolder.txtRating != null) {
                productHolder.txtRating.setText(String.format("%.1f", product.getAverage_rating()));
            }
            double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
            if (productHolder.txtChildCateProductPrice != null) {
                productHolder.txtChildCateProductPrice.setText(formatter.format(discountedPrice));
            }
            if (productHolder.txtChildCateProductDiscount != null) {
                productHolder.txtChildCateProductDiscount.setText("-" + product.getDiscount() + "%");
            }
            if (productHolder.txtChildCateOriginalPrice != null) {
                productHolder.txtChildCateOriginalPrice.setText(formatter.format(product.getPrice()));
            }
            if (productHolder.txtChildCateSold != null) {
                productHolder.txtChildCateSold.setText(product.getSold_quantity() + " sold");
            }

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
                    .placeholder(R.mipmap.ic_logo)
                    .error(R.mipmap.ic_logo)
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
        LinearLayout productContainer;
        ImageView imgChildCateProduct;
        TextView txtChildCateProductName;
        RatingBar ratingBar;
        TextView txtRating;
        TextView txtChildCateProductPrice;
        TextView txtChildCateProductDiscount;
        TextView txtChildCateOriginalPrice;
        TextView txtChildCateSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productContainer = itemView.findViewById(R.id.item_product_container);
            imgChildCateProduct = itemView.findViewById(R.id.img_child_cate_product);
            txtChildCateProductName = itemView.findViewById(R.id.txt_child_cate_product_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            txtRating = itemView.findViewById(R.id.txt_rating);
            txtChildCateProductPrice = itemView.findViewById(R.id.txt_child_cate_product_price);
            txtChildCateProductDiscount = itemView.findViewById(R.id.txt_child_cate_product_discount);
            txtChildCateOriginalPrice = itemView.findViewById(R.id.txt_child_cate_original_price);
            txtChildCateSold = itemView.findViewById(R.id.txt_child_cate_sold);
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