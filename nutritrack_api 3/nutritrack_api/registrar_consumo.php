<?php
/**
 * Registrar consumo de alimentos
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

$usuario_id = $_POST["usuario_id"] ?? "";
$alimento_id = $_POST["alimento_id"] ?? "";
$cantidad = $_POST["cantidad"] ?? "";
$unidad = $_POST["unidad"] ?? "";
$momento = $_POST["momento"] ?? "";
$fecha_consumo = $_POST["fecha_consumo"] ?? "";

if (empty($usuario_id)
    || empty($alimento_id)
    || empty($cantidad)
    || empty($unidad)
    || empty($momento)
    || empty($fecha_consumo)
) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Faltan datos requeridos para registrar el consumo"
        ]
    );

    exit;
}

try {
    // 1. Obtener los valores nutricionales base del alimento
    $sqlAlimento = "SELECT nombre, calorias, proteinas, carbohidratos, grasas FROM alimentos WHERE id = ?";
    $stmtAlimento = $conexion->prepare($sqlAlimento);

    if (!$stmtAlimento) {
        throw new Exception("Error al preparar consulta de alimento");
    }

    $stmtAlimento->bind_param("i", $alimento_id);
    $stmtAlimento->execute();
    $resAlimento = $stmtAlimento->get_result();

    if ($resAlimento->num_rows === 0) {
        $stmtAlimento->close();
        echo json_encode(
            [
                "success" => false,
                "message" => "El alimento seleccionado no existe en el sistema"
            ]
        );
        exit;
    }

    $alimento = $resAlimento->fetch_assoc();
    $stmtAlimento->close();

    $nombre_alimento = $alimento["nombre"];
    $cantidad_val = (float)$cantidad;
    $unidad_limpia = strtolower(trim((string)$unidad));

    // 2. Calcular los macronutrientes y calorías proporcionales a la cantidad y unidad
    if ($unidad_limpia === "gramos" || $unidad_limpia === "g" || $unidad_limpia === "mililitros" || $unidad_limpia === "ml") {
        $factor = $cantidad_val / 100.0;
    } elseif ($unidad_limpia === "taza" || $unidad_limpia === "tazas") {
        $factor = ($cantidad_val * 240.0) / 100.0;
    } else {
        // "unidad", "porción", "porcion" u otra medida
        $factor = $cantidad_val > 0 ? $cantidad_val : 1.0;
    }

    $calorias = round(((float)$alimento["calorias"]) * $factor, 2);
    $proteinas = round(((float)$alimento["proteinas"]) * $factor, 2);
    $carbohidratos = round(((float)$alimento["carbohidratos"]) * $factor, 2);
    $grasas = round(((float)$alimento["grasas"]) * $factor, 2);

    // 3. Insertar el consumo con sus 11 columnas de datos en la tabla consumos
    $sql = "
    INSERT INTO consumos
    (
        usuario_id,
        alimento_id,
        cantidad,
        unidad,
        momento,
        fecha_consumo,
        nombre_alimento,
        calorias,
        proteinas,
        carbohidratos,
        grasas
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ";

    $stmt = $conexion->prepare($sql);

    if (!$stmt) {
        throw new Exception("Error al preparar la inserción en consumos");
    }

    $stmt->bind_param(
        "iidssssdddd",
        $usuario_id,
        $alimento_id,
        $cantidad_val,
        $unidad,
        $momento,
        $fecha_consumo,
        $nombre_alimento,
        $calorias,
        $proteinas,
        $carbohidratos,
        $grasas
    );

    if ($stmt->execute()) {
        echo json_encode(
            [
                "success" => true,
                "message" => "Consumo registrado correctamente"
            ]
        );
    } else {
        echo json_encode(
            [
                "success" => false,
                "message" => "No se pudo registrar el consumo"
            ]
        );
    }

    $stmt->close();
    $conexion->close();

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode(
        [
            "success" => false,
            "message" => "Error del servidor al registrar consumo: " . $e->getMessage()
        ]
    );
}
?>