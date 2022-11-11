package com.pspatel.CRUDService.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.security.WebSecurityConfig;
import com.pspatel.CRUDService.service.OrgServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(OrganizationController.class)
@TestPropertySource(locations = "classpath:test-application.yml")
@AutoConfigureMockMvc(addFilters = false)
public class OrganizationControllerTest {

  static {
    System.setProperty("spring.mongodb.embedded.version", "4.0.2");
  }

  @MockBean private WebSecurityConfig webSecurityConfig;

  @MockBean private OrgServiceImpl orgService;
  @InjectMocks private OrganizationController organizationController;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  private List<Organization> allOrgs;
  private Organization org;

  @BeforeEach
  void setup() {
    org = new Organization("PE01", "Apple Inc.", "United States");
    allOrgs = Arrays.asList(org);
  }

  @WithMockUser(username = "admin", password = "admin")
  @Test
  public void testCreateOrg() throws Exception {

    System.out.println(org);
    System.out.println(mapper.writeValueAsString(org));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/admin/orgs/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(org))
                .characterEncoding("utf-8"))
        .andExpect(status().isCreated())
        .andReturn();

    Organization expectedOrg = orgService.getOrganizationByName(org.getOrgName());
    System.out.println("expectedOrg: " + expectedOrg);
  }

  @Test
  void testGetAllOrgs() throws Exception {
    when(orgService.getOrganizations()).thenReturn(allOrgs);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/admin/orgs").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].orgName", is(org.getOrgName())));
  }

  @Test
  void testGetOrgById() throws Exception {
    when(orgService.getOrganizationById("PE01")).thenReturn(org);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/admin/orgs/id/{organizationId}", "PE01")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orgName", is(org.getOrgName())));
  }

  @Test
  void testGetOrgByName() throws Exception {
    when(orgService.getOrganizationByName("Apple Inc.")).thenReturn(org);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/admin/orgs/{organizationName}", "Apple Inc.")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orgName", is(org.getOrgName())));
  }

  @Test
  void testUpdateOrganization() throws Exception {
    org.setLocation("India");
    when(orgService.updateOrganization(org)).thenReturn(org);
    ObjectMapper mapper = new ObjectMapper();
    mockMvc
        .perform(
            put("/api/admin/orgs/")
                .content(mapper.writeValueAsString(org))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.location", is("India")));
  }

  @Test
  void testDeleteOrg() throws Exception {
    when(orgService.deleteOrganizationById(org.getId()))
        .thenReturn("Organization with given id " + org.getId() + " deleted successfully");

    mockMvc
        .perform(
            delete("/api/admin/orgs/{orgId}", org.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("Organization with given id PE01 deleted successfully"))
        .andExpect(status().isOk());
  }
}
