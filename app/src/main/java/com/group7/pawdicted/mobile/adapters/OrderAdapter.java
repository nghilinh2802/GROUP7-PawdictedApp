package com.group7.pawdicted.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.OrderNotification;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderNotification> orderList;

    public OrderAdapter(List<OrderNotification> orderList) {
        this.orderList = orderList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textTitle, textDescription, textTime;

        public OrderViewHolder(View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_notification, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderNotification order = orderList.get(position);
        holder.imageProduct.setImageResource(order.getImageResId());
        holder.textTitle.setText(order.getTitle());
        holder.textDescription.setText(order.getDescription());
        holder.textTime.setText(order.getTime());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

