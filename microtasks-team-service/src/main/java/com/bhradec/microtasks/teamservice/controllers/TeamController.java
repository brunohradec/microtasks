package com.bhradec.microtasks.teamservice.controllers;

import com.bhradec.microtasks.teamservice.dtos.TeamCommandDto;
import com.bhradec.microtasks.teamservice.dtos.TeamDto;
import com.bhradec.microtasks.teamservice.exceptions.ConflictException;
import com.bhradec.microtasks.teamservice.exceptions.NotFoundException;
import com.bhradec.microtasks.teamservice.mappers.TeamMapper;
import com.bhradec.microtasks.teamservice.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("teams")
public class TeamController {
    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @PostMapping
    public ResponseEntity<TeamDto> save(@Valid @RequestBody TeamCommandDto teamCommandDto) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(teamMapper.mapTeamToDto(teamService.save(teamCommandDto)));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService
                        .findAll()
                        .stream()
                        .map(teamMapper::mapTeamToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    public ResponseEntity<TeamDto> findById(@PathVariable Long id) {
        TeamDto foundTeamDto = teamService
                .findById(id)
                .map(teamMapper::mapTeamToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Team with the provided id not found"));

        return ResponseEntity.status(HttpStatus.OK).body(foundTeamDto);
    }

    @GetMapping("{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.existsById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<TeamDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody TeamCommandDto teamCommandDto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(teamMapper.mapTeamToDto(teamService.updateById(id, teamCommandDto)));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (ConflictException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            teamService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
