package com.group7.pawdicted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.VoucherAdapter;
import com.group7.pawdicted.mobile.models.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShippingVoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<Voucher> shippingVouchers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VoucherAdapter(shippingVouchers, VoucherAdapter.TYPE_SHIPPING);
        recyclerView.setAdapter(adapter);
        loadShippingVouchers();
        return view;
    }

    public void loadShippingVouchers() {
        FirebaseFirestore.getInstance()
                .collection("vouchers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    shippingVouchers.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        if ("shipping".equals(type)) {
                            Voucher v = new Voucher(
                                    doc.getString("code"),
                                    "Min. Spend " + doc.get("minOrderValue"),
                                    "Valid Period: " + formatDateRange(doc),
                                    type,
                                    false
                            );
                            shippingVouchers.add(v);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private String formatDateRange(QueryDocumentSnapshot doc) {
        try {
            com.google.firebase.Timestamp start = doc.getTimestamp("startDate");
            com.google.firebase.Timestamp end = doc.getTimestamp("endDate");

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy MMM dd HH:mm:ss", java.util.Locale.ENGLISH);

            return sdf.format(start.toDate()) + " - " + sdf.format(end.toDate());
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
