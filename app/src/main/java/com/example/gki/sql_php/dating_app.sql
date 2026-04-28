-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema dating_app
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dating_app
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dating_app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `dating_app` ;

-- -----------------------------------------------------
-- Table `dating_app`.`Users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Users` (
  `id_user` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100) NULL DEFAULT NULL,
  `birth_date` DATE NULL DEFAULT NULL,
  `height` VARCHAR(10) NULL DEFAULT NULL,
  `weight` VARCHAR(10) NULL DEFAULT NULL,
  `gender` ENUM('Male', 'Female', 'Other') NULL DEFAULT NULL,
  `profile_img_id` VARCHAR(255) NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_user`),
  UNIQUE INDEX `email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`Hobbies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Hobbies` (
  `id_hobbies` INT NOT NULL AUTO_INCREMENT,
  `id_user` INT NULL DEFAULT NULL,
  `content_hobbies` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id_hobbies`),
  INDEX `id_user` (`id_user` ASC) VISIBLE,
  CONSTRAINT `Hobbies_ibfk_1`
    FOREIGN KEY (`id_user`)
    REFERENCES `dating_app`.`Users` (`id_user`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`Location`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Location` (
  `id_location` INT NOT NULL AUTO_INCREMENT,
  `id_user` INT NULL DEFAULT NULL,
  `country` VARCHAR(100) NULL DEFAULT NULL,
  `city` VARCHAR(100) NULL DEFAULT NULL,
  `ward` VARCHAR(100) NULL DEFAULT NULL,
  `district` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id_location`),
  INDEX `id_user` (`id_user` ASC) VISIBLE,
  CONSTRAINT `Location_ibfk_1`
    FOREIGN KEY (`id_user`)
    REFERENCES `dating_app`.`Users` (`id_user`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`Matches`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Matches` (
  `id_match` INT NOT NULL AUTO_INCREMENT,
  `user_one_id` INT NULL DEFAULT NULL,
  `user_two_id` INT NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `status` INT NULL DEFAULT '0' COMMENT '0: Pending (Chờ), 1: Accepted (Bạn bè)',
  `sender_id` INT NOT NULL COMMENT 'ID người nhấn nút cộng',
  PRIMARY KEY (`id_match`),
  INDEX `user_one_id` (`user_one_id` ASC) VISIBLE,
  INDEX `user_two_id` (`user_two_id` ASC) VISIBLE,
  CONSTRAINT `Matches_ibfk_1`
    FOREIGN KEY (`user_one_id`)
    REFERENCES `dating_app`.`Users` (`id_user`),
  CONSTRAINT `Matches_ibfk_2`
    FOREIGN KEY (`user_two_id`)
    REFERENCES `dating_app`.`Users` (`id_user`))
ENGINE = InnoDB
AUTO_INCREMENT = 41
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`Messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Messages` (
  `id_message` INT NOT NULL AUTO_INCREMENT,
  `id_match` INT NULL DEFAULT NULL,
  `sender_id` INT NULL DEFAULT NULL,
  `content` TEXT NOT NULL,
  `is_read` TINYINT(1) NULL DEFAULT '0',
  `timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_message`),
  INDEX `id_match` (`id_match` ASC) VISIBLE,
  INDEX `sender_id` (`sender_id` ASC) VISIBLE,
  CONSTRAINT `Messages_ibfk_1`
    FOREIGN KEY (`id_match`)
    REFERENCES `dating_app`.`Matches` (`id_match`)
    ON DELETE CASCADE,
  CONSTRAINT `Messages_ibfk_2`
    FOREIGN KEY (`sender_id`)
    REFERENCES `dating_app`.`Users` (`id_user`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`Notes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`Notes` (
  `id_note` INT NOT NULL AUTO_INCREMENT,
  `id_user` INT NULL DEFAULT NULL,
  `content` TEXT NULL DEFAULT NULL,
  `time_note` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_note`),
  INDEX `id_user` (`id_user` ASC) VISIBLE,
  CONSTRAINT `Notes_ibfk_1`
    FOREIGN KEY (`id_user`)
    REFERENCES `dating_app`.`Users` (`id_user`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`User_Actions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`User_Actions` (
  `id_action` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `target_user_id` INT NOT NULL,
  `action_type` ENUM('like', 'dislike') NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_action`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  INDEX `target_user_id` (`target_user_id` ASC) VISIBLE,
  CONSTRAINT `User_Actions_ibfk_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `dating_app`.`Users` (`id_user`),
  CONSTRAINT `User_Actions_ibfk_2`
    FOREIGN KEY (`target_user_id`)
    REFERENCES `dating_app`.`Users` (`id_user`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dating_app`.`up_Img`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dating_app`.`up_Img` (
  `id_img` INT NOT NULL AUTO_INCREMENT,
  `id_user` INT NULL DEFAULT NULL,
  `img_url` VARCHAR(255) NOT NULL,
  `time_upimg` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_img`),
  INDEX `id_user` (`id_user` ASC) VISIBLE,
  CONSTRAINT `up_Img_ibfk_1`
    FOREIGN KEY (`id_user`)
    REFERENCES `dating_app`.`Users` (`id_user`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
