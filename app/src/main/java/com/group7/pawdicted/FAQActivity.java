package com.group7.pawdicted;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group7.pawdicted.mobile.adapters.FAQAdapter;
import com.group7.pawdicted.mobile.models.FAQItem;
import com.group7.pawdicted.mobile.models.ListFAQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private FAQAdapter adapter;

    private AppCompatButton tabOrder, tabRefund, tabMember, tabOther;

    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqactivity);

        expandableListView = findViewById(R.id.faq_expandable_list);

        tabOrder = findViewById(R.id.tab_order);
        tabRefund = findViewById(R.id.tab_refund);
        tabMember = findViewById(R.id.tab_member);
        tabOther = findViewById(R.id.tab_other);

        // Gán selector màu chữ cho tất cả các tab
        ColorStateList tabTextColor = ContextCompat.getColorStateList(this, R.color.tab_text_selector);
        tabOrder.setTextColor(tabTextColor);
        tabRefund.setTextColor(tabTextColor);
        tabMember.setTextColor(tabTextColor);
        tabOther.setTextColor(tabTextColor);

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

        // Thiết lập sự kiện tự động collapse group khi mở group khác
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    private void showFAQ(List<FAQItem> faqList) {
        List<String> groupList = new ArrayList<>();
        HashMap<String, List<FAQItem>> childMap = new HashMap<>();
        for (FAQItem item : faqList) {
            groupList.add(item.getQuestion());
            List<FAQItem> child = new ArrayList<>();
            child.add(item);
            childMap.put(item.getQuestion(), child);
        }
        adapter = new FAQAdapter(this, groupList, childMap);
        expandableListView.setAdapter(adapter);
    }

    private void setTabSelected(AppCompatButton selectedTab) {
        tabOrder.setSelected(false);
        tabRefund.setSelected(false);
        tabMember.setSelected(false);
        tabOther.setSelected(false);

        selectedTab.setSelected(true);

        tabOrder.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabRefund.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabMember.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabOther.setBackgroundResource(R.drawable.bg_tab_unselected);

        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
    }
}