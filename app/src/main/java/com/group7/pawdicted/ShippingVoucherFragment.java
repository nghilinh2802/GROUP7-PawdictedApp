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

import java.util.ArrayList;
import java.util.List;

public class ShippingVoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VoucherAdapter(getShippingVouchers(), VoucherAdapter.TYPE_SHIPPING);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<Voucher> getShippingVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        vouchers.add(new Voucher("SHIPPING FEE up to 20K off", "Min. Spend 0", "VALID TILL: 24 JUNE 2025", true));
        vouchers.add(new Voucher("SHIPPING FEE up to 15K off", "Min. Spend 25K", "VALID PERIOD: 20 MAY 2025 00:00 - 20 May 2025 23:59", false));
        vouchers.add(new Voucher("SHIPPING FEE up to 25K off", "Min. Spend 45K", "VALID PERIOD: 20 MAY 2025 00:00 - 20 May 2025 23:59", false));
        return vouchers;
    }
}

