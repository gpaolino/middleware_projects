# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.5.42)
# Database: middleware
# Generation Time: 2016-04-11 00:06:23 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table CONSUMER
# ------------------------------------------------------------

DROP TABLE IF EXISTS `CONSUMER`;

CREATE TABLE `CONSUMER` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `app_name` varchar(100) NOT NULL DEFAULT '',
  `oauth_consumer_key` varchar(100) NOT NULL DEFAULT '',
  `oauth_signature` varchar(100) NOT NULL,
  `user` int(11) NOT NULL,
  `oauth_callback` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table IMAGE
# ------------------------------------------------------------

DROP TABLE IF EXISTS `IMAGE`;

CREATE TABLE `IMAGE` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `file_name` varchar(50) DEFAULT NULL,
  `location` varchar(300) DEFAULT '',
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table SESSION
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SESSION`;

CREATE TABLE `SESSION` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `token` varchar(200) NOT NULL DEFAULT '',
  `expiration` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table UPLOADSESSION
# ------------------------------------------------------------

DROP TABLE IF EXISTS `UPLOADSESSION`;

CREATE TABLE `UPLOADSESSION` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `status` int(11) DEFAULT NULL,
  `img1` varchar(120) DEFAULT NULL,
  `img2` varchar(120) DEFAULT NULL,
  `img3` varchar(120) DEFAULT NULL,
  `img4` varchar(120) DEFAULT NULL,
  `isCrop` tinyint(1) DEFAULT NULL,
  `isGreyScale` tinyint(1) DEFAULT NULL,
  `uploaded` int(11) DEFAULT NULL,
  `result` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



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




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
