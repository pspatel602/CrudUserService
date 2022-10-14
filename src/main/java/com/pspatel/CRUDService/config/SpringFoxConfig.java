package com.pspatel.CRUDService.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private ApiKey apiKey() {
    return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
  }

  private List<SecurityReference> references() {
    AuthorizationScope scopes = new AuthorizationScope("global", "accessEverything");
    return Arrays.asList(new SecurityReference("JWT", new AuthorizationScope[] {scopes}));
  }

  private List<SecurityContext> securityContexts() {
    return Arrays.asList(SecurityContext.builder().securityReferences(references()).build());
  }

  @Bean
  public Docket api() {

    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.pspatel"))
        .paths(PathSelectors.any())
        .build()
        .securityContexts(securityContexts())
        .securitySchemes(Arrays.asList(apiKey()))
        .apiInfo(getInfo());
  }

  private ApiInfo getInfo() {

    return new ApiInfo(
        "CRUD User Service API",
        "API for performing CRUD Operation on User",
        "1.0.0",
        "Terms of Service",
        new Contact("Parth", "https://parths-portfolio.web.app/", "pspatel602@gmail.com"),
        "Licence of API",
        "API Licence URL",
        Collections.emptyList());
  }
}

//  private ApiInfo getInfo() {
//
//    return new ApiInfoBuilder()
//        .title("CRUD User Service")
//        .description("This Project is developed by Parth Patel")
//        .version("1.0.0")
//        .termsOfServiceUrl("Terms of Service")
//        .contact(new Contact("Parth", "https://pspatel.com", "pspatel602@gmail.com"))
//        .license("Licence of API")
//        .licenseUrl("API Licence URL")
//        .extensions(Collections.emptyList())
//        .build();
//  }
