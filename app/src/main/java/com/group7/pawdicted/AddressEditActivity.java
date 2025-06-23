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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group7.pawdicted.mobile.models.AddressItem;

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

        // Khởi tạo các view
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        streetEditText = findViewById(R.id.streetEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        submitButton = findViewById(R.id.submitButton);
        deleteButton = findViewById(R.id.deleteButton);
        defaultSwitch = findViewById(R.id.defaultSwitch);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        addressItem = (AddressItem) intent.getSerializableExtra("addressItem");
        position = intent.getIntExtra("position", -1);

        // Hiển thị dữ liệu hiện có nếu đang chỉnh sửa
        if (addressItem != null) {
            fullNameEditText.setText(addressItem.getName());
            phoneNumberEditText.setText(addressItem.getPhone());
            defaultSwitch.setChecked(addressItem.isDefault());
            String[] parts = addressItem.getAddress().split(", ");
            if (parts.length >= 4) {
                streetEditText.setText(parts[0].trim());
            }
        }

        // Tải dữ liệu JSON
        loadCities();
        loadDistricts();
        loadWards();

        // Thiết lập spinner city
        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
        cities.add(0, "Select City");
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                    view.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
                return view;
            }
        };
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // Listener cho city spinner
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCity = cities.get(position);
                    List<String> districts = cityToDistricts.get(selectedCity);
                    if (districts != null && !districts.isEmpty()) {
                        districts.add(0, "Select District");
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(AddressEditActivity.this, android.R.layout.simple_spinner_item, districts) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                                }
                                return view;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                                    view.setBackgroundColor(getResources().getColor(android.R.color.white));
                                }
                                return view;
                            }
                        };
                        districtSpinner.setAdapter(districtAdapter);
                        districtSpinner.setSelection(0);
                        // Chọn district hiện tại nếu có
                        if (addressItem != null) {
                            String[] parts = addressItem.getAddress().split(", ");
                            if (parts.length >= 4) {
                                setSpinnerSelection(districtSpinner, parts[2].trim());
                            }
                        }
                    }
                } else {
                    districtSpinner.setAdapter(null);
                    wardSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener cho district spinner
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    List<String> wards = districtToWards.get(selectedDistrict);
                    if (wards != null && !wards.isEmpty()) {
                        wards.add(0, "Select Ward");
                        ArrayAdapter<String> wardAdapter = new ArrayAdapter<String>(AddressEditActivity.this, android.R.layout.simple_spinner_item, wards) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                                }
                                return view;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                                    view.setBackgroundColor(getResources().getColor(android.R.color.white));
                                }
                                return view;
                            }
                        };
                        wardSpinner.setAdapter(wardAdapter);
                        wardSpinner.setSelection(0);
                        // Chọn ward hiện tại nếu có
                        if (addressItem != null) {
                            String[] parts = addressItem.getAddress().split(", ");
                            if (parts.length >= 4) {
                                setSpinnerSelection(wardSpinner, parts[1].trim());
                            }
                        }
                    }
                } else {
                    wardSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Thiết lập giá trị ban đầu cho spinner city
        if (addressItem != null) {
            String[] parts = addressItem.getAddress().split(", ");
            if (parts.length >= 4) {
                setSpinnerSelection(citySpinner, parts[3].trim());
            }
        }

        // Listener cho nút submit
        submitButton.setOnClickListener(v -> saveEditedAddress());

        // Listener cho nút delete
        deleteButton.setOnClickListener(v -> deleteAddress());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveEditedAddress() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "Select District";
        String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "Select Ward";
        boolean isDefault = defaultSwitch.isChecked();

        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.equals("Select City") ||
                district.equals("Select District") || ward.equals("Select Ward")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressDetail = street + ", " + ward + ", " + district + ", " + city;
        addressItem.setName(fullName);
        addressItem.setPhone(phone);
        addressItem.setAddress(addressDetail);
        addressItem.setDefault(isDefault);

        // Load existing address list from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
        String json = prefs.getString("addressList", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<AddressItem>>(){}.getType();
        List<AddressItem> addressList = gson.fromJson(json, type);
        if (addressList == null) {
            addressList = new ArrayList<>();
        }

        // Log trạng thái trước khi thay đổi
        Log.d(TAG, "Before update - Address list: " + addressList);
        Log.d(TAG, "Before update - Default addresses: " + getDefaultAddresses(addressList));

        // Unset all other addresses if this one is set as default (similar to NewAddressActivity)
        if (isDefault) {
            for (AddressItem addr : addressList) {
                addr.setDefault(false);
            }
            addressItem.setDefault(true); // Set current address as default
        }

        // Update the edited address
        if (position >= 0 && position < addressList.size()) {
            addressList.set(position, addressItem);
        } else {
            addressList.add(addressItem); // Case for new address if position is invalid
            position = addressList.size() - 1;
        }

        // Log trạng thái sau khi thay đổi
        Log.d(TAG, "After update - Address list: " + addressList);
        Log.d(TAG, "After update - Default addresses: " + getDefaultAddresses(addressList));

        // Save the updated list back to SharedPreferences
        String updatedJson = gson.toJson(addressList);
        prefs.edit().putString("addressList", updatedJson).commit(); // Sử dụng commit để đảm bảo lưu ngay

        // Trả về kết quả và quay lại AddressSelectionActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("editedAddress", addressItem);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
        finish(); // Quay lại AddressSelectionActivity
    }

    private String getDefaultAddresses(List<AddressItem> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDefault()) {
                sb.append("Position ").append(i).append(": ").append(list.get(i).getAddress()).append(", isDefault=").append(list.get(i).isDefault()).append("; ");
            }
        }
        return sb.toString();
    }

    private void deleteAddress() {
        if (addressItem != null && position >= 0) {
            // Hiển thị dialog xác nhận xóa
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Load existing address list to remove the item
                            SharedPreferences prefs = getSharedPreferences("AddressPrefs", MODE_PRIVATE);
                            String json = prefs.getString("addressList", "[]");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AddressItem>>(){}.getType();
                            List<AddressItem> addressList = gson.fromJson(json, type);
                            if (addressList != null && position < addressList.size()) {
                                addressList.remove(position);
                                String updatedJson = gson.toJson(addressList);
                                prefs.edit().putString("addressList", updatedJson).commit(); // Sử dụng commit
                                setResult(RESULT_OK, new Intent().putExtra("position", position)); // Notify deletion
                                finish();
                            } else {
                                Toast.makeText(AddressEditActivity.this, "Không thể xóa địa chỉ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Đóng dialog nếu chọn No
                        }
                    })
                    .setCancelable(true)
                    .show();
        }
    }

    private void loadCities() {
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String code = jsonArray.getJSONObject(i).getString("code");
                cityCodes.put(name, code);
                cityToDistricts.put(name, new ArrayList<>());
            }
            Log.d(TAG, "Cities loaded: " + cityToDistricts.keySet());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading cities: " + e.getMessage());
        }
    }

    private void loadDistricts() {
        try {
            InputStream inputStream = getAssets().open("districts.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String code = jsonArray.getJSONObject(i).getString("code");
                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
                districtCodes.put(name, code);
                for (Map.Entry<String, String> entry : cityCodes.entrySet()) {
                    if (entry.getValue().equals(parentCode)) {
                        cityToDistricts.get(entry.getKey()).add(name);
                        break;
                    }
                }
            }
            Log.d(TAG, "Districts loaded for cities: " + cityToDistricts);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading districts: " + e.getMessage());
        }
    }

    private void loadWards() {
        try {
            InputStream inputStream = getAssets().open("wards.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
                for (Map.Entry<String, String> entry : districtCodes.entrySet()) {
                    if (entry.getValue().equals(parentCode)) {
                        String districtName = entry.getKey();
                        districtToWards.computeIfAbsent(districtName, k -> new ArrayList<>()).add(name);
                        break;
                    }
                }
            }
            Log.d(TAG, "Wards loaded: " + districtToWards);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading wards: " + e.getMessage());
        }
    }
}