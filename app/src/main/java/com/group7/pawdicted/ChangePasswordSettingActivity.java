package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;

public class ChangePasswordSettingActivity extends AppCompatActivity implements SuccessPasswordDialogFragment.OnLoginWithNewPasswordListener {

    private EditText edtPassword, edtNewPassword, edtNewPasswordAgain;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }
        edtPassword = findViewById(R.id.edtPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordAgain = findViewById(R.id.edtNewPasswordAgain);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }


    // Go back to previous activity
    public void go_back(View view) {
        finish();
    }

    // Handle save new password button
    public void savePassword(View view) {
        String currentPassword = edtPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtNewPasswordAgain.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential).addOnSuccessListener(unused -> {
                currentUser.updatePassword(newPassword).addOnSuccessListener(aVoid -> {
                    // Show the success dialog after successful password update
                    showSuccessDialog();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Display success dialog after changing password
    private void showSuccessDialog() {
        SuccessPasswordDialogFragment dialog = new SuccessPasswordDialogFragment();
        dialog.show(getSupportFragmentManager(), "SuccessPasswordDialog");
    }

    // Open forgot password activity (optional, if needed)
    public void open_forgot_password(View view) {
        Intent intent = new Intent(ChangePasswordSettingActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    // Implement the callback from SuccessPasswordDialogFragment
    @Override
    public void onLoginWithNewPassword() {
        // Handle successful login with new password, such as navigating to the login screen
        Toast.makeText(this, "Login with new password", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
