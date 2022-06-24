package com.abn.recipe.repository.mongo;

import com.abn.recipe.model.mongo.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecipeRepository extends MongoRepository<Recipe, String> {

    Optional<Recipe> findByName(String name);
}
