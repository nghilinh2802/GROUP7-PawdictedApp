package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.PaymentMethod;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;
    private int selectedPosition = 0;
    private OnPaymentMethodSelectedListener listener;

    public interface OnPaymentMethodSelectedListener {
        void onPaymentMethodSelected(PaymentMethod method);
    }

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods, OnPaymentMethodSelectedListener listener) {
        this.context = context;
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod method = paymentMethods.get(position);
        holder.txtPaymentName.setText(method.getName());
        holder.imgPaymentIcon.setImageResource(method.getIconResId());

        // Set background based on selection
        holder.layoutContainer.setBackgroundResource(
                position == selectedPosition ? R.drawable.rounded_edittext_red : R.drawable.rounded_edittext
        );

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onPaymentMethodSelected(method);
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        TextView txtPaymentName;
        ImageView imgPaymentIcon;
        LinearLayout layoutContainer;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPaymentName = itemView.findViewById(R.id.txtPaymentName);
            imgPaymentIcon = itemView.findViewById(R.id.imgPaymentIcon);
            layoutContainer = itemView.findViewById(R.id.payment_method_item);
        }
    }
}