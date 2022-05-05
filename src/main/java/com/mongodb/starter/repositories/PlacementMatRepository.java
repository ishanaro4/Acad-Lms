package com.mongodb.starter.repositories;

import com.mongodb.starter.models.PlacementMaterial;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementMatRepository {

    PlacementMaterial save(PlacementMaterial exp);

    List<PlacementMaterial> saveAll(List<PlacementMaterial> persons);

    List<PlacementMaterial> findAll();

    List<PlacementMaterial> findAll(List<String> ids);

    PlacementMaterial findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    //PlacementMaterial update(PlacementMaterial person);

    long update(List<PlacementMaterial> persons);

    double getAverageAge();

    PlacementMaterial findByUsername(String username);

    Boolean existsByUsername(String username);

    List<PlacementMaterial> findAllBySubject(String sub);

}
