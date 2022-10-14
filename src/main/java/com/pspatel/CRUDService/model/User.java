package com.pspatel.CRUDService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the User")
public class User {
  @ApiModelProperty(notes = "The unique id of the user")
  @Id private String userId;
  private String userName;
  @ApiModelProperty(notes = "Email Id of User")
  private String userEmailId;
  @ApiModelProperty(notes = "User's Password")
  private String userPassword;
}
