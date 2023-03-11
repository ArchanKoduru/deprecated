package com.gourmetoven.gourmetapp.Dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeCreateRequestDto {
    private Integer recipeId;
    private String name;
    private Integer servings;
    private String dishType;
    List<Integer> ingredients;
    List<String> instructions;
}
