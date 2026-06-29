package com.netflix.clone.serviceImpl;

import com.netflix.clone.exception.EmailNotVerifiedException;
import com.netflix.clone.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * @param toEmail
     * @param token
     */
    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Verify Your Email");

            String verificationLink = frontendUrl + "/verify-email?token=" +token;

            String emailBody =
                    "Welcome to Netflix Clone!\n\n"
                    + "Thank you for registering. Please verify your email address by clicking the link below: \n\n"
                    + verificationLink
                    + "\n\n"
                    + "This link will expire in 24 hours.\n\n"
                    + "If you didn't create this account, please ignore this email.\n\n"
                    + "Best Regards.\n"
                    + "Netflix Clone Team";

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Verification email sent to {}", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to send verification email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailNotVerifiedException("Failed to send verification email");
        }
    }

    /**
     * @param toEmail
     * @param token
     */
    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Password Reset");

            String resetLink = frontendUrl + "/reset-password?token=" +token;

            String emailBody =
                    "Hi,\n\n"
                            + "We received a request to reset your password. Click the link below to reset it: \n\n"
                            + resetLink
                            + "\n\n"
                            + "This link will expire in 1 hour.\n\n"
                            + "If you didn't request a password reset, please ignore this email.\n\n"
                            + "Best Regards.\n"
                            + "Netflix Clone Team";

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Password reset email sent to {}", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new RuntimeException("Failed to send password reset email");
        }

    }
}
