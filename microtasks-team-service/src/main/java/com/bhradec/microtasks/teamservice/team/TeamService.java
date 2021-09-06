package com.bhradec.microtasks.teamservice.team;

import com.bhradec.microtasks.teamservice.exception.ConflictException;
import com.bhradec.microtasks.teamservice.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    private Team mapCommandToTeam(TeamCommand teamCommand) {
        return Team
            .builder()
            .name(teamCommand.getName())
            .description(teamCommand.getDescription())
            .build();
    }

    private TeamDto mapTeamToDto(Team team) {
        return TeamDto
            .builder()
            .id(team.getId())
            .name(team.getName())
            .description(team.getDescription())
            .build();
    }

    public TeamDto save(TeamCommand teamCommand) throws ConflictException {
        Team team = mapCommandToTeam(teamCommand);

        if (teamRepository.existsByName(team.getName())) {
            throw new ConflictException("Team with the provided name already exists");
        }

        return mapTeamToDto(teamRepository.save(team));
    }

    public List<TeamDto> findAll() {
        return teamRepository
            .findAll()
            .stream()
            .map(this::mapTeamToDto)
            .collect(Collectors.toList());
    }

    public Boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }

    public Optional<TeamDto> findById(Long id) {
        return teamRepository
            .findById(id)
            .map(this::mapTeamToDto);
    }

    public TeamDto updateById(Long id, TeamCommand teamCommand) throws
        NotFoundException,
        ConflictException {

        Team team = mapCommandToTeam(teamCommand);

        if (!teamRepository.existsById(id)) {
            throw new NotFoundException("Team with the provided id does not exist");
        }

        if (teamRepository.existsByName(team.getName())) {
            throw new ConflictException("Team with the provided name already exists");
        }

        team.setId(id);

        return mapTeamToDto(teamRepository.save(team));
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!teamRepository.existsById(id)) {
            throw new NotFoundException("Team with the provided id does not exist");
        }

        teamRepository.deleteById(id);
    }
}
