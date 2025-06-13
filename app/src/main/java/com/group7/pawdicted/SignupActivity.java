package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.group7.pawdicted.mobile.models.Customer;

import java.util.ArrayList;
import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    EditText edtUsername, edtEmail, edtPhone, edtPassword;
    CheckBox chkAgree;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtEnterPassword);
        chkAgree = findViewById(R.id.ckbAgree);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("customers");

        findViewById(R.id.btnLogin).setOnClickListener(v -> registerUser());
    }

    public void open_login(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }

    public void goBack(View view) {
        finish();
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!chkAgree.isChecked()) {
            Toast.makeText(this, "Bạn phải đồng ý với điều khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔍 Kiểm tra email hoặc số điện thoại đã tồn tại
        mDatabase.orderByChild("customer_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDatabase.orderByChild("phone_number").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot2) {
                        if (snapshot2.exists()) {
                            Toast.makeText(SignupActivity.this, "Số điện thoại đã được sử dụng!", Toast.LENGTH_SHORT).show();
                        } else {
                            createFirebaseUser(username, email, phone, password);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FIREBASE_SIGNUP", "Lỗi kiểm tra phone: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FIREBASE_SIGNUP", "Lỗi kiểm tra email: " + error.getMessage());
            }
        });
    }

    private void createFirebaseUser(String username, String email, String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            Log.e("FIREBASE_SIGNUP", "FirebaseUser null mặc dù task thành công");
                            Toast.makeText(this, "Lỗi tạo tài khoản!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        Customer customer = new Customer(
                                0,
                                username,
                                email,
                                username,
                                null,
                                phone,
                                "",
                                new Date(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        );

                        mDatabase.child(uid).setValue(customer)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FIREBASE_SIGNUP", "Lỗi ghi DB:", e);
                                    Toast.makeText(SignupActivity.this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Exception e = task.getException();
                        Log.e("FIREBASE_SIGNUP", "Lỗi đăng ký:", e);
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
