package com.group7.pawdicted;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.group7.pawdicted.mobile.adapters.CartAdapter;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CartActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalText;
    private Button checkoutBtn;
    private CheckBox selectAllCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }


        recyclerView = findViewById(R.id.recycler_cart);
        totalText = findViewById(R.id.text_total_price);
        checkoutBtn = findViewById(R.id.checkout_button);
        selectAllCheckbox = findViewById(R.id.select_all_checkbox);


        // Dummy data
        cartItemList = new ArrayList<>();
        cartItemList = CartManager.getCartItems();
        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);


        // Gắn callback để cập nhật khi tick sản phẩm
        cartAdapter.setOnCartItemChangeListener(() -> {
            updateTotal();
            syncSelectAllCheckbox();
        });


        // Voucher click
        LinearLayout voucherLayout = findViewById(R.id.voucher_layout);
        voucherLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VoucherManagementActivity.class);
            startActivity(intent);
        });


        // Checkout click
        checkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });


        // Select All logic (optional)
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItemList) {
                item.isSelected = isChecked;
            }
            cartAdapter.notifyDataSetChanged();
        });


        updateTotal();
        syncSelectAllCheckbox();
    }


    private void updateTotal() {
        int total = 0;
        int selectedCount = 0;
        for (CartItem item : cartItemList) {
            if (item.isSelected) {
                total += item.price * item.quantity;
                selectedCount++;
            }
        }
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        totalText.setText(formatter.format(total));
        checkoutBtn.setText("Check Out (" + selectedCount + ")");
    }


    private void syncSelectAllCheckbox() {
        boolean allSelected = true;
        for (CartItem item : cartItemList) {
            if (!item.isSelected) {
                allSelected = false;
                break;
            }
        }
        selectAllCheckbox.setOnCheckedChangeListener(null); // tránh vòng lặp
        selectAllCheckbox.setChecked(allSelected);
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItemList) {
                item.isSelected = isChecked;
            }
            cartAdapter.notifyDataSetChanged();
            updateTotal();
        });
    }


    public void open_voucher_activity(View view) {
        Intent intent = new Intent(CartActivity.this, VoucherManagementActivity.class);
        startActivity(intent);
    }
}



