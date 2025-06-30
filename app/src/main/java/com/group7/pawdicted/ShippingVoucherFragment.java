package com.group7.pawdicted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.group7.pawdicted.mobile.adapters.VoucherAdapter;
import com.group7.pawdicted.mobile.models.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                    Date now = new Date();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        if ("shipping".equals(type)) {
                            Timestamp startDate = doc.getTimestamp("startDate");
                            Timestamp endDate = doc.getTimestamp("endDate");
                            if (startDate != null && endDate != null &&
                                    now.after(startDate.toDate()) && now.before(endDate.toDate())) {
                                int discount = doc.getLong("discount").intValue();
                                int minOrderValue = doc.getLong("minOrderValue").intValue();
                                Voucher v = new Voucher(
                                        doc.getString("code"),
                                        "Min. Spend đ" + NumberFormat.getInstance(Locale.US).format(minOrderValue),
                                        startDate,
                                        endDate,
                                        false,
                                        type,
                                        discount,
                                        minOrderValue
                                );
                                shippingVouchers.add(v);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public Voucher getSelectedVoucher() {
        int selectedPos = adapter.getSelectedPosition();
        if (selectedPos != -1 && selectedPos < shippingVouchers.size()) {
            return shippingVouchers.get(selectedPos);
        }
        return null;
    }
}