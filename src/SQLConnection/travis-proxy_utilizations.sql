-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: travis-proxy
-- ------------------------------------------------------
-- Server version	5.7.10-log

--
-- Table structure for table `utilizations`
--
CREATE DATABASE IF NOT EXISTS travis-proxy;
USE travis-proxy;
CREATE TABLE IF NOT EXISTS `utilizations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teamId` varchar(45) NOT NULL,
  `teamName` varchar(45) NOT NULL,
  `channelId` varchar(45) NOT NULL,
  `channelName` varchar(45) NOT NULL,
  `slugRepo` varchar(45) NOT NULL,
  `incomingWebHook` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


