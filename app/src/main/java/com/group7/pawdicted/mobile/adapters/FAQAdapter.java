package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.pawdicted.R;

import java.util.HashMap;
import java.util.List;

import com.group7.pawdicted.mobile.models.FAQItem;

public class FAQAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<FAQItem>> listDataChild;
    public FAQAdapter(Context context, List<String> listDataHeader, HashMap<String, List<FAQItem>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_group_item, parent, false);
        }

        FAQItem faqItem = (FAQItem) getChild(groupPosition, 0); // Lấy item đầu tiên để hiển thị câu hỏi
        TextView tvQuestion = convertView.findViewById(R.id.tv_question);
        ImageView imgExpand = convertView.findViewById(R.id.img_expand);

        tvQuestion.setText(faqItem.getQuestion());
        imgExpand.setImageResource(isExpanded ? R.mipmap.ic_collapse : R.mipmap.ic_expand);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_child_item, parent, false);
        }

        FAQItem faqItem = (FAQItem) getChild(groupPosition, childPosition);
        TextView tvAnswer = convertView.findViewById(R.id.tv_answer);
        tvAnswer.setText(faqItem.getAnswer());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
