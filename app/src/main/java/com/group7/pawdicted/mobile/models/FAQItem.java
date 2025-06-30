package com.group7.pawdicted.mobile.models;

public class FAQItem {

    private String questionVi;
    private String answerVi;
    private String questionEn;
    private String answerEn;

    public FAQItem(String questionVi, String answerVi, String questionEn, String answerEn) {
        this.questionVi = questionVi;
        this.answerVi = answerVi;
        this.questionEn = questionEn;
        this.answerEn = answerEn;
    }

    // Lấy câu hỏi theo ngôn ngữ ("en" hoặc "vi")
    public String getQuestion(String lang) {
        if ("en".equals(lang)) {
            return questionEn;
        }
        return questionVi;
    }

    // Lấy câu trả lời theo ngôn ngữ ("en" hoặc "vi")
    public String getAnswer(String lang) {
        if ("en".equals(lang)) {
            return answerEn;
        }
        return answerVi;
    }
}
