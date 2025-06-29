package com.group7.pawdicted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group7.pawdicted.mobile.adapters.AddressAdapter;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.google.firebase.Timestamp;

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
                .orderBy("time") // giúp load theo thời gian
                .get()
                .addOnSuccessListener(snapshot -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        AddressItem item = doc.toObject(AddressItem.class);
                        item.setId(doc.getId());
                        addressList.add(item);
                    }

                    // Nếu chỉ có 1 địa chỉ thì set default
                    if (addressList.size() == 1) {
                        addressList.get(0).setDefault(true);
                    }

                    addressAdapter.notifyDataSetChanged(); // cập nhật UI

                    // Xử lý chọn lại vị trí cũ (nếu có)
                    int lastSelected = getIntent().getIntExtra("lastSelectedPosition", -1);
                    if (lastSelected >= 0 && lastSelected < addressList.size()) {
                        addressAdapter.setSelectedPosition(lastSelected);
                    } else {
                        int defaultPos = getDefaultAddressPosition();
                        if (defaultPos != -1) {
                            addressAdapter.setSelectedPosition(defaultPos);
                        }
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
