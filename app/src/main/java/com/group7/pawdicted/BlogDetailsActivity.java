package com.group7.pawdicted;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BlogDetailsActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private LinearLayout contentContainer;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        // Khởi tạo views
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView txtTitle = findViewById(R.id.txtBlogDetailsTitle);
        TextView txtAuthorTime = findViewById(R.id.txtBlogDetailsAuthorTime);
        ImageView imgBlog = findViewById(R.id.imgBlogDetails);
        TextView txtDescription = findViewById(R.id.txtBlogDetailsDescription);
        TextView txtContent = findViewById(R.id.txtBlogDetailsContent);
        progressBar = findViewById(R.id.progressBarDetails);
        contentContainer = findViewById(R.id.contentContainer);

        // Xử lý nút back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Hiển thị ProgressBar khi bắt đầu load
        showProgressBar();

        // Nhận dữ liệu từ Intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String content = getIntent().getStringExtra("content");
        String author = getIntent().getStringExtra("author");
        String imageURL = getIntent().getStringExtra("imageURL");
        String createdAt = getIntent().getStringExtra("createdAt");

        // Load content với progress
        loadContentWithProgress(txtTitle, txtDescription, txtContent, txtAuthorTime,
                imgBlog, title, description, content, author, imageURL, createdAt);
    }

    private void loadContentWithProgress(TextView txtTitle, TextView txtDescription,
                                         TextView txtContent, TextView txtAuthorTime,
                                         ImageView imgBlog, String title, String description,
                                         String content, String author, String imageURL, String createdAt) {

        new Thread(() -> {
            try {
                // Simulate loading với progress steps
                for (int i = 0; i <= 100; i += 10) {
                    progressStatus = i;

                    handler.post(() -> {
                        if (progressBar != null) {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    Thread.sleep(100); // Delay để thấy được progress
                }

                // Khi hoàn thành, cập nhật UI
                handler.post(() -> {
                    hideProgressBar();
                    showContent();

                    // Hiển thị nội dung
                    txtTitle.setText(title != null ? title : "Không có tiêu đề");
                    txtDescription.setText(description != null ? description : "Không có mô tả");

                    if (content != null) {
                        txtContent.setText(android.text.Html.fromHtml(content,
                                android.text.Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        txtContent.setText("Không có nội dung");
                    }

                    // Hiển thị ảnh
                    if (imageURL != null && !imageURL.isEmpty()) {
                        Glide.with(this).load(imageURL).into(imgBlog);
                    } else {
                        imgBlog.setImageResource(R.mipmap.banner);
                    }

                    // Định dạng ngày giờ
                    String formattedDate = formatDate(createdAt);
                    String authorText = (author != null ? author : "Không rõ tác giả") + " • " + formattedDate;
                    txtAuthorTime.setText(authorText);
                });

            } catch (InterruptedException e) {
                handler.post(() -> {
                    hideProgressBar();
                    showContent();
                });
            }
        }).start();
    }

    private String formatDate(String createdAt) {
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        Locale.getDefault());
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(createdAt);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                        Locale.getDefault());
                displayFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                return displayFormat.format(date);
            } catch (ParseException e) {
                return createdAt;
            }
        }
        return "Không rõ thời gian";
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        if (contentContainer != null) {
            contentContainer.setVisibility(View.GONE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showContent() {
        if (contentContainer != null) {
            contentContainer.setVisibility(View.VISIBLE);
        }
    }
}
