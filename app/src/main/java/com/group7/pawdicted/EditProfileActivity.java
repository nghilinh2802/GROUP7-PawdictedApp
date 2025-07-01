package com.group7.pawdicted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.callback.ErrorInfo;
import java.util.HashMap;
import java.util.Map;
import com.group7.pawdicted.mobile.models.Customer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EditProfileActivity extends AppCompatActivity {
    private static final int RESULT_PROFILE_UPDATED = 1001;
    private static final String TAG = "EDIT_PROFILE";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgAvatar, btnChangeAvatar;
    private EditText edtName, edtDob, edtPhone, edtEmail;
    private Spinner spinnerGender;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private String userUid;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }


        // Khởi tạo Cloudinary
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dukssof4h");
            config.put("api_key", "822835938877761");
            config.put("api_secret", "q9dew-JWNhKau5w_UD2HBAe6ObY");
            config.put("secure", "true");
            MediaManager.init(this, config);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo Cloudinary: " + e.getMessage());
        }

        // Ánh xạ view
        initViews();

        db = FirebaseFirestore.getInstance();

        // Kiểm tra user đã đăng nhập
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lưu...");

        loadUserProfile();

        // Set up click listeners
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v -> openImageChooser());
        }
        if (btnChangeAvatar != null) {
            btnChangeAvatar.setOnClickListener(v -> openImageChooser());
        }
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProfile());
        }
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        spinnerGender = findViewById(R.id.spinner_gender);
        edtDob = findViewById(R.id.edtDob);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadUserProfile() {
        if (userUid == null) return;

        db.collection("customers").document(userUid)
                .get().addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Log.e(TAG, "Document không tồn tại");
                        return;
                    }

                    try {
                        Customer c = doc.toObject(Customer.class);
                        if (c == null) {
                            Log.e(TAG, "Customer object is null");
                            return;
                        }

                        // Load avatar
                        if (c.getAvatar_img() != null && !c.getAvatar_img().isEmpty() && imgAvatar != null) {
                            Glide.with(this).load(c.getAvatar_img()).circleCrop().into(imgAvatar);
                        }

                        // Load other fields
                        if (edtName != null) edtName.setText(c.getCustomer_name() != null ? c.getCustomer_name() : "");

                        if (spinnerGender != null && spinnerGender.getAdapter() != null) {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
                            if (c.getGender() != null) {
                                int pos = adapter.getPosition(c.getGender());
                                if (pos >= 0) spinnerGender.setSelection(pos);
                            }
                        }

                        if (edtDob != null && c.getDob() != null) {
                            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            edtDob.setText(fmt.format(c.getDob()));
                        }

                        if (edtPhone != null) edtPhone.setText(c.getPhone_number() != null ? c.getPhone_number() : "");
                        if (edtEmail != null) edtEmail.setText(c.getCustomer_email() != null ? c.getCustomer_email() : "");

                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse dữ liệu: " + e.getMessage());
                    }

                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi load hồ sơ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi load hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                if (imgAvatar != null) {
                    imgAvatar.setImageBitmap(bitmap);
                }
                uploadImageToCloudinary(imageUri);
            } catch (IOException e) {
                Log.e(TAG, "Lỗi load ảnh: " + e.getMessage());
                Toast.makeText(this, "Lỗi load ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi get path: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        String imagePath = getRealPathFromURI(imageUri);
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                try {
                    MediaManager.get().upload(Uri.fromFile(imageFile))
                            .callback(new UploadCallback() {
                                @Override
                                public void onStart(String requestId) {
                                    // Upload started
                                }

                                @Override
                                public void onProgress(String requestId, long bytesUploaded, long totalBytes) {
                                    // Upload progress
                                }

                                @Override
                                public void onSuccess(String requestId, Map resultData) {
                                    // Ưu tiên secure_url (HTTPS), fallback về url
                                    String cloudinaryImageUrl = (String) resultData.get("secure_url");
                                    if (cloudinaryImageUrl == null) {
                                        cloudinaryImageUrl = (String) resultData.get("url");
                                        // Force HTTPS nếu cần
                                        if (cloudinaryImageUrl != null && cloudinaryImageUrl.startsWith("http://")) {
                                            cloudinaryImageUrl = cloudinaryImageUrl.replace("http://", "https://");
                                        }
                                    }

                                    if (cloudinaryImageUrl != null) {
                                        uploadImageUrlToFirestore(cloudinaryImageUrl);
                                    }
                                }

                                @Override
                                public void onError(String requestId, ErrorInfo error) {
                                    runOnUiThread(() ->
                                            Toast.makeText(EditProfileActivity.this,
                                                    "Upload failed: " + error.getDescription(),
                                                    Toast.LENGTH_SHORT).show()
                                    );
                                }

                                @Override
                                public void onReschedule(String requestId, ErrorInfo error) {
                                    // Handle retry
                                }
                            })
                            .dispatch();
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi upload Cloudinary: " + e.getMessage());
                    Toast.makeText(this, "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "File ảnh không tồn tại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Đường dẫn ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageUrlToFirestore(String imageUrl) {
        if (userUid == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("avatar_img", imageUrl);

        db.collection("customers").document(userUid)
                .update(updates)
                .addOnSuccessListener(aVoid ->
                        runOnUiThread(() ->
                                Toast.makeText(this, R.string.update_profile_successful, Toast.LENGTH_SHORT).show()
                        )
                )
                .addOnFailureListener(e ->
                        runOnUiThread(() ->
                                Toast.makeText(this, R.string.update_profile_failed, Toast.LENGTH_SHORT).show()
                        )
                );
    }

    private void saveProfile() {
        if (edtName == null || edtDob == null || edtPhone == null) {
            Toast.makeText(this, "Lỗi giao diện", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtName.getText().toString().trim();
        String gender = spinnerGender != null ? spinnerGender.getSelectedItem().toString() : "";
        String dobStr = edtDob.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

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

        progressDialog.show();

        db.collection("customers").document(userUid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Cập nhật hồ sơ thành công");
                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();

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

    public void go_back(View view) {
        finish();
    }
}