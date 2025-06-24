package com.group7.pawdicted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.adapters.AddressAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<AddressItem> addressList;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address_selection);

        // Áp dụng WindowInsets cho root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tải danh sách địa chỉ
        addressList = loadAddressList();
        if (addressList.isEmpty()) {
            // Thêm địa chỉ mặc định nếu danh sách trống
            addressList.add(new AddressItem("Lê Nguyễn Hà Châu", "0967 663 867",
                    "2 Đồng Khởi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh", true));
            saveAddressList(addressList);
        } else if (addressList.size() == 1) {
            addressList.get(0).setDefault(true);
            saveAddressList(addressList);
        }

        // Khởi tạo AddressAdapter với context và activity
        addressAdapter = new AddressAdapter(addressList, this, this);
        recyclerView.setAdapter(addressAdapter);

        // Nếu chỉ có 1 địa chỉ và là mặc định, chọn RadioButton tự động
        if (addressList.size() == 1 && addressList.get(0).isDefault()) {
            addressAdapter.setSelectedPosition(0); // Chọn RadioButton của địa chỉ đầu tiên
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách từ SharedPreferences khi quay lại màn hình
        addressList = loadAddressList();
        addressAdapter.updateData(addressList);

        // Kiểm tra và khôi phục vị trí RadioButton được chọn từ CheckoutActivity
        Intent intent = getIntent();
        int lastSelected = intent.getIntExtra("lastSelectedPosition", -1);
        if (lastSelected >= 0 && lastSelected < addressList.size()) {
            addressAdapter.setSelectedPosition(lastSelected);
        } else {
            // Nếu không có lastSelected hợp lệ, đặt lại thành vị trí của địa chỉ mặc định
            int defaultPosition = getDefaultAddressPosition();
            if (defaultPosition != -1) {
                addressAdapter.setSelectedPosition(defaultPosition);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Tải lại danh sách từ SharedPreferences để đảm bảo đồng bộ
            addressList = loadAddressList();
            addressAdapter.updateData(addressList);
        }
    }

    private List<AddressItem> loadAddressList() {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        String json = prefs.getString("addressList", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<AddressItem>>(){}.getType();
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    private void saveAddressList(List<AddressItem> addressList) {
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressList);
        editor.putString("addressList", json);
        editor.commit(); // Đảm bảo lưu ngay
    }

    public void open_new_address_activity(View view) {
        Intent intent = new Intent(this, NewAddressActivity.class);
        startActivityForResult(intent, 100); // Sử dụng startActivityForResult để đồng bộ
    }

    private int getDefaultAddressPosition() {
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                return i;
            }
        }
        return -1; // Không tìm thấy địa chỉ mặc định
    }
}