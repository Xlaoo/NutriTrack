<?php
/**
 * Actualizar configuración del sistema
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

    /*
     * Recibir valores enviados por Android
     */

    $mantenimiento = isset($_POST["mantenimiento"])
        ? (int)$_POST["mantenimiento"]
        : 0;

    $notificaciones = isset($_POST["notificaciones"])
        ? (int)$_POST["notificaciones"]
        : 1;

    $registroUsuarios = isset($_POST["registro_usuarios"])
        ? (int)$_POST["registro_usuarios"]
        : 1;


    /*
     * Actualizar configuración
     */

    $sql = "UPDATE configuracion
            SET mantenimiento = :mantenimiento,
                notificaciones = :notificaciones,
                registro_usuarios = :registro_usuarios
            WHERE id = 1";

    $stmt = $conexion->prepare($sql);

    $stmt->bindParam(
        ":mantenimiento",
        $mantenimiento,
        PDO::PARAM_INT
    );

    $stmt->bindParam(
        ":notificaciones",
        $notificaciones,
        PDO::PARAM_INT
    );

    $stmt->bindParam(
        ":registro_usuarios",
        $registroUsuarios,
        PDO::PARAM_INT
    );

    $stmt->execute();


    echo json_encode(
        [
            "success" => true,
            "mensaje" => "Configuración actualizada correctamente"
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (PDOException $e) {

    echo json_encode(
        [
            "success" => false,
            "mensaje" => "Error al actualizar configuración",
            "error" => $e->getMessage()
        ],
        JSON_UNESCAPED_UNICODE
    );
}
?>