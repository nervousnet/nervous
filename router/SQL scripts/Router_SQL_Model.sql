-- MySQL Script generated by MySQL Workbench
-- 08/20/14 13:59:13
-- Model: New Model    Version: 1.0
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`SENSOR_00000000`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`SENSOR_00000000` (
  `UUID` BIGINT UNSIGNED NOT NULL,
  `Timestamp` BIGINT UNSIGNED NOT NULL,
  `X` FLOAT NOT NULL,
  `Y` FLOAT NOT NULL,
  `Z` FLOAT NOT NULL,
  `Accuracy` INT NOT NULL,
  PRIMARY KEY (`UUID`, `Timestamp`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Transaction` (
  `UUID` BIGINT UNSIGNED NOT NULL,
  `Timestamp` BIGINT UNSIGNED NOT NULL,
  `From` BIGINT UNSIGNED NOT NULL,
  `To` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`UUID`, `Timestamp`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;