-- --------------------------------------------------------
-- Base de données de documentaires
-- --------------------------------------------------------
-- Version du serveur :  5.7.9
-- Version de PHP :  5.6.16
-- Generated on : 26 Février 2017 à 18:59
-- --------------------------------------------------------

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `documentaires`
--
CREATE DATABASE IF NOT EXISTS `documentaires` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `documentaires`;

-- --------------------------------------------------------
-- Structure de la table `ref_format`
-- --------------------------------------------------------

DROP TABLE IF EXISTS `ref_format`;
CREATE TABLE IF NOT EXISTS `ref_format` (
  `EXTENSION` varchar(5) NOT NULL,
  `NOM` varchar(200) NOT NULL,
  `ICONE` text NOT NULL,
  PRIMARY KEY (`EXTENSION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------
-- Structure de la table `ref_genre`
-- --------------------------------------------------------

DROP TABLE IF EXISTS `ref_genre`;
CREATE TABLE IF NOT EXISTS `ref_genre` (
  `ID` int(5) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(100) NOT NULL,
  `PARENT` int(5) DEFAULT NULL,
  `ICONE` longtext,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------
--
-- Structure de la table `ref_langue`
--

DROP TABLE IF EXISTS `ref_langue`;
CREATE TABLE IF NOT EXISTS `ref_langue` (
  `CODE` varchar(4) NOT NULL,
  `NOM` varchar(255) NOT NULL,
  `DRAPEAU` text NOT NULL,
  PRIMARY KEY (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `ref_pays`
--

DROP TABLE IF EXISTS `ref_pays`;
CREATE TABLE IF NOT EXISTS `ref_pays` (
  `NOM` varchar(255) NOT NULL,
  `DRAPEAU` text NOT NULL,
  PRIMARY KEY (`NOM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `t_documentaire`
--

DROP TABLE IF EXISTS `t_documentaire`;
CREATE TABLE IF NOT EXISTS `t_documentaire` (
  `ID` int(4) NOT NULL AUTO_INCREMENT,
  `TITRE` varchar(300) NOT NULL,
  `TITRE_ORIGINAL` varchar(300) NOT NULL,
  `DESCRIPTION` text NOT NULL,
  `REALISATEUR` varchar(100) NOT NULL,
  `ANNEE` int(4) NOT NULL,
  `DUREE` int(4) NOT NULL,
  `IMAGE` longtext NOT NULL,
  `GENRE` int(11) NOT NULL,
  `PAYS` varchar(100) DEFAULT NULL,
  `LANGUE` varchar(4) DEFAULT NULL,
  `SOUSTITRES` varchar(4) DEFAULT NULL,
  `NOTE` int(2) DEFAULT NULL,
  `VU` smallint(1) NOT NULL DEFAULT '0',
  `TAILLE` int(5) NOT NULL,
  `FORMAT` varchar(5) DEFAULT NULL,
  `BITRATE` int(10) DEFAULT NULL,
  `QUALITE` smallint(1) NOT NULL,
  `RESOLUTION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_documentaire_pays` (`PAYS`),
  KEY `fk_documentaire_genre` (`GENRE`),
  KEY `fk_documentaire_format` (`FORMAT`),
  KEY `fk_documentaire_langue` (`LANGUE`),
  KEY `fk_documentaire_soustitres` (`SOUSTITRES`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `t_episode`
--

DROP TABLE IF EXISTS `t_episode`;
CREATE TABLE IF NOT EXISTS `t_episode` (
  `ID` int(4) NOT NULL AUTO_INCREMENT,
  `TITRE` varchar(300) NOT NULL,
  `TITRE_ORIGINAL` varchar(300) NOT NULL,
  `DESCRIPTION` text NOT NULL,
  `REALISATEUR` varchar(100) NOT NULL,
  `ANNEE` int(4) NOT NULL,
  `DUREE` int(4) NOT NULL,
  `IMAGE` longtext NOT NULL,
  `LANGUE` varchar(4) NOT NULL,
  `SOUSTITRES` varchar(4) DEFAULT NULL,
  `NOTE` int(2) DEFAULT NULL,
  `VU` smallint(1) NOT NULL DEFAULT '0',
  `TAILLE` int(5) NOT NULL,
  `FORMAT` varchar(5) DEFAULT NULL,
  `BITRATE` int(10) DEFAULT NULL,
  `QUALITE` smallint(1) NOT NULL,
  `RESOLUTION` varchar(20) DEFAULT NULL,
  `SERIE` int(4) NOT NULL,
  `SAISON` int(2) NOT NULL,
  `EPISODE` int(3) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_episode_serie` (`SERIE`),
  KEY `fk_episode_format` (`FORMAT`),
  KEY `fk_episode_langue` (`LANGUE`),
  KEY `fk_episode_soustitres` (`SOUSTITRES`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `t_serie`
--

DROP TABLE IF EXISTS `t_serie`;
CREATE TABLE IF NOT EXISTS `t_serie` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TITRE` varchar(300) NOT NULL,
  `TITRE_ORIGINAL` varchar(300) NOT NULL,
  `DESCRIPTION` text NOT NULL,
  `IMAGE` longtext NOT NULL,
  `ANNEE_DEBUT` int(4) NOT NULL,
  `ANNEE_FIN` int(4) NOT NULL,
  `GENRE` int(11) NOT NULL,
  `PAYS` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_serie_genre` (`GENRE`),
  KEY `fk_serie_pays` (`PAYS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE IF NOT EXISTS `t_user` (
  `userid` varchar(50) NOT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  `nom` varchar(50) DEFAULT NULL,
  `image` text,
  `password` varchar(1000) NOT NULL,
  `isAdmin` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `t_documentaire`
--
ALTER TABLE `t_documentaire`
  ADD CONSTRAINT `fk_documentaire_format` FOREIGN KEY (`FORMAT`) REFERENCES `ref_format` (`EXTENSION`),
  ADD CONSTRAINT `fk_documentaire_genre` FOREIGN KEY (`GENRE`) REFERENCES `ref_genre` (`ID`),
  ADD CONSTRAINT `fk_documentaire_langue` FOREIGN KEY (`LANGUE`) REFERENCES `ref_langue` (`CODE`),
  ADD CONSTRAINT `fk_documentaire_pays` FOREIGN KEY (`PAYS`) REFERENCES `ref_pays` (`NOM`),
  ADD CONSTRAINT `fk_documentaire_soustitres` FOREIGN KEY (`SOUSTITRES`) REFERENCES `ref_langue` (`CODE`);

--
-- Contraintes pour la table `t_episode`
--
ALTER TABLE `t_episode`
  ADD CONSTRAINT `fk_episode_format` FOREIGN KEY (`FORMAT`) REFERENCES `ref_format` (`EXTENSION`),
  ADD CONSTRAINT `fk_episode_langue` FOREIGN KEY (`LANGUE`) REFERENCES `ref_langue` (`CODE`),
  ADD CONSTRAINT `fk_episode_serie` FOREIGN KEY (`SERIE`) REFERENCES `t_serie` (`ID`),
  ADD CONSTRAINT `fk_episode_soustitres` FOREIGN KEY (`SOUSTITRES`) REFERENCES `ref_langue` (`CODE`);

--
-- Contraintes pour la table `t_serie`
--
ALTER TABLE `t_serie`
  ADD CONSTRAINT `fk_serie_genre` FOREIGN KEY (`GENRE`) REFERENCES `ref_genre` (`ID`),
  ADD CONSTRAINT `fk_serie_pays` FOREIGN KEY (`PAYS`) REFERENCES `ref_pays` (`NOM`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
