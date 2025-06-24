package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class ChangeForgetPasswordActivity extends AppCompatActivity implements SuccessPasswordDialogFragment.OnLoginWithNewPasswordListener {

    private ImageButton btnBack;
    private EditText edtNewPassword, edtNewPasswordAgain;
    private Button btnChangePassword;
    private String verifiedPhone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_forget_password);

        // Ánh xạ các thành phần
        btnBack = findViewById(R.id.btnBack);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordAgain = findViewById(R.id.edtNewPasswordAgain);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        verifiedPhone = getIntent().getStringExtra("verifiedPhone");

        btnChangePassword.setOnClickListener(v -> {
            String pass = edtNewPassword.getText().toString().trim();
            String retype = edtNewPasswordAgain.getText().toString().trim();

            if (pass.isEmpty() || retype.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(retype)) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải ≥ 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(pass);
        });
    }

    private void updatePassword(String newPass) {
        db.collection("customers")
                .whereEqualTo("phone_number", verifiedPhone)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot doc = snapshot.getDocuments().get(0);
                    String email = doc.getString("customer_email");

                    mAuth.signInWithEmailAndPassword(email, newPass) // login tạm để cập nhật
                            .addOnSuccessListener(result -> {
                                FirebaseUser user = result.getUser();
                                if (user != null) {
                                    user.updatePassword(newPass)
                                            .addOnSuccessListener(aVoid -> {
                                                mAuth.signOut();
                                                showSuccessDialog();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi đổi mật khẩu: " + e.getMessage(), Toast.LENGTH_LONG).show());
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xác thực người dùng!", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi truy vấn Firestore!", Toast.LENGTH_SHORT).show());
    }

    private void showSuccessDialog() {
        SuccessPasswordDialogFragment dialog = new SuccessPasswordDialogFragment();
        dialog.show(getSupportFragmentManager(), "SuccessDialog");
    }

    @Override
    public void onLoginWithNewPassword() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
