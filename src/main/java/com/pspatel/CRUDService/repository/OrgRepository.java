package com.pspatel.CRUDService.repository;

import com.pspatel.CRUDService.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface OrgRepository extends MongoRepository<Organization, String> {

  Organization findByOrgName(String orgName);

  Boolean existsByOrgName(String orgName);

}
