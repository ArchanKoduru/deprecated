package com.gourmetoven.gourmetapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RecipeInstructionMappingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getAllInstructionsForArecipe(Integer recipeID) {
        String sql = "SELECT instruction from recipe_instruction_mapping where recipe_id =  ?";
        return jdbcTemplate.query(sql, new RIRowMapper(), recipeID);
    }

    public void deleteRecipeMapping(Integer recipeID)
    {
        String deleteQuery = "DELETE FROM recipe_instruction_mapping where recipe_id = ?";
        jdbcTemplate.update(deleteQuery, recipeID);
    }
    public void addInstructionsToRecipe(Integer recipeId, List<String> instructions) {
        String sql = "INSERT INTO recipe_instruction_mapping (recipe_id,instruction) values (?,?)";
        instructions.forEach(p -> jdbcTemplate.update(sql,recipeId,p));
    }

    private class RIRowMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getString("instruction");
        }
    }
}

