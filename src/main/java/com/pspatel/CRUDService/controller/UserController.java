package com.pspatel.CRUDService.controller;

import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.payload.request.UserRequest;
import com.pspatel.CRUDService.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping
  @ApiOperation(value = "Add new User", response = User.class)
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@RequestBody UserRequest user) {
    return userService.addUser(user);
  }

  @GetMapping
  @ApiOperation(value = "Fetch all the users")
  public List<User> getAllUsers() {
    return userService.getUsers();
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @ApiOperation(value = "Find user by Id")
  public User getUserById(
      @ApiParam(value = "ID value for the user you need to retriever", required = true)
          @PathVariable
          String userId) {
    return userService.getUserByUsername(userId);
  }

  @PutMapping
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @ApiOperation(value = "Update user details")
  public User updateUser(@RequestBody User user) {
    return userService.updateUserByUsername(user);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Delete user by Id")
  public String deleteUser(@PathVariable String userId) {
    return userService.deleteUserById(userId);
  }
}
