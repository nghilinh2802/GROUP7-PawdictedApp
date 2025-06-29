package com.group7.pawdicted;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.models.AddressItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressEditActivity extends AppCompatActivity {
    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
    private Spinner citySpinner, districtSpinner, wardSpinner;
    private Button submitButton, deleteButton;
    private SwitchCompat defaultSwitch;
    private Map<String, String> cityCodes = new HashMap<>();
    private Map<String, List<String>> cityToDistricts = new HashMap<>();
    private Map<String, List<String>> districtToWards = new HashMap<>();
    private Map<String, String> districtCodes = new HashMap<>();
    private AddressItem addressItem;
    private int position;
    private static final String TAG = "AddressEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        streetEditText = findViewById(R.id.streetEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        submitButton = findViewById(R.id.submitButton);
        deleteButton = findViewById(R.id.deleteButton);
        defaultSwitch = findViewById(R.id.defaultSwitch);

        Intent intent = getIntent();
        addressItem = (AddressItem) intent.getSerializableExtra("addressItem");
        position = intent.getIntExtra("position", -1);

        if (addressItem != null) {
            fullNameEditText.setText(addressItem.getName());
            phoneNumberEditText.setText(addressItem.getPhone());
            defaultSwitch.setChecked(addressItem.isDefault());
            String[] parts = addressItem.getAddress().split(", ");
            if (parts.length >= 4) {
                streetEditText.setText(parts[0].trim());
            }
        }

        loadCities();
        loadDistricts();
        loadWards();

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDistrictSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateWardSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submitButton.setOnClickListener(v -> saveEditedAddress());
        deleteButton.setOnClickListener(v -> deleteAddress());
    }

    private void updateDistrictSpinner() {
        String city = citySpinner.getSelectedItem().toString();
        List<String> districts = cityToDistricts.get(city);
        if (districts != null) {
            districts.add(0, "Select District");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(adapter);
        }
    }

    private void updateWardSpinner() {
        String district = districtSpinner.getSelectedItem().toString();
        List<String> wards = districtToWards.get(district);
        if (wards != null) {
            wards.add(0, "Select Ward");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wards);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            wardSpinner.setAdapter(adapter);
        }
    }

    private void saveEditedAddress() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String district = districtSpinner.getSelectedItem().toString();
        String ward = wardSpinner.getSelectedItem().toString();
        boolean isDefault = defaultSwitch.isChecked();

        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.equals("Select City") ||
                district.equals("Select District") || ward.equals("Select Ward")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerId = user.getUid();
        String addressDetail = street + ", " + ward + ", " + district + ", " + city;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("name", fullName);
        data.put("phone", phone);
        data.put("address", addressDetail);
        data.put("isDefault", isDefault);
        data.put("time", Timestamp.now());

        db.collection("addresses")
                .document(customerId)
                .collection("items")
                .document(addressItem.getId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteAddress() {
        if (addressItem != null && addressItem.getId() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            String customerId = user.getUid();
            FirebaseFirestore.getInstance()
                    .collection("addresses")
                    .document(customerId)
                    .collection("items")
                    .document(addressItem.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi xóa địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadCities() { /* giữ nguyên */ }
    private void loadDistricts() { /* giữ nguyên */ }
    private void loadWards() { /* giữ nguyên */ }
}
