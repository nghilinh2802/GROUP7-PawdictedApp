package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.Blog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BlogAdapter extends BaseAdapter {

    private Context context;
    private List<Blog> blogs;

    public BlogAdapter(Context context, List<Blog> blogs) {
        this.context = context;
        this.blogs = blogs;
    }

    @Override
    public int getCount() {
        return blogs.size();
    }

    @Override
    public Object getItem(int position) {
        return blogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false);
        }

        Blog blog = blogs.get(position);

        ImageView imgBlog = convertView.findViewById(R.id.img_blog);
        TextView txtBlogTitle = convertView.findViewById(R.id.txtBlogTitle);
        TextView txtBlogDescription = convertView.findViewById(R.id.txtBlogDescription);
        TextView txtBlogAuthorTime = convertView.findViewById(R.id.txtBlogAuthorTime);

        txtBlogTitle.setText(blog.getTitle());
        txtBlogDescription.setText(blog.getDescription());
        String isoString = blog.getCreatedAt();
        String formattedDate;

        if (isoString != null && !isoString.isEmpty()) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(isoString);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                formattedDate = displayFormat.format(date);
            } catch (ParseException e) {
                // Nếu lỗi định dạng, vẫn hiển thị chuỗi gốc thay vì "Không rõ thời gian"
                formattedDate = isoString;
            }
        } else {
            formattedDate = "Không rõ thời gian";
        }

        txtBlogAuthorTime.setText(blog.getAuthor() + " • " + formattedDate);

        if (blog.getImageURL() != null && !blog.getImageURL().isEmpty()) {
            Glide.with(context)
                    .load(blog.getImageURL())
                    .into(imgBlog);
        } else {
            imgBlog.setImageResource(R.mipmap.cat_toy); // nếu muốn ảnh mặc định
        }


        return convertView;
    }
}
