package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;
import com.group7.pawdicted.mobile.services.CartFirestoreService;
import com.group7.pawdicted.mobile.services.CartStorageHelper;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> itemList;
    private OnCartItemChangeListener changeListener;

    public CartAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public interface OnCartItemChangeListener {
        void onCartChanged();
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.changeListener = listener;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView image;
        TextView name, price, clearBtn, quantityText;
        Spinner spinner;
        TextView btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox_select);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.text_name);
            price = itemView.findViewById(R.id.text_price);
            spinner = itemView.findViewById(R.id.spinner_option);
            clearBtn = itemView.findViewById(R.id.text_clear);
            btnPlus = itemView.findViewById(R.id.button_increase);
            btnMinus = itemView.findViewById(R.id.button_decrease);
            quantityText = itemView.findViewById(R.id.text_quantity);
        }

        public void bind(final CartItem item) {
            DecimalFormat formatter = new DecimalFormat("#,###đ");

            // Thiết lập checkbox
            checkbox.setOnCheckedChangeListener(null);
            checkbox.setChecked(item.isSelected);
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.isSelected = isChecked;
                if (changeListener != null) changeListener.onCartChanged();
            });

            // Thiết lập hình ảnh
            if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(item.imageUrl)
                        .placeholder(R.mipmap.ic_ascend_arrows)
                        .error(R.mipmap.ic_ascend_arrows)
                        .into(image);
            } else {
                image.setImageResource(R.mipmap.ic_ascend_arrows);
            }

            // Thiết lập tên, giá và số lượng
            name.setText(item.name);
            price.setText(formatter.format(item.price));
            quantityText.setText(String.valueOf(item.quantity));

            // Thiết lập spinner
            if (item.options != null && !item.options.isEmpty()) {
                spinner.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        item.options);
                spinner.setAdapter(adapter);

                int selectedIndex = item.options.indexOf(item.selectedOption);
                if (selectedIndex >= 0) {
                    spinner.setSelection(selectedIndex);
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String newOption = item.options.get(position);
                        String productId = item.productId;

                        // Kiểm tra xem có mục nào khác trùng productId và newOption
                        CartItem existingItem = null;
                        int existingItemPosition = -1;
                        for (int i = 0; i < itemList.size(); i++) {
                            CartItem other = itemList.get(i);
                            if (other != item && other.productId.equals(productId) && other.selectedOption.equals(newOption)) {
                                existingItem = other;
                                existingItemPosition = i;
                                break;
                            }
                        }

                        if (existingItem != null) {
                            // Gộp số lượng
                            existingItem.quantity += item.quantity;
                            // Gộp luôn ảnh nếu có
                            if (item.optionImageUrls != null && item.optionImageUrls.containsKey(newOption)) {
                                existingItem.imageUrl = item.optionImageUrls.get(newOption);
                            }
                            int currentPosition = getAdapterPosition();
                            itemList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemChanged(existingItemPosition);
                        } else {
                            // Cập nhật option
                            item.selectedOption = newOption;

                            // ✅ Cập nhật lại giá nếu có
                            if (item.optionPrices != null && item.optionPrices.containsKey(newOption)) {
                                item.price = item.optionPrices.get(newOption);
                                price.setText(formatter.format(item.price));
                            }

                            // ✅ Cập nhật lại ảnh theo option
                            if (item.optionImageUrls != null && item.optionImageUrls.containsKey(newOption)) {
                                item.imageUrl = item.optionImageUrls.get(newOption);
                                Glide.with(context)
                                        .load(item.imageUrl)
                                        .placeholder(R.mipmap.ic_ascend_arrows)
                                        .error(R.mipmap.ic_ascend_arrows)
                                        .into(image);
                            } else {
                                image.setImageResource(R.mipmap.ic_ascend_arrows);
                            }
                        }

                        // ✅ Đồng bộ CartManager, local và Firestore
                        String customerId = CartManager.getInstance().getCustomerId();
                        if (customerId != null && !customerId.isEmpty()) {
                            CartManager.getInstance().setCartItems(itemList);
                            CartStorageHelper.saveCart(context, customerId, itemList);
                            CartFirestoreService.syncCartToFirestore(customerId, itemList);
                        }

                        if (changeListener != null) changeListener.onCartChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

            } else {
                spinner.setVisibility(View.GONE);
            }

            // Tăng số lượng
            btnPlus.setOnClickListener(v -> {
                item.quantity++;
                quantityText.setText(String.valueOf(item.quantity));
                if (changeListener != null) changeListener.onCartChanged();
                // Cập nhật local storage và Firestore
                String customerId = CartManager.getInstance().getCustomerId();
                if (customerId != null && !customerId.isEmpty()) {
                    CartStorageHelper.saveCart(context, customerId, itemList);
                    CartFirestoreService.syncCartToFirestore(customerId, itemList);
                }
            });

            // Giảm số lượng
            btnMinus.setOnClickListener(v -> {
                if (item.quantity > 1) {
                    item.quantity--;
                    quantityText.setText(String.valueOf(item.quantity));
                    if (changeListener != null) changeListener.onCartChanged();
                    // Cập nhật local storage và Firestore
                    String customerId = CartManager.getInstance().getCustomerId();
                    if (customerId != null && !customerId.isEmpty()) {
                        CartStorageHelper.saveCart(context, customerId, itemList);
                        CartFirestoreService.syncCartToFirestore(customerId, itemList);
                    }
                }
            });

            // Xóa mục
            clearBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CartItem removedItem = itemList.get(pos);
                    itemList.remove(pos);
                    notifyItemRemoved(pos);

                    CartManager.getInstance().removeItem(removedItem);
                    String customerId = CartManager.getInstance().getCustomerId();
                    if (customerId != null && !customerId.isEmpty()) {
                        CartStorageHelper.saveCart(context, customerId, itemList);
                        CartFirestoreService.syncCartToFirestore(customerId, itemList);
                    }

                    if (changeListener != null) changeListener.onCartChanged();
                }
            });
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}