package com.group7.pawdicted;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BlogDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        // Ánh xạ view đúng ID layout bạn gửi
        TextView txtTitle = findViewById(R.id.txtBlogDetailsTitle);
        TextView txtAuthorTime = findViewById(R.id.txtBlogDetailsAuthorTime);
        ImageView imgBlog = findViewById(R.id.imgBlogDetails);
        TextView txtDescription = findViewById(R.id.txtBlogDetailsDescription);
        TextView txtContent = findViewById(R.id.txtBlogDetailsContent);

        // Nhận dữ liệu từ Intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String content = getIntent().getStringExtra("content");
        String author = getIntent().getStringExtra("author");
        String imageURL = getIntent().getStringExtra("imageURL");
        String createAt = getIntent().getStringExtra("createAt");

        // Hiển thị tiêu đề, mô tả, nội dung
        txtTitle.setText(title);
        txtDescription.setText(description);
        txtContent.setText(android.text.Html.fromHtml(content, android.text.Html.FROM_HTML_MODE_LEGACY));

        // Hiển thị ảnh
        if (imageURL != null && !imageURL.isEmpty()) {
            Glide.with(this).load(imageURL).into(imgBlog);
        } else {
            imgBlog.setImageResource(R.mipmap.banner); // Ảnh mặc định nếu không có
        }

        // Định dạng ngày giờ
        String formattedDate = "";
        if (createAt != null && !createAt.isEmpty()) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(createAt);

                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                displayFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                formattedDate = displayFormat.format(date);
            } catch (ParseException e) {
                formattedDate = createAt;
            }
        } else {
            formattedDate = "Không rõ thời gian";
        }

        txtAuthorTime.setText(author + " • " + formattedDate);
    }
}
