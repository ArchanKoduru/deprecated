package com.gourmetoven.gourmetapp.service;

import com.gourmetoven.gourmetapp.Dto.Request.RecipeCreateRequestDto;
import com.gourmetoven.gourmetapp.Dto.Request.SearchRequestDto;
import com.gourmetoven.gourmetapp.Dto.Response.IngredientResponseDto;
import com.gourmetoven.gourmetapp.Dto.Response.RecipeResponseDto;
import com.gourmetoven.gourmetapp.Exception.AccessErrorException;
import com.gourmetoven.gourmetapp.Exception.DataFormatException;
import com.gourmetoven.gourmetapp.Exception.DataNotAvailableException;
import com.gourmetoven.gourmetapp.entity.Ingredients;
import com.gourmetoven.gourmetapp.entity.Recipe;
import com.gourmetoven.gourmetapp.repository.IngredientRepository;
import com.gourmetoven.gourmetapp.repository.RecipeIngredientMappingRepository;
import com.gourmetoven.gourmetapp.repository.RecipeInstructionMappingRepository;
import com.gourmetoven.gourmetapp.repository.RecipeRepository;
import com.gourmetoven.gourmetapp.repository.UserRepository;
import com.gourmetoven.gourmetapp.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gourmetoven.gourmetapp.util.Constants.ALL;
import static com.gourmetoven.gourmetapp.util.Constants.NONVEG;
import static com.gourmetoven.gourmetapp.util.Constants.VEG;

@Service
public class GourmetOvenService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeInstructionMappingRepository recipeInstructionMappingRepository;

    @Autowired
    RecipeIngredientMappingRepository recipeIngredientMappingRepository;

    public List<IngredientResponseDto> getAllIngredients()
    {
        List<Ingredients> ingredients = ingredientRepository.findAll();
        List<IngredientResponseDto> ingredientResponseDtoList = new ArrayList<>();
        if(ingredients != null && !ingredients.isEmpty())
        {
            ingredients.forEach(p -> extractIngredients(ingredientResponseDtoList,p));
        }
        return ingredientResponseDtoList;
    }

    public List<RecipeResponseDto> getAllRecipes()
    {
        return generatePayload(recipeRepository.findAllRecipes());
    }


    public List<RecipeResponseDto> findRecipesWithType(String type)
    {
        if(type == null || type.isEmpty())
        {
            type = ALL;
        }
        if(!type.equals(ALL) && !type.equals(VEG) && !type.equals(NONVEG))
        {
            type = ALL;
        }
        if(type.equals(ALL))
        {
            return getAllRecipes();
        }
        Integer typeIdex = TypeMapper.typeToIndexMap.get(type);
        List<Recipe> recipesRet = recipeRepository.findRecipesWithType(typeIdex);
        return generatePayload(recipesRet);
    }

    public List<RecipeResponseDto> getspecificRecipes(SearchRequestDto searchRequestDto) {
        //ALL default is as good as returning all recipes
        if(isAllDefault(searchRequestDto))
        {
            return getAllRecipes();
        }
        return processSearchRequest(searchRequestDto);
    }

    public RecipeResponseDto createArecipe(RecipeCreateRequestDto recipeCreateRequestDto, String name) throws Exception {
        //check user exists
        Integer userId = userRepository.getUserIdForUserName(name);
        if(userId == null)
        {
            throw new DataNotAvailableException("User doesn't exists");
        }
        return recipeRepository.createRecipe(recipeCreateRequestDto, userId,false);
    }

    public RecipeResponseDto updateArecipe(RecipeCreateRequestDto recipeCreateRequestDto, String name) throws Exception {
        //check user exists
        Integer userId = userRepository.getUserIdForUserName(name);
        //check recipe exists
        if(recipeCreateRequestDto.getRecipeId() == null || recipeCreateRequestDto.getRecipeId() <= 0)
        {
            throw new DataNotAvailableException("Recipe ID cannot be empty");
        }
        if(!recipeRepository.isRecipeExists(recipeCreateRequestDto.getRecipeId()))
        {
            throw new DataNotAvailableException("Recipe not found");
        }
        if(userId == null)
        {
            throw new DataNotAvailableException("User doesn't exists");
        }
        //Now we should update only if the owning user is same as the logged in user
        Recipe recipe = recipeRepository.findArecipe(recipeCreateRequestDto.getRecipeId());
        if(userId != recipe.getOwningUser())
        {
            throw new AccessErrorException("You cannot update someone's recipe");
        }
        //Now we simply delete all entries from recipe, recipe-ingredientmapping, recipe-instruction mapping

        return recipeRepository.updateRecipe(recipeCreateRequestDto, userId);
    }

    public void deleteRecipe(Integer recipeId, String name) throws Exception {
        //check user exists
        Integer userId = userRepository.getUserIdForUserName(name);
        //check recipe exists
        if(recipeId == null || recipeId <= 0)
        {
            throw new DataFormatException("Recipe ID cannot be empty");
        }
        if(!recipeRepository.isRecipeExists(recipeId))
        {
            throw new DataNotAvailableException("Recipe not found");
        }
        if(userId == null)
        {
            throw new DataNotAvailableException("User doesn't exists");
        }
        //Now we should update only if the owning user is same as the logged in user
        Recipe recipe = recipeRepository.findArecipe(recipeId);
        if(userId != recipe.getOwningUser())
        {
            throw new AccessErrorException("You cannot update someone's recipe");
        }
        //Now we simply delete all entries from recipe, recipe-ingredientmapping, recipe-instruction mapping
        recipeRepository.deleteRecipe(recipeId, userId);
    }

    private List<RecipeResponseDto> processSearchRequest(SearchRequestDto searchRequestDto) {
        String type = searchRequestDto.getType();
        Integer servings = searchRequestDto.getServings();
        List<String> excludeIngredients = searchRequestDto.getExcludeIngredients();
        List<String> includeIngredients = searchRequestDto.getIncludeIngredients();
        List<String> instructions = searchRequestDto.getInstructions();

        //If type is something other than vegetarian/non-vegetarian/all then default it to all
        if(!type.equals(ALL) && !type.equals(VEG) && !type.equals(NONVEG))
        {
            type = ALL;
        }

        List<Recipe> recipes = recipeRepository.findRecipesWithType(type, servings, excludeIngredients,
                includeIngredients, instructions );
        return generatePayload(recipes);
    }

    private boolean isAllDefault(SearchRequestDto searchRequestDto) {
        String type = searchRequestDto.getType();
        Integer servings = searchRequestDto.getServings();
        List<String> excludeIngredients = searchRequestDto.getExcludeIngredients();
        List<String> includeIngredients = searchRequestDto.getIncludeIngredients();
        List<String> instructions = searchRequestDto.getInstructions();
        return type.equals(ALL) && servings == null && excludeIngredients.isEmpty() && includeIngredients.isEmpty() &&
                instructions.isEmpty();
    }

    private void getAllIngredientsForARecipe(Map<Recipe,List<Integer> > ingredients,Recipe recipeId)
    {
        List<Integer> ingredientRet = recipeIngredientMappingRepository.getAllIngredientForArecipe(recipeId.getRecipeId());
        ingredients.put(recipeId,ingredientRet);
    }

    private List<RecipeResponseDto> generatePayload(List<Recipe> recipes)
    {
        Map<Recipe,List<Integer> > ingredients = new HashMap<>();
        if(recipes == null || recipes.isEmpty())
        {
            return Collections.emptyList();
        }
        recipes.forEach(p -> getAllIngredientsForARecipe(ingredients,p));

        List<RecipeResponseDto> recipeResponseDtos = new ArrayList<>();
        ingredients.forEach((key, value) -> populateRecipeResponseDtos(key, value, recipeResponseDtos));
        return recipeResponseDtos;
    }

    private void populateRecipeResponseDtos(Recipe recipe, List<Integer> ingredints,List<RecipeResponseDto> recipeResponseDtos)
    {
        List<Ingredients> ingredientsList = ingredientRepository.findAll(ingredints);
        List<IngredientResponseDto> ingredientResponseDtoList = new ArrayList<>();
        if(ingredientsList != null && !ingredientsList.isEmpty())
        {
            ingredientsList.forEach(p -> extractIngredients(ingredientResponseDtoList,p));
        }

        List<String> instructions = recipeInstructionMappingRepository.getAllInstructionsForArecipe(recipe.getRecipeId());
        RecipeResponseDto recipeResponseDto = RecipeResponseDto.builder().recipeId(recipe.getRecipeId()).recipeName(recipe.getRecipeName())
                .ingredients(ingredientResponseDtoList).servings(recipe.getServings())
                .owningUser(userRepository.getUsernameForUserId(recipe.getOwningUser()))
                .dishType(TypeMapper.getType(recipe.getDishType())).instructions(instructions).
                creationType(TypeMapper.getType(recipe.getCreationType())).build();
        recipeResponseDtos.add(recipeResponseDto);
    }

    private void extractIngredients(List<IngredientResponseDto> ingredientResponseDtoList,Ingredients ingredients )
    {
        ingredientResponseDtoList.add(IngredientResponseDto.builder().ingredientId(ingredients.
                        getIngredientId()).ingredientName(ingredients.getIngredientName()).
                type(TypeMapper.getType(ingredients.getTypeId())).build());
    }
}
