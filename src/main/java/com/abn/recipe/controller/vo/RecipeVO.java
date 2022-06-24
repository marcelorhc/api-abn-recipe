package com.abn.recipe.controller.vo;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document("recipes")
@Builder(toBuilder = true)
public record RecipeVO(
        @NotBlank
        String name,
        @NotBlank
        String instructions,
        @NotNull
        Boolean isVegetarian,
        @NotNull
        Integer servings,
        @NotEmpty
        List<String> ingredients) {
}
