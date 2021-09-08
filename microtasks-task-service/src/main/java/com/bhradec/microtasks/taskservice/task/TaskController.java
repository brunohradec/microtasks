package com.bhradec.microtasks.taskservice.task;

import com.bhradec.microtasks.taskservice.exception.DoesNotRespondException;
import com.bhradec.microtasks.taskservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("task")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDto> save(@Valid @RequestBody TaskCommand taskCommand) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(taskService.save(taskCommand));
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT,
                    exception.getMessage()
            );
        } catch(NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.findAll());
    }

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<TaskDto>> findAllByUserId(@RequestParam Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.findAllByUserId(userId));
    }

    @GetMapping("{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        Optional<TaskDto> foundTaskOptional = taskService.findById(id);

        if (foundTaskOptional.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foundTaskOptional.get());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task with the provided id not found"
            );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<TaskDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody TaskCommand taskCommand) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(taskService.updateById(id, taskCommand));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
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
            taskService.deleteById(id);
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
