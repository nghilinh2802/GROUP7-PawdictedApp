package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.AddressAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<AddressItem> addressList = new ArrayList<>();
    private AddressAdapter addressAdapter;
    ImageView imgBack;
    private String selectedAddressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        addViews();

        recyclerView = findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressAdapter = new AddressAdapter(addressList, this, this);
        recyclerView.setAdapter(addressAdapter);

        selectedAddressId = getIntent().getStringExtra("selectedAddressId");

        loadAddressesFromFirestore();
    }

    private void addViews() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadAddressesFromFirestore() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (customerId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("addresses")
                .document(customerId)
                .collection("items")
                .orderBy("time")
                .get()
                .addOnSuccessListener(snapshot -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        AddressItem item = doc.toObject(AddressItem.class);
                        item.setId(doc.getId());
                        addressList.add(item);
                        Log.d("AddressSelection", "Loaded address: " + item.toString() + ", isDefault: " + item.isDefault());
                    }

                    addressAdapter.notifyDataSetChanged();

                    if (selectedAddressId != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            if (addressList.get(i).getId().equals(selectedAddressId)) {
                                addressAdapter.setSelectedPosition(i);
                                return;
                            }
                        }
                    }

                    int defaultPos = getDefaultAddressPosition();
                    if (defaultPos != -1) {
                        addressAdapter.setSelectedPosition(defaultPos);
                    } else if (!addressList.isEmpty()) {
                        addressAdapter.setSelectedPosition(0);
                    } else {
                        Toast.makeText(this, "No addresses found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải danh sách địa chỉ", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Load error: ", e);
                });
    }

    private int getDefaultAddressPosition() {
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                return i;
            }
        }
        return -1;
    }

    public void open_new_address_activity(View view) {
        Intent intent = new Intent(this, NewAddressActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            loadAddressesFromFirestore();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddressesFromFirestore();
    }
}