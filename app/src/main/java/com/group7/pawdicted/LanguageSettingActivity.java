package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class LanguageSettingActivity extends AppCompatActivity {

    private LinearLayout layoutEnglish, layoutVietnamese;
    private ImageView imgTickEnglish, imgTickVietnamese;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANG = "app_language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy ngôn ngữ đã lưu (nếu có)
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANG, Locale.getDefault().getLanguage());
        setLocale(lang);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        layoutEnglish = findViewById(R.id.layoutEnglish);
        layoutVietnamese = findViewById(R.id.layoutVietnamese);
        imgTickEnglish = findViewById(R.id.imgTickEnglish);
        imgTickVietnamese = findViewById(R.id.imgTickVietnamese);

        // Áp tick theo ngôn ngữ hiện tại
        boolean isEnglish = lang.equals("en");
        updateTick(isEnglish);

        layoutEnglish.setOnClickListener(v -> changeLanguage("en"));
        layoutVietnamese.setOnClickListener(v -> changeLanguage("vi"));
    }

    private void changeLanguage(String langCode) {
        // Lưu ngôn ngữ
        prefs.edit().putString(KEY_LANG, langCode).apply();
        // Áp locale mới
        setLocale(langCode);
        // Khởi động lại chính Activity để áp dụng UI mới
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
    }

    private void updateTick(boolean isEnglish) {
        imgTickEnglish.setVisibility(isEnglish ? View.VISIBLE : View.GONE);
        imgTickVietnamese.setVisibility(isEnglish ? View.GONE : View.VISIBLE);
    }

    public void go_back(View view) {
        finish();
    }
}
