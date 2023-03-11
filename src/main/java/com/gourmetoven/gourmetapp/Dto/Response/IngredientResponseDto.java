package com.gourmetoven.gourmetapp.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponseDto {

    private Integer ingredientId;
    private String ingredientName;
    private String type;
}
