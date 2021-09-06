package com.bhradec.microtasks.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MicrotasksServiceRegistryApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicrotasksServiceRegistryApplication.class, args);
	}
}
