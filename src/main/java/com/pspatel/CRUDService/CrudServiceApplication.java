package com.pspatel.CRUDService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class CrudServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrudServiceApplication.class, args);
  }
}
