package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SettingManagementActivity extends AppCompatActivity {
    private static final String TAG = "SETTING_MANAGEMENT";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    public void go_back(View view) {
        finish();
    }

    public void open_language_setting(View view) {
        Intent intent = new Intent(SettingManagementActivity.this, LanguageSettingActivity.class);
        startActivity(intent);
    }

    public void open_change_password(View view) {
        Intent intent = new Intent(SettingManagementActivity.this, ChangePasswordSettingActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        // Hiển thị dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Đăng xuất khỏi Firebase
                    mAuth.signOut();

                    // Chuyển hướng về LoginActivity và xóa stack Activity
                    Intent intent = new Intent(SettingManagementActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    // Hiển thị thông báo đăng xuất thành công
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    // Kết thúc Activity hiện tại
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}