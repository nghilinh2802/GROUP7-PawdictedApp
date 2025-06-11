package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccountManagementActivity extends AppCompatActivity {
    ImageView ToConfirm, ToPickUp, ToShip, EvaluateOrder, imgViewPurchaseHistory;
    TextView txtToConfirm, txtToPickUp, txtToShip, txtEvaluateOrder, txtViewPurchaseHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imgCartHp), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addViews() {
        ToConfirm = findViewById(R.id.imgToConfirm);
        ToPickUp = findViewById(R.id.imgToPickUp);
        ToShip = findViewById(R.id.imgToShip);
        EvaluateOrder = findViewById(R.id.imgEvaluateOrder);
        txtToConfirm = findViewById(R.id.txtToConfirm);
        txtToPickUp = findViewById(R.id.txtToPickUp);
        txtToShip = findViewById(R.id.txtToShip);
        txtEvaluateOrder = findViewById(R.id.txtEvaluateOrder);
        txtViewPurchaseHistory = findViewById(R.id.txtViewPurchaseHistory);
        imgViewPurchaseHistory = findViewById(R.id.imgViewPurchaseHistory);
    }

    private void addEvents() {
        ToConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Pending Payment");}
        });
        txtToConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Pending Payment");}
        });
        ToPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Shipped");}
        });
        txtToPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Shipped");}
        });
        ToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Out for Delivery");}
        });
        txtToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Out for Delivery");}
        });
        imgViewPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Completed");}
        });
        txtViewPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Completed");}
        });
    }

    private void openPurchaseOrderActivity(String status) {
        Intent intent = new Intent(this, PurchaseOrderActivity.class);
        intent.putExtra("order_status", status);
        startActivity(intent);
    }


    public void open_profile(View view) {
        Intent intent=new Intent(AccountManagementActivity.this,ProfileManagementActivity.class);
        startActivity(intent);
    }

    public void open_setting_activity(View view) {
        Intent intent=new Intent(AccountManagementActivity.this,SettingManagementActivity.class);
        startActivity(intent);
    }
}