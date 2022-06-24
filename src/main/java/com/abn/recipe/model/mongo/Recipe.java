package com.abn.recipe.model.mongo;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("recipes")
@Builder(toBuilder = true)
public record Recipe(@Id String id,
                     String name,
                     String instructions,
                     boolean isVegetarian,
                     int servings,
                     List<String> ingredients) {
}
