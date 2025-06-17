package com.group7.pawdicted.mobile.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> itemList;

    public CartAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView image;
        TextView name, price, clearBtn, quantityText;
        Spinner spinner;
        TextView btnPlus, btnMinus; // Sửa ở đây

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox_select);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.text_name);
            price = itemView.findViewById(R.id.text_price);
            spinner = itemView.findViewById(R.id.spinner_option);
            clearBtn = itemView.findViewById(R.id.text_clear);
            btnPlus = itemView.findViewById(R.id.button_increase);   // TextView
            btnMinus = itemView.findViewById(R.id.button_decrease);  // TextView
            quantityText = itemView.findViewById(R.id.text_quantity);
        }


        public void bind(final CartItem item) {
            checkbox.setChecked(item.isSelected);
            image.setImageResource(item.imageResId);
            name.setText(item.name);
            price.setText(item.price + ".000đ");
            quantityText.setText(String.valueOf(item.quantity));

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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                spinner.setVisibility(View.GONE);
            }


            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> item.isSelected = isChecked);

            btnPlus.setOnClickListener(v -> {
                item.quantity++;
                quantityText.setText(String.valueOf(item.quantity));
            });

            btnMinus.setOnClickListener(v -> {
                if (item.quantity > 1) {
                    item.quantity--;
                    quantityText.setText(String.valueOf(item.quantity));
                }
            });

            clearBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    itemList.remove(pos);
                    notifyItemRemoved(pos);
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

