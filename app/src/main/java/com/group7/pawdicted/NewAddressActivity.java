package com.group7.pawdicted;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import com.group7.pawdicted.mobile.models.AddressItem;

public class NewAddressActivity extends AppCompatActivity {

    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
    private Spinner citySpinner, districtSpinner, wardSpinner;
    private SwitchCompat defaultSwitch;
    private Button submitButton;
    private ListView addressListView;

    private Map<String, String> cityCodes = new HashMap<>(); // Map city name to code
    private Map<String, List<String>> cityToDistricts = new HashMap<>(); // Map city to districts
    private Map<String, List<String>> districtToWards = new HashMap<>(); // Map district to wards
    private Map<String, String> districtCodes = new HashMap<>(); // Map district name to code
    private List<AddressItem> addressList = new ArrayList<>();
    private ArrayAdapter<AddressItem> addressAdapter;

    private static final String PREF_NAME = "AddressPrefs";
    private static final String KEY_ADDRESS_LIST = "addressList";
    private static final String TAG = "NewAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        streetEditText = findViewById(R.id.streetEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        defaultSwitch = findViewById(R.id.defaultSwitch);
        submitButton = findViewById(R.id.submitButton);
        addressListView = findViewById(R.id.addressListView);

        // Log to verify button initialization
        Log.d(TAG, "Submit button initialized: " + (submitButton != null));

        // Load all JSON data
        loadCities();
        loadDistricts();
        loadWards();

        // Load existing address list
        addressList = loadAddressList();
        addressAdapter = new ArrayAdapter<AddressItem>(this, android.R.layout.simple_list_item_1, addressList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setText(addressList.get(position).toString());
                }
                return view;
            }
        };
        addressListView.setAdapter(addressAdapter);

        // Set city data to spinner with custom adapter
        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
        if (cities.isEmpty()) {
            Log.e(TAG, "No cities loaded from JSON!");
            cities.add("Select City"); // Fallback if JSON fails
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
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            updateSubmitButtonState();
        };
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

        // Address list click listener for edit/delete
        addressListView.setOnItemClickListener((parent, view, position, id) -> {
            AddressItem selectedAddress = addressList.get(position);
            showEditDeleteDialog(selectedAddress, position);
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
        Log.d(TAG, "Submit button enabled: " + isValid +
                ", FullName: '" + fullName + "', Phone: '" + phone + "', Street: '" + street +
                "', City: '" + city + "', District: '" + district + "', Ward: '" + ward + "'");

        // Feedback if form is invalid
        if (!isValid && street.isEmpty()) {
            streetEditText.setError("Vui lòng nhập địa chỉ cụ thể!");
            Toast.makeText(this, "Vui lòng nhập địa chỉ cụ thể!", Toast.LENGTH_SHORT).show();
        } else if (!isValid) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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

    private void saveNewAddress() {
        Log.d(TAG, "Saving new address...");
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
        String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "";
        String street = streetEditText.getText().toString().trim();
        boolean isDefault = defaultSwitch.isChecked();

        // Basic validation
        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.equals("Select City") ||
                district.equals("Select District") || ward.equals("Select Ward")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed, Fields: FullName='" + fullName + "', Phone='" + phone + "', Street='" + street + "', City='" + city + "', District='" + district + "', Ward='" + ward + "'");
            return;
        }

        // Create full address string
        String addressDetail = street + ", " + ward + ", " + district + ", " + city;
        AddressItem newAddress = new AddressItem(fullName, phone, addressDetail, isDefault);

        // Load existing address list
        addressList = loadAddressList();

        // If new address is default, unset default for others
        if (isDefault) {
            for (AddressItem addr : addressList) {
                addr.setDefault(false);
            }
        }

        // Add new address
        addressList.add(newAddress);
        saveAddressList(addressList);
        addressAdapter.notifyDataSetChanged();
        clearForm();
        Log.d(TAG, "Address saved successfully");

        // Quay lại AddressSelectionActivity
        finish();
    }

    private void showEditDeleteDialog(AddressItem address, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hành động");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Edit
                        editAddress(address, position);
                        break;
                    case 1: // Delete
                        deleteAddress(position);
                        break;
                }
            }
        });
        builder.show();
    }

    private void editAddress(AddressItem address, int position) {
        // Parse address detail to fill form
        String[] parts = address.getAddress().split(", ");
        if (parts.length >= 4) {
            streetEditText.setText(parts[0].trim());
            // Note: Auto-selecting city, district, ward requires parsing logic based on JSON data
            // This is a simplified version; you may need to map back to spinner values
            fullNameEditText.setText(address.getName());
            phoneNumberEditText.setText(address.getPhone());
            defaultSwitch.setChecked(address.isDefault());
            addressList.remove(position);
            addressAdapter.notifyDataSetChanged();
            Log.d(TAG, "Address edited and form filled for position: " + position);
        } else {
            Toast.makeText(this, "Định dạng địa chỉ không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAddress(int position) {
        addressList.remove(position);
        saveAddressList(addressList);
        addressAdapter.notifyDataSetChanged();
        Log.d(TAG, "Address deleted at position: " + position);
        Toast.makeText(this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        fullNameEditText.setText("");
        phoneNumberEditText.setText("");
        streetEditText.setText("");
        citySpinner.setSelection(0);
        districtSpinner.setEnabled(false);
        districtSpinner.setAdapter(null);
        wardSpinner.setEnabled(false);
        wardSpinner.setAdapter(null);
        defaultSwitch.setChecked(false);
        updateSubmitButtonState();
    }

    private List<AddressItem> loadAddressList() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ADDRESS_LIST, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<AddressItem>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveAddressList(List<AddressItem> addressList) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressList);
        editor.putString(KEY_ADDRESS_LIST, json);
        editor.apply();
    }
}