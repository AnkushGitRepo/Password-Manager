package com.passwordManager;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class EmailVerification {
    static Scanner scanner = new Scanner(System.in);

    public static boolean verifyMail(String email) {
        String verificationCode = generateVerificationCode();
        System.out.println("Verification code (for testing): " + verificationCode);
        boolean result = sendVerificationEmail(email, verificationCode);

        if (result) {
            System.out.println("Verification code sent successfully.");
            System.out.print("Enter the verification code sent to your email: ");
            String userCode = scanner.nextLine();
            if (verifyCode(userCode, verificationCode)) {
                System.out.println("Verification successful.");
                return true;
            } else {
                System.out.println("Verification failed. Incorrect code.");
                return false;
            }
        } else {
            System.out.println("Failed to send verification code.");
            return false;
        }
    }

    private static String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Generate a 4-digit number
        return String.valueOf(code);
    }

    private static boolean sendVerificationEmail(String email, String verificationCode) {
        final String username = "lju.xxx@gmail.com"; // Replace with your email
        final String password = "xxxx tcld xxxx fjbm"; // Replace with your email password

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP server
        properties.put("mail.smtp.port", "587"); // Replace with your SMTP port

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("lju.xxx@gmail.com")); // Replace with your email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + verificationCode);

            Transport.send(message);
            return true;
        } catch (SendFailedException e) {
            System.out.println("Invalid email address or email not found.");
            return false;
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

    private static boolean verifyCode(String userCode, String verificationCode) {
        return userCode.equals(verificationCode);
    }
}
