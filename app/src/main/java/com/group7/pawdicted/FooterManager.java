package com.group7.pawdicted;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class FooterManager {
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final Activity activity;

    static class MenuItem {
        LinearLayout layout;
        ImageView icon;
        TextView text;
        int selectedIconRes;
        int unselectedIconRes;
        Class<? extends Activity> targetActivity;

        public MenuItem(LinearLayout layout, ImageView icon, TextView text, int selectedIconRes, int unselectedIconRes, Class<? extends Activity> targetActivity) {
            this.layout = layout;
            this.icon = icon;
            this.text = text;
            this.selectedIconRes = selectedIconRes;
            this.unselectedIconRes = unselectedIconRes;
            this.targetActivity = targetActivity;
        }
    }

    public FooterManager(Activity activity) {
        this.activity = activity;

        // Initialize layouts and their components
        LinearLayout layoutHome = activity.findViewById(R.id.layoutHome);
        ImageView imgHome = activity.findViewById(R.id.imgHome);
        TextView txtHome = activity.findViewById(R.id.txtHome);

        LinearLayout layoutCategory = activity.findViewById(R.id.layoutCategory);
        ImageView imgCategoryFooter = activity.findViewById(R.id.imgCategoryFooter);
        TextView txtCategoryFooter = activity.findViewById(R.id.txtCategoryFooter);

        LinearLayout layoutNotification = activity.findViewById(R.id.layoutNotification);
        ImageView imgNotification = activity.findViewById(R.id.imgNotification);
        TextView txtNotification = activity.findViewById(R.id.txtNotification);

        LinearLayout layoutAccount = activity.findViewById(R.id.layoutAccount);
        ImageView imgAccount = activity.findViewById(R.id.imgAccount);
        TextView txtAccount = activity.findViewById(R.id.txtAccount);

        // Add menu items with their target activities
        menuItems.add(new MenuItem(layoutHome, imgHome, txtHome,
                R.mipmap.ic_homepage_red, R.mipmap.ic_homepage_black, HomepageActivity.class));
        menuItems.add(new MenuItem(layoutCategory, imgCategoryFooter, txtCategoryFooter,
                R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black, CategoryActivity.class));
        menuItems.add(new MenuItem(layoutNotification, imgNotification, txtNotification,
                R.mipmap.ic_notification_footer_red, R.mipmap.ic_notification_footer_black, NotificationActivity.class));
        menuItems.add(new MenuItem(layoutAccount, imgAccount, txtAccount,
                R.mipmap.ic_account_footer_red, R.mipmap.ic_account_footer_black, AccountManagementActivity.class));

        // Set click listeners and initialize state
        for (MenuItem item : menuItems) {
            item.layout.setOnClickListener(v -> {
                // Only navigate if not already on the target activity
                if (!activity.getClass().equals(item.targetActivity)) {
                    resetAll();
                    item.icon.setImageResource(item.selectedIconRes);
                    item.text.setTextColor(ContextCompat.getColor(activity, R.color.red));
                    Intent intent = new Intent(activity, item.targetActivity);
                    activity.startActivity(intent);
                    // Optional: Add transition animation
                    activity.overridePendingTransition(0, 0);
                }
            });
        }

        // Highlight the current activity's footer item
        resetAll();
        for (MenuItem item : menuItems) {
            if (activity.getClass().equals(item.targetActivity)) {
                item.icon.setImageResource(item.selectedIconRes);
                item.text.setTextColor(ContextCompat.getColor(activity, R.color.red));
                break;
            }
        }
    }

    private void resetAll() {
        for (MenuItem item : menuItems) {
            item.icon.setImageResource(item.unselectedIconRes);
            item.text.setTextColor(ContextCompat.getColor(activity, R.color.black));
        }
    }
}