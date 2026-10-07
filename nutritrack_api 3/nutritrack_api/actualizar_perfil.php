<?php
/**
 * Actualizar perfil de usuario
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

$idUsuario = $_POST["idUsuario"] ?? "";
$nombre = $_POST["nombre"] ?? "";
$apellido = $_POST["apellido"] ?? "";
$correo = $_POST["correo"] ?? "";
$edad = $_POST["edad"] ?? "";
$sexo = $_POST["sexo"] ?? "";
$peso = $_POST["peso"] ?? "";
$altura = $_POST["altura"] ?? "";
$nivelActividad = $_POST["nivel_actividad"] ?? "";
$objetivo = $_POST["objetivo_alimentacion"] ?? "";

if (empty($idUsuario)
    || empty($nombre)
    || empty($apellido)
    || empty($correo)
) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Faltan datos obligatorios"
        ]
    );

    exit;
}

$sql = "
UPDATE usuarios
SET
    nombre = ?,
    apellido = ?,
    correo = ?,
    edad = NULLIF(?, ''),
    sexo = ?,
    peso = NULLIF(?, ''),
    altura = NULLIF(?, ''),
    nivel_actividad = ?,
    objetivo_alimentacion = ?
WHERE id = ?
";

$stmt = $conexion->prepare($sql);

$stmt->bind_param(
    "sssisddssi",
    $nombre,
    $apellido,
    $correo,
    $edad,
    $sexo,
    $peso,
    $altura,
    $nivelActividad,
    $objetivo,
    $idUsuario
);

if ($stmt->execute()) {

    echo json_encode(
        [
            "success" => true,
            "message" => "Perfil actualizado correctamente"
        ]
    );

} else {

    echo json_encode(
        [
            "success" => false,
            "message" => "No se pudo actualizar el perfil"
        ]
    );
}

$stmt->close();
$conexion->close();

?>