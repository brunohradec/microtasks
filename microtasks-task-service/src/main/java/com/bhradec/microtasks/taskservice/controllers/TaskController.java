package com.bhradec.microtasks.taskservice.controllers;

import com.bhradec.microtasks.taskservice.dtos.TaskCommandDto;
import com.bhradec.microtasks.taskservice.dtos.TaskDto;
import com.bhradec.microtasks.taskservice.exceptions.DoesNotRespondException;
import com.bhradec.microtasks.taskservice.exceptions.NotFoundException;
import com.bhradec.microtasks.taskservice.mappers.TaskMapper;
import com.bhradec.microtasks.taskservice.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    public ResponseEntity<TaskDto> save(@Valid @RequestBody TaskCommandDto taskCommandDto) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(taskMapper.mapTaskToDto(taskService.save(taskCommandDto)));
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, exception.getMessage());
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService
                        .findAll()
                        .stream()
                        .map(taskMapper::mapTaskToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<TaskDto>> findAllByUserId(@RequestParam Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService
                        .findAllByUserId(userId)
                        .stream()
                        .map(taskMapper::mapTaskToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        TaskDto foundTaskDto = taskService
                .findById(id)
                .map(taskMapper::mapTaskToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task with the provided id not found"));

        return ResponseEntity.status(HttpStatus.OK).body(foundTaskDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<TaskDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody TaskCommandDto taskCommandDto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(taskMapper.mapTaskToDto(taskService.updateById(id, taskCommandDto)));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (DoesNotRespondException exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, exception.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            taskService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
