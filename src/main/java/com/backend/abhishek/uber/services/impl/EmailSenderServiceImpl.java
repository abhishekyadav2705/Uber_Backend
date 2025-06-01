package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.services.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        sendEmail(new String[]{toEmail}, subject, body);
    }

    @Override
    public void sendEmail(String[] toEmails, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setBcc(toEmails); // Use BCC for multiple
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("✅ Email sent successfully to: {}", Arrays.toString(toEmails));
        } catch (MailException e) {
            log.error("❌ Failed to send email to: {}. Reason: {}", Arrays.toString(toEmails), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendDebitHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true for HTML

            javaMailSender.send(message);
            log.info("📨 HTML Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("❌ Failed to send HTML email to: {}. Reason: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendCreditHtmlEmail(String driverEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(driverEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true for HTML

            javaMailSender.send(message);
            log.info("HTML Email sent successfully to driver: {}", driverEmail);
        } catch (MessagingException e) {
            log.error("❌ Failed to send HTML email to: {}. Reason: {}", driverEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendOtpEmail(String riderEmailForOtp, String html) {
        try {
            String subject = "OTP for Uber Ride";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setBcc(riderEmailForOtp);
            helper.setSubject(subject);
            helper.setText(html, true); // true for HTML

            javaMailSender.send(message);
            log.info("OTP Email sent successfully to: {}", riderEmailForOtp);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}. Reason: {}", riderEmailForOtp, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

}

