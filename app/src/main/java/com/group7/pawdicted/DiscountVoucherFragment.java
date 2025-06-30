package com.group7.pawdicted;

import android.os.Bundle;
import android.util.Log;
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

public class DiscountVoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<Voucher> discountVouchers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VoucherAdapter(discountVouchers, VoucherAdapter.TYPE_DISCOUNT, (VoucherAdapter.OnVoucherSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);
        loadDiscountVouchers();
        return view;
    }

    public void loadDiscountVouchers() {
        FirebaseFirestore.getInstance()
                .collection("vouchers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    discountVouchers.clear();
                    Date now = new Date();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        if ("merchandise".equals(type)) {
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
                                v.setId(doc.getId());
                                discountVouchers.add(v);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public Voucher getSelectedVoucher() {
        int selectedPos = adapter.getSelectedPosition();
        if (selectedPos != -1 && selectedPos < discountVouchers.size()) {
            Voucher selected = discountVouchers.get(selectedPos);
            selected.setSelected(true);
            return selected;
        }
        return null;
    }

    public void clearSelection() {
        int previousSelected = adapter.getSelectedPosition();
        if (previousSelected != -1) {
            discountVouchers.get(previousSelected).setSelected(false);
            adapter.notifyItemChanged(previousSelected);
        }
        // Reset selected position in adapter
        try {
            java.lang.reflect.Field field = VoucherAdapter.class.getDeclaredField("selectedPosition");
            field.setAccessible(true);
            field.set(adapter, -1);
        } catch (Exception e) {
            Log.e("DiscountVoucherFragment", "Error clearing selection", e);
        }
    }
}