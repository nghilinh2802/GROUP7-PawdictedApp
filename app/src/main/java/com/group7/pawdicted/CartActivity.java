package com.group7.pawdicted;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.group7.pawdicted.mobile.adapters.CartAdapter;
import com.group7.pawdicted.mobile.models.CartItem;
import com.group7.pawdicted.mobile.models.CartManager;
import com.group7.pawdicted.mobile.models.Voucher;
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
    private Voucher selectedVoucher;
    private TextView txtVoucherDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_cart);
        totalText = findViewById(R.id.text_total_price);
        checkoutBtn = findViewById(R.id.btnCheckout);
        selectAllCheckbox = findViewById(R.id.select_all_checkbox);
        txtVoucherDetails = findViewById(R.id.txtVoucherDetails);

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

        checkoutBtn.setOnClickListener(v -> {
            List<CartItem> selectedItems = getSelectedCartItems();

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để thanh toán.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Bạn cần đăng nhập để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            String customerId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("addresses")
                    .document(customerId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        Intent intent;

                        if (querySnapshot.isEmpty()) {
                            intent = new Intent(CartActivity.this, NewAddressActivity.class);
                            intent.putExtra("fromCart", true);
                            startActivityForResult(intent, REQUEST_ADD_ADDRESS);
                        } else {
                            intent = new Intent(CartActivity.this, CheckoutActivity.class);
                            String cartJson = new Gson().toJson(selectedItems);
                            intent.putExtra("cartItems", cartJson);
                            if (selectedVoucher != null) {
                                intent.putExtra("selectedVoucher", selectedVoucher);
                                intent.putExtra("voucherCode", txtVoucherDetails.getText().toString());
                            }
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CartActivity.this, "Không thể kiểm tra địa chỉ!", Toast.LENGTH_SHORT).show();
                    });
        });

        updateTotal();
        syncSelectAllCheckbox();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
            String cartJson = new Gson().toJson(getSelectedCartItems());
            checkoutIntent.putExtra("cartItems", cartJson);
            if (selectedVoucher != null) {
                checkoutIntent.putExtra("selectedVoucher", selectedVoucher);
                checkoutIntent.putExtra("voucherCode", txtVoucherDetails.getText().toString());
            }
            startActivity(checkoutIntent);
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedVoucher = (Voucher) data.getSerializableExtra("selectedVoucher");
            if (selectedVoucher != null) {
                txtVoucherDetails.setText(selectedVoucher.getCode());
                updateTotal();
            }
        }
    }

    private List<CartItem> getSelectedCartItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (item.isSelected) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    private void updateTotal() {
        int total = 0;
        int discount = 0;
        int selectedCount = 0;

        for (CartItem item : cartItemList) {
            if (item.isSelected) {
                total += item.price * item.quantity;
                selectedCount++;
            }
        }

        if (selectedVoucher != null && "merchandise".equals(selectedVoucher.getType())) {
            if (total >= selectedVoucher.getMinOrderValue()) {
                discount = selectedVoucher.getDiscountValue(total);
            }
        }

        int finalTotal = total - discount;

        DecimalFormat formatter = new DecimalFormat("#,###đ");
        totalText.setText(formatter.format(finalTotal));
        checkoutBtn.setText("Check Out (" + selectedCount + ")");
        checkoutBtn.setEnabled(selectedCount > 0);
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

    public void open_voucher_activity(View view) {
        Intent intent = new Intent(CartActivity.this, VoucherManagementActivity.class);
        startActivityForResult(intent, 100);
    }
}
