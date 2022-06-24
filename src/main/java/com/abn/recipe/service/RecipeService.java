package com.abn.recipe.service;

import com.abn.recipe.controller.vo.RecipeVO;
import com.abn.recipe.exception.ResourceAlreadyExistException;
import com.abn.recipe.exception.ResourceNotFoundException;
import com.abn.recipe.model.mongo.Recipe;
import com.abn.recipe.repository.mongo.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final MongoTemplate mongoTemplate;

    public Recipe create(RecipeVO recipeVO) {
        Optional<Recipe> maybeRecipe = recipeRepository.findByName(recipeVO.name());
        if (maybeRecipe.isPresent()) {
            throw new ResourceAlreadyExistException("Recipe already exists");
        }

        log.info("Creating recipe: {}", recipeVO);

        Recipe recipe = Recipe.builder()
                .name(recipeVO.name())
                .instructions(recipeVO.instructions())
                .isVegetarian(recipeVO.isVegetarian())
                .servings(recipeVO.servings())
                .ingredients(recipeVO.ingredients())
                .build();

        Recipe recipeCreated = recipeRepository.save(recipe);

        log.info("Recipe created with name {}", recipeCreated.name());

        return recipeCreated;
    }

    public void update(String id, RecipeVO recipeVO) {
        Optional<Recipe> maybeRecipe = recipeRepository.findById(id);
        if (maybeRecipe.isEmpty()) {
            throw new ResourceNotFoundException("Recipe doesn't exist");
        }

        log.info("Updating recipe: {}", recipeVO.name());

        Recipe recipe = maybeRecipe.get()
                .toBuilder()
                .name(recipeVO.name())
                .instructions(recipeVO.instructions())
                .isVegetarian(recipeVO.isVegetarian())
                .servings(recipeVO.servings())
                .ingredients(recipeVO.ingredients())
                .build();

        recipeRepository.save(recipe);

        log.info("Recipe updated {}", recipe.name());
    }

    public void remove(String id) {
        log.info("Removing recipe: {}", id);

        recipeRepository.deleteById(id);

        log.info("Recipe {} removed", id);
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public List<Recipe> find(Boolean isVegetarian, Integer servings, String includeIngredient, String excludeIngredient, String instruction) {
        Query query = new Query();
        if (Objects.nonNull(isVegetarian)) {
            query.addCriteria(where("isVegetarian").is(isVegetarian));
        }
        if (Objects.nonNull(servings)) {
            query.addCriteria(where("servings").is(servings));
        }
        if (Objects.nonNull(includeIngredient)) {
            query.addCriteria(where("ingredients").in(includeIngredient));
        }
        if (Objects.nonNull(excludeIngredient)) {
            query.addCriteria(where("ingredients").not().in(excludeIngredient));
        }
        if (Objects.nonNull(instruction)) {
            query.addCriteria(where("instructions").regex(instruction).regex(instruction));
        }

        return mongoTemplate.find(query, Recipe.class);
    }

}
