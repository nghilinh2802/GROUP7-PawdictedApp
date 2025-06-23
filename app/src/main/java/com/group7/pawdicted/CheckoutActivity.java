package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.adapters.OrderItemAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.group7.pawdicted.mobile.models.OrderItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Barkbutler x Fofos Cheese Box Interactive Toy for Cats", "ORANGE", "đ240.000", 1, R.mipmap.cat_toy));
        items.add(new OrderItem("Squeeezys Latex Monster Brother Chew Toy for Dogs", "", "đ85.000", 2, R.mipmap.fofos));

        OrderItemAdapter adapter = new OrderItemAdapter(this, items);
        recyclerView.setAdapter(adapter);

        // Hiển thị địa chỉ mặc định khi khởi tạo
        AddressItem defaultAddress = getDefaultAddress();
        if (defaultAddress != null) {
            updateAddressUI(defaultAddress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa vị trí cuối cùng khi thoát để quay về địa chỉ mặc định
        saveLastSelectedPosition(-1);
    }

    public void open_voucher_activity(View view) {
        Intent intent = new Intent(this, VoucherManagementActivity.class);
        startActivity(intent);
    }

    public void open_address_selection_activity(View view) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        // Truyền vị trí cuối cùng được chọn (nếu có)
        int lastSelected = getLastSelectedPosition();
        if (lastSelected != -1) {
            intent.putExtra("lastSelectedPosition", lastSelected);
        }
        startActivityForResult(intent, 200); // Sử dụng request code 200
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            AddressItem selectedAddress = (AddressItem) data.getSerializableExtra("selectedAddress");
            if (selectedAddress != null) {
                updateAddressUI(selectedAddress);
                // Lưu vị trí cuối cùng được chọn
                int lastSelected = data.getIntExtra("lastSelectedPosition", -1);
                if (lastSelected != -1) {
                    saveLastSelectedPosition(lastSelected);
                }
            }
        }
    }

    private AddressItem getDefaultAddress() {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        String json = prefs.getString("addressList", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<AddressItem>>(){}.getType();
        List<AddressItem> addressList = gson.fromJson(json, type);
        if (addressList != null && !addressList.isEmpty()) {
            for (AddressItem address : addressList) {
                if (address.isDefault()) {
                    return address;
                }
            }
            // Nếu không có địa chỉ mặc định, đặt địa chỉ đầu tiên làm mặc định
            if (addressList.size() == 1) {
                addressList.get(0).setDefault(true);
                saveAddressList(addressList);
                return addressList.get(0);
            }
        }
        return null;
    }

    private void saveAddressList(List<AddressItem> addressList) {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressList);
        editor.putString("addressList", json);
        editor.commit();
    }

    private void updateAddressUI(AddressItem address) {
        TextView nameTextView = findViewById(R.id.addressNameTextView);
        TextView phoneTextView = findViewById(R.id.addressPhoneTextView);
        TextView addressTextView = findViewById(R.id.addressDetailTextView);
        if (nameTextView != null && phoneTextView != null && addressTextView != null) {
            nameTextView.setText(address.getName());
            phoneTextView.setText(address.getPhone());
            addressTextView.setText(address.getAddress());
        }
    }

    private int getLastSelectedPosition() {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        return prefs.getInt("lastSelectedPosition", -1);
    }

    private void saveLastSelectedPosition(int position) {
        SharedPreferences prefs = getSharedPreferences("CheckoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("lastSelectedPosition", position);
        editor.apply();
    }
}