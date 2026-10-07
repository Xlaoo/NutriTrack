<?php
/**
 * Obtener configuración del sistema
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

$host = "localhost";
$db   = "nutritrack";
$user = "root";
$pass = $_SERVER["DB_PASSWORD"] ?? getenv("DB_PASSWORD") ?: "";

try {

    try {
        $conexion = new PDO(
            "mysql:host=$host;dbname=$db;charset=utf8mb4",
            $user,
            $pass
        );
    } catch (PDOException $e) {
        if ($pass !== "") {
            $conexion = new PDO(
                "mysql:host=$host;dbname=$db;charset=utf8mb4",
                $user,
                ""
            );
        } else {
            throw $e;
        }
    }

    $conexion->setAttribute(
        PDO::ATTR_ERRMODE,
        PDO::ERRMODE_EXCEPTION
    );

    /*
     * Obtener configuración actual
     */

    $sql = "SELECT
                mantenimiento,
                notificaciones,
                registro_usuarios
            FROM configuracion
            WHERE id = 1
            LIMIT 1";

    $stmt = $conexion->prepare($sql);
    $stmt->execute();

    $configuracion = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$configuracion) {

        echo json_encode(
            [
                "success" => false,
                "mensaje" => "No existe configuración"
            ]
        );

        exit;
    }

    echo json_encode(
        [
            "success" => true,
            "mantenimiento" => (int)$configuracion["mantenimiento"],
            "notificaciones" => (int)$configuracion["notificaciones"],
            "registro_usuarios" => (int)$configuracion["registro_usuarios"]
        ]
    );

} catch (PDOException $e) {

    echo json_encode(
        [
            "success" => false,
            "mensaje" => "Error de conexión con la base de datos",
            "error" => $e->getMessage()
        ]
    );
}
?>