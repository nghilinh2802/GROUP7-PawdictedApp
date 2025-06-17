package com.group7.pawdicted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.PromotionAdapter;
import com.group7.pawdicted.mobile.models.PromotionNotification;

import java.util.ArrayList;
import java.util.List;

public class PromotionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PromotionAdapter adapter;
    private List<PromotionNotification> promotionList;

    public PromotionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotions, container, false); // ✅ dùng layout XML riêng

        recyclerView = view.findViewById(R.id.recycler_promotions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu
        promotionList = new ArrayList<>();
        promotionList.add(new PromotionNotification(
                "Big Sale on Whiskas",
                "Get 20% off for all Whiskas products this week!",
                R.drawable.whiskas));

        promotionList.add(new PromotionNotification(
                "Free Delivery!",
                "Enjoy free delivery for orders over $30 until Sunday!",
                R.drawable.whiskas));

        adapter = new PromotionAdapter(promotionList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
