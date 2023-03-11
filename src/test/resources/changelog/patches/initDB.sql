-- liquibase formatted sql
-- changeSet archana-1:1 failOnError:true logicalFilePath:initDB.sql

CREATE TABLE IF NOT EXISTS `users` (
  `user_id` INT NOT NULL auto_increment PRIMARY KEY,
  `user_name` VARCHAR(128) UNIQUE NOT NULL,
  `name_of_user` VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS `types` (
  `type_id` INT NOT NULL auto_increment PRIMARY KEY,
  `type_name` VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS `ingredients` (
  `ingredient_id` INT NOT NULL auto_increment,
  `ingredient_name` VARCHAR(128) NOT NULL,
  `type_id` INT NOT NULL,
  FOREIGN KEY (type_id) REFERENCES types(type_id),
  PRIMARY KEY(ingredient_id, ingredient_name)
);

CREATE TABLE IF NOT EXISTS `recipe` (
  `recipe_id` INT NOT NULL auto_increment PRIMARY KEY,
  `recipe_name` VARCHAR(128) NOT NULL,
  `servings` INT NOT NULL,
  `dish_type` INT NOT NULL,
  `creation_type` INT NOT NULL,
  `owning_user` INT DEFAULT 0,
  FOREIGN KEY (dish_type) REFERENCES types(type_id),
  FOREIGN KEY (creation_type) REFERENCES types(type_id)
);

CREATE TABLE IF NOT EXISTS `recipe_ingredient_mapping` (
  `mapping_id` INT NOT NULL auto_increment PRIMARY KEY,
  `ingredient_id` INT NOT NULL,
  `recipe_id` INT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

CREATE TABLE IF NOT EXISTS `recipe_instruction_mapping` (
  `mapping_id` INT NOT NULL auto_increment PRIMARY KEY,
  `recipe_id` INT NOT NULL,
  `instruction` VARCHAR(128),
  FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id)
);

