package com.pspatel.CRUDService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.repository.RoleRepository;
import com.pspatel.CRUDService.repository.UserRepository;
import com.pspatel.CRUDService.security.services.UserDetailsServiceImpl;
import com.pspatel.CRUDService.service.AuthServiceImpl;
import com.pspatel.CRUDService.service.UserServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test-application.yml")
public class CrudServiceApplicationIntegrationTest {

  static {
    System.setProperty("spring.mongodb.embedded.version", "5.0.0");
  }

  @Autowired private UserServiceImpl userService;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  private UserRequest userRequest;
  private User user;

  // Required properties for AuthServiceImpl
  @Autowired private AuthServiceImpl authService;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserDetailsServiceImpl userDetailsService;
  private LoginRequest loginRequest;

  private SignupRequest signUpRequest;

  @BeforeEach
  void setup() {
    Set<Role> roles = new HashSet<>();
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));
    Role admin = new Role(UUID.randomUUID().toString(), ERole.ROLE_ADMIN);
    Role user_role = new Role(UUID.randomUUID().toString(), ERole.ROLE_USER);
    roleRepository.save(admin);
    roleRepository.save(user_role);

    roles.add(admin);
    userRequest = new UserRequest();
    userRequest =
        new UserRequest()
            .builder()
            .username("parth")
            .email("pspatel602@gmail.com")
            .password("parth@321")
            .verificationCode("123456789")
            .roles(rolesRequest)
            .isEnabled(true)
            .organization(new Organization("PE01", "Dell Inc.", "India"))
            .build();
    Organization newOrg = new Organization("PE01", "Dell Inc.", "India");
    userRequest.setRoles(rolesRequest);
    userRequest.setOrganization(newOrg);
    user = new User();
    user =
        new User()
            .builder()
            .username(userRequest.getUsername())
            .email(userRequest.getEmail())
            .password(userRequest.getPassword())
            .verificationCode(userRequest.getVerificationCode())
            .isEnabled(userRequest.isEnabled())
            .roles(roles)
            .organization(userRequest.getOrganization())
            .build();

    // AuthServiceImpl Setup
    userDetailsService = new UserDetailsServiceImpl(userRepository);
    loginRequest = new LoginRequest().builder().username("admin").password("admin").build();

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

  @Order(1)
  @Test
  public void test_add_user() {
    boolean isExist = userRepository.existsByUsername(userRequest.getUsername());
    assertFalse(isExist);
    userService.addUser(userRequest);
    boolean isUserExist = userRepository.existsByUsername(userRequest.getUsername());
    System.out.println(
        "User details after saving: " + userService.getUserByUsername(userRequest.getUsername()));
    assertTrue(isUserExist);
  }

  @Order(9)
  @Test
  public void test_add_user_without_role() {
    UserRequest userRequestWithoutRole = new UserRequest();
    userRequestWithoutRole =
        userRequestWithoutRole
            .builder()
            .username("rajesh")
            .email("p7600204790@gmail.com")
            .password("parth@321")
            .verificationCode("123456789")
            .roles(null)
            .isEnabled(true)
            .organization(new Organization("PE01", "HP Inc.", "USA"))
            .build();
    System.out.println("userRequestWithoutRole Roles :" + userRequestWithoutRole.getRoles());
    System.out.println("userRequestWithoutRole: " + userRequestWithoutRole);
    boolean isExist = userRepository.existsByUsername(userRequestWithoutRole.getUsername());
    assertFalse(isExist);
    userService.addUser(userRequestWithoutRole);
    boolean isUserExist = userRepository.existsByUsername(userRequestWithoutRole.getUsername());
    System.out.println("isUserExist:" + isUserExist);
    System.out.println(
        "User details after saving: "
            + userService.getUserByUsername(userRequestWithoutRole.getUsername()));
    assertTrue(isUserExist);
  }

  @Test
  @Order(2)
  public void test_getUsers() {
    List<User> allUsers = userRepository.findAll();
    System.out.println(allUsers);
    assertEquals(userRequest.getUsername(), allUsers.stream().findFirst().get().getUsername());
    System.out.println("In test_getUsers all user's list :" + allUsers);
  }

  @Test
  @Order(3)
  public void test_getUserByUsername() {
    User actualUser = userService.getUserByUsername(userRequest.getUsername());
    assertEquals(userRequest.getUsername(), actualUser.getUsername());
    System.out.println("In test_getUserByUsername actual user :" + actualUser);
  }

  @Test
  @Order(4)
  public void test_updateUserByUsername() {

    User actualUser = userService.getUserByUsername(user.getUsername());
    System.out.println("In test_updateUserByUsername Before updating user: " + actualUser);
    String expectedEmail = userService.getUserByUsername(user.getUsername()).getEmail();
    user.setEmail("pparth602@yahoo.com");
    User actualUpdatedUser = userService.updateUserByUsername(user);
    System.out.println(actualUpdatedUser);
    assertNotEquals(expectedEmail, actualUpdatedUser.getEmail());
    System.out.println("In test_updateUserByUsername After updating user :" + actualUpdatedUser);
  }

  @Test
  @Order(5)
  public void test_deleteUserById() {
    boolean isUserExist = userRepository.existsByUsername(userRequest.getUsername());
    assertTrue(isUserExist);
    userService.deleteUserById(userRequest.getUsername());
    isUserExist = userRepository.existsByUsername(userRequest.getUsername());
    assertFalse(isUserExist);
  }

  @Test
  @Order(6)
  public void test_registerUser() {
    ResponseEntity actualRes = authService.registerUser(signUpRequest);

    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actualRes.getBody().toString())
        .isEqualTo("MessageResponse(message=User registered successfully!)");
  }

  @Test
  @Order(7)
  public void test_AuthenticateUser() {
    assertThrows(
        BadCredentialsException.class,
        () -> {
          authService.authenticateUser(loginRequest);
        });
    authService.registerUser(signUpRequest);
    ResponseEntity actualRes = authService.authenticateUser(loginRequest);
    System.out.println("actualRes:" + actualRes);
    System.out.println("actualRes body: " + actualRes.getBody());
    System.out.println("actualRes Headers: " + actualRes.getHeaders());

    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(8)
  void test_Verify() {
    user.setEnabled(false);
    userRepository.save(user);

    assertThrows(
        UserServiceCustomException.class,
        () -> {
          userService.getUserByUsername(user.getUsername());
        });
    assertFalse(authService.verify("invalid_token"));
    boolean isValidToken = authService.verify(user.getVerificationCode());
    System.out.println(isValidToken);
    assertTrue(isValidToken);
    User activatedUser = userService.getUserByUsername(user.getUsername());
    System.out.println("activatedUser enabled status: " + activatedUser.isEnabled());
    assertTrue(activatedUser.isEnabled());
  }

  @Test
  @Order(9)
  public void test_registerUser_without_role() {
    SignupRequest signupRequestWithoutRole = new SignupRequest();

    signupRequestWithoutRole =
        signupRequestWithoutRole
            .builder()
            .username("admin")
            .email("pspatel602@gmail.com")
            .password("admin")
            .enabled(true)
            .verificationCode("12345")
            .roles(null)
            .organization(new Organization("PE01", "Apple Inc.", "United States"))
            .build();
    System.out.println("signupRequestWithoutRole:" + signupRequestWithoutRole);
    ResponseEntity actualRes = authService.registerUser(signupRequestWithoutRole);

    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actualRes.getBody().toString())
        .isEqualTo("MessageResponse(message=User registered successfully!)");
  }
}
