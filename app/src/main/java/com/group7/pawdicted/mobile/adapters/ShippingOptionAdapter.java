package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.ShippingOption;
import java.util.List;

public class ShippingOptionAdapter extends RecyclerView.Adapter<ShippingOptionAdapter.ShippingOptionViewHolder> {

    private Context context;
    private List<ShippingOption> shippingOptions;
    private int selectedPosition = 0; // Default to first option selected
    private OnShippingOptionSelectedListener listener;

    public interface OnShippingOptionSelectedListener {
        void onShippingOptionSelected(ShippingOption option);
    }

    public ShippingOptionAdapter(Context context, List<ShippingOption> shippingOptions, OnShippingOptionSelectedListener listener) {
        this.context = context;
        this.shippingOptions = shippingOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShippingOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shipping_option, parent, false);
        return new ShippingOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingOptionViewHolder holder, int position) {
        ShippingOption option = shippingOptions.get(position);
        holder.txtShippingOptionTitle.setText(option.getTitle());
        holder.txtShippingOptionDetails.setText(option.getDetails());
        holder.radioShippingOption.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onShippingOptionSelected(option);
        });

        holder.radioShippingOption.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onShippingOptionSelected(option);
        });
    }

    @Override
    public int getItemCount() {
        return shippingOptions.size();
    }

    public static class ShippingOptionViewHolder extends RecyclerView.ViewHolder {
        TextView txtShippingOptionTitle, txtShippingOptionDetails;
        RadioButton radioShippingOption;

        public ShippingOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtShippingOptionTitle = itemView.findViewById(R.id.txtShippingOptionTitle);
            txtShippingOptionDetails = itemView.findViewById(R.id.txtShippingOptionDetails);
            radioShippingOption = itemView.findViewById(R.id.radioShippingOption);
        }
    }
}