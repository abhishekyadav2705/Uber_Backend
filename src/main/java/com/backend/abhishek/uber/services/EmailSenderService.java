package com.backend.abhishek.uber.services;

public interface EmailSenderService {

    public void sendEmail(String toEmail, String subject, String body);

    public void sendEmail(String[] toEmail, String subject, String body);

    void sendDebitHtmlEmail(String toEmail, String subject, String htmlContent);

    void sendCreditHtmlEmail(String driverEmail, String subject, String html);

    void sendOtpEmail(String riderEmailForOtp, String html);

    void sendSignUpOtpEmail(String riderEmailForOtp, String html);

}
