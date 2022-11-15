package com.pspatel.CRUDService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.payload.response.JwtResponse;
import com.pspatel.CRUDService.repository.RoleRepository;
import com.pspatel.CRUDService.repository.UserRepository;
import com.pspatel.CRUDService.security.jwt.JwtUtils;
import com.pspatel.CRUDService.security.services.UserDetailsServiceImpl;
import com.pspatel.CRUDService.service.AuthServiceImpl;
import com.pspatel.CRUDService.service.UserServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CrudServiceApplicationIntegrationTest {

  static {
    System.setProperty("spring.mongodb.embedded.version", "5.0.0");
  }

  @MockBean private EmailSenderService emailSenderService;
  @Autowired private UserServiceImpl userService;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;

  @Autowired private AuthServiceImpl authService;
  @Autowired private AuthenticationManager authenticationManager;
  private UserRequest userRequest;
  private User user;

  @Autowired private JwtUtils jwtUtils;
  @Autowired private UserDetailsServiceImpl userDetailsService;
  private LoginRequest loginRequest;

  private SignupRequest signUpRequest;
  private Organization newOrg;

  @BeforeAll
  public void setup() {
    Set<Role> roles = new HashSet<>();
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));
    Role admin = new Role("__ADMIN__", ERole.ROLE_ADMIN);
    Role user_role = new Role("__USER__", ERole.ROLE_USER);
    roleRepository.save(admin);
    roleRepository.save(user_role);

    roles.add(admin);
    signUpRequest = new SignupRequest();

    signUpRequest.setUsername("admin");
    signUpRequest.setEmail("pparth602@yahoo.com");
    signUpRequest.setPassword("admin");
    signUpRequest.setEnabled(true);
    signUpRequest.setVerificationCode("12345");
    signUpRequest.setRoles(rolesRequest);
    signUpRequest.setOrganization(new Organization("PE01", "Apple Inc.", "United States"));

    loginRequest = new LoginRequest();
    loginRequest.setUsername("admin");
    loginRequest.setPassword("admin");

    userRequest = new UserRequest();

    userRequest.setUsername("parth");
    userRequest.setEmail("pspatel602@gmail.com");
    userRequest.setPassword("parth@321");
    userRequest.setVerificationCode("123456789");
    userRequest.setRoles(rolesRequest);
    userRequest.setEnabled(true);
    userRequest.setOrganization(new Organization("PE01", "Dell Inc.", "India"));

    Organization newOrg = new Organization("PE01", "Dell Inc.", "India");
    userRequest.setRoles(rolesRequest);
    userRequest.setOrganization(newOrg);

    user = new User();
    user.setId(Arrays.stream(UUID.randomUUID().toString().split("-")).toArray()[0].toString());
    user.setUsername(userRequest.getUsername());
    user.setEmail(userRequest.getEmail());
    user.setPassword(userRequest.getPassword());
    user.setVerificationCode(userRequest.getVerificationCode());
    user.setEnabled(userRequest.isEnabled());
    user.setRoles(roles);
    user.setOrganization(userRequest.getOrganization());

    userDetailsService = new UserDetailsServiceImpl(userRepository);
    newOrg = new Organization();
    newOrg.setOrgName("Tata Boeing Aerospace");
    newOrg.setLocation("India");
  }

  @AfterAll
  public void teardownAll() {
    roleRepository.deleteAll();
    userRepository.deleteAll();
  }

  @BeforeEach
  void beforeEach() {
    authService.registerUser(signUpRequest);
    authService.authenticateUser(loginRequest);
  }

  @AfterEach
  void teardownEach() {
    userRepository.deleteAll();
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

  @Order(2)
  @Test
  public void test_add_user_without_role() {
    System.out.println("newOrg: " + newOrg);
    UserRequest userRequestWithoutRole =
        new UserRequest(
            "rajesh",
            "p7600204790@gmail.com",
            null,
            "parth@321",
            "123456789",
            true,
            new Organization(UUID.randomUUID().toString(), "Tata", "India"));

    Boolean isExist = userRepository.existsByUsername(userRequestWithoutRole.getUsername());
    System.out.println(isExist);
    assertFalse(isExist);
    userService.addUser(userRequestWithoutRole);
    boolean isUserExist = userRepository.existsByUsername(userRequestWithoutRole.getUsername());
    assertTrue(isUserExist);
  }

  @Test
  @Order(3)
  public void test_getUsers() {
    List<User> allUsers = userRepository.findAll();
    System.out.println(allUsers);
    System.out.println("UserRequest: " + userRequest);
    assertEquals(loginRequest.getUsername(), allUsers.stream().findFirst().get().getUsername());
    System.out.println("In test_getUsers all user's list :" + allUsers);
  }

  @Test
  @Order(4)
  public void test_getUserByUsername() {
    User actualUser = userService.getUserByUsername(loginRequest.getUsername());
    assertEquals(loginRequest.getUsername(), actualUser.getUsername());
    System.out.println("In test_getUserByUsername actual user :" + actualUser);
  }

  @Test
  @Order(5)
  public void test_updateUserByUsername() {
    userRepository.save(user);
    User actualUser = userService.getUserByUsername(user.getUsername());
    String newUpdateEmail = "p7600204790@gmail.com";
    user.setEmail(newUpdateEmail);

    User actualUpdatedUser = userService.updateUserByUsername(user);
    System.out.println(actualUpdatedUser);
    assertNotEquals(actualUser.getEmail(), actualUpdatedUser.getEmail());
    assertEquals(newUpdateEmail, actualUpdatedUser.getEmail());
  }

  @Test
  @Order(6)
  public void test_deleteUserById() {
    User newUser =
        new User()
            .builder()
            .id(Arrays.stream(UUID.randomUUID().toString().split("-")).toArray()[0].toString())
            .username("mahesh")
            .email("pspatel602@gmail.com")
            .password("parth@321")
            .verificationCode("123456789")
            .isEnabled(true)
            .organization(new Organization("PE01", "Dell Inc.", "India"))
            .build();

    userRepository.save(newUser);
    boolean isUserExist = userRepository.existsByUsername(newUser.getUsername());
    assertTrue(isUserExist);
    userService.deleteUserById(newUser.getUsername());
    isUserExist = userRepository.existsByUsername(newUser.getUsername());
    assertFalse(isUserExist);
  }

  @Test
  @Order(7)
  public void test_registerUser() {
    userRepository.deleteAll();
    ResponseEntity actualRes = authService.registerUser(signUpRequest);

    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actualRes.getBody().toString())
        .isEqualTo("MessageResponse(message=User registered successfully!)");
  }

  @Test
  @Order(8)
  public void test_AuthenticateUser() {
    assertThrows(
        BadCredentialsException.class,
        () -> {
          authService.authenticateUser(new LoginRequest("unknown user", "unknown_password"));
        });
    LoginRequest loginRequest1 =
        new LoginRequest(signUpRequest.getUsername(), signUpRequest.getPassword());
    authService.registerUser(signUpRequest);
    ResponseEntity actualRes = authService.authenticateUser(loginRequest1);
    JwtResponse jwtResponse = (JwtResponse) actualRes.getBody();
    System.out.println(jwtResponse);
    System.out.println(jwtResponse.getToken());
    Boolean isValidToken = jwtUtils.validateJwtToken(jwtResponse.getToken());
    assertTrue(isValidToken);
    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(9)
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
    assertTrue(activatedUser.isEnabled());
  }

  @Test
  @Order(10)
  public void test_registerUser_without_role() {
    userRepository.deleteAll();
    SignupRequest signupRequestWithoutRole = new SignupRequest();

    signupRequestWithoutRole.setUsername("admin");
    signupRequestWithoutRole.setEmail("pspatel602@gmail.com");
    signupRequestWithoutRole.setPassword("admin");
    signupRequestWithoutRole.setEnabled(true);
    signupRequestWithoutRole.setVerificationCode("12345");
    signupRequestWithoutRole.setRoles(null);
    signupRequestWithoutRole.setOrganization(
        new Organization("PE01", "Apple Inc.", "United States"));

    ResponseEntity actualRes = authService.registerUser(signupRequestWithoutRole);

    assertThat(actualRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actualRes.getBody().toString())
        .isEqualTo("MessageResponse(message=User registered successfully!)");
  }
}
