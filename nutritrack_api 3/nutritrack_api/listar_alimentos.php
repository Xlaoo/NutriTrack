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

require_once "conexion.php";

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