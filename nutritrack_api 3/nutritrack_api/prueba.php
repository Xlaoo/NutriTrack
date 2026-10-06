<?php
/**
 * Página de planes y precios
 * Muestra los planes disponibles y permite gestionar suscripciones
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  Puente
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://puente.com
 */
$servidor = "localhost";
$usuario = "root";
$password = "susaya123";
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

echo "Conexión exitosa a la base de datos nutritrack";

?>