package org.sake.api.domain.token.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>{

    Optional<RefreshToken> findById(String refreshToken);
    Optional<RefreshToken> DeleteById(String refreshToken);
}
