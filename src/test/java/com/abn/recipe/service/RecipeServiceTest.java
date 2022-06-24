package com.abn.recipe.service;

import com.abn.recipe.controller.vo.RecipeVO;
import com.abn.recipe.exception.ResourceAlreadyExistException;
import com.abn.recipe.exception.ResourceNotFoundException;
import com.abn.recipe.model.mongo.Recipe;
import com.abn.recipe.repository.mongo.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Captor
    private ArgumentCaptor<Recipe> recipeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Query> queryArgumentCaptor;

    @Test
    public void givenExistingRecipeWhenRemoveThenIsDeleted() {
        recipeService.remove("123");

        verify(recipeRepository, times(1)).deleteById(anyString());
    }

    @Test
    public void givenExistingRecipesWhenFindAllThenRecipesAreReturned() {
        Recipe salmonRecipe = Recipe.builder().name("salmon recipe").build();
        when(recipeRepository.findAll()).thenReturn(List.of(salmonRecipe));

        List<Recipe> allRecipes = recipeService.findAll();

        assertThat(allRecipes).hasSize(1);

        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void givenNonExistingRecipeWhenCreateThenRecipesIsCreated() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(Recipe.builder().id("123").build());

        RecipeVO recipe = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put the salmon on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        recipeService.create(recipe);

        verify(recipeRepository, times(1)).save(recipeArgumentCaptor.capture());

        Recipe recipeParameter = recipeArgumentCaptor.getValue();
        assertThat(recipeParameter).isNotNull();
        assertThat(recipeParameter.name()).isEqualTo(recipe.name());
        assertThat(recipeParameter.instructions()).isEqualTo(recipe.instructions());
        assertThat(recipeParameter.isVegetarian()).isEqualTo(recipe.isVegetarian());
        assertThat(recipeParameter.servings()).isEqualTo(recipe.servings());
        assertThat(recipeParameter.ingredients()).isEqualTo(recipe.ingredients());
    }

    @Test
    public void givenExistingRecipeWhenCreateThenRecipesIsNotCreated() {
        Recipe existingRecipe = Recipe.builder().id("123").name("salmon recipe").build();
        when(recipeRepository.findByName(anyString())).thenReturn(Optional.of(existingRecipe));

        RecipeVO recipe = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put the salmon on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        assertThrows(
                ResourceAlreadyExistException.class,
                () -> recipeService.create(recipe),
                "Recipe already exists");

        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }

    @Test
    public void givenExistingRecipeWhenUpdateThenRecipeIsUpdated() {
        String id = "123";
        Recipe existingRecipe = Recipe.builder().id(id).build();
        when(recipeRepository.findById(anyString())).thenReturn(Optional.of(existingRecipe));

        RecipeVO recipe = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put the salmon on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        recipeService.update(id, recipe);

        verify(recipeRepository, times(1)).save(recipeArgumentCaptor.capture());

        Recipe recipeParameter = recipeArgumentCaptor.getValue();
        assertThat(recipeParameter).isNotNull();
        assertThat(recipeParameter.name()).isEqualTo(recipe.name());
        assertThat(recipeParameter.instructions()).isEqualTo(recipe.instructions());
        assertThat(recipeParameter.isVegetarian()).isEqualTo(recipe.isVegetarian());
        assertThat(recipeParameter.servings()).isEqualTo(recipe.servings());
        assertThat(recipeParameter.ingredients()).isEqualTo(recipe.ingredients());
    }

    @Test
    public void givenNonExistingRecipeWhenUpdateThenRecipeIsNotUpdated() {
        RecipeVO recipe = RecipeVO.builder()
                .name("salmon recipe")
                .instructions("put the salmon on the oven")
                .isVegetarian(false)
                .servings(2)
                .ingredients(List.of("salmon", "potatoes"))
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> recipeService.update("123", recipe),
                "Recipe doesn't exist");

        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }

    @Test
    public void givenNonParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(false, null, null, null, null);

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"isVegetarian\" : false");
    }

    @Test
    public void givenIsVegetarianParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(false, null, null, null, null);

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"isVegetarian\" : false");
    }

    @Test
    public void givenServingsParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(null, 1, null, null, null);

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"servings\" : 1");
    }

    @Test
    public void givenIncludedIngredientParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(null, null, "potatoes", null, null);

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"ingredients\" : { \"$in\" : [\"potatoes\"]");
    }

    @Test
    public void givenExcludedIngredientParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(null, null, null, "potatoes", null);

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"ingredients\" : { \"$not\" : { \"$in\" : [\"potatoes\"]");
    }

    @Test
    public void givenInstructionParametersWhenFindRecipeThenRecipeIsCorrectFiltered() {
        recipeService.find(null, null, null, null, "oven");

        verify(mongoTemplate, times(1)).find(queryArgumentCaptor.capture(), eq(Recipe.class));

        Query query = queryArgumentCaptor.getValue();
        String queryString = query.toString();
        assertThat(queryString).contains("\"instructions\" : { \"$regularExpression\" : { \"pattern\" : \"oven\"");
    }
}
