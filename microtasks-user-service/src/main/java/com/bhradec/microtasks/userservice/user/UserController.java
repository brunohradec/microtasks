package com.bhradec.microtasks.userservice.user;

import com.bhradec.microtasks.userservice.exception.ConflictException;
import com.bhradec.microtasks.userservice.exception.DoesNotRespondException;
import com.bhradec.microtasks.userservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserCommand userCommand) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userService.save(userCommand));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT,
                    exception.getMessage()
            );
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @GetMapping(params = {"teamId"})
    public ResponseEntity<List<UserDto>> findAllByTeamId(@RequestParam Long teamId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAllByTeamId(teamId));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        Optional<UserDto> foundUserOptional = userService.findById(id);

        if (foundUserOptional.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foundUserOptional.get());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with the provided id not found"
            );
        }
    }

    @GetMapping("exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.existsById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody UserCommand userCommand) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.updateById(id, userCommand));
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
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT,
                    exception.getMessage()
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            userService.deleteById(id);
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
