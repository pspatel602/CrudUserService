package com.pspatel.CRUDService.exception;

import java.io.Serial;

public class OrgServiceCustomException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 843526760837281988L;
  private String errorCode;

  public OrgServiceCustomException(String errorMessage, String errorCode) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
