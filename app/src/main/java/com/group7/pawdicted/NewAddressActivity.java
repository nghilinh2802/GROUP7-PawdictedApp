package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.group7.pawdicted.mobile.models.AddressItem;

public class NewAddressActivity extends AppCompatActivity {

    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
    private Spinner citySpinner, districtSpinner, wardSpinner;
    private SwitchCompat defaultSwitch;
    private Button submitButton;

    private Map<String, String> cityCodes = new HashMap<>();
    private Map<String, List<String>> cityToDistricts = new HashMap<>();
    private Map<String, List<String>> districtToWards = new HashMap<>();
    private Map<String, String> districtCodes = new HashMap<>();
    private static final String TAG = "NewAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        // Initialize views
        ImageView imgBack = findViewById(R.id.backArrow);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        streetEditText = findViewById(R.id.streetEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        defaultSwitch = findViewById(R.id.defaultSwitch);
        submitButton = findViewById(R.id.submitButton);

        // Log initialization
        Log.d(TAG, "Views initialized: submitButton=" + (submitButton != null));

        // Back button
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        // Load JSON data
        loadCities();
        loadDistricts();
        loadWards();

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

        // Set click listener for submit button
        submitButton.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked");
            saveNewAddress();
        });

        // Enable submit button when form is filled
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> updateSubmitButtonState();
        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
        streetEditText.setOnFocusChangeListener(focusChangeListener);
        streetEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSubmitButtonState();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // City spinner listener
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCity = cities.get(position);
                    districtSpinner.setEnabled(true);
                    List<String> districts = cityToDistricts.get(selectedCity);
                    if (districts != null && !districts.isEmpty()) {
                        districts.add(0, "Select District");
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(NewAddressActivity.this, android.R.layout.simple_spinner_item, districts) {
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
                        wards.add(0, "Select Ward");
                        ArrayAdapter<String> wardAdapter = new ArrayAdapter<String>(NewAddressActivity.this, android.R.layout.simple_spinner_item, wards) {
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
        Log.d(TAG, "Submit button state updated: isValid=" + isValid +
                ", FullName='" + fullName + "', Phone='" + phone + "', Street='" + street +
                "', City='" + city + "', District='" + district + "', Ward='" + ward + "'");

        if (!isValid) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNewAddress() {
        Log.d(TAG, "Saving new address...");
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
        String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "";
        String street = streetEditText.getText().toString().trim();
        boolean isDefault = defaultSwitch.isChecked();

        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.equals("Select City") ||
                district.equals("Select District") || ward.equals("Select Ward")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed, Fields: FullName='" + fullName + "', Phone='" + phone + "', Street='" + street +
                    "', City='" + city + "', District='" + district + "', Ward='" + ward + "'");
            return;
        }

        String addressDetail = street + ", Phường " + ward + ", Quận " + district + ", " + city;
        AddressItem newAddress = new AddressItem(fullName, phone, addressDetail, isDefault, Timestamp.now());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No user logged in");
            return;
        }

        String customerId = user.getUid();
        String addressId = UUID.randomUUID().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("name", fullName);
        data.put("phone", phone);
        data.put("address", addressDetail);
        data.put("isDefault", isDefault);
        data.put("time", Timestamp.now());

        if (isDefault) {
            db.collection("addresses")
                    .document(customerId)
                    .collection("items")
                    .whereEqualTo("isDefault", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            doc.getReference().update("isDefault", false)
                                    .addOnFailureListener(e -> Log.e(TAG, "Error clearing isDefault for address " + doc.getId(), e));
                        }
                        saveAddressToFirestore(db, customerId, addressId, data);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching existing default addresses", e);
                        Toast.makeText(this, "Lỗi khi kiểm tra địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveAddressToFirestore(db, customerId, addressId, data);
        }
    }

    private void saveAddressToFirestore(FirebaseFirestore db, String customerId, String addressId, Map<String, Object> data) {
        db.collection("addresses")
                .document(customerId)
                .collection("items")
                .document(addressId)
                .set(data, SetOptions.merge())
//                .addOnSuccessListener(unused -> {
//                    Log.d(TAG, "Address saved successfully: " + addressId);
//                    Toast.makeText(this, "Đã lưu địa chỉ mới", Toast.LENGTH_SHORT).show();
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("addressId", addressId);
//                    setResult(RESULT_OK, resultIntent);
//                    finish();
//                })
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã lưu địa chỉ mới", Toast.LENGTH_SHORT).show();

                    // Nếu đến từ CartActivity → setResult để quay lại
                    Intent fromCart = getIntent();
                    if (getIntent().getBooleanExtra("fromCart", false)) {
                        setResult(RESULT_OK);
                    }

                    finish();
                })

                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving address to Firestore", e);
                    Toast.makeText(this, "Lỗi khi lưu địa chỉ!", Toast.LENGTH_SHORT).show();
                });
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
            Log.e(TAG, "Error loading wards: " + e.getMessage());
        }
    }
}