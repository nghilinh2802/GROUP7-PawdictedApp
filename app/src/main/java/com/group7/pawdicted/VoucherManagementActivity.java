package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.group7.pawdicted.mobile.models.Voucher;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VoucherManagementActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText voucherCodeInput;
    private Button applyButton;
    private Button btnConfirmVoucher;

    public static Voucher selectedVoucher = null; // make public static for access from fragments

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

        tabLayout.addTab(tabLayout.newTab().setText("Discount"));
        tabLayout.addTab(tabLayout.newTab().setText("Shipping Voucher"));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DiscountVoucherFragment(), "Discount");
        adapter.addFragment(new ShippingVoucherFragment(), "Shipping Voucher");
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

        btnConfirmVoucher.setOnClickListener(v -> {
            if (selectedVoucher != null) {
                Intent intent = new Intent();
                intent.putExtra("selectedVoucher", selectedVoucher);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Please select a voucher", Toast.LENGTH_SHORT).show();
            }
        });
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