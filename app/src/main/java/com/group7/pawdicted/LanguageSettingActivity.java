//package com.group7.pawdicted;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class LanguageSettingActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_language_setting);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}


package com.group7.pawdicted;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LanguageSettingActivity extends AppCompatActivity {

    private LinearLayout layoutEnglish, layoutVietnamese;
    private ImageView imgTickEnglish, imgTickVietnamese;
    private boolean isEnglishSelected = true; // Mặc định chọn English

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các thành phần
        layoutEnglish = findViewById(R.id.layoutEnglish);
        layoutVietnamese = findViewById(R.id.layoutVietnamese);
        imgTickEnglish = findViewById(R.id.imgTickEnglish);
        imgTickVietnamese = findViewById(R.id.imgTickVietnamese);

        // Thiết lập trạng thái mặc định
        updateTickVisibility();

        // Xử lý sự kiện click cho English
        layoutEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnglishSelected = true;
                updateTickVisibility();
            }
        });

        // Xử lý sự kiện click cho Vietnamese
        layoutVietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnglishSelected = false;
                updateTickVisibility();
            }
        });
    }

    private void updateTickVisibility() {
        // Cập nhật hiển thị tick
        imgTickEnglish.setVisibility(isEnglishSelected ? View.VISIBLE : View.GONE);
        imgTickVietnamese.setVisibility(isEnglishSelected ? View.GONE : View.VISIBLE);
    }

    public void go_back(View view) {
        finish();
    }
}