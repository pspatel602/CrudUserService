package com.pspatel.CRUDService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.email.EmailValidator;
import com.pspatel.CRUDService.exception.InvalidEmailCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.payload.response.MessageResponse;
import com.pspatel.CRUDService.repository.OrgRepository;
import com.pspatel.CRUDService.repository.RoleRepository;
import com.pspatel.CRUDService.repository.UserRepository;
import com.pspatel.CRUDService.security.jwt.JwtUtils;
import com.pspatel.CRUDService.security.services.UserDetailsImpl;
import com.pspatel.CRUDService.security.services.UserDetailsServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(classes = {AuthServiceImpl.class})
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @MockBean private EmailValidator emailValidator;
  @MockBean private EmailSenderService emailSenderService;
  @MockBean private AuthenticationManager authenticationManager;
  @MockBean private JwtUtils jwtUtils;
  @MockBean private PasswordEncoder encoder;
  @MockBean private UserRepository userRepository;

  @MockBean private RoleRepository roleRepository;

  private LoginRequest loginRequest;

  private SignupRequest signUpRequest;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @MockBean private OrgRepository orgRepository;
  @InjectMocks private AuthServiceImpl authService;

  @BeforeEach
  void setup() {
    userDetailsService = new UserDetailsServiceImpl(userRepository);
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
  }

  @Test
  void testAuthenticateUser() throws Exception {
    final String username = "existingUserName";
    final User user = mock(User.class);
    when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

    // Act
    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Assert
    assertNotNull(userDetails);
    assertEquals(user.getUsername(), userDetails.getUsername());
  }

  @Test
  void authenticateUser() {

    UserDetailsImpl applicationUser = mock(UserDetailsImpl.class);
    Authentication authentication = mock(Authentication.class);

    UsernamePasswordAuthenticationToken authReq =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getUsername());

    when(authenticationManager.authenticate(authReq)).thenReturn(authentication);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(applicationUser);
    authService.authenticateUser(loginRequest);
    String expectedJwt = jwtUtils.generateJwtToken(authentication);
    System.out.println(expectedJwt);
    System.out.println(
        "authenticationManager.authenticate(authReq): "
            + authenticationManager.authenticate(authReq));
    System.out.println("authentication: " + authentication);
    System.out.println(
        "authService.authenticateUser(loginRequest): "
            + authService.authenticateUser(loginRequest));
  }

  @Test
  void testRegisterUser() {
    Exception exception =
        assertThrows(
            InvalidEmailCustomException.class,
            () -> {
              authService.registerUser(signUpRequest);
            });

    String expectedMessage = "PLEASE ENTER VALID EMAIL ADDRESS";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    ResponseEntity expectedEntity =
        ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    ResponseEntity expectedUsernameErrorEntity =
        ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));

    ResponseEntity expectedEmailErrorEntity =
        ResponseEntity.badRequest()
            .body(
                new MessageResponse(
                    "Error: Email is already taken by someone else. Please "
                        + "Enter different email!"));

    when(emailValidator.test(signUpRequest.getEmail())).thenReturn(true);

    when(roleRepository.findByName(ERole.ROLE_USER))
        .thenReturn(Optional.of(new Role(UUID.randomUUID().toString(), ERole.ROLE_USER)));

    when(roleRepository.findByName(ERole.ROLE_ADMIN))
        .thenReturn(Optional.of(new Role(UUID.randomUUID().toString(), ERole.ROLE_ADMIN)));

    ResponseEntity actualEntity = authService.registerUser(signUpRequest);
    assertEquals(expectedEntity, actualEntity);

    when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);
    ResponseEntity actualEmailErrorEntity = authService.registerUser(signUpRequest);
    assertEquals(actualEmailErrorEntity, expectedEmailErrorEntity);

    when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);
    ResponseEntity actualUsernameErrorEntity = authService.registerUser(signUpRequest);
    assertEquals(actualUsernameErrorEntity, expectedUsernameErrorEntity);
  }

  @Test
  void verify() {
    String verificationCode = "valid_Verification_Code";
    User user = mock(User.class);
    when(userRepository.findByVerificationCode(verificationCode)).thenReturn(user);
    assertTrue(authService.verify("valid_Verification_Code"));
    assertFalse(authService.verify("Invalid_Verification_Code"));
  }
}
