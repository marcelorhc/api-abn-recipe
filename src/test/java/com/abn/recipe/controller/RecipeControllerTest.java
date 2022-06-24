package com.abn.recipe.controller;

import com.abn.recipe.controller.config.IntegrationBaseTest;
import com.abn.recipe.controller.vo.ErrorResponse;
import com.abn.recipe.controller.vo.RecipeVO;
import com.abn.recipe.model.mongo.Recipe;
import com.abn.recipe.repository.mongo.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class RecipeControllerTest extends IntegrationBaseTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void beforeEach() {
        recipeRepository.deleteAll();
    }

    @Test
    void create() {
        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(201);

        var maybeRecipe = recipeRepository.findByName(recipeVO.name());
        assertThat(maybeRecipe.isPresent()).isTrue();

        Recipe recipe = maybeRecipe.get();
        assertThat(recipe.name()).isEqualTo(recipeVO.name());
        assertThat(recipe.instructions()).isEqualTo(recipeVO.instructions());
        assertThat(recipe.isVegetarian()).isEqualTo(recipeVO.isVegetarian());
        assertThat(recipe.servings()).isEqualTo(recipeVO.servings());
        assertThat(recipe.ingredients()).isEqualTo(recipeVO.ingredients());
    }

    @Test
    void createWithoutName() {
        var recipeVO = RecipeVO.builder()
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("name must not be blank");
    }

    @Test
    void createWithoutInstructions() {
        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("instructions must not be blank");
    }

    @Test
    void createWithoutIsVegetarian() {
        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("isVegetarian must not be null");
    }

    @Test
    void createWithoutServings() {
        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("servings must not be null");
    }

    @Test
    void createWithoutIngredients() {
        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("ingredients must not be empty");
    }

    @Test
    void createWithExistingName() {
        var recipe = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        recipeRepository.save(recipe);

        var recipeVO = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .post("/v1/recipe")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("Recipe already exists");

        var allRecipes = recipeRepository.findAll();
        assertThat(allRecipes.size()).isEqualTo(1);
    }

    @Test
    void update() {
        var recipe = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipeSaved = recipeRepository.save(recipe);

        var recipeVO = RecipeVO.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .put("/v1/recipe/" + recipeSaved.id())
                .then()
                .statusCode(200);

        var maybeRecipe = recipeRepository.findByName(recipeVO.name());
        assertThat(maybeRecipe.isPresent()).isTrue();

        Recipe recipeUpdated = maybeRecipe.get();
        assertThat(recipeUpdated.name()).isEqualTo(recipeVO.name());
        assertThat(recipeUpdated.instructions()).isEqualTo(recipeVO.instructions());
        assertThat(recipeUpdated.isVegetarian()).isEqualTo(recipeVO.isVegetarian());
        assertThat(recipeUpdated.servings()).isEqualTo(recipeVO.servings());
        assertThat(recipeUpdated.ingredients()).isEqualTo(recipeVO.ingredients());
    }

    @Test
    void updateWithInvalidId() {
        var recipe = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipeSaved = recipeRepository.save(recipe);

        var recipeVO = RecipeVO.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        ErrorResponse errorResponse = given()
                .body(recipeVO)
                .header("Content-type", "application/json")
                .when()
                .put("/v1/recipe/invalid")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("Recipe doesn't exist");

        var maybeRecipe = recipeRepository.findByName(recipe.name());
        assertThat(maybeRecipe.isPresent()).isTrue();

        Recipe recipeNotUpdated = maybeRecipe.get();
        assertThat(recipeNotUpdated.name()).isEqualTo(recipe.name());
        assertThat(recipeNotUpdated.instructions()).isEqualTo(recipe.instructions());
        assertThat(recipeNotUpdated.isVegetarian()).isEqualTo(recipe.isVegetarian());
        assertThat(recipeNotUpdated.servings()).isEqualTo(recipe.servings());
        assertThat(recipeNotUpdated.ingredients()).isEqualTo(recipe.ingredients());
    }

    @Test
    void findAll() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe/all")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(2);
    }

    @Test
    void remove() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipeSaved = recipeRepository.save(recipe1);

        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/v1/recipe/" + recipeSaved.id())
                .then()
                .statusCode(200);

        var allRecipes = recipeRepository.findAll();
        assertThat(allRecipes.isEmpty()).isTrue();
    }

    @Test
    void findByIsVegetarian() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?isVegetarian=false")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.isVegetarian()).isFalse();
        assertThat(recipeFound.name()).isEqualTo(recipe1.name());
    }

    @Test
    void findByInstructions() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?instruction=oven")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.instructions()).contains("oven");
        assertThat(recipeFound.name()).isEqualTo(recipe1.name());
    }

    @Test
    void findByServings() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?servings=4")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.servings()).isEqualTo(4);
        assertThat(recipeFound.name()).isEqualTo(recipe2.name());
    }

    @Test
    void findByWithSpecificIngredients() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?includeIngredient=carrot")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.ingredients()).contains("carrot");
        assertThat(recipeFound.name()).isEqualTo(recipe2.name());
    }

    @Test
    void findByWithoutSpecificIngredients() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?excludeIngredient=carrot")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.ingredients()).doesNotContain("carrot");
        assertThat(recipeFound.name()).isEqualTo(recipe1.name());
    }

    @Test
    void findByWithSpecificIngredientsAndServings() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?excludeIngredient=carrot&servings=2")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(1);

        RecipeVO recipeFound = recipes.get(0);
        assertThat(recipeFound.ingredients()).doesNotContain("carrot");
        assertThat(recipeFound.servings()).isEqualTo(2);
        assertThat(recipeFound.name()).isEqualTo(recipe1.name());
    }

    @Test
    void findByWithInvalidParameters() {
        var recipe1 = Recipe.builder()
                .name("salmon recipe")
                .instructions("put on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        var recipe2 = Recipe.builder()
                .name("vegetables recipe")
                .instructions("fry on the pan")
                .isVegetarian(true)
                .servings(4)
                .ingredients(List.of("carrot", "potatoes"))
                .build();

        recipeRepository.saveAll(List.of(recipe1, recipe2));

        var recipesArray = given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/recipe?servings=1")
                .then()
                .statusCode(200)
                .extract()
                .as(RecipeVO[].class);

        var recipes = Arrays.asList(recipesArray);
        assertThat(recipes.size()).isEqualTo(0);
    }


}
