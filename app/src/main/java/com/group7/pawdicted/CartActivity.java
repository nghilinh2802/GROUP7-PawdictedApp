package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.group7.pawdicted.CheckoutActivity;
import com.group7.pawdicted.R;
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
    private static final int REQUEST_ADD_ADDRESS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

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
        // Khởi tạo adapter với toàn bộ danh sách sản phẩm trong giỏ hàng
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

        // Sự kiện khi nhấn nút Check Out
        checkoutBtn.setOnClickListener(v -> {
            // Filter the selected items only
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItemList) {
                if (item.isSelected) {
                    selectedItems.add(item);
                }
            }

            if (!selectedItems.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                Gson gson = new Gson();
                String cartJson = gson.toJson(selectedItems); // Pass only selected items
                intent.putExtra("cartItems", cartJson);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để thanh toán.", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(CartActivity.this, "Bạn cần đăng nhập để thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("addresses")
                .document(customerId)
                .collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // CHƯA có địa chỉ nào → chuyển sang NewAddressActivity
                        Intent intent = new Intent(CartActivity.this, NewAddressActivity.class);
                        intent.putExtra("fromCart", true); // đánh dấu quay lại checkout
                        startActivityForResult(intent, REQUEST_ADD_ADDRESS);
                    } else {
                        // Đã có địa chỉ → chuyển sang CheckoutActivity
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Không thể kiểm tra địa chỉ!", Toast.LENGTH_SHORT).show();
                });

        updateTotal();
        syncSelectAllCheckbox();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            // Sau khi thêm địa chỉ → tự động vào Checkout
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        }
    }

    private void updateTotal() {
        int total = 0;
        int selectedCount = 0;
        for (CartItem item : cartItemList) {
            if (item.isSelected) {
                total += item.price * item.quantity; // Only add the price of selected items
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
        totalText.setText(formatter.format(total)); // Update the total price
        checkoutBtn.setText("Check Out (" + selectedCount + ")");
        checkoutBtn.setEnabled(selectedCount > 0); // Disable if no item is selected
        Drawable bg = ContextCompat.getDrawable(this,
                selectedCount > 0 ? R.drawable.rounded_button_red : R.drawable.rounded_button_gray);
        checkoutBtn.setBackground(bg);

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
}
