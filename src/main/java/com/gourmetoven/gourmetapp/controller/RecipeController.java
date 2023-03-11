package com.gourmetoven.gourmetapp.controller;

import com.gourmetoven.gourmetapp.Dto.Request.RecipeCreateRequestDto;
import com.gourmetoven.gourmetapp.Dto.Request.SearchRequestDto;
import com.gourmetoven.gourmetapp.Dto.Response.IngredientResponseDto;
import com.gourmetoven.gourmetapp.Dto.Response.RecipeResponseDto;
import com.gourmetoven.gourmetapp.service.GourmetOvenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/recipe")
public class RecipeController {

    @Value("${app.version}")
    private String appVersion;

    @Autowired
    private GourmetOvenService gourmetOvenService;

    @GetMapping("/version")
    public ResponseEntity<String> getVersion()
    {
        return ResponseEntity.ok().body(appVersion);
    }

    @GetMapping("/all/ingredients")
    public ResponseEntity<List<IngredientResponseDto>> getAllIngredient()
    {
        return ResponseEntity.ok().body(gourmetOvenService.getAllIngredients());
    }

    @Deprecated(since = "The search endpoint can be used for same purpose just by passing empty json input" +
            "keeping this endpoint for initial tests")
    @GetMapping("/all/recipes")
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipes()
    {
        List<RecipeResponseDto> recipeResponseDtoList = gourmetOvenService.getAllRecipes();
        return ResponseEntity.ok().body(recipeResponseDtoList);
    }

    @Deprecated(since = "The search endpoint can be used for same purpose just by passing \"type\" parameter in input" +
            "keeping this endpoint for initial tests")
    @GetMapping("/recipes/type")
    public ResponseEntity<List<RecipeResponseDto>> getspecificRecipeType(@RequestParam("type") String type)
    {
        List<RecipeResponseDto> recipeResponseDtoList = gourmetOvenService.findRecipesWithType(type);
        return ResponseEntity.ok().body(recipeResponseDtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponseDto>> searchForRecipes(@RequestBody SearchRequestDto searchRequestDto)
    {
        List<RecipeResponseDto> recipeResponseDtoList = gourmetOvenService.getspecificRecipes(searchRequestDto);
        return ResponseEntity.ok().body(recipeResponseDtoList);
    }

    @PostMapping("/create")
    public ResponseEntity<RecipeResponseDto> createArecipe(@RequestBody RecipeCreateRequestDto recipeCreateRequestDto,
                                                           Authentication authentication) throws Exception {
        RecipeResponseDto recipeResponseDto = gourmetOvenService.
                createArecipe(recipeCreateRequestDto,authentication.getName());
        return ResponseEntity.ok().body(recipeResponseDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<RecipeResponseDto> updateArecipe(@RequestBody RecipeCreateRequestDto recipeCreateRequestDto,
                                                           Authentication authentication) throws Exception {
        RecipeResponseDto recipeResponseDto = gourmetOvenService.
                updateArecipe(recipeCreateRequestDto,authentication.getName());
        return ResponseEntity.ok().body(recipeResponseDto);
    }

    @DeleteMapping("/delete/{recipeId}")
    public ResponseEntity<String> updateArecipe(@PathVariable("recipeId") Integer recipeId,
                                                           Authentication authentication) throws Exception {
        gourmetOvenService.deleteRecipe(recipeId,authentication.getName());
        return ResponseEntity.ok().body("Recipe deleted succesfully");
    }
}
