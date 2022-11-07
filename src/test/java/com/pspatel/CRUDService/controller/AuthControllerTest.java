package com.pspatel.CRUDService.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.security.services.UserDetailsImpl;
import com.pspatel.CRUDService.service.AuthService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

  @MockBean private AuthenticationManager authenticationManager;
  @MockBean private AuthService authService;

  @InjectMocks private AuthController authController;

  private LoginRequest loginRequest;
  private SignupRequest signupRequest = new SignupRequest();
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  @BeforeEach
  void setup() {

    loginRequest = new LoginRequest("admin", "admin");
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));

    signupRequest =
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
  }

    @Test
    public void testRegisterUser() throws Exception {
      System.out.println(signupRequest);
      System.out.println(mapper.writeValueAsString(signupRequest));
      mockMvc
          .perform(
              post("/api/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(signupRequest))
                  .characterEncoding("utf-8"))
          .andExpect(status().isOk())
          .andReturn();
    }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  void testSignIn() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andReturn();
  }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  void testVerify() throws Exception {
    when(authService.verify("12345")).thenReturn(true);
    mockMvc
        .perform(get("/api/auth/verify/{verificationCode}", "12345").characterEncoding("utf-8"))
        .andExpect(content().string("verify_success"))
        .andReturn();
  }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  void testVerifyFail() throws Exception {
    when(authService.verify("12345")).thenReturn(true);
    mockMvc
        .perform(get("/api/auth/verify/{verificationCode}", "34521").characterEncoding("utf-8"))
        .andExpect(content().string("verify_fail"))
        .andReturn();
  }

  @Test
  public void mockApplicationUser() {
    UserDetailsImpl applicationUser = mock(UserDetailsImpl.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
  }
}
