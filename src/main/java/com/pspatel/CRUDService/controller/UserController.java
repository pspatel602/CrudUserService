package com.pspatel.CRUDService.controller;

import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.service.UserService;
import com.pspatel.CRUDService.service.UserServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@RequestBody User user) {
    return userService.addUser(user);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getUsers();
  }

  @GetMapping("/{userId}")
  public User getUserById(@PathVariable String userId) {
    return userService.getUserById(userId);
  }

  @PutMapping
  public User updateUser(@RequestBody User user) {
    return userService.updateUserById(user);
  }

  @DeleteMapping("/{userId}")
  public String deleteUser(@PathVariable String userId) {
    return userService.deleteUserById(userId);
  }
}
