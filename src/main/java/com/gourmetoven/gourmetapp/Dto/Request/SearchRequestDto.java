package com.gourmetoven.gourmetapp.Dto.Request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.gourmetoven.gourmetapp.util.Constants.ALL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDto {

    private String type=ALL;
    private Integer servings=999999;
    private List<String> includeIngredients = new ArrayList<>();
    private List<String> excludeIngredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();

}
