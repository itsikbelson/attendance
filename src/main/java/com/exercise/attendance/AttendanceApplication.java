package com.exercise.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AttendanceApplication {

    //TODO add metrics
    //TODO use Karate for end-to-end tests
    //TODO add swagger
    //TODO consider timezones
    //TODO fix whitelabel error
    //TODO return DTO instead of model object
    //TODO consider adding descriptive messages for errors
    //TODO should move integration tests to a different package

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(AttendanceApplication.class, args);
    }

}
