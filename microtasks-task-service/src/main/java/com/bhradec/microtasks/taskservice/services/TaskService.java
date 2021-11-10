package com.bhradec.microtasks.taskservice.services;

import com.bhradec.microtasks.taskservice.domain.Task;
import com.bhradec.microtasks.taskservice.dtos.TaskCommandDto;
import com.bhradec.microtasks.taskservice.exceptions.DoesNotRespondException;
import com.bhradec.microtasks.taskservice.exceptions.NotFoundException;
import com.bhradec.microtasks.taskservice.mappers.TaskMapper;
import com.bhradec.microtasks.taskservice.repositories.TaskRepository;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public TaskService(
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {

        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.webClient = webClientBuilder.baseUrl("lb://MICROTASKS-USER-SERVICE").build();
        this.reactiveCircuitBreaker = reactiveCircuitBreakerFactory.create("userServiceCircuitBreaker");
    }

    private Optional<Boolean> userExistsById(Long id) {
        Mono<Boolean> teamExistsMono = reactiveCircuitBreaker.run(
                webClient
                        .get()
                        .uri("/users/{id}/exists", id)
                        .retrieve()
                        .bodyToMono(Boolean.class),
                (throwable) -> Mono.empty());

        return Optional.ofNullable(teamExistsMono.block());
    }

    public Task save(TaskCommandDto taskCommandDto) throws DoesNotRespondException, NotFoundException {
        Task task = taskMapper.mapCommandToTask(taskCommandDto);

        if (userExistsById(task.getUserId()).isEmpty()) {
            throw new DoesNotRespondException("User service does not respond");
        }

        if (!userExistsById(task.getUserId()).get()) {
            throw new NotFoundException("User with the provided id not found");
        }

        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findAllByUserId(Long id) {
        return taskRepository.findAllByUserId(id);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateById(Long id, TaskCommandDto taskCommandDto) throws NotFoundException, DoesNotRespondException {
        Task task = taskMapper.mapCommandToTask(taskCommandDto);

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

        return taskRepository.save(task);
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Tasks with the provided id does not exist");
        }

        taskRepository.deleteById(id);
    }
}
