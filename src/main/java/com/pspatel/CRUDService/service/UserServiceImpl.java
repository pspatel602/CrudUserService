package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.email.EmailSenderService;
import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Role;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.repository.OrgRepository;
import com.pspatel.CRUDService.repository.RoleRepository;
import com.pspatel.CRUDService.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final PasswordEncoder encoder;
  private final UserRepository repository;
  private final RoleRepository roleRepository;
  private final OrgRepository orgRepository;
  private final EmailSenderService emailSenderService;

  @Override
  public User addUser(UserRequest userRequest) {
    String verificationCode = RandomString.make(64);
    if (!orgRepository.existsByOrgName(userRequest.getOrganization().getOrgName())) {
      orgRepository.save(userRequest.getOrganization());
    } else {
      System.out.println("Organization Already exist");
    }

    User user =
        new User(
            userRequest.getUsername(),
            userRequest.getEmail(),
            encoder.encode(userRequest.getPassword()),
            verificationCode,
            false,
            userRequest.getOrganization());

    Set<String> strRoles = userRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole =
          roleRepository
              .findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(
          role -> {
            switch (role) {
              case "admin":
                Role adminRole =
                    roleRepository
                        .findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);

                break;

              default:
                Role userRole =
                    roleRepository
                        .findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            }
          });
    }

    user.setRoles(roles);
    repository.save(user);
    emailSenderService.sendVerificationEmail(user, user.getEmail());
    return user;
  }

  @Override
  public List<User> getUsers() {
    log.info("get All users from db...");
    return repository.findAll();
  }

  @Override
  public User getUserByUsername(String username) {
    log.info("get " + username + " from the db...");
    User foundUser =
        repository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UserServiceCustomException(
                        "User with given username not found", "USER_NOT_FOUND"));

    if (foundUser.isEnabled()) {
      return foundUser;
    }
    throw new UserServiceCustomException(
        "User with given username is not enabled", "USER_IS_NOT_ENABLED");
  }

  @Override
  public User updateUserByUsername(User userRequest) {
    // get existing document from db
    User existingUser =
        repository
            .findByUsername(userRequest.getUsername())
            .orElseThrow(
                () ->
                    new UserServiceCustomException(
                        "User with given username ("
                            + userRequest.getUsername()
                            + ") is not "
                            + "updated "
                            + "successfully",
                        "ENTER_VALID_USER_ID"));

    // update the details of users
    existingUser =
        existingUser
            .builder()
            .username(userRequest.getUsername())
            .email(userRequest.getEmail())
            .password(userRequest.getPassword())
            .roles(userRequest.getRoles())
            .organization(userRequest.getOrganization())
            .build();

    return repository.save(existingUser);
  }

  @Override
  public String deleteUserById(String username) {
    repository.deleteByUsername(username);
    return "User " + username + " deleted successfully.";
  }
}
