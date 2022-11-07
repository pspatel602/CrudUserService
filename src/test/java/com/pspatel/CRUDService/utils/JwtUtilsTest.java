package com.pspatel.CRUDService.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = JwtUtils.class)
public class JwtUtilsTest {

  private LoginRequest loginRequest;

  private SignupRequest signUpRequest;

  @MockBean private AuthenticationManager authenticationManager;

  @MockBean private UserDetailsImpl userPrincipal;

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

    userPrincipal =
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
      password = "user",
      roles = {"ADMIN"})
  @Test
  void testGenerateJwtToken() {

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    String actualToken = jwtUtils.generateJwtToken(authentication);
    System.out.println(actualToken);
    assertNotNull(actualToken);
  }
}
