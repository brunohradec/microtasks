package com.bhradec.microtasks.apigateway.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {
    @Bean
    public JobDetail logStatusJobDetail() {
        return JobBuilder
                .newJob(LogStatusJob.class)
                .withIdentity("logStatusJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger logStatusJobTrigger() {
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(30)
                .repeatForever();

        return TriggerBuilder
                .newTrigger()
                .forJob(logStatusJobDetail())
                .withIdentity("logStatusJobTrigger")
                .withSchedule(simpleScheduleBuilder)
                .build();
    }
}
