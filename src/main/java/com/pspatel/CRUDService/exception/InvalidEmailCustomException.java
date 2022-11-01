package com.pspatel.CRUDService.exception;

import java.io.Serial;

public class InvalidEmailCustomException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -9067821178790340498L;
  private String errorCode;

  public InvalidEmailCustomException(String errorMessage, String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
