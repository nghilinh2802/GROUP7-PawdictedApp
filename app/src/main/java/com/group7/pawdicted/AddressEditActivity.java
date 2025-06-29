package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.group7.pawdicted.mobile.models.AddressItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
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

        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        streetEditText = findViewById(R.id.streetEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        submitButton = findViewById(R.id.submitButton);
        deleteButton = findViewById(R.id.deleteButton);
        defaultSwitch = findViewById(R.id.defaultSwitch);

        // Get AddressItem and position from Intent
        Intent intent = getIntent();
        addressItem = intent.getParcelableExtra("addressItem");
        position = intent.getIntExtra("position", -1);

        // Load JSON data
        loadCities();
        loadDistricts();
        loadWards();

        // Setup spinners
        setupSpinners();

        // Pre-fill form with AddressItem data
        if (addressItem != null) {
            fullNameEditText.setText(addressItem.getName());
            phoneNumberEditText.setText(addressItem.getPhone());
            defaultSwitch.setChecked(addressItem.isDefault());
            prefillAddressFields(addressItem.getAddress());
        }

        // Set listeners for form validation
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> updateSubmitButtonState();
        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
        streetEditText.setOnFocusChangeListener(focusChangeListener);
        streetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSubmitButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set click listeners
        submitButton.setOnClickListener(v -> saveEditedAddress());
        deleteButton.setOnClickListener(v -> deleteAddress());
    }

    private void setupSpinners() {
        // Set city data to spinner
        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
        if (cities.isEmpty()) {
            Log.e(TAG, "No cities loaded from JSON!");
            cities.add("Select City");
        } else {
            cities.add(0, "Select City");
        }
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
        citySpinner.setSelection(0);

        // Disable district and ward spinners initially
        districtSpinner.setEnabled(false);
        wardSpinner.setEnabled(false);

        // City spinner listener
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCity = cities.get(position);
                    districtSpinner.setEnabled(true);
                    List<String> districts = cityToDistricts.get(selectedCity);
                    if (districts != null && !districts.isEmpty()) {
                        districts = new ArrayList<>(districts); // Create a new list to avoid modifying original
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
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(districtAdapter);
                        districtSpinner.setSelection(0);
                    } else {
                        Log.w(TAG, "No districts found for city: " + selectedCity);
                        districtSpinner.setEnabled(false);
                        districtSpinner.setAdapter(null);
                        wardSpinner.setEnabled(false);
                        wardSpinner.setAdapter(null);
                    }
                } else {
                    districtSpinner.setEnabled(false);
                    wardSpinner.setEnabled(false);
                    districtSpinner.setAdapter(null);
                    wardSpinner.setAdapter(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // District spinner listener
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    wardSpinner.setEnabled(true);
                    List<String> wards = districtToWards.get(selectedDistrict);
                    if (wards != null && !wards.isEmpty()) {
                        wards = new ArrayList<>(wards); // Create a new list to avoid modifying original
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
                        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        wardSpinner.setAdapter(wardAdapter);
                        wardSpinner.setSelection(0);
                    } else {
                        Log.w(TAG, "No wards found for district: " + selectedDistrict);
                        wardSpinner.setEnabled(false);
                        wardSpinner.setAdapter(null);
                    }
                } else {
                    wardSpinner.setEnabled(false);
                    wardSpinner.setAdapter(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Ward spinner listener
        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSubmitButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void prefillAddressFields(String address) {
        // Parse address in format: "street, Phường ward, Quận district, city"
        try {
            String[] parts = address.split(", ");
            if (parts.length >= 4) {
                streetEditText.setText(parts[0].trim());
                String ward = parts[1].replace("Phường ", "").trim();
                String district = parts[2].replace("Quận ", "").trim();
                String city = parts[3].trim();

                // Set city spinner
                ArrayAdapter<String> cityAdapter = (ArrayAdapter<String>) citySpinner.getAdapter();
                int cityPos = cityAdapter.getPosition(city);
                if (cityPos >= 0) {
                    citySpinner.setSelection(cityPos);
                } else {
                    Log.w(TAG, "City not found in spinner: " + city);
                }

                // Trigger district spinner population
                if (cityPos > 0) {
                    List<String> districts = cityToDistricts.get(city);
                    if (districts != null && !districts.isEmpty()) {
                        districts = new ArrayList<>(districts);
                        districts.add(0, "Select District");
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districts) {
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
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(districtAdapter);

                        // Set district spinner
                        int districtPos = districtAdapter.getPosition(district);
                        if (districtPos >= 0) {
                            districtSpinner.setSelection(districtPos);
                        } else {
                            Log.w(TAG, "District not found in spinner: " + district);
                        }

                        // Trigger ward spinner population
                        if (districtPos > 0) {
                            List<String> wards = districtToWards.get(district);
                            if (wards != null && !wards.isEmpty()) {
                                wards = new ArrayList<>(wards);
                                wards.add(0, "Select Ward");
                                ArrayAdapter<String> wardAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wards) {
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
                                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                wardSpinner.setAdapter(wardAdapter);

                                // Set ward spinner
                                int wardPos = wardAdapter.getPosition(ward);
                                if (wardPos >= 0) {
                                    wardSpinner.setSelection(wardPos);
                                } else {
                                    Log.w(TAG, "Ward not found in spinner: " + ward);
                                }
                            }
                        }
                    }
                }
            } else {
                Log.e(TAG, "Invalid address format: " + address);
                Toast.makeText(this, "Định dạng địa chỉ không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing address: " + address, e);
            Toast.makeText(this, "Lỗi khi phân tích địa chỉ!", Toast.LENGTH_SHORT).show();
        }
        updateSubmitButtonState();
    }

    private void updateSubmitButtonState() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "Select City";
        String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "Select District";
        String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "Select Ward";

        boolean isValid = !fullName.isEmpty() && !phone.isEmpty() && !street.isEmpty() &&
                !city.equals("Select City") && !district.equals("Select District") && !ward.equals("Select Ward");

        submitButton.setEnabled(isValid);
        Log.d(TAG, "Submit button enabled: " + isValid +
                ", FullName: '" + fullName + "', Phone: '" + phone + "', Street: '" + street +
                "', City: '" + city + "', District: '" + district + "', Ward: '" + ward + "'");

        if (!isValid && street.isEmpty()) {
            streetEditText.setError("Vui lòng nhập địa chỉ cụ thể!");
            Toast.makeText(this, "Vui lòng nhập địa chỉ cụ thể!", Toast.LENGTH_SHORT).show();
        } else if (!isValid) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
        String addressDetail = street + ", Phường " + ward + ", Quận " + district + ", " + city;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("name", fullName);
        data.put("phone", phone);
        data.put("address", addressDetail);
        data.put("isDefault", isDefault);
        data.put("time", com.google.firebase.Timestamp.now());

        if (isDefault) {
            // Clear existing default address
            db.collection("addresses")
                    .document(customerId)
                    .collection("items")
                    .whereEqualTo("isDefault", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            doc.getReference().update("isDefault", false);
                        }
                        // Save updated address
                        db.collection("addresses")
                                .document(customerId)
                                .collection("items")
                                .document(addressItem.getId())
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("updatedPosition", position);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Lỗi khi lưu Firestore", e);
                                    Toast.makeText(this, "Lỗi khi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi khi tìm địa chỉ mặc định", e);
                        Toast.makeText(this, "Lỗi khi kiểm tra địa chỉ mặc định!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Save updated address directly
            db.collection("addresses")
                    .document(customerId)
                    .collection("items")
                    .document(addressItem.getId())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedPosition", position);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi khi lưu Firestore", e);
                        Toast.makeText(this, "Lỗi khi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
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
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deletedPosition", position);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi khi xóa Firestore", e);
                        Toast.makeText(this, "Lỗi khi xóa địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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