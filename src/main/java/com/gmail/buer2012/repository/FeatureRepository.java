package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.Feature;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

public interface FeatureRepository extends CrudRepository<Feature, Long> {
    Feature findByFeatureName(String featureName);
    List<Feature> findAll();
}
