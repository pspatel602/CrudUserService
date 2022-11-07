package com.pspatel.CRUDService.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pspatel.CRUDService.email.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EmailValidator.class)
@ExtendWith(MockitoExtension.class)
public class testEmailValidator {

  @Autowired EmailValidator emailValidator;

  @Test
  void testValidator(){
   boolean actualResult = emailValidator.test("pspatel602@gmail.com");
   assertTrue(actualResult);
  }

}
