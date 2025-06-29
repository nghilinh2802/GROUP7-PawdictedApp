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
    private int selectedPosition = -1;
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

        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                int previousSelected = selectedPosition;
                selectedPosition = clickedPosition;

                if (previousSelected != -1) notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                if (addressList.size() > 1) {
                    AddressItem selectedItem = addressList.get(clickedPosition);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedAddress", selectedItem);
                    resultIntent.putExtra("lastSelectedPosition", clickedPosition);
                    activity.setResult(RESULT_OK, resultIntent);
                    activity.finish();
                }
            }
        });

        holder.editAddress.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                AddressItem itemToEdit = addressList.get(clickedPosition);
                Intent intent = new Intent(context, AddressEditActivity.class);
                intent.putExtra("addressId", itemToEdit.getId());
                intent.putExtra("addressItem", itemToEdit);
                intent.putExtra("position", clickedPosition);

                if (context instanceof AddressSelectionActivity) {
                    ((AddressSelectionActivity) context).startActivityForResult(intent, 100);
                } else {
                    context.startActivity(intent);
                }
            }
        });

        if (addressItem.isDefault()) {
            holder.defaultAddress.setVisibility(View.VISIBLE);
        } else {
            holder.defaultAddress.setVisibility(View.GONE);
        }
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

    public void updateData(List<AddressItem> newList) {
        this.addressList.clear();
        this.addressList.addAll(newList);
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        if (previousSelected != -1) notifyItemChanged(previousSelected);
        if (selectedPosition != -1) notifyItemChanged(selectedPosition);
    }
}