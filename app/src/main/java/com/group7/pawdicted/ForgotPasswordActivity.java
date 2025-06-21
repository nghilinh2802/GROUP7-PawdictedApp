//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {
//
//    private ImageButton btnBack;
//    private EditText edtPhone;
//    private Button btnSendRecoverCode;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_forgot_password);
//
//        // Ánh xạ các thành phần
//        btnBack = findViewById(R.id.btnBack);
//        edtPhone = findViewById(R.id.edtPhone);
//        btnSendRecoverCode = findViewById(R.id.btnSendRecoverCode);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Xử lý nút Back
//        btnBack.setOnClickListener(v -> onBackPressed());
//
//        // Xử lý nút Send Recover Code
//        btnSendRecoverCode.setOnClickListener(v -> {
//            String phone = edtPhone.getText().toString().trim();
//            if (!phone.isEmpty()) {
//                showOtpDialog();
//            } else {
//                Toast.makeText(this, "Please fill phone number", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void goBack(View view) {
//        finish();
//    }
//
//    private void showOtpDialog() {
//        OtpDialogFragment dialog = new OtpDialogFragment();
//        dialog.show(getSupportFragmentManager(), "OtpDialog");
//    }
//
//    @Override
//    public void onOtpVerified(String otp) {
//        // Xử lý khi OTP được xác nhận
//        Toast.makeText(this, "OTP Verified: " + otp, Toast.LENGTH_SHORT).show();
//        // Chuyển sang trang Reset Password
//        Intent intent = new Intent(this, ChangeForgetPasswordActivity.class);
//        startActivity(intent);
//        finish(); // Đóng ForgotPasswordActivity sau khi chuyển
//    }
//}


//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthOptions;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.concurrent.TimeUnit;
//
//public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {
//
//    private ImageButton btnBack;
//    private EditText edtPhone;
//    private Button btnSendRecoverCode;
//    private String mVerificationId;
//    private String enteredPhone;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_forgot_password);
//
//        btnBack = findViewById(R.id.btnBack);
//        edtPhone = findViewById(R.id.edtPhone);
//        btnSendRecoverCode = findViewById(R.id.btnSendRecoverCode);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        btnBack.setOnClickListener(v -> onBackPressed());
//
//        btnSendRecoverCode.setOnClickListener(v -> {
//            String phone = edtPhone.getText().toString().trim();
//            if (!phone.isEmpty()) {
//                FirebaseFirestore.getInstance().collection("customers")
//                        .whereEqualTo("phone_number", phone)
//                        .limit(1)
//                        .get()
//                        .addOnSuccessListener(snapshot -> {
//                            if (!snapshot.isEmpty()) {
//                                sendOtp(phone);
//                                enteredPhone = phone;
//                            } else {
//                                Toast.makeText(this, "Phone number not registered", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(this, "Error checking phone: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//            } else {
//                Toast.makeText(this, "Please fill phone number", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void sendOtp(String phone) {
//        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
//                .setPhoneNumber("+84" + phone.substring(1))
//                .setTimeout(60L, TimeUnit.SECONDS)
//                .setActivity(this)
//                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                    }
//
//                    @Override
//                    public void onVerificationFailed(@NonNull FirebaseException e) {
//                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                        mVerificationId = verificationId;
//                        openOtpDialog();
//                    }
//                })
//                .build();
//
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//
//    private void openOtpDialog() {
//        OtpDialogFragment dialog = OtpDialogFragment.newInstance(mVerificationId, enteredPhone);
//        dialog.show(getSupportFragmentManager(), "OtpDialog");
//    }
//
//    @Override
//    public void onOtpVerified(String otp) {
//        Intent intent = new Intent(this, ChangeForgetPasswordActivity.class);
//        intent.putExtra("verifiedPhone", enteredPhone);
//        startActivity(intent);
//        finish();
//    }
//
//    public void goBack(View view) {
//        finish();
//    }
//}






//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthOptions;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.concurrent.TimeUnit;
//
//public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {
//
//    private ImageButton btnBack;
//    private EditText edtPhone;
//    private Button btnSendRecoverCode;
//    private String mVerificationId;
//    private String enteredPhone;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_forgot_password);
//
//        btnBack = findViewById(R.id.btnBack);
//        edtPhone = findViewById(R.id.edtPhone);
//        btnSendRecoverCode = findViewById(R.id.btnSendRecoverCode);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        btnBack.setOnClickListener(v -> onBackPressed());
//
//        btnSendRecoverCode.setOnClickListener(v -> {
//            String phone = edtPhone.getText().toString().trim();
//            if (!phone.isEmpty()) {
//                // Ensure format +84xxx
//                String fullPhone = phone.startsWith("+") ? phone : "+84" + phone.substring(1);
//
//                FirebaseFirestore.getInstance().collection("customers")
//                        .whereEqualTo("phone_number", phone)
//                        .limit(1)
//                        .get()
//                        .addOnSuccessListener(snapshot -> {
//                            if (!snapshot.isEmpty()) {
//                                sendOtp(fullPhone); // Sử dụng số có +84
//                                enteredPhone = phone;
//                            } else {
//                                Toast.makeText(this, "Phone number not registered", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(this, "Error checking phone: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//            } else {
//                Toast.makeText(this, "Please fill phone number", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void sendOtp(String phone) {
//        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
//                .setPhoneNumber(phone) // Đã có định dạng +84xxxx
//                .setTimeout(60L, TimeUnit.SECONDS)
//                .setActivity(this)
//                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                        // Không cần xử lý auto fill trong dev mode
//                    }
//
//                    @Override
//                    public void onVerificationFailed(@NonNull FirebaseException e) {
//                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                        mVerificationId = verificationId;
//                        openOtpDialog();
//                    }
//                })
//                .build();
//
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//
//    private void openOtpDialog() {
//        OtpDialogFragment dialog = OtpDialogFragment.newInstance(mVerificationId, enteredPhone);
//        dialog.show(getSupportFragmentManager(), "OtpDialog");
//    }
//
//    @Override
//    public void onOtpVerified(String otp) {
//        Intent intent = new Intent(this, ChangeForgetPasswordActivity.class);
//        intent.putExtra("verifiedPhone", enteredPhone);
//        startActivity(intent);
//        finish();
//    }
//
//    public void goBack(View view) {
//        finish();
//    }
//}



// ForgotPasswordActivity.java
package com.group7.pawdicted;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {

    private ImageButton btnBack;
    private EditText edtPhone;
    private Button btnSendRecoverCode;
    private String enteredPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        btnBack = findViewById(R.id.btnBack);
        edtPhone = findViewById(R.id.edtPhone);
        btnSendRecoverCode = findViewById(R.id.btnSendRecoverCode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack.setOnClickListener(v -> finish());

        btnSendRecoverCode.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            if (!phone.isEmpty()) {
                FirebaseFirestore.getInstance().collection("customers")
                        .whereEqualTo("phone_number", phone)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            if (!snapshot.isEmpty()) {
                                String otp = generateOtp();
                                sendSms(phone, otp);
                                enteredPhone = phone;
                                openOtpDialog(otp);
                            } else {
                                Toast.makeText(this, "Phone number not registered", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error checking phone: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Please fill phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateOtp() {
        int otp = 100000 + new java.util.Random().nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendSms(String phoneNumber, String otp) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Your OTP code is: " + otp, null, null);
            Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openOtpDialog(String otp) {
        OtpDialogFragment dialog = OtpDialogFragment.newInstance(otp, enteredPhone);
        dialog.show(getSupportFragmentManager(), "OtpDialog");
    }

    @Override
    public void onOtpVerified(String otp) {
        Intent intent = new Intent(this, ChangeForgetPasswordActivity.class);
        intent.putExtra("verifiedPhone", enteredPhone);
        startActivity(intent);
        finish();
    }
}
