package com.example.SellPhone.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender javaMailSender;
    SpringTemplateEngine springTemplateEngine;

    public void sendConfirmationEmail(String toEmail, String subject, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo(toEmail);  // email nhận
            messageHelper.setSubject(subject);

            Context context = new Context();
            context.setVariable("otp", otp); // Truyền vào template

            // Load HTML template
            String htmlContent = springTemplateEngine.process("EmailExport/otp-email.html", context);
            messageHelper.setText(htmlContent, true);  // body HTML

            javaMailSender.send(mimeMessage);
            System.out.println("Email đã được gửi!");
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
        }
    }
}
