package com.pspatel.CRUDService.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.service.UserServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
@TestPropertySource(locations = "classpath:test-application.yml")
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
  @MockBean private UserServiceImpl userService;

  @InjectMocks private UserController userController;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  private List<User> allUsers;
  private UserRequest userRequest;
  private User user;

  @BeforeEach
  void setup() {
    userRequest = new UserRequest();
    Set<Role> roles = new HashSet<>();
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));
    Role admin = new Role("ADMIN_01", ERole.ROLE_ADMIN);

    roles.add(admin);

    UserRequest userRequest =
        new UserRequest()
            .builder()
            .username("parth")
            .email("pspatel602@gmail.com")
            .password("parth@321")
            .verificationCode("123456789")
            .roles(rolesRequest)
            .isEnabled(false)
            .organization(new Organization("PE01", "Apple Inc.", "United States"))
            .build();
    Organization newOrg = new Organization("PE01", "Apple Inc.", "United States");
    userRequest.setRoles(rolesRequest);
    userRequest.setOrganization(newOrg);
    user = new User();
    user =
        new User()
            .builder()
            .id("user01")
            .username(userRequest.getUsername())
            .email(userRequest.getEmail())
            .password(userRequest.getPassword())
            .verificationCode(userRequest.getVerificationCode())
            .isEnabled(userRequest.isEnabled())
            .roles(roles)
            .organization(userRequest.getOrganization())
            .build();

    allUsers = Arrays.asList(user);
  }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  public void testCreateUserControllerTest() throws Exception {

    String requestJson = mapper.writeValueAsString(userRequest);

    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(requestJson)))
        .andExpect(status().isCreated())
        .andReturn();
  }

  @Test
  public void testGetAllUsers() throws Exception {
    when(userService.getUsers()).thenReturn(allUsers);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/users/")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }

  @Test
  public void testGetUserById() throws Exception {
    when(userService.getUserByUsername("parth")).thenReturn(user);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/users/{userId}", "parth")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @Test
  public void should_throw_exception_when_user_doesnt_exist() throws Exception {

    doThrow(
            new UserServiceCustomException(
                "User with given username " + user.getUsername() + "not found", "USER_NOT_FOUND"))
        .when(userService)
        .getUserByUsername(user.getUsername());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/users/{userId}", "parth")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateUser() throws Exception {
    user.setEmail("updatedEmail@gmail.com");
    when(userService.updateUserByUsername(user)).thenReturn(user);
    ObjectMapper mapper = new ObjectMapper();
    mockMvc
        .perform(
            put("/api/users")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is(user.getEmail())));
  }

  @Test
  public void should_throw_exception_when_update_request_user_doesnt_exist() throws Exception {

    doThrow(
            new UserServiceCustomException(
                "User with given username " + user.getUsername() + "not found", "USER_NOT_FOUND"))
        .when(userService)
        .updateUserByUsername(user);
    ObjectMapper mapper = new ObjectMapper();

    mockMvc
        .perform(
            put("/api/users")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testDeleteUserById() throws Exception {

    when(userService.deleteUserById(user.getId()))
        .thenReturn("User " + user.getId() + " deleted " + "successfully.");

    mockMvc
        .perform(
            delete("/api/users/{userId}", user.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("User user01 deleted successfully."))
        .andExpect(status().isOk());
  }

  public void should_throw_exception_in_getUserByUsername_when_user_doesnt_exist()
      throws Exception {

    doThrow(
            new UserServiceCustomException(
                "User with given username (" + user.getUsername() + ")" + " not " + "found",
                "USER_NOT_FOUND"))
        .when(userService)
        .getUserByUsername(user.getUsername());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/users/{}" + user.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
