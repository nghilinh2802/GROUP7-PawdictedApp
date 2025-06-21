//package com.group7.pawdicted;
//
//import android.Manifest;
//import android.content.Intent;
//import android.os.Bundle;
//import android.telephony.SmsManager;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {
//
//    private ImageButton btnBack;
//    private EditText edtPhone;
//    private Button btnSendRecoverCode;
//    private String enteredPhone;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_forgot_password);
//
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
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
//        btnBack.setOnClickListener(v -> finish());
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
//                                String otp = generateOtp();
//                                sendSms(phone, otp);
//                                enteredPhone = phone;
//                                openOtpDialog(otp);
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
//    private String generateOtp() {
//        int otp = 100000 + new java.util.Random().nextInt(900000);
//        return String.valueOf(otp);
//    }
//
//    private void sendSms(String phoneNumber, String otp) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNumber, null, "Your OTP code is: " + otp, null, null);
//            Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void openOtpDialog(String otp) {
//        OtpDialogFragment dialog = OtpDialogFragment.newInstance(otp, enteredPhone);
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
//}


package com.group7.pawdicted;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

public class ForgotPasswordActivity extends AppCompatActivity implements OtpDialogFragment.OnOtpVerifiedListener {

    private ImageButton btnBack;
    private EditText edtPhone;
    private Button btnSendRecoverCode;
    private String enteredPhone;
    private String sentOtp;
    private String emailToLogin;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                enteredPhone = phone;
                db.collection("customers")
                        .whereEqualTo("phone_number", phone)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            if (!snapshot.isEmpty()) {
                                emailToLogin = snapshot.getDocuments().get(0).getString("customer_email");
                                sentOtp = generateOtp();
                                sendSms(phone, sentOtp);
                                openOtpDialog(sentOtp);
                            } else {
                                Toast.makeText(this, "Số điện thoại chưa đăng ký!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi truy vấn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateOtp() {
        return String.valueOf(100000 + new java.util.Random().nextInt(900000));
    }

    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Mã OTP của bạn là: " + message, null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể gửi OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOtpVerified(String otp) {
        if (!otp.equals(sentOtp)) {
            Toast.makeText(this, "Mã OTP không chính xác!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tempPassword = "pw" + (100000 + new java.util.Random().nextInt(900000));

        mAuth.signInWithEmailAndPassword(emailToLogin, "123456")
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.updatePassword(tempPassword)
                                .addOnSuccessListener(unused -> {
                                    sendSms(enteredPhone, "Mật khẩu mới của bạn là: " + tempPassword);
                                    Toast.makeText(this, "Đăng nhập bằng mật khẩu tạm thời", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.putExtra("temp_email", emailToLogin);
                                    intent.putExtra("temp_password", tempPassword);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Lỗi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không thể đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openOtpDialog(String otp) {
        OtpDialogFragment dialog = OtpDialogFragment.newInstance(otp, enteredPhone);
        dialog.show(getSupportFragmentManager(), "OtpDialog");
    }
}
