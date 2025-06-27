package com.group7.pawdicted;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotions, container, false);

        recyclerView = view.findViewById(R.id.recycler_promotions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        promotionList = new ArrayList<>();
        adapter = new PromotionAdapter(promotionList);
        recyclerView.setAdapter(adapter);

        loadPromotionsFromFirestore();

        return view;
    }

    private void loadPromotionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("promotions")
                .whereEqualTo("status", "sent")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("PromotionsFragment", "Số tài liệu lấy được: " + queryDocumentSnapshots.size());
                    promotionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        PromotionNotification promo = doc.toObject(PromotionNotification.class);
                        promotionList.add(promo);
                        Log.d("PromotionsFragment", "Đã thêm promotion: " + promo.getTitle());
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("PromotionsFragment", "Kích thước danh sách sau khi cập nhật: " + promotionList.size());
                    if (promotionList.isEmpty()) {
                        Toast.makeText(getContext(), "Không tìm thấy promotion nào", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PromotionsFragment", "Lỗi khi lấy dữ liệu promotions", e);
                    Toast.makeText(getContext(), "Không thể tải promotions", Toast.LENGTH_SHORT).show();
                });
    }
}