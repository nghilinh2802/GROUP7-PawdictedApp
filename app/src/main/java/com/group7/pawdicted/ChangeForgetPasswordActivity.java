package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeForgetPasswordActivity extends AppCompatActivity implements SuccessPasswordDialogFragment.OnLoginWithNewPasswordListener {

    private ImageButton btnBack;
    private EditText edtNewPassword, edtNewPasswordAgain;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_forget_password);

        // Ánh xạ các thành phần
        btnBack = findViewById(R.id.btnBack);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordAgain = findViewById(R.id.edtNewPasswordAgain);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> onBackPressed());

        // Xử lý nút Change Password
        btnChangePassword.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtNewPasswordAgain.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Giả sử mật khẩu được đổi thành công (không cần backend)
                showSuccessDialog();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void showSuccessDialog() {
        SuccessPasswordDialogFragment dialog = new SuccessPasswordDialogFragment();
        dialog.show(getSupportFragmentManager(), "SuccessDialog");
    }

    @Override
    public void onLoginWithNewPassword() {
        // Chuyển sang trang Login
        Intent intent = new Intent(this, LoginActivity.class); // Thay LoginActivity bằng activity đăng nhập của bạn
        startActivity(intent);
        finish(); // Đóng ChangeForgetPasswordActivity
    }
}