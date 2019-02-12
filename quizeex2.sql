-- phpMyAdmin SQL Dump
-- version 4.8.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Feb 05, 2019 at 09:57 AM
-- Server version: 10.1.33-MariaDB
-- PHP Version: 7.2.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quizeex`
--

-- --------------------------------------------------------

--
-- Table structure for table `answers`
--

CREATE TABLE `answers` (
  `id` int(11) NOT NULL,
  `id_question` int(11) NOT NULL,
  `answer` text COLLATE utf8_polish_ci NOT NULL,
  `is_correct` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `answers`
--

INSERT INTO `answers` (`id`, `id_question`, `answer`, `is_correct`) VALUES
(6, 2, 'Cztery', 0),
(7, 2, 'Trzy', 0),
(8, 2, 'Dwie', 1),
(9, 3, 'Invercargill', 1),
(10, 3, 'Oban', 0),
(11, 3, 'Christchurch', 0),
(12, 3, 'Queenstown', 0),
(17, 5, '13', 0),
(18, 5, '14', 0),
(19, 5, '16', 1),
(20, 5, '21', 0),
(21, 6, 'Góra Josepha Franza', 0),
(22, 6, 'Góra Cooka', 1),
(23, 6, 'Akaroa', 0),
(24, 6, 'Cape Reinga', 0),
(27, 8, 'Iwi', 0),
(28, 8, 'Te Reo', 0),
(29, 8, 'Aotearoa', 1),
(30, 8, 'Haka', 0),
(31, 9, 'Sprinkbogs', 0),
(32, 9, 'All Blacks', 1),
(33, 9, 'Wallabies', 0),
(34, 9, 'Los Pumas', 0),
(35, 10, '7', 0),
(36, 10, '8', 1),
(37, 10, '9', 0),
(38, 10, '10', 0),
(39, 11, 'Jowisz', 0),
(40, 11, 'Wenus', 1),
(41, 11, 'Neptun', 0),
(42, 11, 'Uran', 0),
(43, 12, 'Pas planetoid', 1),
(44, 12, 'Obłok Oorta', 0),
(45, 12, 'Nibiru', 0),
(46, 12, 'Makemake', 0),
(47, 13, '1', 0),
(48, 13, '2', 1),
(49, 13, '4', 0),
(50, 13, '5', 0),
(51, 14, 'Ziemia', 1),
(52, 14, 'Neptun', 0),
(53, 14, 'Uran', 0),
(54, 14, 'Saturn', 0),
(55, 15, 'Słońce', 1),
(56, 15, 'Księżyc', 0),
(57, 15, 'Płaska Ziemia', 0),
(58, 15, 'Czarna Dziura', 0),
(59, 4, 'Tak', 1),
(60, 4, 'Nie', 0),
(61, 16, 'Ceres', 1),
(62, 16, 'Vesta', 0),
(63, 16, 'Sedna', 0),
(64, 16, 'Haumea', 0),
(65, 17, 'Za Marsem', 0),
(66, 17, 'Za Neptunem', 1),
(67, 17, 'Za Uranem', 0),
(68, 17, 'Za Merkurym', 0);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `id` int(11) NOT NULL,
  `id_quiz` int(11) NOT NULL,
  `question` text COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `id_quiz`, `question`) VALUES
(2, 14, 'Na ile wysp dzieli się Nowa Zelandia?'),
(3, 14, 'Jak nazywa się miasto położone najbardziej na południe?'),
(4, 14, 'Czy Tokelau jest terytorium zależnym Nowej Zelandii?'),
(5, 14, 'Na ile regionów dzieli się Nowa Zelandia?'),
(6, 14, 'Jak nazywa się najwyższy szczyt w Nowej Zelandii?'),
(8, 14, 'Jak brzmi nazwa tego kraju w języku maoryskim?'),
(9, 14, 'Jak nazywa się nowozelandzka drużyna rugby?'),
(10, 16, 'Ile jest planet w układzie słonecznym?'),
(11, 16, 'Która z tych planet należy do planet wewnętrznych?'),
(12, 16, 'Co znajduje się pomiędzy Marsem, a Jowiszem?'),
(13, 16, 'Ile księżycy ma Mars?'),
(14, 16, 'Która planeta jest nazywana \'błękitną planetą\'?'),
(15, 16, 'Co znajduje się w centrum układu słonecznego?'),
(16, 16, 'Jak nazywa się największy obiekt znajdujący się w pasie planetoid?'),
(17, 16, 'Gdzie znajduje się pas Kuipera?');

-- --------------------------------------------------------

--
-- Table structure for table `quizzes`
--

CREATE TABLE `quizzes` (
  `id` int(11) NOT NULL,
  `name` text COLLATE utf8_polish_ci NOT NULL,
  `category` text COLLATE utf8_polish_ci NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `quizzes`
--

INSERT INTO `quizzes` (`id`, `name`, `category`, `active`) VALUES
(14, 'Test wiedzy o Nowej Zelandii', 'Geografia', 1),
(16, 'Układ Słoneczny', 'Astronomia', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(256) COLLATE utf8_polish_ci NOT NULL,
  `name` varchar(64) COLLATE utf8_polish_ci NOT NULL,
  `surname` varchar(64) COLLATE utf8_polish_ci NOT NULL,
  `password` text COLLATE utf8_polish_ci NOT NULL,
  `role` varchar(64) COLLATE utf8_polish_ci NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `points` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `name`, `surname`, `password`, `role`, `active`, `points`) VALUES
(1, 'admin@quizeex.com', 'Admin', 'A.', 'qazwsx', 'admin', 1, 0),
(2, 'johndoe@example.com', 'John', 'Doe', 'qazwsx', 'user', 1, 47),
(3, 'js@blackpearl.com', 'Jack', 'Sparrow', 'qwerty', 'user', 0, 16),
(4, 'jannowak@example.com', 'Jan', 'Nowak', 'ijnmko', 'user', 1, 16);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `answers`
--
ALTER TABLE `answers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_question` (`id_question`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_quiz` (`id_quiz`);

--
-- Indexes for table `quizzes`
--
ALTER TABLE `quizzes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `answers`
--
ALTER TABLE `answers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `quizzes`
--
ALTER TABLE `quizzes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`id_question`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`id_quiz`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
