package com.abn.recipe.controller;

import com.abn.recipe.controller.builder.RecipeVOBuilder;
import com.abn.recipe.controller.vo.RecipeVO;
import com.abn.recipe.model.mongo.Recipe;
import com.abn.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Recipes", description = "Management of recipes")
@RestController
@RequestMapping("/v1/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(method = "Get all recipes", summary = "Get all recipes")
    @GetMapping("/all")
    public List<RecipeVO> getAllRecipes() {
        return recipeService.findAll()
                .stream()
                .map(RecipeVOBuilder::build)
                .toList();
    }

    @Operation(method = "Get recipes by filters", summary = "Get recipes by filters")
    @GetMapping
    public List<RecipeVO> getRecipes(@RequestParam(required = false) Boolean isVegetarian,
                                     @RequestParam(required = false) Integer servings,
                                     @RequestParam(required = false) String includeIngredient,
                                     @RequestParam(required = false) String excludeIngredient,
                                     @RequestParam(required = false) String instruction) {
        return recipeService.find(isVegetarian, servings, includeIngredient, excludeIngredient, instruction)
                .stream()
                .map(RecipeVOBuilder::build)
                .toList();
    }

    @Operation(method = "Create a recipe", summary = "Create a recipe")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createRecipe(@RequestBody @Valid RecipeVO recipeVO) {
        Recipe recipe = recipeService.create(recipeVO);
        return "Recipe created id " + recipe.id();
    }

    @Operation(method = "Update a recipe", summary = "Update a recipe")
    @PutMapping("/{id}")
    public String updateRecipe(@PathVariable String id, @RequestBody @Valid RecipeVO recipeVO) {
        recipeService.update(id, recipeVO);
        return "Recipe updated";
    }

    @Operation(method = "Delete a recipe", summary = "Delete a recipe")
    @DeleteMapping("/{id}")
    public String removeRecipe(@PathVariable String id) {
        recipeService.remove(id);
        return "Recipe deleted";
    }
}
