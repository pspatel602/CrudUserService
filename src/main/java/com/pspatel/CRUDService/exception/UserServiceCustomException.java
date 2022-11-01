package com.pspatel.CRUDService.exception;

import java.io.Serial;
import lombok.Data;

@Data
public class UserServiceCustomException extends RuntimeException{

  @Serial
  private static final long serialVersionUID = -8338078351757707091L;
  private String errorCode;

  public UserServiceCustomException(String errorMessage , String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
