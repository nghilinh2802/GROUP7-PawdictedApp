package com.group7.pawdicted.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.AddressItem;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressItem> addressList;
    private int selectedPosition = -1; // Vị trí được chọn, -1 nếu chưa có

    public AddressAdapter(List<AddressItem> addressList) {
        this.addressList = addressList;
        // Tìm và đặt địa chỉ mặc định là được chọn ban đầu
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        AddressItem addressItem = addressList.get(position);
        holder.name.setText(addressItem.getName());
        holder.phone.setText(addressItem.getPhone());
        holder.address.setText(addressItem.getAddress());
        holder.defaultAddress.setVisibility(addressItem.isDefault() ? View.VISIBLE : View.GONE);

        // Đặt trạng thái checked cho RadioButton
        holder.radioButton.setChecked(position == selectedPosition);

        // Xử lý sự kiện click cho RadioButton
        holder.radioButton.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected); // Cập nhật giao diện item trước đó
            notifyItemChanged(selectedPosition); // Cập nhật giao diện item hiện tại
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, address, defaultAddress, editAddress;
        RadioButton radioButton;

        public AddressViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.addressName);
            phone = itemView.findViewById(R.id.addressPhone);
            address = itemView.findViewById(R.id.addressDetail);
            defaultAddress = itemView.findViewById(R.id.defaultAddress);
            editAddress = itemView.findViewById(R.id.editAddress);
            radioButton = itemView.findViewById(R.id.addressRadioButton);
        }
    }
}