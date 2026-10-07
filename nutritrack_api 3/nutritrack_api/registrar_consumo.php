<?php
/**
 * Registrar consumo de alimentos
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  NutriTrack
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://nutritrack.com
 */

header("Content-Type: application/json; charset=UTF-8");

require_once "conexion.php";

$usuario_id = $_POST["usuario_id"] ?? "";
$alimento_id = $_POST["alimento_id"] ?? "";
$cantidad = $_POST["cantidad"] ?? "";
$unidad = $_POST["unidad"] ?? "";
$momento = $_POST["momento"] ?? "";
$fecha_consumo = $_POST["fecha_consumo"] ?? "";

if (empty($usuario_id)
    || empty($alimento_id)
    || empty($cantidad)
    || empty($unidad)
    || empty($momento)
    || empty($fecha_consumo)
) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Faltan datos"
        ]
    );

    exit;
}

$sql = "
INSERT INTO consumos
(
    usuario_id,
    alimento_id,
    cantidad,
    unidad,
    momento,
    fecha_consumo
)
VALUES (?, ?, ?, ?, ?, ?)
";

$stmt = $conexion->prepare($sql);

$stmt->bind_param(
    "iidsss",
    $usuario_id,
    $alimento_id,
    $cantidad,
    $unidad,
    $momento,
    $fecha_consumo
);

if ($stmt->execute()) {

    echo json_encode(
        [
            "success" => true,
            "message" => "Consumo registrado correctamente"
        ]
    );

} else {

    echo json_encode(
        [
            "success" => false,
            "message" => "No se pudo registrar el consumo"
        ]
    );
}

$stmt->close();
$conexion->close();

?>