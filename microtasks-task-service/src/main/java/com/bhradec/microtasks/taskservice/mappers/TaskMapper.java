package com.bhradec.microtasks.taskservice.mappers;

import com.bhradec.microtasks.taskservice.domain.Task;
import com.bhradec.microtasks.taskservice.dtos.TaskCommandDto;
import com.bhradec.microtasks.taskservice.dtos.TaskDto;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task mapCommandToTask(TaskCommandDto taskCommandDto) {
        return Task
                .builder()
                .name(taskCommandDto.getName())
                .description(taskCommandDto.getDescription())
                .startTime(taskCommandDto.getStartTime())
                .deadline(taskCommandDto.getDeadline())
                .userId(taskCommandDto.getUserId())
                .build();
    }

    public TaskDto mapTaskToDto(Task task) {
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
}
