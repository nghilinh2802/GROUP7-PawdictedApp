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

public class DiscountVoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VoucherAdapter(getDiscountVouchers(), VoucherAdapter.TYPE_DISCOUNT);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<Voucher> getDiscountVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        vouchers.add(new Voucher("15% off Capped at 100K", "Min. Spend 500K", "Valid Period: 20 May 2025 00:00 - 20 May 2025 23:59", false));
        vouchers.add(new Voucher("10% off Capped at 50K", "Min. Spend 99K", "Valid Period: 20 May 2025 00:00 - 20 May 2025 23:59", true));
        vouchers.add(new Voucher("13% off Capped at 80K", "Min. Spend 150K", "Valid Period: 20 May 2025 00:00 - 20 May 2025 23:59", false));
        return vouchers;
    }
}
