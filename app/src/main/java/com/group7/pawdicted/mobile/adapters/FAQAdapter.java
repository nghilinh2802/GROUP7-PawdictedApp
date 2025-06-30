package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.FAQItem;

import java.util.HashMap;
import java.util.List;

public class FAQAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<FAQItem>> listDataChild;
    private String lang;

    public FAQAdapter(Context context, List<String> listDataHeader, HashMap<String, List<FAQItem>> listDataChild, String lang) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.lang = lang;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // Không cần child riêng vì đã gộp câu hỏi và trả lời trong group view
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Gộp câu hỏi và câu trả lời vào group view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FAQItem faqItem = listDataChild.get(listDataHeader.get(groupPosition)).get(0); // mỗi nhóm chỉ có 1 item

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_faq, parent, false);
        }

        TextView tvQuestion = convertView.findViewById(R.id.tv_question);
        TextView tvAnswer = convertView.findViewById(R.id.tv_answer);
        ImageView imgExpand = convertView.findViewById(R.id.img_expand);

        tvQuestion.setText(faqItem.getQuestion(lang));
        tvAnswer.setText(faqItem.getAnswer(lang));
        imgExpand.setImageResource(isExpanded ? R.mipmap.ic_collapse : R.mipmap.ic_expand);

        // Hiển thị hoặc ẩn câu trả lời dựa vào trạng thái expand
        tvAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null; // Không dùng child view riêng
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
