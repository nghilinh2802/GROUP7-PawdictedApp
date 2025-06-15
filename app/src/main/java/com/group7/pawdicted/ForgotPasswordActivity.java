package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {

    private ImageButton btnBack;
    private EditText edtUsername, edtPhone;
    private Button btnSendRecoverCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ các thành phần
        btnBack = findViewById(R.id.btnBack);
        edtUsername = findViewById(R.id.edtUsername);
        edtPhone = findViewById(R.id.edtPhone);
        btnSendRecoverCode = findViewById(R.id.btnSendRecoverCode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> onBackPressed());

        // Xử lý nút Send Recover Code
        btnSendRecoverCode.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            if (!username.isEmpty() && !phone.isEmpty()) {
                showOtpDialog();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void showOtpDialog() {
        OtpDialogFragment dialog = new OtpDialogFragment();
        dialog.show(getSupportFragmentManager(), "OtpDialog");
    }

    @Override
    public void onOtpVerified(String otp) {
        // Xử lý khi OTP được xác nhận
        Toast.makeText(this, "OTP Verified: " + otp, Toast.LENGTH_SHORT).show();
        // Chuyển sang trang Reset Password
        Intent intent = new Intent(this, ChangeForgetPasswordActivity.class);
        startActivity(intent);
        finish(); // Đóng ForgotPasswordActivity sau khi chuyển
    }
}