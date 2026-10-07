<?php
/**
 * Registrar medición de peso y altura
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
$peso = $_POST["peso"] ?? "";
$altura = $_POST["altura"] ?? "";

if (empty($usuario_id)
    || empty($peso)
    || empty($altura)
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
INSERT INTO mediciones
(
    usuario_id,
    peso,
    altura
)
VALUES (?, ?, ?)
";

$stmt = $conexion->prepare($sql);

$stmt->bind_param(
    "idd",
    $usuario_id,
    $peso,
    $altura
);

if ($stmt->execute()) {

    /*
     * También actualizamos
     * los datos principales del usuario.
     */

    $sqlActualizar = "
    UPDATE usuarios
    SET
        peso = ?,
        altura = ?
    WHERE id = ?
    ";

    $stmtActualizar = $conexion->prepare(
        $sqlActualizar
    );

    $stmtActualizar->bind_param(
        "ddi",
        $peso,
        $altura,
        $usuario_id
    );

    $stmtActualizar->execute();

    $stmtActualizar->close();

    echo json_encode(
        [
            "success" => true,
            "message" => "Medición guardada correctamente"
        ]
    );

} else {

    echo json_encode(
        [
            "success" => false,
            "message" => "No se pudo guardar la medición"
        ]
    );
}

$stmt->close();

$conexion->close();

?>