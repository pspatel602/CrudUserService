package com.pspatel.CRUDService.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pspatel.CRUDService.exception.OrgServiceCustomException;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.repository.OrgRepository;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrgServiceTest {

  @Mock private OrgRepository orgRepository;

  @InjectMocks private OrgServiceImpl orgService;

  private Organization organization;

  @BeforeEach
  void setup() {
    organization = new Organization("PE01", "Apple Inc.", "United States");
  }

  @Test
  public void addOrganization() {
    when(orgRepository.save(ArgumentMatchers.any(Organization.class))).thenReturn(organization);
    Organization actualOrg = orgService.addOrganization(organization);
    assertThat(actualOrg.getOrgName()).isSameAs(organization.getOrgName());
    verify(orgRepository, times(1)).save(ArgumentMatchers.any(Organization.class));
  }

  @Test
  public void getOrganizationsTest() {
    when(orgRepository.findAll()).thenReturn(List.of(new Organization(), new Organization()));
    assertThat(orgService.getOrganizations()).hasSize(2);
    verify(orgRepository, times(1)).findAll();
    verifyNoMoreInteractions(orgRepository);
  }

  @Test
  public void getOrganizationByIdTest() {
    Organization expectedOrg = new Organization("PE02", "Expected Inc.", "United States");

    when(orgRepository.findById("PE02")).thenReturn(Optional.of(expectedOrg));

    Organization actualOrg = orgService.getOrganizationById("PE02");

    // Assert
    assertThat(actualOrg).usingRecursiveComparison().isEqualTo(expectedOrg);
    verify(orgRepository, times(1)).findById(anyString());
    verifyNoMoreInteractions(orgRepository);
  }

  @Test
  public void testGetOrganizationByOrgName() {
    Organization expectedOrg = new Organization("PE02", "Expected Inc.", "United States");

    when(orgRepository.findByOrgName("Expected Inc.")).thenReturn(expectedOrg);

    Organization actualOrg = orgService.getOrganizationByName("Expected Inc.");

    // Assert
    assertThat(actualOrg).usingRecursiveComparison().isEqualTo(expectedOrg);
    verify(orgRepository, times(1)).findByOrgName(anyString());
    verifyNoMoreInteractions(orgRepository);
  }

  @Test
  public void testUpdateOrgByUsername() {

    when(orgRepository.findById("PE01")).thenReturn(Optional.ofNullable(organization));
    when(orgRepository.save(ArgumentMatchers.any(Organization.class))).thenReturn(organization);
    organization.setLocation("India");
    Organization updatedOrg = orgService.updateOrganization(organization);
    assertThat(updatedOrg.getLocation()).isEqualTo("India");
  }

  @Test
  public void should_throw_exception_when_update_org_doesnt_exist() {
    Assert.assertThrows(
        OrgServiceCustomException.class,
        () -> {
          orgService.updateOrganization(organization);
        });
  }

  @Test
  public void testDeleteOrganizationById() {

    doNothing().when(orgRepository).deleteById(organization.getId());
    orgService.deleteOrganizationById(organization.getId());
    verify(orgRepository, times(1)).deleteById(organization.getId());
  }
}
