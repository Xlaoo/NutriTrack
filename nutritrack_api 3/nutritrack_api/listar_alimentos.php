<?php
/**
 * Listar alimentos disponibles
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  NutriTrack
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://nuetritrack.com
 */

header("Content-Type: application/json; charset=UTF-8");

$conexion = new mysqli(
    "localhost",
    "root",
    "susaya123",
    "nutritrack"
);

if ($conexion->connect_error) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Error de conexión"
        ]
    );

    exit;
}

$conexion->set_charset("utf8mb4");

$sql = "
SELECT
    id,
    nombre,
    calorias,
    proteinas,
    carbohidratos,
    grasas,
    categoria,
    descripcion,
    azucares,
    sodio
FROM alimentos
ORDER BY nombre ASC
";

$resultado = $conexion->query($sql);

$alimentos = [];

if ($resultado) {

    while ($fila = $resultado->fetch_assoc()) {

        $alimentos[] = $fila;
    }
}

echo json_encode(
    [
        "success" => true,
        "alimentos" => $alimentos
    ]
);

$conexion->close();

?>