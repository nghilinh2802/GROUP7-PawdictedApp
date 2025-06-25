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

        // Gửi email reset mật khẩu sau khi xác minh OTP thành công
        mAuth.sendPasswordResetEmail(emailToLogin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi email để thay đổi mật khẩu.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi gửi email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openOtpDialog(String otp) {
        OtpDialogFragment dialog = OtpDialogFragment.newInstance(otp, enteredPhone);
        dialog.show(getSupportFragmentManager(), "OtpDialog");
    }
}
