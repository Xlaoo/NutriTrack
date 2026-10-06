<?php
/**
 * Verificar estado de mantenimiento del sistema
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
$pass = "susaya123";

try {

    $conexion = new PDO(
        "mysql:host=$host;dbname=$db;charset=utf8mb4",
        $user,
        $pass
    );

    $conexion->setAttribute(
        PDO::ATTR_ERRMODE,
        PDO::ERRMODE_EXCEPTION
    );

    $sql = "SELECT mantenimiento
            FROM configuracion
            WHERE id = 1
            LIMIT 1";

    $stmt = $conexion->prepare($sql);
    $stmt->execute();

    $resultado = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($resultado) {

        echo json_encode(
            [
                "success" => true,
                "mantenimiento" => (int)$resultado["mantenimiento"]
            ]
        );

    } else {

        echo json_encode(
            [
                "success" => false,
                "mantenimiento" => 0
            ]
        );
    }

} catch (PDOException $e) {

    echo json_encode(
        [
            "success" => false,
            "mensaje" => "Error de conexión"
        ]
    );
}
?>