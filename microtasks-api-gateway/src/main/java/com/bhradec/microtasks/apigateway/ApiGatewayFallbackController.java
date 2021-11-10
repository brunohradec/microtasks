package com.bhradec.microtasks.apigateway;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api-gateway-fallback")
public class ApiGatewayFallbackController {
    @RequestMapping("teamServiceFallback")
    public void teamFallback() {
        throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Team service does not respond");
    }

    @RequestMapping("userServiceFallback")
    public void userFallback() {
        throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "User service does not respond");
    }

    @RequestMapping("taskServiceFallback")
    public void taskFallback() {
        throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Task service does not respond");
    }
}
