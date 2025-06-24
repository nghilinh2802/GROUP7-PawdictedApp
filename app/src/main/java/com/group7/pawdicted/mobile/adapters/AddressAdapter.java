package com.group7.pawdicted.mobile.adapters;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.AddressEditActivity;
import com.group7.pawdicted.AddressSelectionActivity;
import com.group7.pawdicted.CheckoutActivity;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.AddressItem;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressItem> addressList;
    private int selectedPosition = -1; // Theo dõi vị trí được chọn tạm thời
    private Context context;
    private AddressSelectionActivity activity;

    public AddressAdapter(List<AddressItem> addressList, Context context, AddressSelectionActivity activity) {
        this.addressList = addressList;
        this.context = context;
        this.activity = activity;
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

        // Đặt trạng thái checked cho RadioButton dựa trên selectedPosition
        holder.radioButton.setChecked(position == selectedPosition);

        // Xử lý sự kiện click cho RadioButton để chọn tạm thời
        holder.radioButton.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected); // Cập nhật item cũ
            }
            notifyItemChanged(position); // Cập nhật item mới

            // Nếu có nhiều hơn 1 địa chỉ, quay về CheckoutActivity với địa chỉ được chọn
            if (addressList.size() > 1) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedAddress", addressItem);
                resultIntent.putExtra("lastSelectedPosition", position); // Lưu vị trí đã chọn
                activity.setResult(RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        // Xử lý sự kiện click cho editAddress
        holder.editAddress.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddressEditActivity.class);
            intent.putExtra("addressItem", addressList.get(holder.getAdapterPosition()));
            intent.putExtra("position", holder.getAdapterPosition());
            if (context instanceof AddressSelectionActivity) {
                ((AddressSelectionActivity) context).startActivityForResult(intent, 100);
            } else {
                context.startActivity(intent);
            }
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

    // Phương thức để cập nhật danh sách
    public void updateData(List<AddressItem> newList) {
        this.addressList = newList;
        notifyDataSetChanged();
    }

    // Phương thức để thiết lập vị trí RadioButton được chọn
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected); // Cập nhật item cũ
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition); // Cập nhật item mới
        }
    }
}