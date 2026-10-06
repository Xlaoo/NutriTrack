<?php
/**
 * Obtener progreso del usuario
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

$idUsuario = $_POST["idUsuario"] ?? "";

if (empty($idUsuario)) {

    echo json_encode(
        [
            "success" => false,
            "message" => "ID de usuario requerido"
        ]
    );

    exit;
}

/*
 * Datos actuales del usuario
 */

$sqlUsuario = "
SELECT
    peso,
    altura
FROM usuarios
WHERE id = ?
";

$stmtUsuario = $conexion->prepare($sqlUsuario);

$stmtUsuario->bind_param(
    "i",
    $idUsuario
);

$stmtUsuario->execute();

$resultadoUsuario = $stmtUsuario->get_result();

if ($resultadoUsuario->num_rows == 0) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Usuario no encontrado"
        ]
    );

    exit;
}

$usuario = $resultadoUsuario->fetch_assoc();

$stmtUsuario->close();

/*
 * Historial
 */

$sqlMediciones = "
SELECT
    id,
    peso,
    altura,
    fecha_medicion
FROM mediciones
WHERE usuario_id = ?
ORDER BY fecha_medicion DESC
";

$stmtMediciones = $conexion->prepare($sqlMediciones);

$stmtMediciones->bind_param(
    "i",
    $idUsuario
);

$stmtMediciones->execute();

$resultadoMediciones = $stmtMediciones->get_result();

$mediciones = [];

while ($fila = $resultadoMediciones->fetch_assoc()) {

    $mediciones[] = $fila;
}

echo json_encode(
    [
        "success" => true,
        "usuario" => $usuario,
        "mediciones" => $mediciones
    ]
);

$stmtMediciones->close();

$conexion->close();

?>