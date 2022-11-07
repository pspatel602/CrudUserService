package com.pspatel.CRUDService.payload.response;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 8063550442153900525L;
  private String errorMessage;
  private String errorCode;

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
