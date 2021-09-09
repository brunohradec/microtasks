package com.bhradec.microtasks.apigateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@SpringBootApplication
@EnableEurekaClient
public class MicrotasksApiGatewayApplication {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerCustomizer() {
        return (factory) -> factory
                .configureDefault((id) -> new Resilience4JConfigBuilder(id)
                        .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                        .timeLimiterConfig(TimeLimiterConfig
                                .custom()
                                .timeoutDuration(Duration.ofSeconds(10))
                                .build())
                        .build());
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route((predicateSpec) -> predicateSpec
                        .path("/team/**")
                        .filters((gatewayFilterSpec) -> gatewayFilterSpec
                                .circuitBreaker((config) -> config
                                        .setName("defaultCircuitBreaker")
                                        .setFallbackUri("forward:/api-gateway-fallback/teamServiceFallback")))
                        .uri("lb://MICROTASKS-TEAM-SERVICE"))
                .route((predicateSpec) -> predicateSpec
                        .path("/user/**")
                        .filters((gatewayFilterSpec) -> gatewayFilterSpec
                                .circuitBreaker((config) -> config
                                        .setName("defaultCircuitBreaker")
                                        .setFallbackUri("forward:/api-gateway-fallback/userServiceFallback")))
                        .uri("lb://MICROTASKS-USER-SERVICE"))
                .route((predicateSpec) -> predicateSpec
                        .path("/task/**")
                        .filters((gatewayFilterSpec) -> gatewayFilterSpec
                                .circuitBreaker((config) -> config
                                        .setName("defaultCircuitBreaker")
                                        .setFallbackUri("forward:/api-gateway-fallback/taskServiceFallback")))
                        .uri("lb://MICROTASKS-TASK-SERVICE"))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MicrotasksApiGatewayApplication.class, args);
    }
}
