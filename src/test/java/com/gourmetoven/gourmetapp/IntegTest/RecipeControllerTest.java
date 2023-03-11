package com.gourmetoven.gourmetapp.IntegTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gourmetoven.gourmetapp.Dto.Request.RecipeCreateRequestDto;
import com.gourmetoven.gourmetapp.Dto.Request.SearchRequestDto;
import com.gourmetoven.gourmetapp.util.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@TestExecutionListeners(listeners = { SqlScriptsTestExecutionListener.class })
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@Sql(scripts = {"file:src/test/resources/data.sql"})
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.version}")
    private String appVersion;

    @Test
    @Order(1)
    void accessing_without_usercredentials_returns_error() throws Exception {
        final String version = "1.0";
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/version")).
                andReturn();
        Assertions.assertEquals(401, result.getResponse().getStatus());

    }

    @Test
    @Order(2)
    void getVersion_returns_version() throws Exception {
        final String version = "1.0";
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/version").
                        with(user("user1").password("pass1"))).andReturn();
        String ret = result.getResponse().getContentAsString();
        Assertions.assertEquals(version, ret);

    }

    @Test
    @Order(3)
    void getAllIngredient_return_Ingredients() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/all/ingredients").
                with(user("user1").password("pass1"))).andReturn();

        byte [] ret = result.getResponse().getContentAsByteArray();
        String strRetunred=new String(ret);
        Assertions.assertEquals(TestConstants.ingredientsExpected,strRetunred);
    }

    @Test
    @Order(4)
    void getAllRecipes_return_Recipes() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/all/recipes").
                with(user("user1").password("pass1"))).andReturn();

        byte [] ret = result.getResponse().getContentAsByteArray();
        String strRetunred=new String(ret);
        Assertions.assertEquals(TestConstants.recipesExpected_All_Deafult,strRetunred);
    }

    @Test
    @Order(5)
    void getType_return_Recipes() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/recipes/type?type=vegetarian").
                with(user("user1").password("pass1"))).andReturn();

        byte [] ret = result.getResponse().getContentAsByteArray();
        String strRetunred=new String(ret);
        Assertions.assertEquals(TestConstants.recipesExpectedForType_Vegetarian,strRetunred);
    }

    @Test
    @Order(6)
    void search_returns_results() throws Exception
    {
        SearchRequestDto searchRequestDto = new SearchRequestDto();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Assertions.assertEquals(TestConstants.recipesExpected_All_Deafult,search_Helper(searchRequestDto));
        }
        catch (Exception ex) {
            fail("Test search_returns_results failed");
        }
    }

    @Test
    @Order(7)
    void search_for_vegetarian_returns_results() throws Exception
    {
        SearchRequestDto searchRequestDto = new SearchRequestDto();
        searchRequestDto.setType("vegetarian");
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Assertions.assertEquals(TestConstants.recipesExpectedForType_Vegetarian,search_Helper(searchRequestDto));
        }
        catch (Exception ex) {
            fail("Test search_for_vegetarian_returns_results failed");
        }
    }

    @Test
    @Order(8)
    void search_for_Serving_ingredient_type_returns_results() throws Exception
    {
        SearchRequestDto searchRequestDto = new SearchRequestDto();
        searchRequestDto.setType("non-vegetarian");
        searchRequestDto.setServings(2);
        searchRequestDto.setIncludeIngredients(List.of("Salami"));
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Assertions.assertEquals(TestConstants.searchResult_2_nonVeg_Salami,search_Helper(searchRequestDto));
        }
        catch (Exception ex) {
            fail("Test search_for_Serving_ingredient_type_returns_results failed");
        }
    }

    @Test
    @Order(9)
    void search_for_Serving_excludeingredient_type_returns_results() throws Exception
    {
        SearchRequestDto searchRequestDto = new SearchRequestDto();
        searchRequestDto.setServings(4);
        searchRequestDto.setExcludeIngredients(List.of("Salami"));
        searchRequestDto.setInstructions(List.of("No Paprika"));
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Assertions.assertEquals(TestConstants.recipeExpected_4_ALL_ExcludeSalami_Include_Instruction
                    ,search_Helper(searchRequestDto));
        }
        catch (Exception ex) {
            fail("Test search_for_Serving_excludeingredient_type_returns_results failed");
        }
    }

    @Test
    @Order(10)
    void createteRecipe_returns_Success() throws Exception {
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101,102)).instructions(List.of("More spicy")).
                servings(2).name("sambar").build();

        int recipeId=999999;

        try {
            String ret = create_Helper(recipeCreateRequestDto,
                    "user1","pass1",true);
            Assertions.assertEquals(TestConstants.recipeCreateResult, ret);
            String [] words = ret.split(",");
            for (String word : words) {
                if (word.contains("recipeId")) {
                    String id = word.split(":")[1];
                    recipeId = Integer.parseInt(id);
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            fail("createteRecipe_returns_Success failed");
        }
        finally {
            cleanUpCreateRecipe(recipeId,"user1","pass1");
        }
    }

    @Test
    @Order(11)
    void createteRecipe_WithoutVitalData_returns_Failure() throws Exception {
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101,102)).instructions(List.of("More spicy")).
                servings(2).build();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(recipeCreateRequestDto);
            MvcResult result;
            result = this.mockMvc.perform(MockMvcRequestBuilders.post("/recipe/create")
                    .contentType(MediaType.APPLICATION_JSON).content(payload).
                    with(user("user1").password("pass1"))).andReturn();
            Assertions.assertEquals(417,result.getResponse().getStatus());
        }
        catch (Exception ex)
        {
            fail("createteRecipe_WithoutVitalData_returns_Failure failed");
        }
    }

    @Test
    @Order(12)
    void updateRecipe_returns_Success() throws Exception {
        //First create recipe
        int recipeId = 999999 ;
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101,102)).instructions(List.of("More spicy")).
                servings(2).name("sambar").build();

        try {
            String retVal = create_Helper(recipeCreateRequestDto,"user1","pass1",true);
            String [] words = retVal.split(",");
            for (String word : words) {
                if (word.contains("recipeId")) {
                    String id = word.split(":")[1];
                    recipeId = Integer.parseInt(id);
                    break;
                }
            }
            RecipeCreateRequestDto recipeupdateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                    .ingredients(List.of(101,102,103)).instructions(List.of("Less spicy","Less Garlic")).
                    servings(6).name("Sambar").recipeId(recipeId).build();
            Assertions.assertEquals(TestConstants.recipeUpdateResult, create_Helper(recipeupdateRequestDto,"user1","pass1",false));
        }
        catch (Exception ex)
        {
            fail("updateRecipe_returns_Success failed");
        }
        finally {
            cleanUpCreateRecipe(recipeId,"user1","pass1");
        }
    }

    @Test
    @Order(13)
    void updating_wrong_recipeIdThrows_404() throws Exception {
        RecipeCreateRequestDto recipeupdateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101,102,103)).instructions(List.of("Less spicy","Less Garlic")).
                servings(6).name("Sambar").recipeId(123456).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(recipeupdateRequestDto);
        MvcResult result;
        result = this.mockMvc.perform(MockMvcRequestBuilders.patch("/recipe/update")
                .contentType(MediaType.APPLICATION_JSON).content(payload).
                with(user("user1").password("pass1"))).andReturn();
        Assertions.assertEquals(404,result.getResponse().getStatus());
    }

    @Test
    @Order(14)
    void updateRecipe_with_wrong_user_returns_Failure() throws Exception {
        //First create recipe
        int recipeId = 999999 ;
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101,102)).instructions(List.of("More spicy")).
                servings(2).name("sambar").build();

        try {
            String retVal = create_Helper(recipeCreateRequestDto,"user1","pass1",true);
            String [] words = retVal.split(",");
            for (String word : words) {
                if (word.contains("recipeId")) {
                    String id = word.split(":")[1];
                    recipeId = Integer.parseInt(id);
                    break;
                }
            }

            RecipeCreateRequestDto recipeupdateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                    .ingredients(List.of(101,102,103)).instructions(List.of("Less spicy","Less Garlic")).
                    servings(6).name("Sambar").recipeId(recipeId).build();
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(recipeupdateRequestDto);
            MvcResult result;
            result = this.mockMvc.perform(MockMvcRequestBuilders.patch("/recipe/update")
                    .contentType(MediaType.APPLICATION_JSON).content(payload).
                    with(user("user2").password("pass2"))).andReturn();
            Assertions.assertEquals(403,result.getResponse().getStatus());
        }
        catch (Exception ex)
        {
            fail("updateRecipe_with_wrong_user_returns_Failure failed");
        }
        finally {
            cleanUpCreateRecipe(recipeId,"user1","pass1");
        }
    }

    @Test
    @Order(15)
    void delete_recipe_success() {
        //First create recipe
        int recipeId = 999999;
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                .ingredients(List.of(101, 102)).instructions(List.of("More spicy")).
                servings(2).name("sambar").build();

        try {
            String retVal = create_Helper(recipeCreateRequestDto, "user1", "pass1", true);
            String[] words = retVal.split(",");
            for (String word : words) {
                if (word.contains("recipeId")) {
                    String id = word.split(":")[1];
                    recipeId = Integer.parseInt(id);
                    break;
                }
            }

            MvcResult result;
            result = this.mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/recipe/delete/%d", recipeId)).
                    with(user("user1").password("pass1"))).andReturn();
            Assertions.assertEquals(200, result.getResponse().getStatus());
        } catch (Exception ex) {
            fail("delete_recipe_success failed");
        }
    }

        @Test
        @Order(16)
        void delete_recipe_With_Wrong_User_Failure()
        {
            //First create recipe
            int recipeId = 999999 ;
            RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.builder().dishType("vegetarian")
                    .ingredients(List.of(101,102)).instructions(List.of("More spicy")).
                    servings(2).name("sambar").build();

            try {
                String retVal = create_Helper(recipeCreateRequestDto,"user1","pass1",true);
                String [] words = retVal.split(",");
                for (String word : words) {
                    if (word.contains("recipeId")) {
                        String id = word.split(":")[1];
                        recipeId = Integer.parseInt(id);
                        break;
                    }
                }

                MvcResult result;
                result = this.mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/recipe/delete/%d",recipeId)).
                        with(user("user2").password("pass2"))).andReturn();
                Assertions.assertEquals(403,result.getResponse().getStatus());
            }
            catch (Exception ex)
            {
                fail("delete_recipe_With_Wrong_User_Failure failed");
            }
    }

    private String search_Helper(SearchRequestDto searchRequestDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(searchRequestDto);
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/recipe/search")
                .contentType(MediaType.APPLICATION_JSON).content(payload).
                with(user("user1").password("pass1"))).andReturn();

        byte [] ret = result.getResponse().getContentAsByteArray();
        return new String(ret);
    }

    private String create_Helper(RecipeCreateRequestDto recipeCreateRequestDto
    ,String userName, String password, boolean isCreate) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(recipeCreateRequestDto);
        MvcResult result;
        if(isCreate)
        {
            result = this.mockMvc.perform(MockMvcRequestBuilders.post("/recipe/create")
                .contentType(MediaType.APPLICATION_JSON).content(payload).
                with(user(userName).password(password))).andReturn();
        }
        else {
            result = this.mockMvc.perform(MockMvcRequestBuilders.patch("/recipe/update")
                    .contentType(MediaType.APPLICATION_JSON).content(payload).
                    with(user(userName).password(password))).andReturn();
        }
        byte [] ret = result.getResponse().getContentAsByteArray();
        String retString = new String(ret);
        return retString;
    }

    private void cleanUpCreateRecipe(int recipeId, String user, String password) throws Exception {
       this.mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/recipe/delete/%d", recipeId)).
                with(user(user).password(password))).andReturn();
    }
}