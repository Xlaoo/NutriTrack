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

$servidor = "localhost";
$usuario = "root";
$password = getenv("DB_PASSWORD");
$base_datos = "nutritrack";

$conn = new mysqli(
    $servidor,
    $usuario,
    $password,
    $base_datos
);

if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

?>