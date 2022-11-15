package com.pspatel.CRUDService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspatel.CRUDService.controller.AuthController;
import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.repository.OrgRepository;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudControllerIntegrationTest {
  static {
    System.setProperty("spring.mongodb.embedded.version", "5.0.0");
  }

  @MockBean private EmailSenderService emailSenderService;
  @Autowired private UserServiceImpl userService;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private AuthController authController;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  private UserRequest userRequest;
  private User user;
  @Autowired private AuthServiceImpl authService;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserDetailsServiceImpl userDetailsService;
  @Autowired private OrgRepository orgRepository;
  private LoginRequest loginRequest;
  private SignupRequest signUpRequest;

  @BeforeAll
  public void setupAll() {
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
  }

  @AfterAll
  public void teardownAll() {
    roleRepository.deleteAll();
    userRepository.deleteAll();
    orgRepository.deleteAll();
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

  @Test
  @Order(1)
  public void testCreateUserControllerTest() throws Exception {

    mockMvc
        .perform(
            post("/api/users", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRequest))
                .characterEncoding("utf-8"))
        .andExpect(status().isCreated())
        .andReturn();
    Boolean isUserCreated = userRepository.existsByUsername(userRequest.getUsername());
    assertTrue(isUserCreated);
  }

  @Test
  @Order(2)
  public void testGetAllUsers() throws Exception {
    List<User> expectedUser = userService.getUsers();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(expectedUser.get(0).getUsername())));
  }

  @Test
  @Order(3)
  public void testGetUserById() throws Exception {

    User expectedUser = userService.getUserByUsername(loginRequest.getUsername());
    System.out.println("expectedUser: " + expectedUser);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/users/{userId}", loginRequest.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is(expectedUser.getUsername())));
  }

  @Test
  @Order(4)
  void testDeleteUser() throws Exception {

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
    Boolean isUserExist = userRepository.existsByUsername(newUser.getUsername());
    assertTrue(isUserExist);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/users/{userId}", newUser.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk());

    isUserExist = userRepository.existsByUsername(newUser.getUsername());
    assertFalse(isUserExist);
  }
}
