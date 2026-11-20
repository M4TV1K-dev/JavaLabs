package org.mbesch.lab5.repository;

import jakarta.persistence.LockModeType;
import org.mbesch.lab5.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserSession> findByRefreshToken(String refreshToken);

    List<UserSession> findByUsername(String username);
}
