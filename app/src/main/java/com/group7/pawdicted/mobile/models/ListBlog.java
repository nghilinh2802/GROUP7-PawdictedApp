package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.List;

public class ListBlog {
    public static List<Blog> getFakeBlogs() {
    List<Blog> blogs = new ArrayList<>();

    List<String> images1 = new ArrayList<>();
    images1.add("https://mtv.vn/uploads/2023/02/25/meo-dd.jpg");
    images1.add("https://toigingiuvedep.vn/wp-content/uploads/2021/04/hinh-anh-nen-con-meo-cute.jpg");

    List<String> images2 = new ArrayList<>();
    images2.add("https://toigingiuvedep.vn/wp-content/uploads/2021/04/hinh-anh-nen-con-meo-cute.jpg");

    List<String> images3 = new ArrayList<>();
    images3.add("https://example.com/pet4.jpg");
    images3.add("https://example.com/pet5.jpg");

    blogs.add(new Blog(
            1,
            "Cách chăm sóc thú cưng mùa hè",
            "Những lưu ý khi chăm sóc thú cưng vào mùa hè nóng bức",
            "Mùa hè là thời điểm chúng ta cần chú ý đến sức khỏe của thú cưng nhiều hơn. Hãy luôn đảm bảo thú cưng có đủ nước sạch và chỗ nghỉ mát. Tránh cho thú cưng ra ngoài vào giờ nắng gắt, đặc biệt là chó và mèo.",
            "Nguyễn Văn A",
            images1,
            "2025-06-10T08:30:00",
            "2025-06-12T09:15:00"
    ));

    blogs.add(new Blog(
            2,
            "Chọn đồ dùng thú cưng phù hợp",
            "Hướng dẫn chọn đồ dùng phù hợp cho thú cưng theo từng độ tuổi",
            "Khi chọn đồ dùng cho thú cưng, bạn cần chú ý đến độ tuổi, giống loài và sở thích của chúng. Đồ chơi, thức ăn, phụ kiện cần phù hợp với kích thước và tính cách của thú cưng để đảm bảo an toàn và thoải mái.",
            "Trần Thị B",
            images2,
            "2025-06-08T14:20:00",
            "2025-06-09T10:00:00"
    ));

    blogs.add(new Blog(
            3,
            "Dinh dưỡng cho mèo con",
            "Những lưu ý về dinh dưỡng cho mèo con dưới 6 tháng tuổi",
            "Mèo con dưới 6 tháng tuổi cần được chăm sóc đặc biệt về dinh dưỡng. Bạn nên chọn thức ăn chuyên dụng cho mèo con, chia nhỏ bữa ăn và đảm bảo đủ nước sạch. Tránh cho mèo con ăn thức ăn của người hoặc thức ăn không phù hợp.",
            "Lê Văn C",
            images3,
            "2025-06-05T09:00:00",
            "2025-06-07T11:30:00"
    ));

    return blogs;
}
}