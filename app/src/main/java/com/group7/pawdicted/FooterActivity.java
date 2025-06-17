package com.group7.pawdicted;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class FooterActivity extends AppCompatActivity {
    ImageView imgHome, imgCategory, imgNotification, imgAccount;
    TextView txtHome, txtCategory, txtNotification, txtAccount;

    // List chứa thông tin icon + text + icon resource tương ứng
    List<MenuItem> menuItems = new ArrayList<>();

    // Class đại diện cho 1 mục menu
    static class MenuItem {
        ImageView icon;
        TextView text;
        int selectedIconRes;
        int unselectedIconRes;

        public MenuItem(ImageView icon, TextView text, int selectedIconRes, int unselectedIconRes) {
            this.icon = icon;
            this.text = text;
            this.selectedIconRes = selectedIconRes;
            this.unselectedIconRes = unselectedIconRes;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_footer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        imgHome = findViewById(R.id.imgHome);
        txtHome = findViewById(R.id.txtHome);
        imgCategory = findViewById(R.id.imgCategory);
        txtCategory = findViewById(R.id.txtCategoryFooter);
        imgNotification = findViewById(R.id.imgNotification);
        txtNotification = findViewById(R.id.txtNotification);
        imgAccount = findViewById(R.id.imgAccount);
        txtAccount = findViewById(R.id.txtAccount);

        // Thêm menu vào danh sách với hình đã chỉ định
        menuItems.add(new MenuItem(imgHome, txtHome, R.mipmap.ic_homepage_red, R.mipmap.ic_homepage_black));
        menuItems.add(new MenuItem(imgCategory, txtCategory, R.mipmap.ic_furniture_cate_red, R.mipmap.ic_furniture_cate_black));
        menuItems.add(new MenuItem(imgNotification, txtNotification, R.mipmap.ic_notification_footer_red, R.mipmap.ic_notification_footer_black));
        menuItems.add(new MenuItem(imgAccount, txtAccount, R.mipmap.ic_account_footer_red, R.mipmap.ic_account_footer_black));

        // Gán click cho từng mục
        for (MenuItem item : menuItems) {
            View.OnClickListener listener = v -> {
                resetAll(); // reset tất cả về unselected
                item.icon.setImageResource(item.selectedIconRes);
                item.text.setTextColor(ContextCompat.getColor(this, R.color.red));
            };

            item.icon.setOnClickListener(listener);
            item.text.setOnClickListener(listener);
        }

        menuItems.get(0).icon.setImageResource(menuItems.get(0).selectedIconRes);
        menuItems.get(0).text.setTextColor(ContextCompat.getColor(this, R.color.red));
    }

    // Reset toàn bộ về trạng thái unselected
    private void resetAll() {
        for (MenuItem item : menuItems) {
            item.icon.setImageResource(item.unselectedIconRes);
            item.text.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }
}
