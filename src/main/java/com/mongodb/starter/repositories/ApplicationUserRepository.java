package com.mongodb.starter.repositories;

import com.mongodb.starter.models.ApplicationUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationUserRepository {

    ApplicationUser save(ApplicationUser person);

    List<ApplicationUser> saveAll(List<ApplicationUser> persons);

    List<ApplicationUser> findAll();

    List<ApplicationUser> findAll(List<String> ids);

    ApplicationUser findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    ApplicationUser update(ApplicationUser person);

    long update(List<ApplicationUser> persons);

    double getAverageAge();

    ApplicationUser findByUsername(String username);

    Boolean existsByUsername(String username);

}
