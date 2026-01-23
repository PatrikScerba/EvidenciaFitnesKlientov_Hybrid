SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

DROP DATABASE IF EXISTS `fk_evidencia_hybrid`;
CREATE DATABASE IF NOT EXISTS `fk_evidencia_hybrid` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `fk_evidencia_hybrid`;

DROP TABLE IF EXISTS `klienti`;
CREATE TABLE `klienti` (
  `id` int(11) NOT NULL,
  `krstne_meno` varchar(50) DEFAULT NULL,
  `priezvisko` varchar(50) DEFAULT NULL,
  `datum_narodenia` date DEFAULT NULL,
  `telefonne_cislo` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `adresa` varchar(255) DEFAULT NULL,
  `datum_registracie` date DEFAULT curdate(),
  `permanentka_platna_do` date DEFAULT NULL,
  `qr_cesta` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `vstupy`;
CREATE TABLE `vstupy` (
  `id` int(11) NOT NULL,
  `klient_id` int(11) NOT NULL,
  `datum` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


ALTER TABLE `klienti`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `vstupy`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `klient_id` (`klient_id`,`datum`);


ALTER TABLE `klienti`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `vstupy`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;


ALTER TABLE `vstupy`
  ADD CONSTRAINT `vstupy_klienti` FOREIGN KEY (`klient_id`) REFERENCES `klienti` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
SET FOREIGN_KEY_CHECKS=1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
