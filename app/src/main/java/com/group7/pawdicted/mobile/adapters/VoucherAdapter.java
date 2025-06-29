package com.group7.pawdicted.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Voucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    public static final int TYPE_DISCOUNT = 0;
    public static final int TYPE_SHIPPING = 1;

    private List<Voucher> vouchers;
    private int type;
    private int selectedPosition = -1;

    public VoucherAdapter(List<Voucher> vouchers, int type) {
        this.vouchers = vouchers;
        this.type = type;
        for (int i = 0; i < vouchers.size(); i++) {
            if (vouchers.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @Override
    public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_item, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.code.setText(voucher.getCode());
        holder.minSpend.setText(voucher.getMinSpend());
        holder.validity.setText(voucher.getValidity());
        holder.radioButton.setChecked(position == selectedPosition);

        if (type == TYPE_DISCOUNT) {
            holder.icon.setImageResource(R.drawable.ic_discount);
        } else {
            holder.icon.setImageResource(R.drawable.ic_shipping);
        }

        holder.radioButton.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (previousSelected != selectedPosition) {
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView code, minSpend, validity;
        RadioButton radioButton;

        public VoucherViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.voucher_icon);
            code = itemView.findViewById(R.id.voucher_code);
            minSpend = itemView.findViewById(R.id.voucher_min_spend);
            validity = itemView.findViewById(R.id.voucher_validity);
            radioButton = itemView.findViewById(R.id.voucher_radio_button);
        }
    }
}
