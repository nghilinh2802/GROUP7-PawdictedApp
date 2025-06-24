package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        // Khởi tạo views với đúng ID từ layout
        ImageButton btnBack = findViewById(R.id.btn_back);
        ListView listView = findViewById(R.id.lvBlog);
        progressBar = findViewById(R.id.progressBar);

        // Xử lý nút back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Khởi tạo adapter
        adapter = new BlogAdapter(this, blogs);
        listView.setAdapter(adapter);

        // Hiển thị ProgressBar khi bắt đầu load dữ liệu
        showProgressBar();

        // Lấy dữ liệu từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("blogs")
                .get()
                .addOnCompleteListener(task -> {
                    // Sử dụng Handler để ẩn ProgressBar và cập nhật UI
                    handler.postDelayed(() -> {
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            blogs.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Blog blog = document.toObject(Blog.class);
                                blogs.add(blog);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // Xử lý lỗi nếu cần
                        }
                    }, 1000); // Delay 1 giây để thấy được progress
                });

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

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
