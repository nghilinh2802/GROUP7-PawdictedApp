package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group7.pawdicted.mobile.adapters.CartAdapter;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;
import com.group7.pawdicted.mobile.services.CartFirestoreService;
import com.group7.pawdicted.mobile.services.CartStorageHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

        recyclerView = findViewById(R.id.recycler_cart);
        totalText = findViewById(R.id.text_total_price);
        checkoutBtn = findViewById(R.id.checkout_button);
        selectAllCheckbox = findViewById(R.id.select_all_checkbox);

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String customerId = user.getUid();
            cartItemList = CartStorageHelper.loadCart(this, customerId);

            if (cartItemList.isEmpty()) {
                // Nếu local rỗng → load từ Firestore
                CartManager.getInstance().loadCartFromFirestore(this, customerId, () -> {
                    cartItemList = CartManager.getInstance().getCartItems();
                    CartStorageHelper.saveCart(this, customerId, cartItemList);
                    CartManager.getInstance().setCustomerId(customerId);
                    setupRecyclerAndEvents();
                });
            } else {
                CartManager.getInstance().setCartItems(cartItemList);
                CartManager.getInstance().setCustomerId(customerId);
                setupRecyclerAndEvents();
            }
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            cartItemList = new ArrayList<>();
            setupRecyclerAndEvents();
        }
    }

    private void setupRecyclerAndEvents() {
        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnCartItemChangeListener(() -> {
            updateTotal();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String customerId = user.getUid();
                CartStorageHelper.saveCart(this, customerId, cartItemList);
                CartFirestoreService.syncCartToFirestore(customerId, cartItemList);
            }
        });

        LinearLayout voucherLayout = findViewById(R.id.voucher_layout);
        voucherLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VoucherManagementActivity.class);
            startActivity(intent);
        });

        checkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });

        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItemList) {
                item.isSelected = isChecked;
            }
            cartAdapter.notifyDataSetChanged();
            updateTotal();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String customerId = user.getUid();
            CartStorageHelper.saveCart(this, customerId, cartItemList);
            CartFirestoreService.syncCartToFirestore(customerId, cartItemList);
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

        selectAllCheckbox.setOnCheckedChangeListener(null);
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
