package com.pspatel.CRUDService.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "roles")
public class Role implements Serializable {

  @Serial
  private static final long serialVersionUID = -7014377621911648671L;
  @Id private String id;

  private ERole name;
}
