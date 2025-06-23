//////package com.group7.pawdicted;
//////
//////import android.app.ProgressDialog;
//////import android.content.Intent;
//////import android.os.Bundle;
//////import android.util.Log;
//////import android.view.View;
//////import android.widget.*;
//////import androidx.activity.EdgeToEdge;
//////import androidx.appcompat.app.AppCompatActivity;
//////import androidx.core.graphics.Insets;
//////import androidx.core.view.ViewCompat;
//////import androidx.core.view.WindowInsetsCompat;
//////
//////import com.bumptech.glide.Glide;
//////import com.google.firebase.auth.FirebaseAuth;
//////import com.google.firebase.firestore.FirebaseFirestore;
//////import com.group7.pawdicted.mobile.models.Customer;
//////
//////import java.text.SimpleDateFormat;
//////import java.util.*;
//////
//////public class EditProfileActivity extends AppCompatActivity {
//////    private static final int RESULT_PROFILE_UPDATED = 1001; // Mã kết quả cho cập nhật thành công
//////    private static final String TAG = "EDIT_PROFILE";
//////
//////    private ImageView imgAvatar;
//////    private EditText edtName, edtDob, edtPhone, edtEmail, edtAddress;
//////    private Spinner spinnerGender;
//////    private Button btnSave;
//////    private FirebaseFirestore db;
//////    private String userUid;
//////    private ProgressDialog progressDialog;
//////
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        EdgeToEdge.enable(this);
//////        setContentView(R.layout.activity_edit_profile);
//////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//////            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//////            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
//////            return insets;
//////        });
//////
//////        // Ánh xạ view
//////        imgAvatar = findViewById(R.id.imgAvatar);
//////        edtName = findViewById(R.id.edtName);
//////        spinnerGender = findViewById(R.id.spinner_gender);
//////        edtDob = findViewById(R.id.edtDob);
//////        edtPhone = findViewById(R.id.edtPhone);
//////        edtEmail = findViewById(R.id.edtEmail);
//////        edtAddress = findViewById(R.id.edtAddress);
//////        btnSave = findViewById(R.id.btnSave);
//////
//////        db = FirebaseFirestore.getInstance();
//////        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////
//////        progressDialog = new ProgressDialog(this);
//////        progressDialog.setCancelable(false);
//////        progressDialog.setMessage("Đang lưu...");
//////
//////        loadUserProfile();
//////
//////        btnSave.setOnClickListener(v -> saveProfile());
//////    }
//////
//////    private void loadUserProfile() {
//////        db.collection("customers").document(userUid)
//////                .get().addOnSuccessListener(doc -> {
//////                    if (!doc.exists()) {
//////                        Log.e(TAG, "Document không tồn tại");
//////                        return;
//////                    }
//////                    Customer c = doc.toObject(Customer.class);
//////                    if (c == null) {
//////                        Log.e(TAG, "Customer object is null");
//////                        return;
//////                    }
//////
//////                    // Avatar
//////                    if (c.getAvatar_img() != null && !c.getAvatar_img().isEmpty()) {
//////                        Glide.with(this).load(c.getAvatar_img()).circleCrop().into(imgAvatar);
//////                    }
//////
//////                    edtName.setText(c.getCustomer_name());
//////                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
//////                    int pos = adapter.getPosition(c.getGender());
//////                    if (pos >= 0) spinnerGender.setSelection(pos);
//////
//////                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//////                    edtDob.setText(c.getDob() != null ? fmt.format(c.getDob()) : "");
//////                    edtPhone.setText(c.getPhone_number());
//////                    edtEmail.setText(c.getCustomer_email());
//////                    edtAddress.setText(c.getAddress());
//////                }).addOnFailureListener(e -> {
//////                    Log.e(TAG, "Lỗi load hồ sơ: " + e.getMessage());
//////                    Toast.makeText(this, "Lỗi load hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//////                });
//////    }
//////
//////    private void saveProfile() {
//////        String name = edtName.getText().toString().trim();
//////        String gender = spinnerGender.getSelectedItem().toString();
//////        String dobStr = edtDob.getText().toString().trim();
//////        String phone = edtPhone.getText().toString().trim();
//////        String address = edtAddress.getText().toString().trim();
//////
//////        if (name.isEmpty() || dobStr.isEmpty() || phone.isEmpty()) {
//////            Toast.makeText(this, "Tên, ngày sinh, số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
//////            return;
//////        }
//////
//////        Date dob;
//////        try {
//////            dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
//////        } catch (Exception ex) {
//////            Toast.makeText(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
//////            return;
//////        }
//////
//////        Map<String, Object> updates = new HashMap<>();
//////        updates.put("customer_name", name);
//////        updates.put("gender", gender);
//////        updates.put("dob", dob);
//////        updates.put("phone_number", phone);
//////        updates.put("address", address);
//////
//////        progressDialog.show();
//////
//////        db.collection("customers").document(userUid)
//////                .update(updates)
//////                .addOnSuccessListener(aVoid -> {
//////                    progressDialog.dismiss();
//////                    Log.d(TAG, "Cập nhật hồ sơ thành công, trả về kết quả");
//////                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
//////                    // Trả về kết quả để thông báo ProfileManagementActivity tải lại dữ liệu
//////                    Intent resultIntent = new Intent();
//////                    setResult(RESULT_PROFILE_UPDATED, resultIntent);
//////                    finish();
//////                })
//////                .addOnFailureListener(e -> {
//////                    progressDialog.dismiss();
//////                    Log.e(TAG, "Lỗi lưu hồ sơ: " + e.getMessage());
//////                    Toast.makeText(this, "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//////                });
//////    }
//////
//////    public void go_back(View v) {
//////        Log.d(TAG, "Quay lại mà không lưu");
//////        finish();
//////    }
//////}
////
////
////package com.group7.pawdicted;
////
////import android.app.ProgressDialog;
////import android.content.Intent;
////import android.graphics.Bitmap;
////import android.net.Uri;
////import android.os.Bundle;
////import android.provider.MediaStore;
////import android.util.Base64;
////import android.util.Log;
////import android.view.View;
////import android.widget.*;
////
////import androidx.activity.EdgeToEdge;
////import androidx.annotation.Nullable;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.core.graphics.Insets;
////import androidx.core.view.ViewCompat;
////import androidx.core.view.WindowInsetsCompat;
////
////import com.bumptech.glide.Glide;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.firestore.FirebaseFirestore;
////import com.group7.pawdicted.mobile.models.Customer;
////
////import java.io.ByteArrayOutputStream;
////import java.io.IOException;
////import java.text.SimpleDateFormat;
////import java.util.*;
////
////public class EditProfileActivity extends AppCompatActivity {
////    private static final int RESULT_PROFILE_UPDATED = 1001; // Mã kết quả cho cập nhật thành công
////    private static final String TAG = "EDIT_PROFILE";
////    private static final int PICK_IMAGE_REQUEST = 1;
////
////    private ImageView imgAvatar;
////    private EditText edtName, edtDob, edtPhone, edtEmail, edtAddress;
////    private Spinner spinnerGender;
////    private Button btnSave;
////    private FirebaseFirestore db;
////    private String userUid;
////    private Uri imageUri;
////    private ProgressDialog progressDialog;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        EdgeToEdge.enable(this);
////        setContentView(R.layout.activity_edit_profile);
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
////            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
////            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
////            return insets;
////        });
////
////        // Ánh xạ view
////        imgAvatar = findViewById(R.id.imgAvatar);
////        edtName = findViewById(R.id.edtName);
////        spinnerGender = findViewById(R.id.spinner_gender);
////        edtDob = findViewById(R.id.edtDob);
////        edtPhone = findViewById(R.id.edtPhone);
////        edtEmail = findViewById(R.id.edtEmail);
////        edtAddress = findViewById(R.id.edtAddress);
////        btnSave = findViewById(R.id.btnSave);
////
////        db = FirebaseFirestore.getInstance();
////        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
////
////        progressDialog = new ProgressDialog(this);
////        progressDialog.setCancelable(false);
////        progressDialog.setMessage("Đang lưu...");
////
////        loadUserProfile();
////
////        // Mở thư viện ảnh khi click vào avatar
////        imgAvatar.setOnClickListener(v -> openImageChooser());
////
////        btnSave.setOnClickListener(v -> saveProfile());
////    }
////
////    private void loadUserProfile() {
////        db.collection("customers").document(userUid)
////                .get().addOnSuccessListener(doc -> {
////                    if (!doc.exists()) {
////                        Log.e(TAG, "Document không tồn tại");
////                        return;
////                    }
////                    Customer c = doc.toObject(Customer.class);
////                    if (c == null) {
////                        Log.e(TAG, "Customer object is null");
////                        return;
////                    }
////
////                    // Avatar
////                    if (c.getAvatar_img() != null && !c.getAvatar_img().isEmpty()) {
////                        Glide.with(this).load(c.getAvatar_img()).circleCrop().into(imgAvatar);
////                    }
////
////                    edtName.setText(c.getCustomer_name());
////                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
////                    int pos = adapter.getPosition(c.getGender());
////                    if (pos >= 0) spinnerGender.setSelection(pos);
////
////                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
////                    edtDob.setText(c.getDob() != null ? fmt.format(c.getDob()) : "");
////                    edtPhone.setText(c.getPhone_number());
////                    edtEmail.setText(c.getCustomer_email());
////                    edtAddress.setText(c.getAddress());
////                }).addOnFailureListener(e -> {
////                    Log.e(TAG, "Lỗi load hồ sơ: " + e.getMessage());
////                    Toast.makeText(this, "Lỗi load hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////                });
////    }
////
////    private void openImageChooser() {
////        Intent intent = new Intent(Intent.ACTION_PICK);
////        intent.setType("image/*");
////        startActivityForResult(intent, PICK_IMAGE_REQUEST);
////    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
////            imageUri = data.getData();
////            try {
////                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
////                imgAvatar.setImageBitmap(bitmap);
////                String encodedImage = encodeImageToBase64(bitmap);
////                uploadImageToFirestore(encodedImage);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////    }
////
////    private String encodeImageToBase64(Bitmap bitmap) {
////        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
////        byte[] byteArray = byteArrayOutputStream.toByteArray();
////        return Base64.encodeToString(byteArray, Base64.DEFAULT);
////    }
////
////    private void uploadImageToFirestore(String base64Image) {
////        Map<String, Object> updates = new HashMap<>();
////        updates.put("avatar_img", base64Image);
////
////        db.collection("customers").document(userUid)
////                .update(updates)
////                .addOnSuccessListener(aVoid -> {
////                    Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show();
////                })
////                .addOnFailureListener(e -> {
////                    Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show();
////                });
////    }
////
////    private void saveProfile() {
////        String name = edtName.getText().toString().trim();
////        String gender = spinnerGender.getSelectedItem().toString();
////        String dobStr = edtDob.getText().toString().trim();
////        String phone = edtPhone.getText().toString().trim();
////        String address = edtAddress.getText().toString().trim();
////
////        if (name.isEmpty() || dobStr.isEmpty() || phone.isEmpty()) {
////            Toast.makeText(this, "Tên, ngày sinh, số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        Date dob;
////        try {
////            dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
////        } catch (Exception ex) {
////            Toast.makeText(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        Map<String, Object> updates = new HashMap<>();
////        updates.put("customer_name", name);
////        updates.put("gender", gender);
////        updates.put("dob", dob);
////        updates.put("phone_number", phone);
////        updates.put("address", address);
////
////        progressDialog.show();
////
////        db.collection("customers").document(userUid)
////                .update(updates)
////                .addOnSuccessListener(aVoid -> {
////                    progressDialog.dismiss();
////                    Log.d(TAG, "Cập nhật hồ sơ thành công, trả về kết quả");
////                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
////                    // Trả về kết quả để thông báo ProfileManagementActivity tải lại dữ liệu
////                    Intent resultIntent = new Intent();
////                    setResult(RESULT_PROFILE_UPDATED, resultIntent);
////                    finish();
////                })
////                .addOnFailureListener(e -> {
////                    progressDialog.dismiss();
////                    Log.e(TAG, "Lỗi lưu hồ sơ: " + e.getMessage());
////                    Toast.makeText(this, "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////                });
////    }
////
////    public void go_back(View v) {
////        finish();
////    }
////}
//
//
//package com.group7.pawdicted;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.*;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.rpc.ErrorInfo;
//import com.group7.pawdicted.mobile.models.Customer;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import com.cloudinary.android.MediaManager;
//import com.cloudinary.android.callback.UploadCallback;
//import com.cloudinary.android.utils.ObjectUtils;
//import com.cloudinary.android.utils.ErrorInfo;
//
//
//public class EditProfileActivity extends AppCompatActivity {
//    private static final int RESULT_PROFILE_UPDATED = 1001; // Mã kết quả cho cập nhật thành công
//    private static final String TAG = "EDIT_PROFILE";
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private ImageView imgAvatar;
//    private EditText edtName, edtDob, edtPhone, edtEmail, edtAddress;
//    private Spinner spinnerGender;
//    private Button btnSave;
//    private FirebaseFirestore db;
//    private String userUid;
//    private Uri imageUri;
//    private ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_edit_profile);
//
//        // Khởi tạo Cloudinary
//        Map<String, String> config = ObjectUtils.asMap(
//                "cloud_name", "dukssof4h", // Cloud name của bạn
//                "api_key", "822835938877761", // API key của bạn
//                "api_secret", "q9dew-JWNhKau5w_UD2HBAe6ObY" // API secret của bạn (không chia sẻ công khai)
//        );
//        MediaManager.init(this, config);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
//            return insets;
//        });
//
//        // Ánh xạ view
//        imgAvatar = findViewById(R.id.imgAvatar);
//        edtName = findViewById(R.id.edtName);
//        spinnerGender = findViewById(R.id.spinner_gender);
//        edtDob = findViewById(R.id.edtDob);
//        edtPhone = findViewById(R.id.edtPhone);
//        edtEmail = findViewById(R.id.edtEmail);
//        edtAddress = findViewById(R.id.edtAddress);
//        btnSave = findViewById(R.id.btnSave);
//
//        db = FirebaseFirestore.getInstance();
//        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Đang lưu...");
//
//        loadUserProfile();
//
//        // Mở thư viện ảnh khi click vào avatar
//        imgAvatar.setOnClickListener(v -> openImageChooser());
//
//        btnSave.setOnClickListener(v -> saveProfile());
//    }
//
//    private void loadUserProfile() {
//        db.collection("customers").document(userUid)
//                .get().addOnSuccessListener(doc -> {
//                    if (!doc.exists()) {
//                        Log.e(TAG, "Document không tồn tại");
//                        return;
//                    }
//                    Customer c = doc.toObject(Customer.class);
//                    if (c == null) {
//                        Log.e(TAG, "Customer object is null");
//                        return;
//                    }
//
//                    // Avatar
//                    if (c.getAvatar_img() != null && !c.getAvatar_img().isEmpty()) {
//                        Glide.with(this).load(c.getAvatar_img()).circleCrop().into(imgAvatar);
//                    }
//
//                    edtName.setText(c.getCustomer_name());
//                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
//                    int pos = adapter.getPosition(c.getGender());
//                    if (pos >= 0) spinnerGender.setSelection(pos);
//
//                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                    edtDob.setText(c.getDob() != null ? fmt.format(c.getDob()) : "");
//                    edtPhone.setText(c.getPhone_number());
//                    edtEmail.setText(c.getCustomer_email());
//                    edtAddress.setText(c.getAddress());
//                }).addOnFailureListener(e -> {
//                    Log.e(TAG, "Lỗi load hồ sơ: " + e.getMessage());
//                    Toast.makeText(this, "Lỗi load hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void openImageChooser() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                imgAvatar.setImageBitmap(bitmap); // Hiển thị ảnh trên ImageView
//
//                // Tải ảnh lên Cloudinary
//                uploadImageToCloudinary(imageUri);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void uploadImageToCloudinary(Uri imageUri) {
//        File imageFile = new File(getRealPathFromURI(imageUri));  // Lấy đường dẫn ảnh từ URI
//
//        // Upload image to Cloudinary
//        MediaManager.get().upload(imageFile).callback(new UploadCallback() {
//            @Override
//            public void onStart(String requestId) {
//                // Khi bắt đầu tải lên
//            }
//
//            @Override
//            public void onProgress(String requestId, long bytesUploaded, long totalBytes) {
//                // Cập nhật tiến trình tải lên nếu cần
//            }
//
//            @Override
//            public void onSuccess(String requestId, Map resultData) {
//                String cloudinaryImageUrl = (String) resultData.get("url");  // Lấy URL ảnh từ Cloudinary
//                uploadImageUrlToFirestore(cloudinaryImageUrl);  // Lưu URL vào Firestore
//            }
//
//            @Override
//            public void onError(String requestId, ErrorInfo error) {
//                // Xử lý lỗi nếu có
//                Toast.makeText(EditProfileActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onReschedule(String requestId, ErrorInfo error) {
//                // Xử lý khi cần phải tiếp tục tải lên
//            }
//        }).dispatch();
//    }
//
//    private void uploadImageUrlToFirestore(String imageUrl) {
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("avatar_img", imageUrl);
//
//        db.collection("customers").document(userUid)
//                .update(updates)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void saveProfile() {
//        String name = edtName.getText().toString().trim();
//        String gender = spinnerGender.getSelectedItem().toString();
//        String dobStr = edtDob.getText().toString().trim();
//        String phone = edtPhone.getText().toString().trim();
//        String address = edtAddress.getText().toString().trim();
//
//        if (name.isEmpty() || dobStr.isEmpty() || phone.isEmpty()) {
//            Toast.makeText(this, "Tên, ngày sinh, số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Date dob;
//        try {
//            dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
//        } catch (Exception ex) {
//            Toast.makeText(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("customer_name", name);
//        updates.put("gender", gender);
//        updates.put("dob", dob);
//        updates.put("phone_number", phone);
//        updates.put("address", address);
//
//        progressDialog.show();
//
//        db.collection("customers").document(userUid)
//                .update(updates)
//                .addOnSuccessListener(aVoid -> {
//                    progressDialog.dismiss();
//                    Log.d(TAG, "Cập nhật hồ sơ thành công, trả về kết quả");
//                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
//                    // Trả về kết quả để thông báo ProfileManagementActivity tải lại dữ liệu
//                    Intent resultIntent = new Intent();
//                    setResult(RESULT_PROFILE_UPDATED, resultIntent);
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Log.e(TAG, "Lỗi lưu hồ sơ: " + e.getMessage());
//                    Toast.makeText(this, "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    public void go_back(View v) {
//        finish();
//    }
//}


package com.group7.pawdicted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    private ImageView imgAvatar;
    private EditText edtName, edtDob, edtPhone, edtEmail, edtAddress;
    private Spinner spinnerGender;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userUid;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dukssof4h"); // Cloud name
        config.put("api_key", "822835938877761"); // API key
        config.put("api_secret", "q9dew-JWNhKau5w_UD2HBAe6ObY"); // API secret

        // Initialize MediaManager with the configuration
        MediaManager.init(this, config);

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

        // Mở thư viện ảnh khi click vào avatar
        imgAvatar.setOnClickListener(v -> openImageChooser());

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

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap); // Hiển thị ảnh trên ImageView

                // Tải ảnh lên Cloudinary
                uploadImageToCloudinary(imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        } else {
            return null;
        }
    }


    private void uploadImageToCloudinary(Uri imageUri) {
        String imagePath = getRealPathFromURI(imageUri); // Get the file path from URI
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath); // Create File object from path

            // Check if the file exists
            if (imageFile.exists()) {
                // Upload image to Cloudinary
                MediaManager.get().upload(Uri.fromFile(imageFile))
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                                // Handle when upload starts
                            }

                            @Override
                            public void onProgress(String requestId, long bytesUploaded, long totalBytes) {
                                // Handle upload progress
                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                // Handle successful upload and extract the URL
                                String cloudinaryImageUrl = (String) resultData.get("url");
                                uploadImageUrlToFirestore(cloudinaryImageUrl);  // Upload the URL to Firestore
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                // Handle error if upload fails
                                Toast.makeText(EditProfileActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {
                                // Handle retry if needed
                            }
                        })
                        .dispatch();
            } else {
                Toast.makeText(this, "Image file does not exist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid image path", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageUrlToFirestore(String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("avatar_img", imageUrl);

        db.collection("customers").document(userUid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show();
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
        finish();
    }
}
