# api-abn-recipe by Marcelo Haddad


### Stack
- Java 17
- Spring boot 2.6
- Mongo
- Junit5
- Spring test

### Instructions
This is a spring boot application, you can run with maven or using Intellij.

Before running install:
- Docker
- Java 17
- Maven

Running Docker image: docker-compose up (The docker-compose file is in the root of this directory)

Running using maven: mvn spring-boot:run

Running tests maven: mvn test

Swagger documentation: http://localhost:8080/api/swagger-ui/index.html

This is a json to create a recipe to help test the application.
```json
{
  "name" : "Salmon on oven",
  "instructions" : "Salmon on ovenSalmon on ovenSalmon on ovenSalmon on ovenSalmon on oven",
  "isVegetarian" : false,
  "servings" : 2,
  "ingredients" : ["salmon", "potatoes"]
}
```

### Post note
The swagger documentation could be better, sorry about that but I had a busy week and visitors at my apartment.