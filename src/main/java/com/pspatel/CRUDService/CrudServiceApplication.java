package com.pspatel.CRUDService;

import com.pspatel.CRUDService.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class CrudServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrudServiceApplication.class, args);
  }
}
