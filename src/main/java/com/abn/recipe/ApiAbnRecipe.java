package com.abn.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class ApiAbnRecipe {
    public static void main(String[] args) {
        SpringApplication.run(ApiAbnRecipe.class, args);
    }
}
