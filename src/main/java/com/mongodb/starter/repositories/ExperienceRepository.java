package com.mongodb.starter.repositories;

import com.mongodb.starter.models.Experience;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository {

    Experience save(Experience exp);

    List<Experience> saveAll(List<Experience> persons);

    List<Experience> findAll();

    List<Experience> findAll(List<String> ids);

    Experience findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    Experience update(Experience person);

    long update(List<Experience> persons);

    double getAverageAge();

    Experience findByUsername(String username);

    Boolean existsByUsername(String username);

}
