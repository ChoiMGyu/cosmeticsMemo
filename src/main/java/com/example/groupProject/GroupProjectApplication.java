package com.example.groupProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.groupProject.repository")
@EnableMongoRepositories(basePackages = "com.example.groupProject.mongo")
@EnableRedisRepositories(basePackages = "com.example.groupProject.redis")
public class GroupProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupProjectApplication.class, args);
    }

}
