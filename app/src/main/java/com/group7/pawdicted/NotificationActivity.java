package com.group7.pawdicted;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class NotificationActivity extends AppCompatActivity {

    private Button btnPromotions, btnOrders;
    private FrameLayout contentFrame;
    FooterManager footerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        btnPromotions = findViewById(R.id.btn_promotions);
        btnOrders = findViewById(R.id.btn_orders);
        contentFrame = findViewById(R.id.notification_content);

        setupTabs();
        footerManager = new FooterManager(this);
    }

    private void setupTabs() {
        btnPromotions.setOnClickListener(v -> selectTab(true));
        btnOrders.setOnClickListener(v -> selectTab(false));
        selectTab(true); // Tab mặc định
    }

    private void selectTab(boolean isPromotions) {
        if (isPromotions) {
            btnPromotions.setBackgroundResource(R.drawable.tab_selected_left);
            btnPromotions.setTextColor(Color.WHITE);
            btnOrders.setBackgroundResource(R.drawable.tab_unselected_right);
            btnOrders.setTextColor(Color.parseColor("#9C1B2C"));
            loadFragment(new PromotionsFragment());
        } else {
            btnOrders.setBackgroundResource(R.drawable.tab_selected_right);
            btnOrders.setTextColor(Color.WHITE);
            btnPromotions.setBackgroundResource(R.drawable.tab_unselected_left);
            btnPromotions.setTextColor(Color.parseColor("#9C1B2C"));
            loadFragment(new OrdersFragment());
        }
    }


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.notification_content, fragment)
                .commit();
    }
}
