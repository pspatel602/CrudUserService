package com.pspatel.CRUDService.controller;

import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.User;
import com.pspatel.CRUDService.service.OrgService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/orgs")
@RequiredArgsConstructor
public class OrganizationController {

  private final OrgService orgService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Add new Organization", response = User.class)
  @ResponseStatus(HttpStatus.CREATED)
  public Organization createOrg(@RequestBody Organization organization) {
    return orgService.addOrganization(organization);
  }

  @GetMapping
  @ApiOperation(value = "Fetch all the Organization")
  public List<Organization> getAllUsers() {
    return orgService.getOrganizations();
  }

  @GetMapping("/id/{organizationId}")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Find Organization by Id")
  public Organization getOrgById(
      @ApiParam(value = "ID value for the organization you need to retriever", required = true)
          @PathVariable
          String organizationId) {
    return orgService.getOrganizationById(organizationId);
  }

  @GetMapping("/{organizationName}")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Find Organization by Name")
  public Organization getOrgByName(
      @ApiParam(value = "Name value for the organization you need to retriever", required = true)
          @PathVariable
          String organizationName) {
    return orgService.getOrganizationByName(organizationName);
  }

  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Update Organization details")
  public Organization updateUser(@RequestBody Organization organization) {
    return orgService.updateOrganizationByUsername(organization);
  }

  @DeleteMapping("/{orgId}")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiOperation(value = "Delete Organization by Id")
  public String deleteOrg(@PathVariable String orgId) {
    return orgService.deleteOrganizationById(orgId);
  }
}
