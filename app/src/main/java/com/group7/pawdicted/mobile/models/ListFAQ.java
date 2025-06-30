package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.List;

public class ListFAQ {

    public static List<FAQItem> getOrderFAQ() {
        List<FAQItem> orderList = new ArrayList<>();

        orderList.add(new FAQItem(
                "Làm thế nào để đặt hàng?",
                "Bạn có thể đặt hàng bằng cách chọn sản phẩm và nhấn vào nút \"Đặt hàng\" trên màn hình sản phẩm.",
                "How to place an order?",
                "You can place an order by selecting a product and clicking the \"Order\" button on the product screen."
        ));

        orderList.add(new FAQItem(
                "Tôi có thể thay đổi đơn hàng sau khi đặt không?",
                "Bạn chỉ có thể thay đổi đơn hàng trước khi đơn được xác nhận. Sau khi xác nhận, vui lòng liên hệ bộ phận hỗ trợ.",
                "Can I change my order after placing it?",
                "You can only change your order before it is confirmed. After confirmation, please contact customer support."
        ));

        orderList.add(new FAQItem(
                "Thời gian giao hàng là bao lâu?",
                "Thời gian giao hàng thường từ 2-5 ngày làm việc tùy vào khu vực của bạn.",
                "How long does delivery take?",
                "Delivery usually takes 2-5 business days depending on your area."
        ));

        return orderList;
    }

    public static List<FAQItem> getRefundFAQ() {
        List<FAQItem> refundList = new ArrayList<>();

        refundList.add(new FAQItem(
                "Làm thế nào để yêu cầu hoàn tiền?",
                "Bạn có thể yêu cầu hoàn tiền trong phần \"Đơn hàng của tôi\" hoặc liên hệ bộ phận hỗ trợ.",
                "How to request a refund?",
                "You can request a refund in the \"My Orders\" section or contact customer support."
        ));

        refundList.add(new FAQItem(
                "Thời gian xử lý hoàn tiền là bao lâu?",
                "Thời gian xử lý hoàn tiền thường từ 3-7 ngày làm việc sau khi yêu cầu được duyệt.",
                "How long does refund processing take?",
                "Refund processing usually takes 3-7 business days after the request is approved."
        ));

        refundList.add(new FAQItem(
                "Tôi có thể hoàn tiền nếu không hài lòng với sản phẩm?",
                "Có, bạn có thể yêu cầu hoàn tiền nếu sản phẩm không đúng mô tả hoặc bị lỗi.",
                "Can I get a refund if I am not satisfied with the product?",
                "Yes, you can request a refund if the product is not as described or defective."
        ));

        return refundList;
    }

    public static List<FAQItem> getMemberFAQ() {
        List<FAQItem> memberList = new ArrayList<>();

        memberList.add(new FAQItem(
                "Làm thế nào để đăng ký thành viên?",
                "Bạn có thể đăng ký thành viên bằng cách nhấn vào nút \"Đăng ký\" trên màn hình đăng nhập.",
                "How to register as a member?",
                "You can register by clicking the \"Register\" button on the login screen."
        ));

        memberList.add(new FAQItem(
                "Thành viên có những quyền lợi gì?",
                "Thành viên sẽ nhận được ưu đãi, tích điểm và các chương trình khuyến mãi đặc biệt.",
                "What are the benefits of being a member?",
                "Members receive discounts, earn points, and enjoy special promotions."
        ));

        memberList.add(new FAQItem(
                "Làm thế nào để khôi phục mật khẩu?",
                "Bạn có thể khôi phục mật khẩu bằng cách nhấn vào \"Quên mật khẩu\" trên màn hình đăng nhập.",
                "How to recover my password?",
                "You can recover your password by clicking \"Forgot password\" on the login screen."
        ));

        return memberList;
    }

    public static List<FAQItem> getOtherFAQ() {
        List<FAQItem> othersList = new ArrayList<>();

        othersList.add(new FAQItem(
                "Làm thế nào để liên hệ hỗ trợ?",
                "Bạn có thể liên hệ hỗ trợ qua số điện thoại hoặc email được cung cấp trong phần \"Liên hệ\" của ứng dụng.",
                "How to contact support?",
                "You can contact support via phone or email provided in the \"Contact\" section of the app."
        ));

        othersList.add(new FAQItem(
                "Ứng dụng có hỗ trợ thanh toán quốc tế không?",
                "Hiện tại ứng dụng chỉ hỗ trợ thanh toán trong nước.",
                "Does the app support international payments?",
                "Currently, the app only supports domestic payments."
        ));

        othersList.add(new FAQItem(
                "Tôi có thể sử dụng ứng dụng trên nhiều thiết bị không?",
                "Có, bạn có thể đăng nhập và sử dụng ứng dụng trên nhiều thiết bị với cùng một tài khoản.",
                "Can I use the app on multiple devices?",
                "Yes, you can log in and use the app on multiple devices with the same account."
        ));

        return othersList;
    }
}
