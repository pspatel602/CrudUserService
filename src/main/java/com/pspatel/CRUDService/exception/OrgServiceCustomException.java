package com.pspatel.CRUDService.exception;

public class OrgServiceCustomException extends RuntimeException {

  private String errorCode;

  public OrgServiceCustomException(String errorMessage, String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
