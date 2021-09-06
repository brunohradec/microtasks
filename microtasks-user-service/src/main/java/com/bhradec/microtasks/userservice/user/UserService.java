package com.bhradec.microtasks.userservice.user;

import com.bhradec.microtasks.userservice.exception.ConflictException;
import com.bhradec.microtasks.userservice.exception.DoesNotRespondException;
import com.bhradec.microtasks.userservice.exception.NotFoundException;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public UserService(
            UserRepository userRepository,
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {

        this.userRepository = userRepository;
        this.webClient = webClientBuilder
                .baseUrl("lb://MICROTASKS-TEAM-SERVICE")
                .build();
        this.reactiveCircuitBreaker = reactiveCircuitBreakerFactory
                .create("teamServiceCircuitBreaker");
    }

    private User mapCommandToUser(UserCommand userCommand) {
        return User
                .builder()
                .firstName(userCommand.getFirstName())
                .lastName(userCommand.getLastName())
                .username(userCommand.getUsername())
                .email(userCommand.getEmail())
                .teamId(userCommand.getTeamId())
                .build();
    }

    private UserDto mapUserToDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .teamId(user.getTeamId())
                .build();
    }

    private Optional<Boolean> teamExistsById(Long id) {
        Mono<Boolean> teamExistsMono = reactiveCircuitBreaker.run(
                webClient
                        .get()
                        .uri("/team/exists/{id}", id)
                        .retrieve()
                        .bodyToMono(Boolean.class),
                (throwable) -> Mono.empty());

        return Optional.ofNullable(teamExistsMono.block());
    }

    public UserDto save(UserCommand userCommand) throws
            ConflictException,
            DoesNotRespondException,
            NotFoundException {

        User user = mapCommandToUser(userCommand);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("User with the provided username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("User with the provided email already exists");
        }

        if (teamExistsById(user.getTeamId()).isEmpty()) {
            throw new DoesNotRespondException("Team service does not respond");
        }

        if (!teamExistsById(user.getTeamId()).get()) {
            throw new NotFoundException("Team with the provided id not found");
        }

        return mapUserToDto(userRepository.save(user));
    }

    public List<UserDto> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> findAllByTeamId(Long id) {
        return userRepository
                .findAllByTeamId(id)
                .stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<UserDto> findById(Long id) {
        return userRepository
                .findById(id)
                .map(this::mapUserToDto);
    }

    public UserDto updateById(Long id, UserCommand userCommand) throws
            NotFoundException,
            DoesNotRespondException,
            ConflictException {

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User with the provided id does not exist"));

        User updatedUser = mapCommandToUser(userCommand);
        updatedUser.setId(user.getId());

        if (!user.getUsername().equals(updatedUser.getUsername())
                && userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("User with the provided username already exists");
        }

        if (!user.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("User with the provided email already exists");
        }

        if (teamExistsById(user.getTeamId()).isEmpty()) {
            throw new DoesNotRespondException("Team service does not respond");
        }

        if (!teamExistsById(user.getTeamId()).get()) {
            throw new NotFoundException("Team with the provided id not found");
        }

        return mapUserToDto(userRepository.save(updatedUser));
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with the provided id does not exist");
        }

        userRepository.deleteById(id);
    }
}
