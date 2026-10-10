CREATE DATABASE  IF NOT EXISTS `nutritrack` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `nutritrack`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: nutritrack
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alimentos`
--

DROP TABLE IF EXISTS `alimentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alimentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `calorias` decimal(6,2) DEFAULT 0.00,
  `proteinas` decimal(6,2) DEFAULT 0.00,
  `carbohidratos` decimal(6,2) DEFAULT 0.00,
  `grasas` decimal(6,2) DEFAULT 0.00,
  `categoria` varchar(100) DEFAULT '',
  `descripcion` text DEFAULT NULL,
  `azucares` decimal(6,2) DEFAULT 0.00,
  `sodio` decimal(6,2) DEFAULT 0.00,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alimentos`
--

LOCK TABLES `alimentos` WRITE;
/*!40000 ALTER TABLE `alimentos` DISABLE KEYS */;
INSERT INTO `alimentos` VALUES (1,'Manzana',52.00,0.30,14.00,0.20,'Frutas','Manzana fresca rica en fibra y agua',10.40,1.00,'2026-10-10 18:49:48'),(2,'Plátano',89.00,1.10,22.80,0.30,'Frutas','Plátano maduro fuente natural de potasio',12.20,1.00,'2026-10-10 18:49:48'),(3,'Arroz blanco cocido',130.00,2.70,28.20,0.30,'Cereales','Arroz blanco cocido básico en la dieta',0.10,1.00,'2026-10-10 18:49:48'),(4,'Pechuga de pollo',165.00,31.00,0.00,3.60,'Carnes','Pechuga de pollo magra cocida a la plancha',0.00,74.00,'2026-10-10 18:49:48'),(5,'Huevo',155.00,13.00,1.10,11.00,'Proteínas','Huevo entero de gallina cocido',1.10,124.00,'2026-10-10 18:49:48'),(6,'Leche entera',61.00,3.20,4.80,3.30,'Lácteos','Leche entera pasteurizada líquida',5.10,43.00,'2026-10-10 18:49:48'),(7,'Avena',389.00,16.90,66.30,6.90,'Cereales','Avena en hojuelas integral rica en fibra',0.00,2.00,'2026-10-10 18:49:48'),(8,'Papa sancochada',87.00,1.90,20.10,0.10,'Tubérculos','Papa cocida o hervida sin piel',0.90,6.00,'2026-10-10 18:49:48'),(9,'Pan integral',247.00,13.00,41.30,3.40,'Panadería','Pan elaborado con harina de trigo integral',4.30,400.00,'2026-10-10 18:49:48'),(10,'Yogur natural',61.00,3.50,4.70,3.30,'Lácteos','Yogur natural sin azúcar añadida',4.70,46.00,'2026-10-10 18:49:48'),(11,'Pescado',105.00,22.00,0.00,1.20,'Pescados','Filete de pescado blanco cocido',0.00,68.00,'2026-10-10 18:49:48'),(12,'Zanahoria',41.00,0.90,9.60,0.20,'Verduras','Zanahoria fresca fuente de betacarotenos',4.70,69.00,'2026-10-10 18:49:48'),(13,'Brócoli',34.00,2.80,6.60,0.40,'Verduras','Brócoli fresco rico en antioxidantes y vitamina C',1.70,33.00,'2026-10-10 18:49:48'),(14,'Naranja',47.00,0.90,11.80,0.10,'Frutas','Naranja fresca rica en vitamina C',9.40,0.00,'2026-10-10 18:49:48'),(15,'Lentejas cocidas',116.00,9.00,20.10,0.40,'Legumbres','Lentejas cocidas ricas en hierro y proteína vegetal',1.80,2.00,'2026-10-10 18:49:48');
/*!40000 ALTER TABLE `alimentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `codigos_recuperacion`
--

DROP TABLE IF EXISTS `codigos_recuperacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `codigos_recuperacion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `codigo` varchar(50) NOT NULL,
  `token` varchar(100) DEFAULT NULL,
  `expiracion` datetime NOT NULL,
  `intentos` int(11) DEFAULT 0,
  `utilizado` tinyint(1) DEFAULT 0,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `token` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `codigos_recuperacion`
--

LOCK TABLES `codigos_recuperacion` WRITE;
/*!40000 ALTER TABLE `codigos_recuperacion` DISABLE KEYS */;
INSERT INTO `codigos_recuperacion` VALUES (1,1,'629118',NULL,'2026-10-07 04:57:44',0,1,'2026-10-07 02:42:44'),(2,1,'308221',NULL,'2026-10-07 04:59:50',0,1,'2026-10-07 02:44:50'),(3,1,'724144','73772ecf2755370aa13243a7ac4f1a3682b5ba1779b63974','2026-10-07 05:02:13',1,1,'2026-10-07 02:47:13');
/*!40000 ALTER TABLE `codigos_recuperacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion`
--

DROP TABLE IF EXISTS `configuracion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mantenimiento` tinyint(1) DEFAULT 0,
  `notificaciones` tinyint(1) DEFAULT 1,
  `registro_usuarios` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion`
--

LOCK TABLES `configuracion` WRITE;
/*!40000 ALTER TABLE `configuracion` DISABLE KEYS */;
INSERT INTO `configuracion` VALUES (1,0,1,1);
/*!40000 ALTER TABLE `configuracion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consumos`
--

DROP TABLE IF EXISTS `consumos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consumos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `alimento_id` int(11) NOT NULL,
  `cantidad` decimal(6,2) NOT NULL,
  `unidad` varchar(50) DEFAULT 'g',
  `momento` varchar(50) DEFAULT '',
  `fecha_consumo` varchar(50) NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `nombre_alimento` varchar(150) DEFAULT NULL,
  `calorias` decimal(8,2) DEFAULT 0.00,
  `proteinas` decimal(8,2) DEFAULT 0.00,
  `carbohidratos` decimal(8,2) DEFAULT 0.00,
  `grasas` decimal(8,2) DEFAULT 0.00,
  PRIMARY KEY (`id`),
  KEY `fk_consumos_usuario` (`usuario_id`),
  CONSTRAINT `fk_consumos_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumos`
--

LOCK TABLES `consumos` WRITE;
/*!40000 ALTER TABLE `consumos` DISABLE KEYS */;
INSERT INTO `consumos` VALUES (1,1,1,150.00,'gramos','Desayuno','2026-10-10','2026-10-10 18:54:19','Manzana',78.00,0.45,21.00,0.30),(2,1,2,200.00,'gramos','Desayuno','2026-10-10','2026-10-10 18:58:53','Plátano',178.00,2.20,45.60,0.60);
/*!40000 ALTER TABLE `consumos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mediciones`
--

DROP TABLE IF EXISTS `mediciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mediciones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `peso` decimal(5,2) NOT NULL,
  `altura` decimal(5,2) NOT NULL,
  `fecha_medicion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mediciones`
--

LOCK TABLES `mediciones` WRITE;
/*!40000 ALTER TABLE `mediciones` DISABLE KEYS */;
INSERT INTO `mediciones` VALUES (1,1,71.00,1.75,'2026-10-07 01:20:14');
/*!40000 ALTER TABLE `mediciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `edad` int(11) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `peso` decimal(5,2) DEFAULT NULL,
  `altura` decimal(5,2) DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `rol` varchar(50) DEFAULT 'usuario',
  `nivel_actividad` varchar(50) DEFAULT NULL,
  `objetivo_alimentacion` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Juan','Perez','juan.perez@example.com','$2y$10$FrVY43BNTKLHCjE2.QF5iuxDpLwOyYctDCkkskVYa6YjWpS3l5PWS',26,'Masculino',71.00,1.75,'2026-10-07 01:18:57','usuario','Moderado','Mantener peso'),(2,'Administrador','NutriTrack','admin@gmail.com','$2y$10$N7xuWYF6gdeTF.XJtWrKGe7alc8YLOw7CdN6Gciezv7jrcTY9OK9q',NULL,NULL,NULL,NULL,'2026-10-07 01:50:01','administrador',NULL,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-10 14:11:21
