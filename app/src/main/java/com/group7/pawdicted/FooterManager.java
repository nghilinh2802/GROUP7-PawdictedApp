package com.group7.pawdicted;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class FooterManager {
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final Activity activity;

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

    public FooterManager(Activity activity) {
        this.activity = activity;

        ImageView imgHome = activity.findViewById(R.id.imgHome);
        TextView txtHome = activity.findViewById(R.id.txtHome);
        ImageView imgCategoryFooter = activity.findViewById(R.id.imgCategoryFooter);
        TextView txtCategoryFooter = activity.findViewById(R.id.txtCategoryFooter);
        ImageView imgNotification = activity.findViewById(R.id.imgNotification);
        TextView txtNotification = activity.findViewById(R.id.txtNotification);
        ImageView imgAccount = activity.findViewById(R.id.imgAccount);
        TextView txtAccount = activity.findViewById(R.id.txtAccount);

        menuItems.add(new MenuItem(imgHome, txtHome, R.mipmap.ic_homepage_red, R.mipmap.ic_homepage_black));
        menuItems.add(new MenuItem(imgCategoryFooter, txtCategoryFooter, R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black));
        menuItems.add(new MenuItem(imgNotification, txtNotification, R.mipmap.ic_notification_footer_red, R.mipmap.ic_notification_footer_black));
        menuItems.add(new MenuItem(imgAccount, txtAccount, R.mipmap.ic_account_footer_red, R.mipmap.ic_account_footer_black));

        for (MenuItem item : menuItems) {
            View.OnClickListener listener = v -> {
                resetAll();
                item.icon.setImageResource(item.selectedIconRes);
                item.text.setTextColor(ContextCompat.getColor(activity, R.color.red));

                if (item.text.getId() == R.id.txtCategoryFooter || item.icon.getId() == R.id.imgCategoryFooter) {
                    Intent intent = new Intent(activity, CategoryActivity.class);
                    activity.startActivity(intent);
                }

                else if (item.text.getId() == R.id.txtAccount || item.icon.getId() == R.id.imgAccount) {
                    Intent intent = new Intent(activity, AccountManagementActivity.class);
                    activity.startActivity(intent);
                }
            };
            item.icon.setOnClickListener(listener);
            item.text.setOnClickListener(listener);
        }
        resetAll();

        if (activity instanceof CategoryActivity) {
            imgCategoryFooter.setImageResource(R.mipmap.ic_all_product_red);
            txtCategoryFooter.setTextColor(ContextCompat.getColor(activity, R.color.red));
        } else if (activity instanceof AccountManagementActivity) {
            imgAccount.setImageResource(R.mipmap.ic_account_footer_red);
            txtAccount.setTextColor(ContextCompat.getColor(activity, R.color.red));
        } else if  (activity instanceof HomepageActivity){
            imgHome.setImageResource(R.mipmap.ic_homepage_red);
            txtHome.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }
        else {
            imgNotification.setImageResource(R.mipmap.ic_notification_footer_red);
            txtNotification.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }
    }

    private void resetAll() {
        for (MenuItem item : menuItems) {
            item.icon.setImageResource(item.unselectedIconRes);
            item.text.setTextColor(ContextCompat.getColor(activity, R.color.black));
        }
    }
}
