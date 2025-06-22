package com.group7.pawdicted;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAddressActivity extends AppCompatActivity {

    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
    private Spinner citySpinner, districtSpinner, wardSpinner;
    private SwitchCompat defaultSwitch;
    private Button submitButton;

    // Maps to store hierarchical data
    private Map<String, String> cityCodes = new HashMap<>(); // Map city name to code
    private Map<String, List<String>> cityToDistricts = new HashMap<>(); // Map city to districts
    private Map<String, List<String>> districtToWards = new HashMap<>(); // Map district to wards
    private Map<String, String> districtCodes = new HashMap<>(); // Map district name to code

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

        // Load all JSON data
        loadCities();
        loadDistricts();
        loadWards();

        // Set city data to spinner with custom adapter for black text and white background
        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
        cities.add(0, "Select City"); // Add hint as first item
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
                    view.setBackgroundColor(getResources().getColor(android.R.color.white)); // Set white background
                }
                return view;
            }
        };
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setSelection(0); // Set "Select City" as default

        // Disable district and ward spinners initially
        districtSpinner.setEnabled(false);
        wardSpinner.setEnabled(false);

        // Set click listener for submit button
        submitButton.setOnClickListener(v -> {
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
                // TODO: Show error (e.g., Toast)
                return;
            }

            // TODO: Implement save logic (e.g., to database or API)
            System.out.println("Address: " + fullName + ", " + phone + ", " + city + ", " + district + ", " + ward + ", " + street + ", Default: " + isDefault);
        });

        // Enable submit button when form is filled
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            boolean isValid = !fullNameEditText.getText().toString().trim().isEmpty() &&
                    !phoneNumberEditText.getText().toString().trim().isEmpty() &&
                    !streetEditText.getText().toString().trim().isEmpty() &&
                    !citySpinner.getSelectedItem().toString().equals("Select City");
            submitButton.setEnabled(isValid);
        };
        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
        streetEditText.setOnFocusChangeListener(focusChangeListener);

        // City spinner listener
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCity = cities.get(position);
                    districtSpinner.setEnabled(true);
                    List<String> districts = new ArrayList<>(cityToDistricts.get(selectedCity));
                    if (districts != null && !districts.isEmpty()) {
                        districts.add(0, "Select District"); // Add hint
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
                                    view.setBackgroundColor(getResources().getColor(android.R.color.white)); // Set white background
                                }
                                return view;
                            }
                        };
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(districtAdapter);
                        districtSpinner.setSelection(0); // Set "Select District" as default
                    }
                } else {
                    districtSpinner.setEnabled(false);
                    wardSpinner.setEnabled(false);
                    districtSpinner.setAdapter(null);
                    wardSpinner.setAdapter(null);
                }
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
                        wards.add(0, "Select Ward"); // Add hint
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
                                    view.setBackgroundColor(getResources().getColor(android.R.color.white)); // Set white background
                                }
                                return view;
                            }
                        };
                        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        wardSpinner.setAdapter(wardAdapter);
                        wardSpinner.setSelection(0); // Set "Select Ward" as default
                    }
                } else {
                    wardSpinner.setEnabled(false);
                    wardSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
//////package com.group7.pawdicted;
//////
//////import android.os.Bundle;
//////import android.view.View;
//////import android.widget.AdapterView;
//////import android.widget.ArrayAdapter;
//////import android.widget.Button;
//////import android.widget.EditText;
//////import android.widget.Spinner;
//////import androidx.appcompat.widget.SwitchCompat; // Thay Switch bằng SwitchCompat
//////import androidx.appcompat.app.AppCompatActivity;
//////import org.json.JSONArray;
//////import org.json.JSONException;
//////import java.io.IOException;
//////import java.io.InputStream;
//////import java.nio.charset.StandardCharsets;
//////import java.util.ArrayList;
//////import java.util.List;
//////
//////public class NewAddressActivity extends AppCompatActivity {
//////
//////    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
//////    private Spinner citySpinner, districtSpinner, wardSpinner;
//////    private SwitchCompat defaultSwitch; // Sử dụng SwitchCompat
//////    private Button submitButton;
//////
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_new_address);
//////
//////        // Initialize views
//////        fullNameEditText = findViewById(R.id.fullNameEditText);
//////        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
//////        streetEditText = findViewById(R.id.streetEditText);
//////        citySpinner = findViewById(R.id.citySpinner);
//////        districtSpinner = findViewById(R.id.districtSpinner);
//////        wardSpinner = findViewById(R.id.wardSpinner);
//////        defaultSwitch = findViewById(R.id.defaultSwitch); // Không cần cast
//////        submitButton = findViewById(R.id.submitButton);
//////
//////        // Load and set city data
//////        List<String> cities = loadProvincesFromJson();
//////        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
//////        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//////        citySpinner.setAdapter(cityAdapter);
//////
//////        // Disable district and ward spinners initially
//////        districtSpinner.setEnabled(false);
//////        wardSpinner.setEnabled(false);
//////
//////        // Set click listener for submit button
//////        submitButton.setOnClickListener(v -> {
//////            String fullName = fullNameEditText.getText().toString();
//////            String phone = phoneNumberEditText.getText().toString();
//////            String city = citySpinner.getSelectedItem().toString();
//////            String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
//////            String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "";
//////            String street = streetEditText.getText().toString();
//////            boolean isDefault = defaultSwitch.isChecked();
//////
//////            // TODO: Implement save logic (e.g., to database or API)
//////            // Example: Log or show toast with data
//////            System.out.println("Address: " + fullName + ", " + phone + ", " + city + ", " + district + ", " + ward + ", " + street + ", Default: " + isDefault);
//////        });
//////
//////        // Enable submit button when form is filled (basic validation)
//////        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
//////            boolean isValid = !fullNameEditText.getText().toString().isEmpty() &&
//////                    !phoneNumberEditText.getText().toString().isEmpty() &&
//////                    !streetEditText.getText().toString().isEmpty() &&
//////                    citySpinner.getSelectedItemPosition() > 0;
//////            submitButton.setEnabled(isValid);
//////        };
//////        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
//////        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
//////        streetEditText.setOnFocusChangeListener(focusChangeListener);
//////        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//////            @Override
//////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//////                if (position > 0) {
//////                    // TODO: Load districts based on selected city
//////                    districtSpinner.setEnabled(true);
//////                    // Example: Load dummy districts
//////                    List<String> districts = new ArrayList<>();
//////                    districts.add("Select District");
//////                    districts.add("District 1");
//////                    districts.add("District 2");
//////                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, districts);
//////                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//////                    districtSpinner.setAdapter(districtAdapter);
//////                } else {
//////                    districtSpinner.setEnabled(false);
//////                    wardSpinner.setEnabled(false);
//////                }
//////            }
//////
//////            @Override
//////            public void onNothingSelected(AdapterView<?> parent) {}
//////        });
//////        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//////            @Override
//////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//////                if (position > 0) {
//////                    // TODO: Load wards based on selected district
//////                    wardSpinner.setEnabled(true);
//////                    // Example: Load dummy wards
//////                    List<String> wards = new ArrayList<>();
//////                    wards.add("Select Ward");
//////                    wards.add("Ward A");
//////                    wards.add("Ward B");
//////                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, wards);
//////                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//////                    wardSpinner.setAdapter(wardAdapter);
//////                } else {
//////                    wardSpinner.setEnabled(false);
//////                }
//////            }
//////
//////            @Override
//////            public void onNothingSelected(AdapterView<?> parent) {}
//////        });
//////    }
//////
//////    private List<String> loadProvincesFromJson() {
//////        List<String> provinceList = new ArrayList<>();
//////        provinceList.add("Select City");
//////        try {
//////            InputStream inputStream = getAssets().open("cities.json");
//////            int size = inputStream.available();
//////            byte[] buffer = new byte[size];
//////            inputStream.read(buffer);
//////            inputStream.close();
//////            String jsonString = new String(buffer, StandardCharsets.UTF_8);
//////            JSONArray jsonArray = new JSONArray(jsonString);
//////            for (int i = 0; i < jsonArray.length(); i++) {
//////                provinceList.add(jsonArray.getJSONObject(i).getString("name"));
//////            }
//////        } catch (IOException | JSONException e) {
//////            e.printStackTrace();
//////        }
//////        return provinceList;
//////    }
//////}
////
////package com.group7.pawdicted;
////
////import android.os.Bundle;
////import android.view.View;
////import android.widget.AdapterView;
////import android.widget.ArrayAdapter;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.Spinner;
////import androidx.appcompat.widget.SwitchCompat;
////import androidx.appcompat.app.AppCompatActivity;
////import org.json.JSONArray;
////import org.json.JSONException;
////import java.io.IOException;
////import java.io.InputStream;
////import java.nio.charset.StandardCharsets;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////public class NewAddressActivity extends AppCompatActivity {
////
////    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
////    private Spinner citySpinner, districtSpinner, wardSpinner;
////    private SwitchCompat defaultSwitch;
////    private Button submitButton;
////
////    // Maps to store district and ward data
////    private Map<String, List<String>> cityToDistricts = new HashMap<>();
////    private Map<String, List<String>> districtToWards = new HashMap<>();
////    private Map<String, String> districtCodes = new HashMap<>(); // Map district name to code
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_new_address);
////
////        // Initialize views
////        fullNameEditText = findViewById(R.id.fullNameEditText);
////        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
////        streetEditText = findViewById(R.id.streetEditText);
////        citySpinner = findViewById(R.id.citySpinner);
////        districtSpinner = findViewById(R.id.districtSpinner);
////        wardSpinner = findViewById(R.id.wardSpinner);
////        defaultSwitch = findViewById(R.id.defaultSwitch);
////        submitButton = findViewById(R.id.submitButton);
////
////        // Load all JSON data
////        loadCities();
////        loadDistricts();
////        loadWards();
////
////        // Set city data to spinner
////        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
////        cities.add(0, "Select City"); // Default option
////        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
////        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        citySpinner.setAdapter(cityAdapter);
////
////        // Disable district and ward spinners initially
////        districtSpinner.setEnabled(false);
////        wardSpinner.setEnabled(false);
////
////        // Set click listener for submit button
////        submitButton.setOnClickListener(v -> {
////            String fullName = fullNameEditText.getText().toString();
////            String phone = phoneNumberEditText.getText().toString();
////            String city = citySpinner.getSelectedItem().toString();
////            String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
////            String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "";
////            String street = streetEditText.getText().toString();
////            boolean isDefault = defaultSwitch.isChecked();
////
////            // TODO: Implement save logic (e.g., to database or API)
////            System.out.println("Address: " + fullName + ", " + phone + ", " + city + ", " + district + ", " + ward + ", " + street + ", Default: " + isDefault);
////        });
////
////        // Enable submit button when form is filled (basic validation)
////        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
////            boolean isValid = !fullNameEditText.getText().toString().isEmpty() &&
////                    !phoneNumberEditText.getText().toString().isEmpty() &&
////                    !streetEditText.getText().toString().isEmpty() &&
////                    citySpinner.getSelectedItemPosition() > 0;
////            submitButton.setEnabled(isValid);
////        };
////        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
////        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
////        streetEditText.setOnFocusChangeListener(focusChangeListener);
////
////        // City spinner listener
////        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                if (position > 0) {
////                    String selectedCity = cities.get(position);
////                    districtSpinner.setEnabled(true);
////                    List<String> districts = cityToDistricts.get(selectedCity);
////                    districts.add(0, "Select District"); // Default option
////                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, districts);
////                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                    districtSpinner.setAdapter(districtAdapter);
////                } else {
////                    districtSpinner.setEnabled(false);
////                    wardSpinner.setEnabled(false);
////                    districtSpinner.setAdapter(null);
////                    wardSpinner.setAdapter(null);
////                }
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> parent) {}
////        });
////
////        // District spinner listener
////        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                if (position > 0) {
////                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
////                    wardSpinner.setEnabled(true);
////                    List<String> wards = districtToWards.get(selectedDistrict);
////                    wards.add(0, "Select Ward"); // Default option
////                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, wards);
////                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                    wardSpinner.setAdapter(wardAdapter);
////                } else {
////                    wardSpinner.setEnabled(false);
////                    wardSpinner.setAdapter(null);
////                }
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> parent) {}
////        });
////    }
////
////    private void loadCities() {
////        try {
////            InputStream inputStream = getAssets().open("cities.json");
////            int size = inputStream.available();
////            byte[] buffer = new byte[size];
////            inputStream.read(buffer);
////            inputStream.close();
////            String jsonString = new String(buffer, StandardCharsets.UTF_8);
////            JSONArray jsonArray = new JSONArray(jsonString);
////            for (int i = 0; i < jsonArray.length(); i++) {
////                cityToDistricts.put(jsonArray.getJSONObject(i).getString("name"), new ArrayList<>());
////            }
////        } catch (IOException | JSONException e) {
////            e.printStackTrace();
////        }
////    }
////
////    private void loadDistricts() {
////        try {
////            InputStream inputStream = getAssets().open("districts.json");
////            int size = inputStream.available();
////            byte[] buffer = new byte[size];
////            inputStream.read(buffer);
////            inputStream.close();
////            String jsonString = new String(buffer, StandardCharsets.UTF_8);
////            JSONArray jsonArray = new JSONArray(jsonString);
////            for (int i = 0; i < jsonArray.length(); i++) {
////                String districtName = jsonArray.getJSONObject(i).getString("name");
////                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
////                String code = jsonArray.getJSONObject(i).getString("code");
////                districtCodes.put(districtName, code);
////                // Find corresponding city (simplified; adjust based on full data)
////                for (String city : cityToDistricts.keySet()) {
////                    // Placeholder logic; you need to map parent_code to city code
////                    cityToDistricts.get(city).add(districtName);
////                }
////            }
////        } catch (IOException | JSONException e) {
////            e.printStackTrace();
////        }
////    }
////
////    private void loadWards() {
////        try {
////            InputStream inputStream = getAssets().open("wards.json");
////            int size = inputStream.available();
////            byte[] buffer = new byte[size];
////            inputStream.read(buffer);
////            inputStream.close();
////            String jsonString = new String(buffer, StandardCharsets.UTF_8);
////            JSONArray jsonArray = new JSONArray(jsonString);
////            for (int i = 0; i < jsonArray.length(); i++) {
////                String wardName = jsonArray.getJSONObject(i).getString("name");
////                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
////                for (Map.Entry<String, String> entry : districtCodes.entrySet()) {
////                    if (entry.getValue().equals(parentCode)) {
////                        String districtName = entry.getKey();
////                        if (districtToWards.containsKey(districtName)) {
////                            districtToWards.get(districtName).add(wardName);
////                        } else {
////                            List<String> wards = new ArrayList<>();
////                            wards.add(wardName);
////                            districtToWards.put(districtName, wards);
////                        }
////                        break; // Avoid duplicate additions
////                    }
////                }
////            }
////        } catch (IOException | JSONException e) {
////            e.printStackTrace();
////        }
////    }
////}
//
//package com.group7.pawdicted;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import androidx.appcompat.widget.SwitchCompat;
//import androidx.appcompat.app.AppCompatActivity;
//import org.json.JSONArray;
//import org.json.JSONException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class NewAddressActivity extends AppCompatActivity {
//
//    private EditText fullNameEditText, phoneNumberEditText, streetEditText;
//    private Spinner citySpinner, districtSpinner, wardSpinner;
//    private SwitchCompat defaultSwitch;
//    private Button submitButton;
//
//    // Maps to store hierarchical data
//    private Map<String, String> cityCodes = new HashMap<>(); // Map city name to code
//    private Map<String, List<String>> cityToDistricts = new HashMap<>(); // Map city to districts
//    private Map<String, List<String>> districtToWards = new HashMap<>(); // Map district to wards
//    private Map<String, String> districtCodes = new HashMap<>(); // Map district name to code
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_address);
//
//        // Initialize views
//        fullNameEditText = findViewById(R.id.fullNameEditText);
//        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
//        streetEditText = findViewById(R.id.streetEditText);
//        citySpinner = findViewById(R.id.citySpinner);
//        districtSpinner = findViewById(R.id.districtSpinner);
//        wardSpinner = findViewById(R.id.wardSpinner);
//        defaultSwitch = findViewById(R.id.defaultSwitch);
//        submitButton = findViewById(R.id.submitButton);
//
//        // Load all JSON data
//        loadCities();
//        loadDistricts();
//        loadWards();
//
//        // Set city data to spinner
//        List<String> cities = new ArrayList<>(cityToDistricts.keySet());
//        cities.add(0, "Select City"); // Add hint as first item
//        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        citySpinner.setAdapter(cityAdapter);
//        citySpinner.setSelection(0); // Set "Select City" as default
//
//        // Disable district and ward spinners initially
//        districtSpinner.setEnabled(false);
//        wardSpinner.setEnabled(false);
//
//        // Set click listener for submit button
//        submitButton.setOnClickListener(v -> {
//            String fullName = fullNameEditText.getText().toString().trim();
//            String phone = phoneNumberEditText.getText().toString().trim();
//            String city = citySpinner.getSelectedItem().toString();
//            String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
//            String ward = wardSpinner.getSelectedItem() != null ? wardSpinner.getSelectedItem().toString() : "";
//            String street = streetEditText.getText().toString().trim();
//            boolean isDefault = defaultSwitch.isChecked();
//
//            // Basic validation
//            if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.equals("Select City") ||
//                    district.equals("Select District") || ward.equals("Select Ward")) {
//                // TODO: Show error (e.g., Toast)
//                return;
//            }
//
//            // TODO: Implement save logic (e.g., to database or API)
//            System.out.println("Address: " + fullName + ", " + phone + ", " + city + ", " + district + ", " + ward + ", " + street + ", Default: " + isDefault);
//        });
//
//        // Enable submit button when form is filled
//        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
//            boolean isValid = !fullNameEditText.getText().toString().trim().isEmpty() &&
//                    !phoneNumberEditText.getText().toString().trim().isEmpty() &&
//                    !streetEditText.getText().toString().trim().isEmpty() &&
//                    !citySpinner.getSelectedItem().toString().equals("Select City");
//            submitButton.setEnabled(isValid);
//        };
//        fullNameEditText.setOnFocusChangeListener(focusChangeListener);
//        phoneNumberEditText.setOnFocusChangeListener(focusChangeListener);
//        streetEditText.setOnFocusChangeListener(focusChangeListener);
//
//        // City spinner listener
//        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    String selectedCity = cities.get(position);
//                    districtSpinner.setEnabled(true);
//                    List<String> districts = new ArrayList<>(cityToDistricts.get(selectedCity));
//                    if (districts != null && !districts.isEmpty()) {
//                        districts.add(0, "Select District"); // Add hint
//                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, districts);
//                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        districtSpinner.setAdapter(districtAdapter);
//                        districtSpinner.setSelection(0); // Set "Select District" as default
//                    }
//                } else {
//                    districtSpinner.setEnabled(false);
//                    wardSpinner.setEnabled(false);
//                    districtSpinner.setAdapter(null);
//                    wardSpinner.setAdapter(null);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//
//        // District spinner listener
//        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
//                    wardSpinner.setEnabled(true);
//                    List<String> wards = districtToWards.get(selectedDistrict);
//                    if (wards != null && !wards.isEmpty()) {
//                        wards.add(0, "Select Ward"); // Add hint
//                        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(NewAddressActivity.this, android.R.layout.simple_spinner_item, wards);
//                        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        wardSpinner.setAdapter(wardAdapter);
//                        wardSpinner.setSelection(0); // Set "Select Ward" as default
//                    }
//                } else {
//                    wardSpinner.setEnabled(false);
//                    wardSpinner.setAdapter(null);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//    }
//
//    private void loadCities() {
//        try {
//            InputStream inputStream = getAssets().open("cities.json");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            String jsonString = new String(buffer, StandardCharsets.UTF_8);
//            JSONArray jsonArray = new JSONArray(jsonString);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String name = jsonArray.getJSONObject(i).getString("name");
//                String code = jsonArray.getJSONObject(i).getString("code");
//                cityCodes.put(name, code);
//                cityToDistricts.put(name, new ArrayList<>());
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadDistricts() {
//        try {
//            InputStream inputStream = getAssets().open("districts.json");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            String jsonString = new String(buffer, StandardCharsets.UTF_8);
//            JSONArray jsonArray = new JSONArray(jsonString);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String name = jsonArray.getJSONObject(i).getString("name");
//                String code = jsonArray.getJSONObject(i).getString("code");
//                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
//                districtCodes.put(name, code);
//                for (Map.Entry<String, String> entry : cityCodes.entrySet()) {
//                    if (entry.getValue().equals(parentCode)) {
//                        cityToDistricts.get(entry.getKey()).add(name);
//                        break;
//                    }
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadWards() {
//        try {
//            InputStream inputStream = getAssets().open("wards.json");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            String jsonString = new String(buffer, StandardCharsets.UTF_8);
//            JSONArray jsonArray = new JSONArray(jsonString);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String name = jsonArray.getJSONObject(i).getString("name");
//                String parentCode = jsonArray.getJSONObject(i).getString("parent_code");
//                for (Map.Entry<String, String> entry : districtCodes.entrySet()) {
//                    if (entry.getValue().equals(parentCode)) {
//                        String districtName = entry.getKey();
//                        districtToWards.computeIfAbsent(districtName, k -> new ArrayList<>()).add(name);
//                        break;
//                    }
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}