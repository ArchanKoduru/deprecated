package com.gourmetoven.gourmetapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ingredients {

private Integer ingredientId;
private String ingredientName;
private Integer typeId;
}
