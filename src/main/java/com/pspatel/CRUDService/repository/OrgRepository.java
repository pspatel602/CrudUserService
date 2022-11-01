package com.pspatel.CRUDService.repository;

import com.pspatel.CRUDService.model.Organization;
import com.pspatel.CRUDService.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends MongoRepository<Organization, String> {

  Organization findByOrgName(String orgName);

  Boolean existsByOrgName(String orgName);

}
