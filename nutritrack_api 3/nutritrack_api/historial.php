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

header("Content-Type: application/json; charset=UTF-8");

require_once "conexion.php";

// Aceptar parámetros vía GET o POST para compatibilidad con la app móvil
$usuario_id = $_GET["usuario_id"]
    ?? $_POST["usuario_id"]
    ?? $_GET["idUsuario"]
    ?? $_POST["idUsuario"]
    ?? null;

$usuario_id = filter_var($usuario_id, FILTER_VALIDATE_INT);

if (!$usuario_id || $usuario_id <= 0) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "error" => "Debes proporcionar un usuario_id válido."
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

try {
    // Consultar las 13 columnas de la tabla consumos, con respaldo en alimentos por si existen registros previos
    $sql = "
    SELECT
        c.id,
        c.usuario_id,
        c.alimento_id,
        c.cantidad,
        c.unidad,
        c.momento AS tipo_comida,
        c.fecha_consumo,
        c.fecha_registro,
        COALESCE(NULLIF(c.nombre_alimento, ''), a.nombre, 'Alimento') AS nombre_alimento,
        COALESCE(c.calorias, ROUND(a.calorias * (CASE WHEN LOWER(c.unidad) IN ('gramos', 'g', 'mililitros', 'ml') THEN c.cantidad / 100.0 WHEN LOWER(c.unidad) IN ('taza', 'tazas') THEN (c.cantidad * 240.0) / 100.0 ELSE c.cantidad END), 2), 0.00) AS calorias,
        COALESCE(c.proteinas, ROUND(a.proteinas * (CASE WHEN LOWER(c.unidad) IN ('gramos', 'g', 'mililitros', 'ml') THEN c.cantidad / 100.0 WHEN LOWER(c.unidad) IN ('taza', 'tazas') THEN (c.cantidad * 240.0) / 100.0 ELSE c.cantidad END), 2), 0.00) AS proteinas,
        COALESCE(c.carbohidratos, ROUND(a.carbohidratos * (CASE WHEN LOWER(c.unidad) IN ('gramos', 'g', 'mililitros', 'ml') THEN c.cantidad / 100.0 WHEN LOWER(c.unidad) IN ('taza', 'tazas') THEN (c.cantidad * 240.0) / 100.0 ELSE c.cantidad END), 2), 0.00) AS carbohidratos,
        COALESCE(c.grasas, ROUND(a.grasas * (CASE WHEN LOWER(c.unidad) IN ('gramos', 'g', 'mililitros', 'ml') THEN c.cantidad / 100.0 WHEN LOWER(c.unidad) IN ('taza', 'tazas') THEN (c.cantidad * 240.0) / 100.0 ELSE c.cantidad END), 2), 0.00) AS grasas
    FROM consumos c
    LEFT JOIN alimentos a ON c.alimento_id = a.id
    WHERE c.usuario_id = ?
    ORDER BY c.fecha_registro DESC, c.id DESC
    ";

    $stmt = $conexion->prepare($sql);

    if (!$stmt) {
        throw new Exception("No se pudo preparar la consulta del historial.");
    }

    $stmt->bind_param("i", $usuario_id);
    $stmt->execute();

    $resultado = $stmt->get_result();
    $historial = [];

    while ($fila = $resultado->fetch_assoc()) {
        $historial[] = [
            "id" => (int)$fila["id"],
            "usuario_id" => (int)$fila["usuario_id"],
            "alimento_id" => (int)$fila["alimento_id"],
            "nombre_alimento" => $fila["nombre_alimento"],
            "cantidad" => (string)$fila["cantidad"],
            "unidad" => $fila["unidad"],
            "calorias" => (string)$fila["calorias"],
            "proteinas" => (string)$fila["proteinas"],
            "carbohidratos" => (string)$fila["carbohidratos"],
            "grasas" => (string)$fila["grasas"],
            "tipo_comida" => !empty($fila["tipo_comida"]) ? $fila["tipo_comida"] : "General",
            "fecha_consumo" => !empty($fila["fecha_consumo"]) ? $fila["fecha_consumo"] : (string)$fila["fecha_registro"]
        ];
    }

    echo json_encode(
        [
            "success" => true,
            "total" => count($historial),
            "historial" => $historial
        ],
        JSON_UNESCAPED_UNICODE
    );

    $stmt->close();
    $conexion->close();

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode(
        [
            "success" => false,
            "error" => "Error al consultar el historial: " . $e->getMessage()
        ],
        JSON_UNESCAPED_UNICODE
    );
}
?>
