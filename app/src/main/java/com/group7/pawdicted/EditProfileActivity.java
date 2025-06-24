package com.group7.pawdicted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group7.pawdicted.mobile.models.Customer;

import java.text.SimpleDateFormat;
import java.util.*;

public class EditProfileActivity extends AppCompatActivity {
    private static final int RESULT_PROFILE_UPDATED = 1001; // Mã kết quả cho cập nhật thành công
    private static final String TAG = "EDIT_PROFILE";

    private ImageView imgAvatar;
    private EditText edtName, edtDob, edtPhone, edtEmail, edtAddress;
    private Spinner spinnerGender;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userUid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        // Ánh xạ view
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        spinnerGender = findViewById(R.id.spinner_gender);
        edtDob = findViewById(R.id.edtDob);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lưu...");

        loadUserProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserProfile() {
        db.collection("customers").document(userUid)
                .get().addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Log.e(TAG, "Document không tồn tại");
                        return;
                    }
                    Customer c = doc.toObject(Customer.class);
                    if (c == null) {
                        Log.e(TAG, "Customer object is null");
                        return;
                    }

                    // Avatar
                    if (c.getAvatar_img() != null && !c.getAvatar_img().isEmpty()) {
                        Glide.with(this).load(c.getAvatar_img()).circleCrop().into(imgAvatar);
                    }

                    edtName.setText(c.getCustomer_name());
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
                    int pos = adapter.getPosition(c.getGender());
                    if (pos >= 0) spinnerGender.setSelection(pos);

                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    edtDob.setText(c.getDob() != null ? fmt.format(c.getDob()) : "");
                    edtPhone.setText(c.getPhone_number());
                    edtEmail.setText(c.getCustomer_email());
                    edtAddress.setText(c.getAddress());
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi load hồ sơ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi load hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfile() {
        String name = edtName.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String dobStr = edtDob.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || dobStr.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Tên, ngày sinh, số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dob;
        try {
            dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
        } catch (Exception ex) {
            Toast.makeText(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("customer_name", name);
        updates.put("gender", gender);
        updates.put("dob", dob);
        updates.put("phone_number", phone);
        updates.put("address", address);

        progressDialog.show();

        db.collection("customers").document(userUid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Cập nhật hồ sơ thành công, trả về kết quả");
                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    // Trả về kết quả để thông báo ProfileManagementActivity tải lại dữ liệu
                    Intent resultIntent = new Intent();
                    setResult(RESULT_PROFILE_UPDATED, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Lỗi lưu hồ sơ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void go_back(View v) {
        Log.d(TAG, "Quay lại mà không lưu");
        finish();
    }
}