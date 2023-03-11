-- liquibase formatted sql
-- changeSet archana:1 failOnError:true logicalFilePath:initDB.sql

--default types
INSERT INTO `types` VALUES (1, 'vegetarian');
INSERT INTO `types` VALUES (2, 'non-vegetarian');
INSERT INTO `types` VALUES (3, 'ingredient');
INSERT INTO `types` VALUES (4, 'recipe');
INSERT INTO `types` VALUES (5, 'user-defined');
INSERT INTO `types` VALUES (6, 'default');

--default users
INSERT INTO `users` VALUES (0, 'default','system');
INSERT INTO `users` VALUES (100, 'user1','Thomas');
INSERT INTO `users` VALUES (101, 'user2','Mathew');
INSERT INTO `users` VALUES (102, 'user3','Alex');


--default ingredients
INSERT INTO `ingredients` VALUES (101,'Cheese',1);
INSERT INTO `ingredients` VALUES (102,'Paprika',1);
INSERT INTO `ingredients` VALUES (103,'Salami',2);
INSERT INTO `ingredients` VALUES (104,'Pineapple',1);
INSERT INTO `ingredients` VALUES (105,'Potato',1);
INSERT INTO `ingredients` VALUES (106,'Salt',1);
INSERT INTO `ingredients` VALUES (107,'Salmon',2);
INSERT INTO `ingredients` VALUES (108,'Onion',1);
INSERT INTO `ingredients` VALUES (109,'Garlic',1);
INSERT INTO `ingredients` VALUES (110,'Chicken',2);
INSERT INTO `ingredients` VALUES (111,'Egg',2);
INSERT INTO `ingredients` VALUES (112,'Mustard',1);
INSERT INTO `ingredients` VALUES (113,'Lettuce',1);

--default Recipes
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`) VALUES (101,'Pineapple Pizza',4,1,6);
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`) VALUES (102,'Salami Pizza',2,2,6);
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`) VALUES (103,'Potato Pizza',2,1,6);
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`) VALUES (104,'Omlette',5,2,6);

--pineapple pizza
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (10,104,101);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (11,101,101);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (12,106,101);

--Salami pizza
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (13,101,102);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (14,103,102);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (15,105,102);

--Potato pizza
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (16,112,103);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (17,109,103);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (18,105,103);


--Omlette
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (19,111,104);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (20,109,104);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (21,106,104);

--instruction for pineapple pizza
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (1,101,'No Garlic');
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (2,101,'No Paprika');
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (3,101,'Lots of Pineapple');

--instruction for omlette
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (4,104,'Lots of Garlic');
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (5,104,'No Paprika');

-----------------------------------------------------------------------------------------------------------------------

--Pre-create 2 recipes for user1
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`,`owning_user`) VALUES (106,'Spanish Omlette',2,2,5,100);
INSERT INTO `recipe` (`recipe_id`,`recipe_name`,`servings`,`dish_type`,`creation_type`,`owning_user`) VALUES (107,'Potato stew',1,1,5,100);


--spanish omlette
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (22,111,106);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (23,101,106);

--potato stew
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (24,105,107);
INSERT INTO `recipe_ingredient_mapping` (`mapping_id`,`ingredient_id`,`recipe_id`) VALUES (25,106,107);

--instruction for spanish omlette
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (6,106,'Two eggs');
--instruction for potato stew
INSERT INTO `recipe_instruction_mapping` (`mapping_id`,`recipe_id`,`instruction`) VALUES (7,107,'Lots of potatos');






