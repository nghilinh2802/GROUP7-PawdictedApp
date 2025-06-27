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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class AccountManagementActivity extends AppCompatActivity {
    ImageView ToConfirm, ToPickUp, ToShip, EvaluateOrder, imgViewPurchaseHistory, imgAvatar;
    TextView txtToConfirm, txtToPickUp, txtToShip, txtEvaluateOrder, txtViewPurchaseHistory, txtUsername;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FooterManager footerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imgCartBackbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Gọi hàm load dữ liệu header
        loadHeaderUserInfo();

        footerManager = new FooterManager(this);
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

        imgAvatar = findViewById(R.id.imgAvatar);
        txtUsername = findViewById(R.id.txtUsername);
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
            public void onClick(View view) {openPurchaseOrderActivity("Delivered");}
        });
        txtToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openPurchaseOrderActivity("Delivered");}
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

    public void open_cart_activity(View view) {
        Intent intent=new Intent(AccountManagementActivity.this,CartActivity.class);
        startActivity(intent);
    }

    private void loadHeaderUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            txtUsername.setText("Paw'dicted");
            imgAvatar.setImageResource(R.mipmap.ic_logo);
            return;
        }

        String uid = user.getUid();
        db.collection("customers").document(uid)
                .get(Source.SERVER)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("customer_username");
                        String avatar = doc.getString("avatar_img");

                        txtUsername.setText(username != null ? username : "Paw'dicted");
                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this).load(avatar)
                                    .placeholder(R.mipmap.ic_logo)
                                    .circleCrop()
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.mipmap.ic_logo);
                        }
                    } else {
                        txtUsername.setText("Paw'dicted");
                        imgAvatar.setImageResource(R.mipmap.ic_logo);
                    }
                })
                .addOnFailureListener(e -> {
                    txtUsername.setText("Paw'dicted");
                    imgAvatar.setImageResource(R.mipmap.ic_logo);
                });
    }
}