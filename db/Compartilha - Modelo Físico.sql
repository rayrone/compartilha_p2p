SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `CompartilhaDB` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `CompartilhaDB`;

-- -----------------------------------------------------
-- Table `CompartilhaDB`.`Hosts`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CompartilhaDB`.`Hosts` (
  `ip` VARCHAR(20) NOT NULL ,
  `portae` INT NULL DEFAULT 9010 ,
  `portar` INT NULL DEFAULT 9011 ,
  `online` BOOLEAN NOT NULL DEFAULT FALSE ,
  PRIMARY KEY (`ip`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CompartilhaDB`.`Arquivos`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CompartilhaDB`.`Arquivos` (
  `codigo` INT NOT NULL AUTO_INCREMENT ,
  `nome` VARCHAR(255) NOT NULL ,
  `extensao` VARCHAR(5) NOT NULL ,
  `tamanho` BIGINT NOT NULL ,
  PRIMARY KEY (`codigo`) ,
  UNIQUE INDEX UN_NOME_EXT (`nome` ASC, `extensao` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CompartilhaDB`.`Hosts_Arquivos`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CompartilhaDB`.`Hosts_Arquivos` (
  `Hosts_ip` VARCHAR(20) NOT NULL ,
  `Arquivos_codigo` INT NOT NULL ,
  PRIMARY KEY (`Hosts_ip`, `Arquivos_codigo`) ,
  INDEX fk_Hosts_has_Arquivos_Hosts (`Hosts_ip` ASC) ,
  INDEX fk_Hosts_has_Arquivos_Arquivos (`Arquivos_codigo` ASC) ,
  CONSTRAINT `fk_Hosts_has_Arquivos_Hosts`
    FOREIGN KEY (`Hosts_ip` )
    REFERENCES `CompartilhaDB`.`Hosts` (`ip` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_Hosts_has_Arquivos_Arquivos`
    FOREIGN KEY (`Arquivos_codigo` )
    REFERENCES `CompartilhaDB`.`Arquivos` (`codigo` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
