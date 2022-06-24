package com.abn.recipe.controller.builder;

import com.abn.recipe.controller.vo.RecipeVO;
import com.abn.recipe.model.mongo.Recipe;

public class RecipeVOBuilder {

    public static RecipeVO build(Recipe recipe) {
        return RecipeVO.builder()
                .name(recipe.name())
                .instructions(recipe.instructions())
                .isVegetarian(recipe.isVegetarian())
                .servings(recipe.servings())
                .ingredients(recipe.ingredients())
                .build();
    }
}
