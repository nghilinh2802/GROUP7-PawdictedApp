package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.VoucherAdapter;
import com.group7.pawdicted.mobile.models.Voucher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VoucherManagementActivity extends AppCompatActivity implements VoucherAdapter.OnVoucherSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText voucherCodeInput;
    private Button applyButton;
    private Button btnConfirmVoucher;
    private DiscountVoucherFragment discountVoucherFragment;
    private ShippingVoucherFragment shippingVoucherFragment;
    private Voucher appliedVoucher; // Store voucher applied via code input or selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_management);

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        voucherCodeInput = findViewById(R.id.voucher_code_input);
        applyButton = findViewById(R.id.apply_button);
        btnConfirmVoucher = findViewById(R.id.btn_confirm_voucher);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        discountVoucherFragment = new DiscountVoucherFragment();
        shippingVoucherFragment = new ShippingVoucherFragment();
        adapter.addFragment(discountVoucherFragment, "Discount");
        adapter.addFragment(shippingVoucherFragment, "Shipping Voucher");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        voucherCodeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyButton.setBackgroundResource(s.length() > 0
                        ? R.drawable.red_fill_rounded_background
                        : R.drawable.dark_gray_fill_rounded_background);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        applyButton.setOnClickListener(v -> {
            String code = voucherCodeInput.getText().toString().trim();
            if (!code.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection("vouchers")
                        .whereEqualTo("code", code)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Timestamp startDate = doc.getTimestamp("startDate");
                                    Timestamp endDate = doc.getTimestamp("endDate");
                                    Date now = new Date();
                                    if (startDate != null && endDate != null &&
                                            now.after(startDate.toDate()) && now.before(endDate.toDate())) {
                                        int discount = doc.getLong("discount").intValue();
                                        int minOrderValue = doc.getLong("minOrderValue").intValue();
                                        String type = doc.getString("type");
                                        appliedVoucher = new Voucher(
                                                code,
                                                "Min. Spend đ" + String.format("%,d", minOrderValue),
                                                startDate,
                                                endDate,
                                                true,
                                                type,
                                                discount,
                                                minOrderValue
                                        );
                                        appliedVoucher.setId(doc.getId());
                                        Toast.makeText(this, "Voucher applied: " + code, Toast.LENGTH_SHORT).show();
                                        Log.d("VoucherManagement", "Applied voucher: " + code);
                                        // Clear fragment selections
                                        discountVoucherFragment.clearSelection();
                                        shippingVoucherFragment.clearSelection();
                                    } else {
                                        Toast.makeText(this, "Voucher is not valid or has expired", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Invalid voucher code", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error checking voucher code", Toast.LENGTH_SHORT).show();
                            Log.e("VoucherManagement", "Error checking voucher code", e);
                        });
            } else {
                Toast.makeText(this, "Please enter a voucher code", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmVoucher.setOnClickListener(v -> {
            Voucher selectedVoucher = appliedVoucher;
            if (selectedVoucher == null) {
                selectedVoucher = discountVoucherFragment.getSelectedVoucher();
                if (selectedVoucher == null) {
                    selectedVoucher = shippingVoucherFragment.getSelectedVoucher();
                }
            }

            if (selectedVoucher != null) {
                Intent intent = new Intent();
                intent.putExtra("selectedVoucher", selectedVoucher);
                setResult(RESULT_OK, intent);
                Log.d("VoucherManagement", "Confirming voucher: " + selectedVoucher.getCode());
                finish();
            } else {
                Toast.makeText(this, "Please select a voucher", Toast.LENGTH_SHORT).show();
                Log.d("VoucherManagement", "No voucher selected");
            }
        });
    }

    @Override
    public void onVoucherSelected(Voucher voucher) {
        appliedVoucher = voucher;
        // Clear manual code input if a voucher is selected from the list
        voucherCodeInput.setText("");
        Log.d("VoucherManagement", "Voucher selected from adapter: " + voucher.getCode());
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override public Fragment getItem(int position) { return fragments.get(position); }
        @Override public int getCount() { return fragments.size(); }
        @Override public CharSequence getPageTitle(int position) { return fragmentTitles.get(position); }
    }
}