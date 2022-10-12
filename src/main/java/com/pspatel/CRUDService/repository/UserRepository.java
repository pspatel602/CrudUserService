package com.pspatel.CRUDService.repository;

import com.pspatel.CRUDService.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends MongoRepository<User, String> {}
