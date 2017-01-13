-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Värd: 127.0.0.1
-- Tid vid skapande: 13 jan 2017 kl 14:04
-- Serverversion: 10.1.16-MariaDB
-- PHP-version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databas: `kompis_kak`
--

-- --------------------------------------------------------

--
-- Tabellstruktur `favorittag`
--

CREATE TABLE `favorittag` (
  `ID` int(11) NOT NULL,
  `Tag_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumpning av Data i tabell `favorittag`
--

INSERT INTO `favorittag` (`ID`, `Tag_ID`) VALUES
(1, 0);

-- --------------------------------------------------------

--
-- Ersättningsstruktur för vy `infoingrediense`
--
CREATE TABLE `infoingrediense` (
`recept_id` int(11)
,`name` varchar(75)
,`amount` varchar(20)
);

-- --------------------------------------------------------

--
-- Ersättningsstruktur för vy `inforecept`
--
CREATE TABLE `inforecept` (
`ID` int(11)
,`ReceptNamn` varchar(75)
,`description` varchar(150)
,`TagNamn` varchar(45)
,`Author` varchar(45)
);

-- --------------------------------------------------------

--
-- Tabellstruktur `ingrediense`
--

CREATE TABLE `ingrediense` (
  `ID` int(11) NOT NULL,
  `name` varchar(75) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumpning av Data i tabell `ingrediense`
--

INSERT INTO `ingrediense` (`ID`, `name`) VALUES
(1, 'Socker'),
(2, 'Smör');

-- --------------------------------------------------------

--
-- Tabellstruktur `recept`
--

CREATE TABLE `recept` (
  `ID` int(11) NOT NULL,
  `name` varchar(75) NOT NULL,
  `description` varchar(150) NOT NULL,
  `author_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur `recept_collective`
--

CREATE TABLE `recept_collective` (
  `recept_id` int(11) NOT NULL,
  `amount` varchar(20) NOT NULL,
  `ing_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumpning av Data i tabell `recept_collective`
--

INSERT INTO `recept_collective` (`recept_id`, `amount`, `ing_id`) VALUES
(1, '2 tsk', 1),
(1, '2 liter', 2);

-- --------------------------------------------------------

--
-- Tabellstruktur `tag`
--

CREATE TABLE `tag` (
  `ID` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumpning av Data i tabell `tag`
--

INSERT INTO `tag` (`ID`, `name`, `description`) VALUES
(1, 'Efterrätt', 'Det bästa i en middag');

-- --------------------------------------------------------

--
-- Tabellstruktur `user`
--

CREATE TABLE `user` (
  `ID` int(11) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Privileges` int(3) NOT NULL DEFAULT '1',
  `Fav_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumpning av Data i tabell `user`
--

INSERT INTO `user` (`ID`, `Name`, `Privileges`, `Fav_ID`) VALUES
(1, 'Casper Bjork', 3, 1);

-- --------------------------------------------------------

--
-- Struktur för vy `infoingrediense`
--
DROP TABLE IF EXISTS `infoingrediense`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `infoingrediense`  AS  select `recept_collective`.`recept_id` AS `recept_id`,`ingrediense`.`name` AS `name`,`recept_collective`.`amount` AS `amount` from (`recept_collective` join `ingrediense`) where (`ingrediense`.`ID` = `recept_collective`.`ing_id`) ;

-- --------------------------------------------------------

--
-- Struktur för vy `inforecept`
--
DROP TABLE IF EXISTS `inforecept`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `inforecept`  AS  select `recept`.`ID` AS `ID`,`recept`.`name` AS `ReceptNamn`,`recept`.`description` AS `description`,`tag`.`name` AS `TagNamn`,`user`.`Name` AS `Author` from ((`recept` join `tag`) join `user`) where ((`tag`.`ID` = `recept`.`tag_id`) and (`user`.`ID` = `recept`.`author_id`)) ;

--
-- Index för dumpade tabeller
--

--
-- Index för tabell `favorittag`
--
ALTER TABLE `favorittag`
  ADD PRIMARY KEY (`ID`);

--
-- Index för tabell `ingrediense`
--
ALTER TABLE `ingrediense`
  ADD PRIMARY KEY (`ID`);

--
-- Index för tabell `recept`
--
ALTER TABLE `recept`
  ADD PRIMARY KEY (`ID`);

--
-- Index för tabell `tag`
--
ALTER TABLE `tag`
  ADD PRIMARY KEY (`ID`);

--
-- Index för tabell `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT för dumpade tabeller
--

--
-- AUTO_INCREMENT för tabell `favorittag`
--
ALTER TABLE `favorittag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT för tabell `ingrediense`
--
ALTER TABLE `ingrediense`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT för tabell `recept`
--
ALTER TABLE `recept`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT för tabell `tag`
--
ALTER TABLE `tag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT för tabell `user`
--
ALTER TABLE `user`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
