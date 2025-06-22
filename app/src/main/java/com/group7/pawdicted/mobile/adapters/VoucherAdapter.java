package com.group7.pawdicted.mobile.adapters;

import android.graphics.Color;
import android.graphics.Paint;
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
    private int selectedPosition = -1; // Theo dõi vị trí được chọn, -1 nếu chưa chọn

    public VoucherAdapter(List<Voucher> vouchers, int type) {
        this.vouchers = vouchers;
        this.type = type;
        // Kiểm tra nếu có voucher nào được chọn mặc định
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
        holder.title.setText(voucher.getTitle());
        holder.minSpend.setText(voucher.getMinSpend());
        holder.validity.setText(voucher.getValidity());
        holder.radioButton.setChecked(position == selectedPosition);

        if (type == TYPE_DISCOUNT) {
            holder.icon.setImageResource(R.drawable.ic_discount);
        } else {
            holder.icon.setImageResource(R.drawable.ic_shipping);
        }

        holder.terms.setText("Terms and Conditions");
        holder.terms.setTextColor(Color.BLUE);
        holder.terms.setPaintFlags(holder.terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Xử lý sự kiện khi RadioButton được click
        holder.radioButton.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (previousSelected != selectedPosition) {
                notifyItemChanged(previousSelected); // Cập nhật item trước đó
                notifyItemChanged(selectedPosition); // Cập nhật item hiện tại
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, minSpend, validity, terms;
        RadioButton radioButton;

        public VoucherViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.voucher_icon);
            title = itemView.findViewById(R.id.voucher_title);
            minSpend = itemView.findViewById(R.id.voucher_min_spend);
            validity = itemView.findViewById(R.id.voucher_validity);
            terms = itemView.findViewById(R.id.terms_and_conditions);
            radioButton = itemView.findViewById(R.id.voucher_radio_button); // Cập nhật ID
        }
    }
}