<?php
/**
 * Reportes del sistema
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
header("Access-Control-Allow-Origin: *");

require_once "conexion.php";

try {

    // ==========================================
    // USUARIOS
    // ==========================================

    $sqlUsuarios
        = "SELECT COUNT(*) AS total FROM usuarios WHERE rol = 'usuario'";

    $resultadoUsuarios
        = $conn->query($sqlUsuarios);

    if (!$resultadoUsuarios) {
        throw new Exception(
            "Error al consultar usuarios: "
            . $conn->error
        );
    }

    $filaUsuarios
        = $resultadoUsuarios->fetch_assoc();

    $totalUsuarios
        = (int)$filaUsuarios["total"];


    // ==========================================
    // ALIMENTOS
    // ==========================================

    $sqlAlimentos
        = "SELECT COUNT(*) AS total FROM alimentos";

    $resultadoAlimentos
        = $conn->query($sqlAlimentos);

    if (!$resultadoAlimentos) {
        throw new Exception(
            "Error al consultar alimentos: "
            . $conn->error
        );
    }

    $filaAlimentos
        = $resultadoAlimentos->fetch_assoc();

    $totalAlimentos
        = (int)$filaAlimentos["total"];


    // ==========================================
    // CONSUMOS
    // ==========================================

    $sqlConsumos
        = "SELECT COUNT(*) AS total FROM consumos";

    $resultadoConsumos
        = $conn->query($sqlConsumos);

    if (!$resultadoConsumos) {
        throw new Exception(
            "Error al consultar consumos: "
            . $conn->error
        );
    }

    $filaConsumos
        = $resultadoConsumos->fetch_assoc();

    $totalConsumos
        = (int)$filaConsumos["total"];


    // ==========================================
    // RESPUESTA
    // ==========================================

    echo json_encode(
        [
            "success" => true,
            "usuarios" => $totalUsuarios,
            "alimentos" => $totalAlimentos,
            "consumos" => $totalConsumos
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (Exception $e) {

    http_response_code(500);

    echo json_encode(
        [
            "success" => false,
            "message" => $e->getMessage()
        ],
        JSON_UNESCAPED_UNICODE
    );
}

$conn->close();

?>