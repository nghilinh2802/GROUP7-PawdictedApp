package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.group7.pawdicted.mobile.adapters.BlogAdapter;
import com.group7.pawdicted.mobile.models.Blog;
import com.group7.pawdicted.mobile.models.ListBlog;
import java.util.List;

public class BlogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog); // XML của bạn

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        ListView listView = findViewById(R.id.lvBlog);

        // Lấy danh sách blog
        List<Blog> blogs = ListBlog.getFakeBlogs(); // Hoặc lấy từ nguồn khác nếu bạn không dùng fake data

        // Tạo adapter và gắn vào ListView
        BlogAdapter adapter = new BlogAdapter(this, blogs);
        listView.setAdapter(adapter);

        // Xử lý click vào item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Blog blog = blogs.get(position);
            Intent intent = getIntent();
            intent.putExtra("title", blog.getTitle());
            intent.putExtra("description", blog.getDescription());
            intent.putExtra("content", blog.getContent());
            intent.putExtra("author", blog.getAuthor());
            intent.putStringArrayListExtra("images", new java.util.ArrayList<>(blog.getImages()));
            intent.putExtra("createAt", blog.getCreateAt());
            intent.putExtra("updateAt", blog.getUpdateAt());
            startActivity(intent);
        });
    }
}
