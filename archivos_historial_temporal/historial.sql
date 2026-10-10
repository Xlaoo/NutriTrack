use nutritrack;
CREATE TABLE historial_alimentacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nombre_alimento VARCHAR(150) NOT NULL,
    cantidad DECIMAL(8,2) NOT NULL,
    unidad VARCHAR(30) DEFAULT 'gramos',
    calorias DECIMAL(8,2) DEFAULT 0,
    proteinas DECIMAL(8,2) DEFAULT 0,
    carbohidratos DECIMAL(8,2) DEFAULT 0,
    grasas DECIMAL(8,2) DEFAULT 0,
    tipo_comida VARCHAR(30),
    fecha_consumo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historial_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

SELECT
    id,
    nombre_alimento,
    cantidad,
    unidad,
    calorias,
    proteinas,
    carbohidratos,
    grasas,
    tipo_comida,
    fecha_consumo
FROM historial_alimentacion
WHERE usuario_id = 1
ORDER BY fecha_consumo DESC;