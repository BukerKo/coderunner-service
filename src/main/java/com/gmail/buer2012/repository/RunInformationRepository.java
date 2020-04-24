package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.RunInformation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RunInformationRepository extends PagingAndSortingRepository<RunInformation, Long> {
    Optional<RunInformation> findByUserId(Long userId);
    Page<RunInformation> findByUser_UsernameContainingIgnoreCase(Pageable page, String user_username);
    void deleteById(Long id);
}
