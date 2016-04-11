# ************************************************************
# Sequel Pro SQL dump
# Version 4135
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.42)
# Database: middleware
# Generation Time: 2016-04-02 19:40:35 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table IMAGE
# ------------------------------------------------------------

DROP TABLE IF EXISTS `IMAGE`;

CREATE TABLE `IMAGE` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `location` varchar(300) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `IMAGE` WRITE;
/*!40000 ALTER TABLE `IMAGE` DISABLE KEYS */;

INSERT INTO `IMAGE` (`id`, `user`, `name`, `location`)
VALUES
	(19,8,'Untitled Diagram.png','https://www.dropbox.com/s/bqbxj768x0fkndo/19_Untitled%20Diagram.png?dl=0'),
	(20,8,'Untitled Diagram.png','https://www.dropbox.com/s/gdd3e4b2t0bj0ag/20_Untitled%20Diagram.png?raw=1'),
	(21,8,'Untitled Diagram.png','https://www.dropbox.com/s/sdqn1z9l4zhyulj/21_Untitled%20Diagram.png?raw=1'),
	(22,8,'Untitled Diagram.png','https://www.dropbox.com/s/mp1eco31o7wpyoa/22_Untitled%20Diagram.png?raw=1'),
	(23,8,'file000555007525.jpg','https://www.dropbox.com/s/fybxah1c2m8sgul/23_file000555007525.jpg?raw=1'),
	(24,8,'file000132701536.jpg','https://www.dropbox.com/s/8w4q6pdxwmaqiei/24_file000132701536.jpg?raw=1'),
	(25,8,'file000132701536.jpg','https://www.dropbox.com/s/4een7bbsi9srnte/25_file000132701536.jpg?raw=1'),
	(26,8,'file000267804564.jpg','https://www.dropbox.com/s/794ku7qvaee3sb6/26_file000267804564.jpg?raw=1'),
	(27,8,'file0001971407787.jpg','https://www.dropbox.com/s/c4otfnz1k395xk1/27_file0001971407787.jpg?raw=1');

/*!40000 ALTER TABLE `IMAGE` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table SERVICE
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SERVICE`;

CREATE TABLE `SERVICE` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `redirecturl` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `SERVICE` WRITE;
/*!40000 ALTER TABLE `SERVICE` DISABLE KEYS */;

INSERT INTO `SERVICE` (`id`, `name`, `redirecturl`)
VALUES
	(1,'self','/');

/*!40000 ALTER TABLE `SERVICE` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table SESSION
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SESSION`;

CREATE TABLE `SESSION` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `token` varchar(200) NOT NULL DEFAULT '',
  `expiration` datetime NOT NULL,
  `service` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `SESSION` WRITE;
/*!40000 ALTER TABLE `SESSION` DISABLE KEYS */;

INSERT INTO `SESSION` (`id`, `user`, `token`, `expiration`, `service`)
VALUES
	(4,1,'hvj3vjo7k4g0s20psgl1v98ndf','2016-04-02 15:12:30',1),
	(5,8,'c29d09f8r0v0e9ovdcmf0vrd07','2016-04-02 15:34:09',1),
	(6,8,'eppfl76tjk2ntu4d1p4991hb63','2016-04-03 16:32:02',1);

/*!40000 ALTER TABLE `SESSION` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table USER
# ------------------------------------------------------------

DROP TABLE IF EXISTS `USER`;

CREATE TABLE `USER` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(40) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  `password` varchar(100) NOT NULL DEFAULT '',
  `dropboxToken` varchar(100) DEFAULT '',
  `dropboxTemp` varchar(100) DEFAULT NULL,
  `pairedWithDropbox` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;

INSERT INTO `USER` (`id`, `email`, `name`, `password`, `dropboxToken`, `dropboxTemp`, `pairedWithDropbox`)
VALUES
	(6,'sdjhvjg','lh','bmhgv ','iwGYtHwvJAEAAAAAAAAVMDJCNHcmU2VcojFur-Xy1uc24dUAQFj4bP_H68XlyTcy',NULL,0),
	(8,'andreagulino@me.com','Andrea','ciao','iwGYtHwvJAEAAAAAAAAVTwRIP27dAvZSy6H7UKFT32pli9tTDmDCKgSOnpMCOtSj','https://www.dropbox.com/1/oauth2/authorize?locale=it_IT&client_id=jz090j7tkxkqjc0&response_type=code',1);

/*!40000 ALTER TABLE `USER` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
