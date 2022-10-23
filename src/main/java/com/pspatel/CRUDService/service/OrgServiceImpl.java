package com.pspatel.CRUDService.service;

import com.pspatel.CRUDService.exception.OrgServiceCustomException;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.repository.OrgRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgServiceImpl implements OrgService {

  @Autowired private OrgRepository orgRepository;

  @Override
  public Organization addOrganization(Organization organizationRequest) {
    Organization newOrg = new Organization();
    newOrg =
        newOrg
            .builder()
            .id(UUID.randomUUID().toString())
            .orgName(organizationRequest.getOrgName())
            .location(organizationRequest.getLocation())
            .build();
    orgRepository.save(newOrg);
    return newOrg;
  }

  @Override
  public List<Organization> getOrganizations() {
    return orgRepository.findAll();
  }

  @Override
  public Organization getOrganizationById(String organizationId) {
    return orgRepository
        .findById(organizationId)
        .orElseThrow(() -> new RuntimeException("Error: Organization is not found."));
  }

  @Override
  public Organization updateOrganizationByUsername(Organization organizationRequest) {
    // get existing org from db
    Organization existingOrg =
        orgRepository
            .findById(organizationRequest.getId())
            .orElseThrow(
                () ->
                    new OrgServiceCustomException(
                        "Organization with given Id ("
                            + organizationRequest.getId()
                            + ") is not "
                            + "updated "
                            + "successfully",
                        "ENTER_VALID_ORGANIZATION_ID"));

    // update the details of users
    existingOrg =
        existingOrg
            .builder()
            .id(organizationRequest.getId())
            .orgName(organizationRequest.getOrgName())
            .location(organizationRequest.getLocation())
            .build();

    return orgRepository.save(existingOrg);
  }

  @Override
  public String deleteOrganizationById(String organizationId) {
    orgRepository.deleteById(organizationId);
    return "Organization with given id " + organizationId + " deleted successfully";
  }
}
