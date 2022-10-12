package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.model.User;
import java.util.List;

public interface UserService {

  // Create Operation
  public User addUser(User userRequest);

  // Read Operation
  public List<User> getUsers();

  // Read Operation
  public User getUserById(String userId);

  // Update Operation
  public User updateUserById(User userRequest);

  // Delete Operation
  public String deleteUserById(String userId);
}
