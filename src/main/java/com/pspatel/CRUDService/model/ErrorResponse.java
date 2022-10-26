package com.pspatel.CRUDService.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 8063550442153900525L;
  private String errorMessage;
  private String errorCode;

}
