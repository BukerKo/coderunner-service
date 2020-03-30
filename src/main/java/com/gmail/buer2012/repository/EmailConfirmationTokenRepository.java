package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.EmailConfirmationToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EmailConfirmationTokenRepository extends CrudRepository<EmailConfirmationToken, Long> {
    Optional<EmailConfirmationToken> findByToken(String token);
}
