
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: carloan
-- ------------------------------------------------------
-- Server version	5.7.10-log
--
-- Table structure for table `admin`
--
CREATE DATABASE IF NOT EXISTS Carloan;
USE Carloan;


CREATE TABLE IF NOT EXISTS `admin` (
  `Username` varchar(45) NOT NULL,
  `Password` varchar(32) NOT NULL,
  `Primary` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Username`),
  UNIQUE KEY `Username_UNIQUE` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `sede`
--

CREATE TABLE IF NOT EXISTS `sede` (
  `Sede` varchar(25) NOT NULL,
  PRIMARY KEY (`Sede`),
  UNIQUE KEY `Sede_UNIQUE` (`Sede`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





--
-- Table structure for table `client`
--
CREATE TABLE IF NOT EXISTS `client` (
  `Username` varchar(15) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Surname` varchar(45) NOT NULL,
  `Phone` varchar(10) NOT NULL,
  `Password` varchar(32) NOT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `optional`
--

CREATE TABLE IF NOT EXISTS `optional` (
  `Optional` varchar(45) NOT NULL,
  PRIMARY KEY (`Optional`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `auto`
--


CREATE TABLE IF NOT EXISTS `auto` (
  `Targa` varchar(7) NOT NULL,
  `Nome` varchar(45) NOT NULL,
  `Categoria` varchar(25) NOT NULL,
  `Marchio` varchar(25) NOT NULL,
  `Alimentazione` varchar(15) NOT NULL,
  `ConsumoMedio` varchar(10) NOT NULL,
  `Km` double NOT NULL,
  `Dettagli` mediumtext NOT NULL,
  `Foto` varchar(60) NOT NULL,
  `FKSede` varchar(25) NOT NULL,
  `Stato` varchar(15) NOT NULL,
  `CC` float NOT NULL,
  `PrezzoG` double NOT NULL,
  PRIMARY KEY (`Targa`),
  KEY `Sede_idx` (`FKSede`),
  CONSTRAINT `Sede` FOREIGN KEY (`FKSede`) REFERENCES `sede` (`Sede`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




--
-- Table structure for table `prenotation`
--
CREATE TABLE IF NOT EXISTS `prenotation` (
  `IDPrenotazione` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `FKClient` varchar(15) NOT NULL,
  `Targa` varchar(7) NOT NULL,
  `PrezzoIniziale` double unsigned NOT NULL,
  `DataStart` varchar(10) NOT NULL,
  `DataEnd` varchar(10) NOT NULL,
  `FKStartSede` varchar(25) NOT NULL,
  `FKEndSede` varchar(25) NOT NULL,
  `KMUnlimited` tinyint(1) NOT NULL,
  `OptionalList` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`IDPrenotazione`),
  UNIQUE KEY `IDPrenotazione_UNIQUE` (`IDPrenotazione`),
  KEY `Client_idx` (`FKClient`),
  KEY `Auto_idx` (`Targa`),
  KEY `FKStartSede_idx` (`FKEndSede`),
  KEY `FKEndSede` (`FKStartSede`),
  CONSTRAINT `Auto` FOREIGN KEY (`Targa`) REFERENCES `auto` (`Targa`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Client` FOREIGN KEY (`FKClient`) REFERENCES `client` (`Username`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKEndSede` FOREIGN KEY (`FKStartSede`) REFERENCES `sede` (`Sede`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKStartSede` FOREIGN KEY (`FKEndSede`) REFERENCES `sede` (`Sede`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `sede`
--

INSERT INTO `admin` VALUES ('admin','21232f297a57a5a743894a0e4a801fc3',1);

