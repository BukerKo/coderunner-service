package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.RunInformation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RunInformationRepository extends CrudRepository<RunInformation, Long> {
    Optional<RunInformation> findByUserId(Long userId);
}
