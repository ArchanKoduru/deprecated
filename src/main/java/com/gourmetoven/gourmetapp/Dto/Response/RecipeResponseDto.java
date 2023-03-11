package com.gourmetoven.gourmetapp.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto {
    private Integer recipeId;
    private String recipeName;
    private Integer servings;
    private String dishType;
    private String creationType;
    private String owningUser;
    List<IngredientResponseDto> ingredients;
    List<String> instructions;
}


