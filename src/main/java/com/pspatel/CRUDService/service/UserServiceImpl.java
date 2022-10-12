package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.exception.UserServiceCustomException;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
  @Autowired private UserRepository repository;

  @Override
  public User addUser(User user) {
    User newUser =
        User.builder()
            .userId(UUID.randomUUID().toString().split("-")[0])
            .userName(user.getUserName())
            .userEmailId(user.getUserEmailId())
            .userPassword(user.getUserPassword())
            .build();

    return repository.save(newUser);
  }

  @Override
  public List<User> getUsers() {
    return repository.findAll();
  }

  @Override
  public User getUserById(String userId) {
    return repository
        .findById(userId)
        .orElseThrow(
            () -> new UserServiceCustomException("User with given Id (" + userId + ") not found",
                "USER_NOT_FOUND"));
  }

  @Override
  public User updateUserById(User userRequest) {
    // get existing document from db
    User existingUser =
        repository
            .findById(userRequest.getUserId())
            .orElseThrow(
                () ->
                    new UserServiceCustomException(
                        "User with given Id ("+ userRequest.getUserId() + ") is not updated "
                            + "successfully", "ENTER_VALID_USER_ID"));

    // update the details of users
    existingUser =
        existingUser
            .builder()
            .userId(userRequest.getUserId())
            .userName(userRequest.getUserName())
            .userEmailId(userRequest.getUserEmailId())
            .userPassword(userRequest.getUserPassword())
            .build();

    return repository.save(existingUser);
  }

  @Override
  public String deleteUserById(String userId) {
    repository.deleteById(userId);
    return "User " + userId + " deleted successfully.";
  }

}
