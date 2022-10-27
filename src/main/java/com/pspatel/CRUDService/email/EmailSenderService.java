package com.pspatel.CRUDService.email;

import com.pspatel.CRUDService.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
  private JavaMailSender mailSender;

  public EmailSenderService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendVerificationEmail(User user, String toEmail, String token) {
    String content =
        "Dear user ,"
            + user.getUsername()
            + "Please click the link below to verify your registration:\t \t \n"
            + "http://localhost:8080/api/auth/verify/"
            + user.getVerificationCode()
            + "\n\n\n"
            + "Thank you";
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("pspatel602@gmail.com");
    message.setTo(toEmail);
    message.setText(content);
    message.setSubject("Confirm your email");

    mailSender.send(message);
    System.out.println("Verification Mail Send...");
  }
}
