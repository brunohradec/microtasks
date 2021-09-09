# Microtasks

A simple task management application with microservice architecture.

## API Gateway Service

### Microtasks API Gateway

All request for the services come to the API gateway and are redirected to 
the corresponding services. The API gateway is configured to run on port `5050`. 
The API Gateway and the routing to the services is implemented using 
[Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway).

### Circuit breaker

API Gateway has a circuit breaker. The circuit breaker redirects the 
requests if the requested service is unavailable. URL-s that the circuit
breaker redirects to if the requested service is unavailable are defined
in the `ApiGatewayFallbackController` class. The requested service 
is considered unavailable if the service does not respond in 10 seconds
(HTTP status `REQUEST_TIMEOUT` and response with the error message description
is returned). Circuit breaker is implemented using Resilience4j which is a part of
[Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker).

### REST API Description

Look at the REST API Description for each service and replace the port number
with `5050`. If the service can't be reached, object containing the error
description message is returned with the status `REQUEST_TIMEOUT`.

## Microtasks Service Registry 

The service registry is a database containing the network locations of all 
microservices. It also tracks service status (up or down). All services have
a name assigned to them, so they don't have to be addressed by IP addresses 
and port numbers. It is configured to run on port `8761`. Service registry is 
implemented using Netflix Eureka Server which is a part of 
[Spring Cloud Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)

## Distributed logging

All services use [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)
and Spring Cloud Zipkin for distributed logging. Logs are sent to Zipkin server that
is expected to run on port `9411`. More about running Zipkin server can be found in 
the [Zipkin quickstart guide](https://zipkin.io/pages/quickstart.html).

## Microtasks Team Service

Team service saves, finds, updates and deletes data about application teams.
It is configured to run on port `5001`. In other services, the team service
is addressed using the name `MICROTASKS-TEAM-SERVICE`.

### Team Service API Description

<table>
    <tr>
        <th>Description</th>
        <th>Method</th>
        <th>URL</th>
        <th>Request body</th>
        <th>Success</th>
        <th>Fail</th>
    </tr>
    <tr>
        <td>Add new team</td>
        <td>POST</td>
        <td>http://localhost:5001/team</td>
        <td>TeamCommand</td>
        <td>TeamDto and CREATED</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Find all teams</td>
        <td>GET</td>
        <td>http://localhost:5001/team</td>
        <td>None</td>
        <td>TeamDto array and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Find team by ID</td>
        <td>GET</td>
        <td>http://localhost:5001/team/{ID}</td>
        <td>None</td>
        <td>TeamDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Does team with the provided ID exist</td>
        <td>GET</td>
        <td>http://localhost:5001/team/exists/{ID}</td>
        <td>None</td>
        <td>Boolean and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Update team by ID</td>
        <td>PUT</td>
        <td>http://localhost:5001/team/{ID}</td>
        <td>TeamCommand</td>
        <td>TeamDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Delete team by ID</td>
        <td>DELETE</td>
        <td>http://localhost:5001/team/{ID}</td>
        <td>None</td>
        <td>None and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
</table>

### Team Command

```Java
public class TeamCommand {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank or empty")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank or empty")
    private String description;
}
```

### Team DTO

```Java
public class TeamDto {
    private Long id;
    private String name;
    private String description;
}
```
## Microtasks User Service

User service saves, finds, updates and deletes data about application users.
It is configured to run on port `5002`. In other services, the user service
is addressed using the name `MICROTASKS-USER-SERVICE`. User service has a 
circuit breaker in case the team service does not respond (HTTP status 
REQUEST_TIMEOUT and response with the error message description is returned).

### User Service API Description

<table>
    <tr>
        <th>Description</th>
        <th>Method</th>
        <th>URL</th>
        <th>Request body</th>
        <th>Success</th>
        <th>Fail</th>
    </tr>
    <tr>
        <td>Add new user</td>
        <td>POST</td>
        <td>http://localhost:5002/user</td>
        <td>UserCommand</td>
        <td>UserDto and CREATED</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Find all users</td>
        <td>GET</td>
        <td>http://localhost:5002/user</td>
        <td>None</td>
        <td>UserDto array and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Find user by ID</td>
        <td>GET</td>
        <td>http://localhost:5002/user/{ID}</td>
        <td>None</td>
        <td>UserDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Find all users by team ID</td>
        <td>GET</td>
        <td>http://localhost:5002/user/teamId={ID}</td>
        <td>None</td>
        <td>UserDto array and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Does user with the provided ID exist</td>
        <td>GET</td>
        <td>http://localhost:5002/user/exists/{ID}</td>
        <td>None</td>
        <td>Boolean and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Update user by ID</td>
        <td>PUT</td>
        <td>http://localhost:5002/user/{ID}</td>
        <td>UserCommand</td>
        <td>UserDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Delete user by ID</td>
        <td>DELETE</td>
        <td>http://localhost:5002/user/{ID}</td>
        <td>None</td>
        <td>None and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
</table>

### User Command

```Java
public class UserCommand {
    @NotNull(message = "First name must not be null")
    @NotBlank(message = "First name must not be blank or empty")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @NotBlank(message = "Last name must not be blank or empty")
    private String lastName;

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank or empty")
    private String username;

    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank or empty")
    @Email(message = "Email must be a valid email adress")
    private String email;

    @NotNull(message = "Team id must not be null")
    private Long teamId;
}
```

### User DTO

```Java
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Long teamId;
}
```

## Microtasks Task Service

Task service saves, finds, updates and deletes data about application tasks.
It is configured to run on port `5003`. In other services, the task service
is addressed using the name `MICROTASKS-TASK-SERVICE`. Task service has a 
circuit breaker in case the user service does not respond (HTTP status 
REQUEST_TIMEOUT and response with the error message description is returned).

### Task Service API Description

<table>
    <tr>
        <th>Description</th>
        <th>Method</th>
        <th>URL</th>
        <th>Request body</th>
        <th>Success</th>
        <th>Fail</th>
    </tr>
    <tr>
        <td>Add new task</td>
        <td>POST</td>
        <td>http://localhost:5003/task</td>
        <td>TaskCommand</td>
        <td>TaskDto and CREATED</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Find all tasks</td>
        <td>GET</td>
        <td>http://localhost:5003/task</td>
        <td>None</td>
        <td>TaskDto array and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Find task by ID</td>
        <td>GET</td>
        <td>http://localhost:5003/task/{ID}</td>
        <td>None</td>
        <td>TaskDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Find all tasks by user ID</td>
        <td>GET</td>
        <td>http://localhost:5003/task/userId={ID}</td>
        <td>None</td>
        <td>TaskDto array and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Does task with the provided ID exist</td>
        <td>GET</td>
        <td>http://localhost:5003/task/exists/{ID}</td>
        <td>None</td>
        <td>Boolean and OK</td>
        <td>None</td>
    </tr>
    <tr>
        <td>Update task by ID</td>
        <td>PUT</td>
        <td>http://localhost:5003/task/{ID}</td>
        <td>TaskCommand</td>
        <td>TaskDto and OK</td>
        <td>
            Object containing the message with the error cause and 
            CONFLICT or NOT_FOUND
        </td>
    </tr>
    <tr>
        <td>Delete task by ID</td>
        <td>DELETE</td>
        <td>http://localhost:5003/task/{ID}</td>
        <td>None</td>
        <td>None and OK</td>
        <td>
            Object containing the message with the error cause and 
            NOT_FOUND
        </td>
    </tr>
</table>

### Task Command

```Java
public class TaskCommand {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Start time must not be null")
    private Date startTime;

    @NotNull(message = "Deadline must not be null")
    private Date deadline;

    @NotNull(message = "User id must not be null")
    private Long userId;
}
```

### Task DTO

```Java
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private Date startTime;
    private Date deadline;
    private Long userId;
}
```
