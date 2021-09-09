package com.bhradec.microtasks.apigateway.scheduler;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

public class LogStatusJob extends QuartzJobBean {
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    private final Logger logger = LoggerFactory.getLogger(LogStatusJob.class);

    private Mono<StatusDto> serviceHealthCheck(String servicePath) {
        return reactiveCircuitBreaker.run(
                webClient
                        .get()
                        .uri(servicePath + "/actuator/health")
                        .retrieve()
                        .bodyToMono(StatusDto.class),
                (throwable) -> Mono.just(((new StatusDto("UNREACHABLE")))));
    }

    public LogStatusJob(
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {

        this.webClient = webClientBuilder.build();
        this.reactiveCircuitBreaker = reactiveCircuitBreakerFactory
                .create("logStatusJobCircuitBreaker");
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("Current date and time: " + (new Date()));

        serviceHealthCheck("lb://MICROTASKS-TEAM-SERVICE").subscribe(
                (result) -> logger.info("Team service: " + result.getStatus()),
                (error) -> logger.error("Error getting team service status")
        );

        serviceHealthCheck("lb://MICROTASKS-USER-SERVICE").subscribe(
                (result) -> logger.info("User service: " + result.getStatus()),
                (error) -> logger.error("Error getting user service status")
        );

        serviceHealthCheck("lb://MICROTASKS-TASK-SERVICE").subscribe(
                (result) -> logger.info("Task service: " + result.getStatus()),
                (error) -> logger.error("Error getting task service status")
        );
    }
}
