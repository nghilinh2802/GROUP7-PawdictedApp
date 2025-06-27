package com.group7.pawdicted.mobile.adapters;


import android.content.Context;
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


    public CartAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    public interface OnCartItemChangeListener {
        void onCartChanged();
    }


    private OnCartItemChangeListener changeListener;


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


            checkbox.setOnCheckedChangeListener(null); // tránh vòng lặp khi reuse view
            checkbox.setChecked(item.isSelected);
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.isSelected = isChecked;
                if (changeListener != null) changeListener.onCartChanged();
            });


            if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(item.imageUrl)
                        .placeholder(R.mipmap.ic_ascend_arrows)
                        .error(R.mipmap.ic_ascend_arrows)
                        .into(image);
            } else {
                image.setImageResource(R.mipmap.ic_ascend_arrows);
            }


            name.setText(item.name);
            price.setText(formatter.format(item.price));
            quantityText.setText(String.valueOf(item.quantity));


            // Spinner
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
                        item.selectedOption = item.options.get(position);
                        if (item.optionPrices != null && item.optionPrices.containsKey(item.selectedOption)) {
                            item.price = item.optionPrices.get(item.selectedOption);
                            price.setText(formatter.format(item.price));
                            if (changeListener != null) changeListener.onCartChanged();
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                spinner.setVisibility(View.GONE);
            }


            btnPlus.setOnClickListener(v -> {
                item.quantity++;
                quantityText.setText(String.valueOf(item.quantity));
                if (changeListener != null) changeListener.onCartChanged();
            });


            btnMinus.setOnClickListener(v -> {
                if (item.quantity > 1) {
                    item.quantity--;
                    quantityText.setText(String.valueOf(item.quantity));
                    if (changeListener != null) changeListener.onCartChanged();
                }
            });

            clearBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CartItem removedItem = itemList.get(pos);

                    // Xóa khỏi danh sách hiển thị
                    itemList.remove(pos);
                    notifyItemRemoved(pos);

                    // Xóa khỏi CartManager
                    CartManager.getInstance().removeItem(removedItem);

                    // Cập nhật local & Firestore
                    String customerId = CartManager.getInstance().getCustomerId();
                    CartStorageHelper.saveCart(context, customerId, itemList);
                    CartFirestoreService.syncCartToFirestore(customerId, itemList);

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

