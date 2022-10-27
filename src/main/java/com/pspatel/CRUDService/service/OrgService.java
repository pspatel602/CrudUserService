package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.model.Organization;
import java.util.List;

public interface OrgService {

  Organization addOrganization(Organization organizationRequest);

  // Read Operation
  public List<Organization> getOrganizations();

  // Read Operation
  public Organization getOrganizationById(String organizationId);

  public Organization getOrganizationByName(String organizationName);

  // Update Operation
  public Organization updateOrganizationByUsername(Organization organizationRequest);

  // Delete Operation
  public String deleteOrganizationById(String organizationId);
}
