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
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> roles;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @Size(max = 64)
  private String verificationCode;

  private boolean isEnabled;
  private Organization organization;

  public UserRequest(
      String username,
      String email,
      String password,
      String verificationCode,
      boolean isEnabled,
      Organization org) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.verificationCode = verificationCode;
    this.isEnabled = isEnabled;
    this.organization = org;
  }
}
