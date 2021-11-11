package com.bhradec.microtasks.userservice.services;

import com.bhradec.microtasks.userservice.domain.User;
import com.bhradec.microtasks.userservice.dtos.UserCommandDto;
import com.bhradec.microtasks.userservice.exceptions.ConflictException;
import com.bhradec.microtasks.userservice.exceptions.DoesNotRespondException;
import com.bhradec.microtasks.userservice.exceptions.NotFoundException;
import com.bhradec.microtasks.userservice.mappers.UserMapper;
import com.bhradec.microtasks.userservice.repositories.UserRepository;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory,
            BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.webClient = webClientBuilder.baseUrl("lb://MICROTASKS-TEAM-SERVICE").build();
        this.reactiveCircuitBreaker = reactiveCircuitBreakerFactory.create("teamServiceCircuitBreaker");
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private Optional<Boolean> teamExistsById(Long id) {
        Mono<Boolean> teamExistsMono = reactiveCircuitBreaker.run(
                webClient
                        .get()
                        .uri("/teams/{id}/exists", id)
                        .retrieve()
                        .bodyToMono(Boolean.class),
                (throwable) -> Mono.empty());

        return Optional.ofNullable(teamExistsMono.block());
    }

    public User save(UserCommandDto userCommandDto) throws
            ConflictException,
            DoesNotRespondException,
            NotFoundException {

        User user = userMapper.mapCommandToUser(userCommandDto);

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

        return userRepository.save(user);
    }

    public Boolean verifyUserPasswordByUsername(String username, String password) throws NotFoundException {
        User foundUser = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with the provided username not found"));

        if (bCryptPasswordEncoder.matches(password, foundUser.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllByTeamId(Long id) {
        return userRepository.findAllByTeamId(id);
    }

    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findFirstByUsername(username);
    }

    public User updateById(Long id, UserCommandDto userCommandDto) throws
            NotFoundException,
            DoesNotRespondException,
            ConflictException {

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User with the provided id does not exist"));

        User updatedUser = userMapper.mapCommandToUser(userCommandDto);
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

        return userRepository.save(updatedUser);
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with the provided id does not exist");
        }

        userRepository.deleteById(id);
    }
}
