CREATE TABLE IF NOT EXISTS `utilizations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teamId` varchar(45) NOT NULL,
  `teamName` varchar(45) NOT NULL,
  `channelId` varchar(45) NOT NULL,
  `channelName` varchar(45) NOT NULL,
  `slugRepo` varchar(45) NOT NULL,
  `incomingWebHook` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`teamId`,`channelId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;