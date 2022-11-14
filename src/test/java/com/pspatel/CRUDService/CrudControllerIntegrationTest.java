package com.pspatel.CRUDService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspatel.CRUDService.controller.AuthController;
import com.pspatel.CRUDService.exception.UserServiceCustomException;
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
import com.pspatel.CRUDService.service.OrgServiceImpl;
import com.pspatel.CRUDService.service.UserServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test-application.yml")
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudControllerIntegrationTest {
  static {
    System.setProperty("spring.mongodb.embedded.version", "5.0.0");
  }

  @Autowired private UserServiceImpl userService;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private AuthController authController;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;
  private UserRequest userRequest;
  private User user;

  // Required properties for AuthServiceImpl
  @Autowired private AuthServiceImpl authService;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserDetailsServiceImpl userDetailsService;
  @Autowired private OrgServiceImpl orgService;
  @Autowired private OrgRepository orgRepository;
  private LoginRequest loginRequest;
  private SignupRequest signUpRequest;
  private Organization newOrganization;

  @BeforeEach
  void setup() {
    Set<Role> roles = new HashSet<>();
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));
    Role admin = new Role(UUID.randomUUID().toString(), ERole.ROLE_ADMIN);
    Role user_role = new Role(UUID.randomUUID().toString(), ERole.ROLE_USER);
    roleRepository.save(admin);
    roleRepository.save(user_role);

    roles.add(admin);

    signUpRequest =
        new SignupRequest()
            .builder()
            .username("admin")
            .email("pparth602@yahoo.com")
            .password("admin")
            .enabled(true)
            .verificationCode("12345")
            .roles(rolesRequest)
            .organization(new Organization("PE01", "Apple Inc.", "United States"))
            .build();

    loginRequest = new LoginRequest().builder().username("admin").password("admin").build();

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
    user =
        new User()
            .builder()
            .id(Arrays.stream(UUID.randomUUID().toString().split("-")).toArray()[0].toString())
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
    newOrganization = new Organization(UUID.randomUUID().toString(), "Tata", "India");

    System.out.println("==============In Setup==============");
    System.out.println("In Setup: " + signUpRequest.toString());
    System.out.println("In Setup: " + loginRequest.toString());
    System.out.println("In Setup: " + userRequest.toString());
    System.out.println("In Setup: " + user.toString());
  }

  @Test
  @Order(1)
  public void testRegisterUser() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(signUpRequest))
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andReturn();
  }

  @WithMockUser(username = "admin", password = "admin")
  @Order(2)
  @Test
  void testSignIn() throws Exception {
    authService.registerUser(signUpRequest);
    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest))
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

    System.out.println("result:" + result);
    System.out.println("result.getRequest():" + result.getRequest());
    System.out.println("result.getResponse():" + result.getResponse());
  }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  @Order(3)
  public void testCreateUserControllerTest() throws Exception {
    System.out.println("In testCreateUserControllerTest: " + userRequest.toString());
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
  @Order(4)
  @WithMockUser(username = "admin", password = "admin", roles = "admin")
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
  @Order(5)
  @WithMockUser(username = "admin", password = "admin", roles = "admin")
  public void testGetUserById() throws Exception {
    authService.authenticateUser(loginRequest);
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
  @WithMockUser(username = "admin", password = "admin")
  @Order(6)
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
            MockMvcRequestBuilders.get("/api/users/{userId}", loginRequest.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk());

    isUserExist = userRepository.existsByUsername(userRequest.getUsername());
    assertFalse(isUserExist);
  }
}
