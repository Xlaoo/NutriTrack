<?php

/**
 * Conexión a la base de datos
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  NutriTrack
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://nutritrack.com
 */
require_once "conexion.php";

$nombre = "Administrador";
$apellido = "NutriTrack";
$correo = "admin@gmail.com";
$password = "123456";
$rol = "administrador";

$passwordHash = password_hash(
    $password,
    PASSWORD_DEFAULT
);

$sql = "INSERT INTO usuarios
        (nombre, apellido, correo, password, rol)
        VALUES (?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);

$stmt->bind_param(
    "sssss",
    $nombre,
    $apellido,
    $correo,
    $passwordHash,
    $rol
);

if ($stmt->execute()) {

    echo "Administrador creado correctamente";

} else {

    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();

?>
