package com.gourmetoven.gourmetapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recipe {

    private Integer recipeId;
    private String recipeName;
    private Integer servings;
    private Integer dishType;
    private Integer creationType;
    private Integer owningUser;
}
