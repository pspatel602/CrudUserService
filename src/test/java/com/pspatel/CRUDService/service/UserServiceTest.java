package com.pspatel.CRUDService.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.exception.InvalidEmailCustomException;
import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.repository.OrgRepository;
import com.pspatel.CRUDService.repository.RoleRepository;
import com.pspatel.CRUDService.repository.UserRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();
  @Mock private PasswordEncoder encoder;
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private OrgRepository orgRepository;
  @Mock private EmailSenderService emailSenderService;
  @InjectMocks private UserServiceImpl userService;
  private UserRequest userRequest;
  private User user;

  @BeforeEach
  void setup() {
    Set<Role> roles = new HashSet<>();
    Set<String> rolesRequest = new HashSet<>(Arrays.asList("user", "admin"));
    Role admin = new Role("ADMIN_01", ERole.ROLE_ADMIN);
    Role user_role = new Role("USER_01", ERole.ROLE_USER);

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
            .username(userRequest.getUsername())
            .email(userRequest.getEmail())
            .password(userRequest.getPassword())
            .verificationCode(userRequest.getVerificationCode())
            .isEnabled(userRequest.isEnabled())
            .roles(roles)
            .organization(userRequest.getOrganization())
            .build();
  }

  @Test
  public void addUserTest() {

    Role admin = new Role("ADMIN_01", ERole.ROLE_ADMIN);
    Role user_role = new Role("USER_01", ERole.ROLE_USER);

    when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(admin));
    when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(user_role));
    when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);
    User created = userService.addUser(userRequest);
    assertThat(created.getUsername()).isSameAs(user.getUsername());
    verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
  }

  @Test
  public void getUsersTest() {
    when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
    assertThat(userService.getUsers()).hasSize(2);
    verify(userRepository, times(1)).findAll();
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  public void getUserByUsernameTest() {
    User expectedUser =
        User.builder().username("Jimmy Olsen").password("Jimmy@321").isEnabled(true).build();

    when(userRepository.findByUsername("Jimmy Olsen")).thenReturn(Optional.of(expectedUser));

    User actualUser = userService.getUserByUsername("Jimmy Olsen");

    assertThat(actualUser).usingRecursiveComparison().isEqualTo(expectedUser);
    verify(userRepository, times(1)).findByUsername("Jimmy Olsen");
    verifyNoMoreInteractions(userRepository);

    Exception exception =
        assertThrows(
            UserServiceCustomException.class,
            () -> {
              userService.getUserByUsername("Jimmy");
            });

    String expectedMessage = "User with given username not found";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void throw_exception_when_user_is_disabled() {
    User expectedDisabledUser =
        User.builder().username("Jimmy").password("Jimmy@321").isEnabled(false).build();
    when(userRepository.findByUsername("Jimmy")).thenReturn(Optional.of(expectedDisabledUser));

    Exception exceptionDisabledUser =
        assertThrows(
            UserServiceCustomException.class,
            () -> {
              userService.getUserByUsername("Jimmy");
            });

    String exceptionDisabledUserMessage = "User with given username is not enabled";
    String actualDisabledUserMessage = exceptionDisabledUser.getMessage();
    assertTrue(actualDisabledUserMessage.contains(exceptionDisabledUserMessage));
  }

  @Test
  public void updateUserByUsernameTest() {

    Set<Role> roles = new HashSet<>();
    Role admin = new Role("ADMIN_01", ERole.ROLE_ADMIN);
    roles.add(admin);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);
    user.setEmail("kimmleJimmy@gmail.com");
    User updatedUser = userService.updateUserByUsername(user);
    assertThat(updatedUser.getEmail()).isEqualTo("kimmleJimmy@gmail.com");
  }

  @Test
  public void should_throw_exception_when_update_user_doesnt_exist() {
    Assert.assertThrows(
        UserServiceCustomException.class,
        () -> {
          userService.updateUserByUsername(user);
        });
  }

  @Test
  public void deleteUserByIdTest() {

    doNothing().when(userRepository).deleteByUsername(user.getId());
    userService.deleteUserById(user.getId());
    verify(userRepository, times(1)).deleteByUsername(user.getId());
  }

  @Test
  public void should_throw_exception_when_get_user_doesnt_exist() {
    Assert.assertThrows(
        UserServiceCustomException.class,
        () -> {
          userService.getUserByUsername(anyString());
        });
  }
}
