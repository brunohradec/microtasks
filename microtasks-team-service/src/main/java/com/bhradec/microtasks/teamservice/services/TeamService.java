package com.bhradec.microtasks.teamservice.services;

import com.bhradec.microtasks.teamservice.domain.Team;
import com.bhradec.microtasks.teamservice.dtos.TeamCommandDto;
import com.bhradec.microtasks.teamservice.exceptions.ConflictException;
import com.bhradec.microtasks.teamservice.exceptions.NotFoundException;
import com.bhradec.microtasks.teamservice.mappers.TeamMapper;
import com.bhradec.microtasks.teamservice.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    public Team save(TeamCommandDto teamCommandDto) throws ConflictException {
        Team team = teamMapper.mapCommandToTeam(teamCommandDto);

        if (teamRepository.existsByName(team.getName())) {
            throw new ConflictException("Team with the provided name already exists");
        }

        return teamRepository.save(team);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public Team updateById(Long id, TeamCommandDto teamCommandDto) throws NotFoundException, ConflictException {

        Team team = teamMapper.mapCommandToTeam(teamCommandDto);

        if (!teamRepository.existsById(id)) {
            throw new NotFoundException("Team with the provided id does not exist");
        }

        if (teamRepository.existsByName(team.getName())) {
            throw new ConflictException("Team with the provided name already exists");
        }

        team.setId(id);

        return teamRepository.save(team);
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!teamRepository.existsById(id)) {
            throw new NotFoundException("Team with the provided id does not exist");
        }

        teamRepository.deleteById(id);
    }
}
