package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ToolBarActivity extends AppCompatActivity {

    private LinearLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tool_bar);

        // Initialize views
        searchBar = findViewById(R.id.search_bar);

        // Set up click listener for search bar
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SearchActivity
                Intent intent = new Intent(ToolBarActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to handle chat button click (if needed)
    public void open_chat(View view) {
        // Handle chat functionality here
    }

    public void open_search(View view) {
    }

    public void open_cart(View view) {
    }
}