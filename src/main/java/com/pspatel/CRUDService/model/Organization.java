package com.pspatel.CRUDService.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Organization {
  @Id private String id;
  @NotBlank private String orgName;
  @NotBlank private String location;
}