CREATE DATABASE nutritrack;
USE nutritrack;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    edad INT,
    sexo VARCHAR(20),
    peso DECIMAL(5,2),
    altura DECIMAL(5,2),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

USE nutritrack;

SELECT * FROM usuarios;