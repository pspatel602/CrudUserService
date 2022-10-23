package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.UserRequest;
import java.util.List;

public interface UserService {

  // Create Operation
  public User addUser(UserRequest userRequest);

  // Read Operation
  public List<User> getUsers();

  // Read Operation
  public User getUserByUsername(String userId);

  // Update Operation
  public User updateUserByUsername(User userRequest);

  // Delete Operation
  public String deleteUserById(String userId);
}
