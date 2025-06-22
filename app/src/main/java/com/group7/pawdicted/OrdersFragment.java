package com.group7.pawdicted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.OrderAdapter;
import com.group7.pawdicted.mobile.models.OrderNotification;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderNotification> orderList;

    public OrdersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false); // ✅ đúng layout riêng

        recyclerView = view.findViewById(R.id.recycler_orders); // ✅ đúng ID RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderList.add(new OrderNotification(
                "Order successful",
                "Hello Chau, Karu has received the order 20030624 you just placed",
                "18-05-2025 11:14:40",
                R.mipmap.fofos));

        adapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
