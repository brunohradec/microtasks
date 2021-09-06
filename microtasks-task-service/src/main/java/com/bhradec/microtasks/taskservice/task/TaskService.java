package com.bhradec.microtasks.taskservice.task;

import com.bhradec.microtasks.taskservice.exception.DoesNotRespondException;
import com.bhradec.microtasks.taskservice.exception.NotFoundException;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public TaskService(
            TaskRepository taskRepository,
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {

        this.taskRepository = taskRepository;
        this.webClient = webClientBuilder
                .baseUrl("lb://MICROTASKS-USER-SERVICE")
                .build();
        this.reactiveCircuitBreaker = reactiveCircuitBreakerFactory
                .create("userServiceCircuitBreaker");
    }

    private Task mapCommandToTask(TaskCommand taskCommand) {
        return Task
                .builder()
                .name(taskCommand.getName())
                .description(taskCommand.getDescription())
                .startTime(taskCommand.getStartTime())
                .deadline(taskCommand.getDeadline())
                .userId(taskCommand.getUserId())
                .build();
    }

    private TaskDto mapTaskToDto(Task task) {
        return TaskDto
                .builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .startTime(task.getStartTime())
                .deadline(task.getDeadline())
                .userId(task.getUserId())
                .build();
    }

    private Optional<Boolean> userExistsById(Long id) {
        Mono<Boolean> teamExistsMono = reactiveCircuitBreaker.run(
                webClient
                        .get()
                        .uri("/user/exists/{id}", id)
                        .retrieve()
                        .bodyToMono(Boolean.class),
                (throwable) -> Mono.empty());

        return Optional.ofNullable(teamExistsMono.block());
    }

    public TaskDto save(TaskCommand taskCommand) throws
            DoesNotRespondException,
            NotFoundException {

        Task task = mapCommandToTask(taskCommand);

        if (userExistsById(task.getUserId()).isEmpty()) {
            throw new DoesNotRespondException("User service does not respond");
        }

        if (!userExistsById(task.getUserId()).get()) {
            throw new NotFoundException("User with the provided id not found");
        }

        return mapTaskToDto(taskRepository.save(mapCommandToTask(taskCommand)));
    }

    public List<TaskDto> findAll() {
        return taskRepository
                .findAll()
                .stream()
                .map(this::mapTaskToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> findAllByUserId(Long id) {
        return taskRepository
                .findAllByUserId(id)
                .stream()
                .map(this::mapTaskToDto)
                .collect(Collectors.toList());
    }

    public Optional<TaskDto> findById(Long id) {
        return taskRepository
                .findById(id)
                .map(this::mapTaskToDto);
    }

    public TaskDto updateById(Long id, TaskCommand taskCommand) throws
            NotFoundException,
            DoesNotRespondException {

        Task task = mapCommandToTask(taskCommand);

        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Tasks with the provided id does not exist");
        }

        if (userExistsById(task.getUserId()).isEmpty()) {
            throw new DoesNotRespondException("User service does not respond");
        }

        if (!userExistsById(task.getUserId()).get()) {
            throw new NotFoundException("User with the provided id not found");
        }

        task.setId(id);

        return mapTaskToDto(taskRepository.save(task));
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Tasks with the provided id does not exist");
        }

        taskRepository.deleteById(id);
    }
}
