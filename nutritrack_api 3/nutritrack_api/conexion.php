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
$password = $_SERVER["DB_PASSWORD"] ?? getenv("DB_PASSWORD") ?: "";
$base_datos = "nutritrack";

$conn = null;
try {
    $conn = new mysqli($servidor, $usuario, $password, $base_datos);
} catch (mysqli_sql_exception $e) {
    if ($password !== "") {
        try {
            $conn = new mysqli($servidor, $usuario, "", $base_datos);
        } catch (mysqli_sql_exception $e2) {
            http_response_code(500);
            echo json_encode(
                [
                    "success" => false,
                    "message" => "Error de conexión con la base de datos"
                ],
                JSON_UNESCAPED_UNICODE
            );
            exit;
        }
    } else {
        http_response_code(500);
        echo json_encode(
            [
                "success" => false,
                "message" => "Error de conexión con la base de datos"
            ],
            JSON_UNESCAPED_UNICODE
        );
        exit;
    }
}

$conn->set_charset("utf8mb4");

// Alias para compatibilidad con archivos que usan $conexion
$conexion = $conn;

?>