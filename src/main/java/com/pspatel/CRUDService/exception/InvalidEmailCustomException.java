package com.pspatel.CRUDService.exception;

public class InvalidEmailCustomException extends RuntimeException {

  private String errorCode;

  public InvalidEmailCustomException(String errorMessage, String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
