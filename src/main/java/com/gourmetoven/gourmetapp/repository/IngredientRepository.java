package com.gourmetoven.gourmetapp.repository;

import com.gourmetoven.gourmetapp.entity.Ingredients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class IngredientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Ingredients> findAll() {
        return jdbcTemplate.query("SELECT * from ingredients", BeanPropertyRowMapper.newInstance(Ingredients.class));
    }

    public List<Ingredients> findAll(List<Integer> ingredientId) {
        String inSql = String.join(",", Collections.nCopies(ingredientId.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM ingredients WHERE ingredient_id IN (%s)", inSql),
                ingredientId.toArray(),
                (rs, rowNum) -> new Ingredients(rs.getInt("ingredient_id"), rs.getString("ingredient_name"),
                        rs.getInt("type_id")));
    }

    public boolean isValidIngredients(List<Integer> ingredientIds)
    {
        List<Ingredients> ingredientsRet = findAll(ingredientIds);
        return ingredientIds.size() == ingredientsRet.size();
    }

    private static class IngredientRowMapper implements RowMapper {

        private List<Ingredients> ingredientsList;

        public IngredientRowMapper(List<Ingredients> ingredientsList) {
            this.ingredientsList = ingredientsList;
        }

        @Override
        public Integer mapRow(ResultSet res, int row) throws SQLException {

            int g = 20;
            return 1;
        }

    }


}
