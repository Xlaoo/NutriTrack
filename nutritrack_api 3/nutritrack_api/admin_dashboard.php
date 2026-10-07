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
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

require_once "conexion.php";

try {

    // ==========================================
    // CANTIDAD DE USUARIOS
    // ==========================================

    $sqlUsuarios = "SELECT COUNT(*) AS total FROM usuarios WHERE rol = 'usuario'";

    $resultadoUsuarios = $conn->query($sqlUsuarios);

    if (!$resultadoUsuarios) {
        throw new Exception("Error al consultar usuarios");
    }

    $filaUsuarios = $resultadoUsuarios->fetch_assoc();

    $totalUsuarios = (int)$filaUsuarios["total"];


    // ==========================================
    // ÚLTIMO USUARIO REGISTRADO
    // ==========================================

    $sqlUltimoUsuario = "
        SELECT id, nombre, apellido, correo
        FROM usuarios
        WHERE rol = 'usuario'
        ORDER BY id DESC
        LIMIT 1
    ";

    $resultadoUltimo = $conn->query($sqlUltimoUsuario);

    $ultimoUsuario = null;

    if ($resultadoUltimo && $resultadoUltimo->num_rows > 0) {

        $ultimoUsuario = $resultadoUltimo->fetch_assoc();
    }


    // ==========================================
    // CANTIDAD DE ALIMENTOS
    // ==========================================

    $sqlAlimentos = "SELECT COUNT(*) AS total FROM alimentos";

    $resultadoAlimentos = $conn->query($sqlAlimentos);

    if (!$resultadoAlimentos) {
        throw new Exception("Error al consultar alimentos");
    }

    $filaAlimentos = $resultadoAlimentos->fetch_assoc();

    $totalAlimentos = (int)$filaAlimentos["total"];


    // ==========================================
    // CANTIDAD DE REGISTROS
    // ==========================================

    $sqlRegistros = "SELECT COUNT(*) AS total FROM consumos";

    $resultadoRegistros = $conn->query($sqlRegistros);

    if (!$resultadoRegistros) {
        throw new Exception("Error al consultar registros");
    }

    $filaRegistros = $resultadoRegistros->fetch_assoc();

    $totalRegistros = (int)$filaRegistros["total"];


    // ==========================================
    // RESPUESTA
    // ==========================================

    echo json_encode(
        [
            "success" => true,
            "usuarios" => $totalUsuarios,
            "alimentos" => $totalAlimentos,
            "registros" => $totalRegistros,
            "ultimo_usuario" => $ultimoUsuario
        ]
    );

} catch (Exception $e) {

    echo json_encode(
        [
            "success" => false,
            "message" => $e->getMessage()
        ]
    );
}

?>