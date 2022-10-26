package com.pspatel.CRUDService.payload.request;

import com.pspatel.CRUDService.model.Organization;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> roles ;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @Size(max = 64)
  private String verificationCode;

  private boolean enabled;

  private Organization organization;
}
