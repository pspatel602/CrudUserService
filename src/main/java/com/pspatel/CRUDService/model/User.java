package com.pspatel.CRUDService.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  @Serial private static final long serialVersionUID = 2605093102857375803L;
  @Id private String id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Size(max = 64)
  private String verificationCode;

  private boolean isEnabled;
  @DBRef private Set<Role> roles = new HashSet<>();
  private Organization organization;

  public User(
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
