package com.group7.pawdicted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        recyclerView = findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressAdapter = new AddressAdapter(addressList, this, this);
        recyclerView.setAdapter(addressAdapter);

        loadAddressesFromFirestore();
    }

    private void loadAddressesFromFirestore() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (customerId == null) return;

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
                    }

                    if (addressList.size() == 1) {
                        addressList.get(0).setDefault(true);
                    }

                    addressAdapter.notifyDataSetChanged();

                    String selectedAddressId = getIntent().getStringExtra("selectedAddressId");
                    int selectedPos = -1;
                    if (selectedAddressId != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            if (addressList.get(i).getId().equals(selectedAddressId)) {
                                selectedPos = i;
                                break;
                            }
                        }
                    }
                    if (selectedPos == -1) {
                        selectedPos = getDefaultAddressPosition();
                    }
                    if (selectedPos != -1) {
                        addressAdapter.setSelectedPosition(selectedPos);
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