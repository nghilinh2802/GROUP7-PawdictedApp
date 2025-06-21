package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        // Get current language stored in SharedPreferences, or default to "en" (English) if not set
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANG, "en");  // Default to "en" if no language is set
        setLocale(lang);  // Apply the default language immediately when opening the settings screen

        setContentView(R.layout.activity_language_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutEnglish = findViewById(R.id.layoutEnglish);
        layoutVietnamese = findViewById(R.id.layoutVietnamese);
        imgTickEnglish = findViewById(R.id.imgTickEnglish);
        imgTickVietnamese = findViewById(R.id.imgTickVietnamese);

        // Set the tick based on the current language
        boolean isEnglish = lang.equals("en");
        updateTick(isEnglish);

        // Change language to English
        layoutEnglish.setOnClickListener(v -> changeLanguage("en"));

        // Change language to Vietnamese
        layoutVietnamese.setOnClickListener(v -> changeLanguage("vi"));
    }

    private void changeLanguage(String langCode) {
        // Save the language preference
        prefs.edit().putString(KEY_LANG, langCode).apply();

        // Apply the new locale immediately
        setLocale(langCode);

        // Update the UI with the selected language and show the changes
        updateTick(langCode.equals("en"));

        // After language change, navigate back to the home screen
        navigateToHome();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        // Set the configuration for the app with the new language
        getResources().getConfiguration().setLocale(locale);
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
    }

    private void updateTick(boolean isEnglish) {
        imgTickEnglish.setVisibility(isEnglish ? View.VISIBLE : View.GONE);
        imgTickVietnamese.setVisibility(isEnglish ? View.GONE : View.VISIBLE);
    }

    private void navigateToHome() {
        Intent intent = new Intent(LanguageSettingActivity.this, HomepageActivity.class);  // Change to your home screen class
        startActivity(intent);
        finish();  // Close the current LanguageSettingActivity
    }

    public void go_back(View view) {
        finish();
    }
}