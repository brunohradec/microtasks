package com.bhradec.microtasks.userservice.repositories;

import com.bhradec.microtasks.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByUsername(String username);
    List<User> findAllByTeamId(Long teamId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
