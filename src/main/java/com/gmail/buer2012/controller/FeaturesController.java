package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.Feature;
import com.gmail.buer2012.repository.FeatureRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/features")
public class FeaturesController {

  private FeatureRepository featureRepository;

  @PostMapping(value = "/apply")
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(rollbackFor = RuntimeException.class)
  public ResponseEntity<Integer> applySettings(
      @RequestBody List<Feature> featureRequests) {
    featureRequests.forEach((featureToBeApplied) -> {
      Feature feature =
          featureRepository
              .findByFeatureName(featureToBeApplied.getFeatureName());
      if (feature != null) {
        feature.setEnabled(featureToBeApplied.getEnabled());
        featureRepository.save(feature);
      } else {
        throw new RuntimeException("Can't find feature");
      }
    });
    return ResponseEntity.ok(0);
  }

  @GetMapping(value = "/get")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Feature>> getSettings() {
    return ResponseEntity.ok(featureRepository.findAll());
  }

}
