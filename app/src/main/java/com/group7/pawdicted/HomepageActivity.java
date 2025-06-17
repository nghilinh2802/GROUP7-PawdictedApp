package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomepageActivity extends AppCompatActivity {
    FooterManager footerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayoutmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        footerManager = new FooterManager(this);
    }

    public void open_blogs(View view) {
        Intent intent=new Intent(HomepageActivity.this,BlogActivity.class);
        startActivity(intent);
    }

    public void open_policy(View view) {
        Intent intent=new Intent(HomepageActivity.this,PolicynSecurityActivity.class);
        startActivity(intent);
    }

    public void open_faq(View view) {
        Intent intent=new Intent(HomepageActivity.this,FAQActivity.class);
        startActivity(intent);
    }

    public void open_category(View view) {
        Intent intent=new Intent(HomepageActivity.this,CategoryActivity.class);
        startActivity(intent);
    }

    public void open_newArrival(View view) {
        Intent intent=new Intent(HomepageActivity.this,NewArrivalActivity.class);
        startActivity(intent);
    }
}