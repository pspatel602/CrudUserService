package com.pspatel.CRUDService.model;

import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organization")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Organization implements Serializable {

  @Serial private static final long serialVersionUID = 4361766830303263077L;
  @Id private String id;
  @NotBlank private String orgName;
  @NotBlank private String location;
}
