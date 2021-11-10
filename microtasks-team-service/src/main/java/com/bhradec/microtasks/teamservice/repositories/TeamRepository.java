package com.bhradec.microtasks.teamservice.repositories;

import com.bhradec.microtasks.teamservice.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findFirstByName(String name);
    boolean existsByName(String name);
}
