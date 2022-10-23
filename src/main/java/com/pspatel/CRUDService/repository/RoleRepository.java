package com.pspatel.CRUDService.repository;

import com.pspatel.CRUDService.model.ERole;
import com.pspatel.CRUDService.model.Role;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
