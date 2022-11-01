package com.pspatel.CRUDService.repository;

import com.pspatel.CRUDService.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  User findByVerificationCode(String code);

  void deleteByUsername(String username);
}
