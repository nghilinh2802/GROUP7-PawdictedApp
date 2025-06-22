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
import java.util.List;

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
        txtBlogAuthorTime.setText(blog.getAuthor() + " • " + blog.getCreateAt());

        if (blog.getImages() != null && !blog.getImages().isEmpty()) {
            Glide.with(context)
                    .load(blog.getImages().get(0))
                    .into(imgBlog);
        }

        return convertView;
    }
}
