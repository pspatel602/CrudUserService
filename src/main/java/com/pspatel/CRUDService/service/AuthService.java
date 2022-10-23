package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.payload.request.LoginRequest;
import com.pspatel.CRUDService.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

  public ResponseEntity<?> registerUser(SignupRequest signUpRequest);

  public boolean verify(String verificationCode);
}
