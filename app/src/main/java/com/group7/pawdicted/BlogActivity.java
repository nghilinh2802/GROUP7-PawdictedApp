package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.BlogAdapter;
import com.group7.pawdicted.mobile.models.Blog;

import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends AppCompatActivity {
    private List<Blog> blogs = new ArrayList<>();
    private BlogAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog); // XML của bạn

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        ListView listView = findViewById(R.id.lvBlog);
        adapter = new BlogAdapter(this, blogs);
        listView.setAdapter(adapter);

        // Lấy dữ liệu từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("blogs") // Tên collection trong Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        blogs.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Blog blog = document.toObject(Blog.class);
                            blogs.add(blog);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Xử lý lỗi nếu cần
                    }});

        // Xử lý click vào item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Blog blog = blogs.get(position);
            Intent intent = new Intent(this, BlogDetailsActivity.class);
            intent.putExtra("title", blog.getTitle());
            intent.putExtra("description", blog.getDescription());
            intent.putExtra("content", blog.getContent());
            intent.putExtra("author", blog.getAuthor());
            intent.putExtra("imageURL", blog.getImageURL());
            intent.putExtra("createdAt", blog.getCreatedAt());
            intent.putExtra("updatedAt", blog.getUpdatedAt());
            startActivity(intent);
        });
    }
}
