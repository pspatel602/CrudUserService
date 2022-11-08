package com.pspatel.CRUDService.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.security.jwt.JwtUtils;
import com.pspatel.CRUDService.security.services.UserDetailsImpl;
import com.pspatel.CRUDService.security.services.UserDetailsServiceImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = JwtUtils.class)
public class testJwtUtils {

  private LoginRequest loginRequest;
  private SignupRequest signUpRequest;

  @MockBean private AuthenticationManager authenticationManager;
  @MockBean private Authentication authentication;
  @MockBean private UserDetailsImpl userDetails;
  @MockBean private UserDetailsServiceImpl userDetailsServiceImpl;

  @InjectMocks private JwtUtils jwtUtils;

  @BeforeEach
  void setup() {

    loginRequest = new LoginRequest("admin", "admin");
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));

    signUpRequest =
        new SignupRequest()
            .builder()
            .username("admin")
            .email("pspatel602@gmail.com")
            .password("admin")
            .enabled(true)
            .verificationCode("12345")
            .roles(rolesRequest)
            .organization(new Organization("PE01", "Apple Inc.", "United States"))
            .build();

    userDetails =
        new UserDetailsImpl(
            "01",
            "admin",
            "pspatel602@gmail.com",
            "admin",
            "12345",
            true,
            Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
  }

  @WithMockUser(
      username = "admin",
      password = "admin",
      roles = {"ADMIN"})
  @Test
  void testGenerateJwtToken() {

    when((UserDetailsImpl) authentication.getPrincipal()).thenReturn(userDetails);
    String token = jwtUtils.generateJwtToken(authentication);
    System.out.println(token);
    assertNotNull(token);
  }

  @Test
  void testValidateJwtToken() {

    when((UserDetailsImpl) authentication.getPrincipal()).thenReturn(userDetails);
    String token = jwtUtils.generateJwtToken(authentication);
    System.out.println(token);
    boolean isTokenValid = jwtUtils.validateJwtToken(token);
    assertTrue(isTokenValid);
  }

  @Test
  void testGetUserNameFromJwtToken() {
    when((UserDetailsImpl) authentication.getPrincipal()).thenReturn(userDetails);
    String token = jwtUtils.generateJwtToken(authentication);
    System.out.println(token);
    String actualUsername = jwtUtils.getUserNameFromJwtToken(token);
    String expectedUsername = "admin";
    assertEquals(expectedUsername, actualUsername);
  }
}
