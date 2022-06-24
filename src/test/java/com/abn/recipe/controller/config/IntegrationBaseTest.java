package com.abn.recipe.controller.config;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class IntegrationBaseTest {

    protected static Long producerId;

    @BeforeAll
    public static void before() {
        RestAssured.baseURI = "http://localhost:7777/api";
    }

}
