package com.gourmetoven.gourmetapp.repository;

import com.gourmetoven.gourmetapp.Dto.Request.RecipeCreateRequestDto;
import com.gourmetoven.gourmetapp.Dto.Response.IngredientResponseDto;
import com.gourmetoven.gourmetapp.Dto.Response.RecipeResponseDto;
import com.gourmetoven.gourmetapp.Exception.DataFormatException;
import com.gourmetoven.gourmetapp.entity.Ingredients;
import com.gourmetoven.gourmetapp.entity.Recipe;
import com.gourmetoven.gourmetapp.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gourmetoven.gourmetapp.util.Constants.ALL;
import static com.gourmetoven.gourmetapp.util.Constants.NONVEG;
import static com.gourmetoven.gourmetapp.util.Constants.VEG;

@Repository
public class RecipeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientMappingRepository recipeIngredientMappingRepository;

    @Autowired
    private RecipeInstructionMappingRepository recipeInstructionMappingRepository;

    @Autowired
    private UserRepository userRepository;

    SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public void MessageRepositorySimpleJDBCInsert(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("recipe").usingGeneratedKeyColumns("recipe_id");
    }

    public void deleteRecipe(Integer recipeID)
    {
        String deleteQuery = "DELETE FROM recipe where recipe_id = ?";
        jdbcTemplate.update(deleteQuery, recipeID);
    }
    public boolean isRecipeExists(Integer recipeId)
    {
        String sql = "SELECT count(*) FROM recipe WHERE recipe_id = ?";
        boolean exists = false;
        int count = jdbcTemplate.queryForObject(sql, new Object[] { recipeId }, Integer.class);
        return count == 1 ;
    }
    public List<Recipe> findAllRecipes() {
        return jdbcTemplate.query("SELECT * FROM recipe",
                BeanPropertyRowMapper.newInstance(Recipe.class));
    }

    public Recipe findArecipe(Integer recipeId) {
        String sql = "SELECT * FROM recipe WHERE recipe_id = ?";
        return jdbcTemplate.queryForObject(sql, new RecipeRowMapper(), recipeId);
    }

    public List<Recipe> findRecipesWithType(Integer dishType) {
        String sql = "SELECT * FROM recipe WHERE dish_type = ?";
        return jdbcTemplate.query(sql, new RecipeRowMapper(), dishType);
    }

    public List<Recipe> findRecipesWithType(String type, Integer servings, List<String> excludeIngredients,
                                            List<String> includeIngredients, List<String> instructions ) {

        StringBuilder sb=new StringBuilder("select recipe.recipe_id, recipe.recipe_name, recipe.servings, recipe.dish_type, recipe.creation_type, recipe.owning_user\n" +
                " from recipe");
        if(!includeIngredients.isEmpty())
        {
            sb.append(" INNER JOIN recipe_ingredient_mapping ON recipe.recipe_id = recipe_ingredient_mapping.recipe_id INNER JOIN \n" +
                    "ingredients ON ingredients.ingredient_id=recipe_ingredient_mapping.ingredient_id ");
            for(String each : includeIngredients)
            {
                sb.append(" AND ingredients.ingredient_name='");
                sb.append(each);
                sb.append("'");
            }

        }
        if(!excludeIngredients.isEmpty())
        {
            sb.append(" INNER JOIN recipe_ingredient_mapping ON recipe.recipe_id != recipe_ingredient_mapping.recipe_id INNER JOIN \n" +
                    "ingredients ON ingredients.ingredient_id=recipe_ingredient_mapping.ingredient_id ");
            for(String each : excludeIngredients)
            {
                sb.append(" AND ingredients.ingredient_name='");
                sb.append(each);
                sb.append("'");
            }

        }

        if(!type.equals(ALL))
        {
            Integer typeIdx = TypeMapper.typeToIndexMap.get(type);
            sb.append(" where recipe.dish_type = ");
            sb.append(typeIdx);
            if(servings != 999999)
            {
                sb.append(" AND recipe.servings = ");
                sb.append(servings);
            }
        }
        else {
            if(servings != 999999)
            {
                sb.append(" AND recipe.servings = ");
                sb.append(servings);
            }

        }

        if(!instructions.isEmpty())
        {
            sb.append(" INNER JOIN recipe_instruction_mapping ON recipe.recipe_id=recipe_instruction_mapping.recipe_id");
            for(String each : instructions)
            {
                sb.append(" AND recipe_instruction_mapping.instruction='");
                sb.append(each);
                sb.append("'");
            }

        }
        sb.append(";");
        String finalSql = sb.toString();
        return jdbcTemplate.query(finalSql,
                BeanPropertyRowMapper.newInstance(Recipe.class));
    }

    public RecipeResponseDto createRecipe(RecipeCreateRequestDto recipeCreateRequestDto,
                                          Integer userId, boolean isUpdate) throws Exception {
        if(recipeCreateRequestDto.getIngredients().isEmpty())
        {
            throw  new DataFormatException("Ingredients can't be empty");
        }
        if(recipeCreateRequestDto.getName() == null || recipeCreateRequestDto.getName().isEmpty())
        {
            throw  new DataFormatException("Recipe name can't be empty");
        }
        if(!recipeCreateRequestDto.getDishType().equals(VEG) && !recipeCreateRequestDto.getDishType().equals(NONVEG))
        {
            throw  new DataFormatException("Recipe must be either vegetarian or non-vegetarian");
        }
        if(!ingredientRepository.isValidIngredients(recipeCreateRequestDto.getIngredients()))
        {
            throw  new DataFormatException("Invalid ingredients mentioned");
        }
        //If no servings then we default to 1
        if(recipeCreateRequestDto.getServings() == null || recipeCreateRequestDto.getServings() == 0)
        {
            recipeCreateRequestDto.setServings(1);
        }
        return createRecipeWithIngredients(recipeCreateRequestDto,userId,isUpdate);
    }

    public RecipeResponseDto updateRecipe(RecipeCreateRequestDto recipeCreateRequestDto, Integer userId) throws Exception {
        Integer recipeId = recipeCreateRequestDto.getRecipeId();
        recipeInstructionMappingRepository.deleteRecipeMapping(recipeId);
        recipeIngredientMappingRepository.deleteRecipeMapping(recipeId);
        //now recreate it
        return createRecipe(recipeCreateRequestDto, userId,true);
    }

    public void deleteRecipe(Integer recipeId, Integer userId) {
        recipeInstructionMappingRepository.deleteRecipeMapping(recipeId);
        recipeIngredientMappingRepository.deleteRecipeMapping(recipeId);
        deleteRecipe(recipeId);
    }

    private RecipeResponseDto createRecipeWithIngredients(RecipeCreateRequestDto recipeCreateRequestDto,
                                                          Integer userId,Boolean isUpdate) {
        Map<String, Object> params = new HashMap<>();
        params.put("recipe_name", recipeCreateRequestDto.getName());
        params.put("servings", recipeCreateRequestDto.getServings());
        params.put("dish_type", TypeMapper.typeToIndexMap.get((recipeCreateRequestDto.getDishType())));
        params.put("creation_type", 5);
        params.put("owning_user", userId);
        Integer recipeId;
        if(!isUpdate) {
            Number pkId = simpleJdbcInsert.executeAndReturnKey(params);
            recipeId = (int) pkId;
        }
        else {
            updateRecipeWithSameKey(recipeCreateRequestDto);
            recipeId = recipeCreateRequestDto.getRecipeId();
        }
        recipeIngredientMappingRepository.addIngredientsToRecipe(recipeId, recipeCreateRequestDto.getIngredients());
        if(recipeCreateRequestDto.getInstructions() != null && !recipeCreateRequestDto.getInstructions().isEmpty()) {
            recipeInstructionMappingRepository.addInstructionsToRecipe(recipeId, recipeCreateRequestDto.getInstructions());
        }
        List<Ingredients> ingredientsList = ingredientRepository.findAll(recipeCreateRequestDto.getIngredients());
        List<IngredientResponseDto> ingredientResponseDtoList = new ArrayList<>();
        List<String> instructions = recipeInstructionMappingRepository.getAllInstructionsForArecipe(recipeId);
        String user = userRepository.getUsernameForUserId(userId);
        ingredientsList.forEach(p -> extractIngredients(ingredientResponseDtoList,p));
        return RecipeResponseDto.builder().recipeId(recipeId).recipeName(recipeCreateRequestDto.getName()).
                ingredients(ingredientResponseDtoList).creationType("user-defined").owningUser(user).
                dishType(recipeCreateRequestDto.getDishType()).servings(recipeCreateRequestDto.getServings()).
                instructions(instructions).build();
    }

    private void updateRecipeWithSameKey(RecipeCreateRequestDto recipeCreateRequestDto)
    {

        StringBuilder sb=new StringBuilder("UPDATE recipe SET recipe_name = '");
        sb.append(recipeCreateRequestDto.getName());
        sb.append("'");
        sb.append(",servings = ");
        sb.append(recipeCreateRequestDto.getServings());
        sb.append(",dish_type = ");
        sb.append(TypeMapper.typeToIndexMap.get((recipeCreateRequestDto.getDishType())));
        sb.append(" WHERE recipe_id = ");
        sb.append(recipeCreateRequestDto.getRecipeId());
        jdbcTemplate.update(sb.toString());

    }


    private void extractIngredients(List<IngredientResponseDto> ingredientResponseDtoList,Ingredients ingredients )
    {
        ingredientResponseDtoList.add(IngredientResponseDto.builder().ingredientId(ingredients.
                        getIngredientId()).ingredientName(ingredients.getIngredientName()).
                type(TypeMapper.getType(ingredients.getTypeId())).build());
    }

    private class RecipeRowMapper implements RowMapper<Recipe> {
        @Override
        public Recipe mapRow(ResultSet rs, int i) throws SQLException {
            int rid = rs.getInt("recipe_id");
            String recipeName = rs.getString("recipe_name");
            int servings = rs.getInt("servings");
            int dishType = rs.getInt("dish_type");
            int creationType = rs.getInt("creation_type");
            int owningUser = rs.getInt("owning_user");
            return Recipe.builder().recipeId(rid).recipeName(recipeName).creationType(creationType).
                    dishType(dishType).servings(servings).owningUser(owningUser).build();
        }
    }
}
