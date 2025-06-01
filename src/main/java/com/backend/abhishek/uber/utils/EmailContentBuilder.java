package com.backend.abhishek.uber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailContentBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String buildCreditEmail(double amount, String method, LocalDateTime timestamp, String transactionId) {
        String formattedTime = timestamp.format(FORMATTER);
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<title>Credit Transaction</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333; }" +
                ".container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px; }" +
                "h1 { color: #2a9d8f; }" +
                "table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
                "th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }" +
                "th { background-color: #2a9d8f; color: #fff; }" +
                ".footer { text-align: center; font-size: 14px; color: #999; margin-top: 40px; }" +
                "</style>" +
                "</head><body>" +
                "<div class=\"container\">" +
                "<h1>Funds Credited to Your Wallet</h1>" +
                "<p>Hello,</p>" +
                "<p>We’re happy to inform you that funds have been <strong>successfully credited</strong> to your wallet.</p>" +
                "<table>" +
                "<tr><th>Amount</th><td>₹" + String.format("%.2f", amount) + "</td></tr>" +
                "<tr><th>Transaction Type</th><td>Credit</td></tr>" +
                "<tr><th>Method</th><td>" + escapeHtml(method) + "</td></tr>" +
                "<tr><th>Timestamp</th><td>" + formattedTime + "</td></tr>" +
                "<tr><th>Transaction ID</th><td>" + escapeHtml(transactionId) + "</td></tr>" +
                "</table>" +
                "<p>Thank you for using our service!</p>" +
                "<div class=\"footer\">&copy; 2025 Abhishek-Uber Inc. This is an automated message.</div>" +
                "</div></body></html>";
    }

    public static String buildDebitEmail(double amount, String method, LocalDateTime timestamp, String transactionId) {
        String formattedTime = timestamp.format(FORMATTER);
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<title>Debit Transaction</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333; }" +
                ".container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px; }" +
                "h1 { color: #e76f51; }" +
                "table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
                "th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }" +
                "th { background-color: #e76f51; color: #fff; }" +
                ".footer { text-align: center; font-size: 14px; color: #999; margin-top: 40px; }" +
                "</style>" +
                "</head><body>" +
                "<div class=\"container\">" +
                "<h1>Debit Transaction Processed</h1>" +
                "<p>Hello,</p>" +
                "<p>A recent transaction has <strong>debited funds</strong> from your wallet. Please review the details below:</p>" +
                "<table>" +
                "<tr><th>Amount</th><td>₹" + String.format("%.2f", amount) + "</td></tr>" +
                "<tr><th>Transaction Type</th><td>Debit</td></tr>" +
                "<tr><th>Method</th><td>" + escapeHtml(method) + "</td></tr>" +
                "<tr><th>Timestamp</th><td>" + formattedTime + "</td></tr>" +
                "<tr><th>Transaction ID</th><td>" + escapeHtml(transactionId) + "</td></tr>" +
                "</table>" +
                "<p>If you did not authorize this transaction, contact our support team immediately.</p>" +
                "<div class=\"footer\">&copy; 2025 Abhishek-Uber Inc. This is an automated message.</div>" +
                "</div></body></html>";
    }

    public static String buildOtpEmail(String otp, String purpose, LocalDateTime timestamp) {
        String formattedTime = timestamp.format(FORMATTER);

        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>OTP Verification</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; color: #333; }" +
                ".container { max-width: 600px; margin: 40px auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "h1 { color: #264653; }" +
                "p { font-size: 16px; line-height: 1.5; }" +
                ".otp-box { font-size: 28px; font-weight: bold; background: #e9f5f2; color: #2a9d8f; padding: 15px; border-radius: 6px; text-align: center; letter-spacing: 3px; margin: 20px 0; }" +
                ".footer { font-size: 14px; color: #888; margin-top: 40px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<h1>Your OTP Code</h1>" +
                "<p>Dear User,</p>" +
                "<p>You have requested an OTP for <strong>" + escapeHtml(purpose) + "</strong>.</p>" +
                "<div class=\"otp-box\">" + escapeHtml(otp) + "</div>" +
                "<p>This OTP is valid for a limited time. Please do not share it with anyone.</p>" +
                "<p>Generated at: <strong>" + formattedTime + "</strong></p>" +
                "<p>If you did not request this, please ignore this email or contact support.</p>" +
                "<div class=\"footer\">&copy; 2025 Abhishek-Uber Inc. This is an automated message. Do not reply.</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }


    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
