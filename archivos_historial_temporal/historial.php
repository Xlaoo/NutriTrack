<?php
/**
 * Obtener historial de alimentación del usuario
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  NutriTrack
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://nutritrack.com
 */

header('Content-Type: application/json; charset=utf-8');

require_once 'conexion.php';

// Verificar el método de consulta
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(
        [
            'error' => 'Método no permitido. Utiliza GET.'
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

// Recibir el ID del usuario
$usuario_id = filter_input(
    INPUT_GET,
    'usuario_id',
    FILTER_VALIDATE_INT
);

if (!$usuario_id || $usuario_id <= 0) {
    http_response_code(400);
    echo json_encode(
        [
            'error' => 'Debes proporcionar un usuario_id válido.'
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

try {
    // Consultar el historial del usuario
    $sql = "SELECT
                id,
                nombre_alimento,
                cantidad,
                unidad,
                calorias,
                proteinas,
                carbohidratos,
                grasas,
                tipo_comida,
                fecha_consumo
            FROM historial_alimentacion
            WHERE usuario_id = ?
            ORDER BY fecha_consumo DESC";

    $stmt = $conn->prepare($sql);

    if (!$stmt) {
        throw new Exception('No se pudo preparar la consulta.');
    }

    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();

    $resultado = $stmt->get_result();
    $historial = [];

    while ($fila = $resultado->fetch_assoc()) {
        $historial[] = $fila;
    }

    echo json_encode(
        [
            'success' => true,
            'total' => count($historial),
            'historial' => $historial
        ],
        JSON_UNESCAPED_UNICODE
    );

    $stmt->close();
    $conn->close();

} catch (Throwable $e) {
    http_response_code(500);

    echo json_encode(
        [
            'success' => false,
            'error' => 'Error al consultar el historial.'
        ],
        JSON_UNESCAPED_UNICODE
    );
}
?>