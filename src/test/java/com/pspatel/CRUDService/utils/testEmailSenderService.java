package com.pspatel.CRUDService.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest(classes = EmailSenderService.class)
@ExtendWith(MockitoExtension.class)
public class testEmailSenderService {

  @MockBean private JavaMailSender mailSender;
  @InjectMocks private EmailSenderService emailSenderService;

  @Test
  void testSendVerificationEmail() {
    User user = mock(User.class);
    emailSenderService.sendVerificationEmail(user, "pspatel602@gmail.com");
  }
}
