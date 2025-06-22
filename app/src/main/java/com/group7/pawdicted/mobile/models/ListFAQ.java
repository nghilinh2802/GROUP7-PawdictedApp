package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListFAQ {
    public static List<FAQItem> getOrderFAQ() {
        List<FAQItem> orderList = new ArrayList<>();
        orderList.add(new FAQItem("Làm thế nào để đặt hàng?", "Bạn có thể đặt hàng bằng cách chọn sản phẩm và nhấn vào nút \"Đặt hàng\" trên màn hình sản phẩm."));
        orderList.add(new FAQItem("Tôi có thể thay đổi đơn hàng sau khi đặt không?", "Bạn chỉ có thể thay đổi đơn hàng trước khi đơn được xác nhận. Sau khi xác nhận, vui lòng liên hệ bộ phận hỗ trợ."));
        orderList.add(new FAQItem("Thời gian giao hàng là bao lâu?", "Thời gian giao hàng thường từ 2-5 ngày làm việc tùy vào khu vực của bạn."));
        return orderList;
    }
    public static List<FAQItem> getRefundFAQ() {
        List<FAQItem> refundList = new ArrayList<>();
        refundList.add(new FAQItem("Làm thế nào để yêu cầu hoàn tiền?", "Bạn có thể yêu cầu hoàn tiền trong phần \"Đơn hàng của tôi\" hoặc liên hệ bộ phận hỗ trợ."));
        refundList.add(new FAQItem("Thời gian xử lý hoàn tiền là bao lâu?", "Thời gian xử lý hoàn tiền thường từ 3-7 ngày làm việc sau khi yêu cầu được duyệt."));
        refundList.add(new FAQItem("Tôi có thể hoàn tiền nếu không hài lòng với sản phẩm?", "Có, bạn có thể yêu cầu hoàn tiền nếu sản phẩm không đúng mô tả hoặc bị lỗi."));
        return refundList;
    }
    public static List<FAQItem> getMemberFAQ() {
        List<FAQItem> memberList = new ArrayList<>();
        memberList.add(new FAQItem("Làm thế nào để đăng ký thành viên?", "Bạn có thể đăng ký thành viên bằng cách nhấn vào nút \"Đăng ký\" trên màn hình đăng nhập."));
        memberList.add(new FAQItem("Thành viên có những quyền lợi gì?", "Thành viên sẽ nhận được ưu đãi, tích điểm và các chương trình khuyến mãi đặc biệt."));
        memberList.add(new FAQItem("Làm thế nào để khôi phục mật khẩu?", "Bạn có thể khôi phục mật khẩu bằng cách nhấn vào \"Quên mật khẩu\" trên màn hình đăng nhập."));
        return memberList;
    }
    public static List<FAQItem> getOtherFAQ() {
        List<FAQItem> othersList = new ArrayList<>();
        othersList.add(new FAQItem("Làm thế nào để liên hệ hỗ trợ?", "Bạn có thể liên hệ hỗ trợ qua số điện thoại hoặc email được cung cấp trong phần \"Liên hệ\" của ứng dụng."));
        othersList.add(new FAQItem("Ứng dụng có hỗ trợ thanh toán quốc tế không?", "Hiện tại ứng dụng chỉ hỗ trợ thanh toán trong nước."));
        othersList.add(new FAQItem("Tôi có thể sử dụng ứng dụng trên nhiều thiết bị không?", "Có, bạn có thể đăng nhập và sử dụng ứng dụng trên nhiều thiết bị với cùng một tài khoản."));
        return othersList;
    }
}

