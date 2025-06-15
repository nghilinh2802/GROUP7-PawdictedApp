package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.group7.pawdicted.mobile.models.Customer;

import java.util.ArrayList;
import java.util.Date;

public class SignupActivity extends AppCompatActivity implements SuccessSignupDialogFragment.OnSignupListener {

    EditText edtUsername, edtEmail, edtPhone, edtPassword;
    CheckBox chkAgree;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        // 1. Validation
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
        if (!phone.matches("[0-9]+")) {
            Toast.makeText(this, "Số điện thoại phải là chữ số!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Check duplicate email
        mDatabase.orderByChild("customer_email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapEmail) {
                        if (snapEmail.exists()) {
                            Toast.makeText(SignupActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 3. Check duplicate phone
                        mDatabase.orderByChild("phone_number").equalTo(phone)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapPhone) {
                                        if (snapPhone.exists()) {
                                            Toast.makeText(SignupActivity.this, "Số điện thoại đã được sử dụng!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        // 4. Everything OK -> create FirebaseAuth user
                                        createFirebaseUser(username, email, phone, password);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.e("FIREBASE_SIGNUP", "Error checking phone: " + error.getMessage());
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FIREBASE_SIGNUP", "Error checking email: " + error.getMessage());
                    }
                });
    }

    private void createFirebaseUser(String username, String email, String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        Log.e("FIREBASE_SIGNUP", "Registration failed:", e);
                        Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        Log.e("FIREBASE_SIGNUP", "FirebaseUser null dù đăng ký thành công");
                        Toast.makeText(this, "Lỗi khi tạo tài khoản!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = firebaseUser.getUid();
                    Customer customer = new Customer(
                            uid,
                            username,
                            email,
                            username,
                            null,
                            phone,
                            "",
                            "Male",
                            new Date(),
                            new Date(),
                            "",
                            "Customer",
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>()
                    );

                    // 5. Save under customers/UID
                    mDatabase.child(uid).setValue(customer)
                            .addOnSuccessListener(unused -> showSuccessDialog())
                            .addOnFailureListener(e -> {
                                Log.e("FIREBASE_SIGNUP", "Error saving customer data:", e);
                                Toast.makeText(SignupActivity.this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void showSuccessDialog() {
        new SuccessSignupDialogFragment()
                .show(getSupportFragmentManager(), "SuccessDialog");
    }

    @Override
    public void onSignupComplete() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}
