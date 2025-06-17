package com.group7.pawdicted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Date;
import java.util.Locale;

public class ProfileManagementActivity extends AppCompatActivity {
    private static final int REQUEST_EDIT_PROFILE = 1001;
    private static final String TAG = "PROFILE_MANAGEMENT";

    private ImageView imgAvatar;
    private TextView txtName, txtGender, txtDob, txtPhone, txtEmail, txtAddress;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        // Ánh xạ view
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.tvName);
        txtGender = findViewById(R.id.tvGender);
        txtDob = findViewById(R.id.tvDob);
        txtPhone = findViewById(R.id.tvPhone);
        txtEmail = findViewById(R.id.tvEmail);
        txtAddress = findViewById(R.id.tvAddress);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang tải dữ liệu...");

        Log.d(TAG, "onCreate: Bắt đầu tải hồ sơ");
        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Người dùng chưa đăng nhập");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String uid = user.getUid();
        progressDialog.show();
        Log.d(TAG, "loadUserProfile: Tải dữ liệu cho userUid: " + uid);

        // Lấy dữ liệu từ server để tránh cache
        db.collection("customers").document(uid)
                .get(Source.SERVER)
                .addOnSuccessListener(doc -> {
                    progressDialog.dismiss();
                    if (doc.exists()) {
                        Log.d(TAG, "Tải dữ liệu thành công");
                        // Giả sử các field đúng như trong Customer model
                        String name = doc.getString("customer_name");
                        String gender = doc.getString("gender");
                        Date dobDate = doc.getDate("dob");
                        String phone = doc.getString("phone_number");
                        String email = doc.getString("customer_email");
                        String address = doc.getString("address");
                        String avatar = doc.getString("avatar_img");

                        // Cập nhật giao diện
                        txtName.setText(name != null ? name : "");
                        txtGender.setText(gender != null ? gender : "");
                        txtDob.setText(dobDate != null ?
                                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dobDate) : "");
                        txtPhone.setText(phone != null ? phone : "");
                        txtEmail.setText(email != null ? email : "");
                        txtAddress.setText(address != null ? address : "");

                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this).load(avatar)
                                    .placeholder(R.mipmap.ic_account_footer_red)
                                    .circleCrop()
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.mipmap.ic_account_footer_red);
                        }
                    } else {
                        Log.e(TAG, "Document không tồn tại");
                        Toast.makeText(this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Lỗi tải dữ liệu: " + e.getMessage());
                    Toast.makeText(this, "Không thể tải profile", Toast.LENGTH_SHORT).show();
                });
    }

    public void open_edit_profile(View view) {
        Log.d(TAG, "Mở EditProfileActivity với requestCode: " + REQUEST_EDIT_PROFILE);
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_PROFILE);
    }

    public void go_back(View view) {
        Log.d(TAG, "Quay lại màn hình trước");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == 1001) { // Kiểm tra resultCode khớp với RESULT_PROFILE_UPDATED
            Log.d(TAG, "Nhận kết quả cập nhật hồ sơ, tải lại dữ liệu");
            loadUserProfile();
        } else {
            Log.d(TAG, "Không nhận được kết quả cập nhật hoặc bị hủy");
        }
    }
}