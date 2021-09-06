package com.bhradec.microtasks.teamservice.team;

import com.bhradec.microtasks.teamservice.exception.ConflictException;
import com.bhradec.microtasks.teamservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("team")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamDto> save(@Valid @RequestBody TeamCommand teamCommand) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(teamService.save(teamCommand));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<TeamDto> findById(@PathVariable Long id) {
        Optional<TeamDto> foundTeamOptional = teamService.findById(id);

        if (foundTeamOptional.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foundTeamOptional.get());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Team with the provided id not found"
            );
        }
    }

    @GetMapping("exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.existsById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<TeamDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody TeamCommand teamCommand) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(teamService.updateById(id, teamCommand));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            teamService.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }
}
