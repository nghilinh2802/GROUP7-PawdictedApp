package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<CartItem> cartItems;

    public OrderItemAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkout_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Thiết lập tên sản phẩm
        holder.txtProductName.setText(cartItem.name);

        // Kiểm tra và ẩn variant nếu chỉ có một variant "Default"
        if (cartItem.options == null || cartItem.options.isEmpty() ||
                (cartItem.options.size() == 1 && "Default".equals(cartItem.options.get(0)))) {
            holder.txtProductColor.setVisibility(View.GONE);
            Log.d("OrderItemAdapter", "Ẩn txtProductColor cho sản phẩm: " + cartItem.name + ", options: " + cartItem.options);
        } else {
            holder.txtProductColor.setVisibility(View.VISIBLE);
            holder.txtProductColor.setText(cartItem.selectedOption);
            Log.d("OrderItemAdapter", "Hiển thị txtProductColor cho sản phẩm: " + cartItem.name + ", selectedOption: " + cartItem.selectedOption);
        }

        // Định dạng giá
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        holder.txtProductPrice.setText(formatter.format(cartItem.price));

        // Thiết lập số lượng
        holder.txtProductQuantity.setText("x" + cartItem.quantity);

        // Tải hình ảnh sản phẩm
        Glide.with(context)
                .load(cartItem.imageUrl)
                .placeholder(R.mipmap.ic_ascend_arrows)
                .error(R.mipmap.ic_ascend_arrows)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductColor, txtProductPrice, txtProductQuantity;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductColor = itemView.findViewById(R.id.txtProductColor);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
        }
    }
}