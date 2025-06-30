package com.group7.pawdicted;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.group7.pawdicted.mobile.adapters.FAQAdapter;
import com.group7.pawdicted.mobile.models.FAQItem;
import com.group7.pawdicted.mobile.models.ListFAQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FAQActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private FAQAdapter adapter;
    private AppCompatButton tabOrder, tabRefund, tabMember, tabOther;
    private int lastExpandedPosition = -1;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy ngôn ngữ hệ thống (en hoặc vi)
        lang = Locale.getDefault().getLanguage();

        setContentView(R.layout.activity_faqactivity);

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        expandableListView = findViewById(R.id.faq_expandable_list);
        tabOrder = findViewById(R.id.tab_order);
        tabRefund = findViewById(R.id.tab_refund);
        tabMember = findViewById(R.id.tab_member);
        tabOther = findViewById(R.id.tab_other);

        // Gán selector màu chữ cho tất cả các tab
        tabOrder.setTextColor(ContextCompat.getColorStateList(this, R.color.tab_text_selector));
        tabRefund.setTextColor(ContextCompat.getColorStateList(this, R.color.tab_text_selector));
        tabMember.setTextColor(ContextCompat.getColorStateList(this, R.color.tab_text_selector));
        tabOther.setTextColor(ContextCompat.getColorStateList(this, R.color.tab_text_selector));

        // Mặc định hiển thị Order
        showFAQ(ListFAQ.getOrderFAQ());
        setTabSelected(tabOrder);

        tabOrder.setOnClickListener(v -> {
            showFAQ(ListFAQ.getOrderFAQ());
            setTabSelected(tabOrder);
        });

        tabRefund.setOnClickListener(v -> {
            showFAQ(ListFAQ.getRefundFAQ());
            setTabSelected(tabRefund);
        });

        tabMember.setOnClickListener(v -> {
            showFAQ(ListFAQ.getMemberFAQ());
            setTabSelected(tabMember);
        });

        tabOther.setOnClickListener(v -> {
            showFAQ(ListFAQ.getOtherFAQ());
            setTabSelected(tabOther);
        });

        // Tự động collapse group khi mở group khác
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                expandableListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition;
        });
    }

    private void showFAQ(List<FAQItem> faqList) {
        List<String> groupList = new ArrayList<>();
        HashMap<String, List<FAQItem>> childMap = new HashMap<>();

        for (FAQItem item : faqList) {
            groupList.add(item.getQuestion(lang));
            List<FAQItem> child = new ArrayList<>();
            child.add(item);
            childMap.put(item.getQuestion(lang), child);
        }

        adapter = new FAQAdapter(this, groupList, childMap, lang);
        expandableListView.setAdapter(adapter);
    }

    private void setTabSelected(AppCompatButton selectedTab) {
        tabOrder.setSelected(false);
        tabRefund.setSelected(false);
        tabMember.setSelected(false);
        tabOther.setSelected(false);

        tabOrder.setBackgroundResource(R.drawable.red_large_rounded_background);
        tabRefund.setBackgroundResource(R.drawable.red_large_rounded_background);
        tabMember.setBackgroundResource(R.drawable.red_large_rounded_background);
        tabOther.setBackgroundResource(R.drawable.red_large_rounded_background);

        selectedTab.setSelected(true);
        selectedTab.setBackgroundResource(R.drawable.red_fill_rounded_background);
    }
}
