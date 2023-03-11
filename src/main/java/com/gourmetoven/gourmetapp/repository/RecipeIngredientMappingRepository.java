package com.gourmetoven.gourmetapp.repository;

import com.gourmetoven.gourmetapp.entity.Ingredients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecipeIngredientMappingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Integer> getAllIngredientForArecipe(Integer recipeID) {
        String sql = "SELECT ingredient_id from recipe_ingredient_mapping where recipe_id =  ?";
        return jdbcTemplate.query(sql, new RIRowMapper(), recipeID);
    }

    public void deleteRecipeMapping(Integer recipeID)
    {
        String deleteQuery = "DELETE FROM recipe_ingredient_mapping where recipe_id = ?";
        jdbcTemplate.update(deleteQuery, recipeID);
    }
    public void addIngredientsToRecipe(Integer recipeId, List<Integer> ingredients) {
        String sql = "INSERT INTO recipe_ingredient_mapping (ingredient_id,recipe_id) values (?,?)";
        ingredients.forEach(p -> jdbcTemplate.update(sql,p,recipeId));
    }

    private class RIRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int i) throws SQLException {
            int ingId = rs.getInt("ingredient_id");
           return ingId;
        }
    }
}

