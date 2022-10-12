package com.pspatel.CRUDService.exception;

import lombok.Data;

@Data
public class UserServiceCustomException extends RuntimeException{

private String errorCode;

  public UserServiceCustomException(String errorMessage , String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
