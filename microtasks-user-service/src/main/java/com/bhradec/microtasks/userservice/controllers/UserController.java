package com.bhradec.microtasks.userservice.controllers;

import com.bhradec.microtasks.userservice.domain.User;
import com.bhradec.microtasks.userservice.dtos.UserCommandDto;
import com.bhradec.microtasks.userservice.dtos.UserDto;
import com.bhradec.microtasks.userservice.exceptions.ConflictException;
import com.bhradec.microtasks.userservice.exceptions.DoesNotRespondException;
import com.bhradec.microtasks.userservice.exceptions.NotFoundException;
import com.bhradec.microtasks.userservice.mappers.UserMapper;
import com.bhradec.microtasks.userservice.services.UserService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserCommandDto userCommandDto) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userMapper.mapUserToDto(userService.save(userCommandDto)));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, exception.getMessage());
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping("{username}/verify")
    public ResponseEntity<Boolean> verifyUserPasswordByUsername(
            @PathVariable String username,
            @RequestBody String password) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.verifyUserPasswordByUsername(username, password));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService
                        .findAll()
                        .stream()
                        .map(userMapper::mapUserToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping(params = {"teamId"})
    public ResponseEntity<List<UserDto>> findAllByTeamId(@RequestParam Long teamId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService
                        .findAllByTeamId(teamId)
                        .stream()
                        .map(userMapper::mapUserToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        UserDto foundUserDto = userService
                .findById(id)
                .map(userMapper::mapUserToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the provided id not found"));

        return ResponseEntity.status(HttpStatus.OK).body(foundUserDto);
    }

    @GetMapping(params = {"username"})
    public ResponseEntity<UserDto> findByUsername(@RequestParam String username) {
        UserDto foundUserDto = userService
                .findByUsername(username)
                .map(userMapper::mapUserToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the provided username not found"));

        return ResponseEntity.status(HttpStatus.OK).body(foundUserDto);
    }

    @GetMapping("{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.existsById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody UserCommandDto userCommandDto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userMapper.mapUserToDto(userService.updateById(id, userCommandDto)));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (ConflictException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, exception.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
