package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.OrderItemAdapter;
import com.group7.pawdicted.mobile.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Barkbutler x Fofos Cheese Box Interactive Toy for Cats", "ORANGE", "đ240.000", 1, R.mipmap.cat_toy));
        items.add(new OrderItem("Squeeezys Latex Monster Brother Chew Toy for Dogs", "", "đ85.000", 2, R.mipmap.fofos));

        OrderItemAdapter adapter = new OrderItemAdapter(this, items);
        recyclerView.setAdapter(adapter);

    }

    public void open_voucher_activity(View view) {
        Intent intent = new Intent(this, VoucherManagementActivity.class);
        startActivity(intent);
    }

    public void open_address_selection_activity(View view) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        startActivity(intent);
    }
}