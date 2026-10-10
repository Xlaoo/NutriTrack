<?php
/**
 * Registrar consumo de alimentos - HU-03 NutriTrack
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

// 1. Recibir parámetros enviados por Android vía POST
$usuario_id = $_POST["usuario_id"] ?? "";
$alimento_id = $_POST["alimento_id"] ?? "";
$cantidad = $_POST["cantidad"] ?? "";
$unidad = $_POST["unidad"] ?? "";
$momento = $_POST["momento"] ?? "";
$fecha_consumo = $_POST["fecha_consumo"] ?? "";

// 2. Validaciones básicas de presencia
if (empty($usuario_id)
    || empty($alimento_id)
    || $cantidad === ""
    || empty($unidad)
    || empty($momento)
    || empty($fecha_consumo)
) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "message" => "Todos los campos son obligatorios para registrar el consumo"
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

// 3. Validación de usuario_id
$usuario_id_val = filter_var($usuario_id, FILTER_VALIDATE_INT);
if ($usuario_id_val === false || $usuario_id_val <= 0) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "message" => "El ID de usuario no es válido"
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

// 4. Validación de alimento_id
$alimento_id_val = filter_var($alimento_id, FILTER_VALIDATE_INT);
if ($alimento_id_val === false || $alimento_id_val <= 0) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "message" => "El ID del alimento no es válido"
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

// 5. Validación de cantidad (numérica, finita y mayor que cero)
if (!is_numeric($cantidad)) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "message" => "La cantidad debe ser un valor numérico válido"
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

$cantidad_val = (float)$cantidad;
if ($cantidad_val <= 0 || is_nan($cantidad_val) || is_infinite($cantidad_val)) {
    http_response_code(400);
    echo json_encode(
        [
            "success" => false,
            "message" => "La cantidad consumida debe ser mayor a cero"
        ],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

try {
    // 6. Verificar que el usuario exista en la tabla usuarios (respetar clave foránea fk_consumos_usuario)
    $sqlUsuario = "SELECT id FROM usuarios WHERE id = ?";
    $stmtUsuario = $conexion->prepare($sqlUsuario);
    if (!$stmtUsuario) {
        throw new Exception("Error al preparar validación de usuario");
    }
    $stmtUsuario->bind_param("i", $usuario_id_val);
    $stmtUsuario->execute();
    $resUsuario = $stmtUsuario->get_result();

    if ($resUsuario->num_rows === 0) {
        $stmtUsuario->close();
        http_response_code(404);
        echo json_encode(
            [
                "success" => false,
                "message" => "El usuario autenticado no existe en la base de datos"
            ],
            JSON_UNESCAPED_UNICODE
        );
        exit;
    }
    $stmtUsuario->close();

    // 7. Obtener los valores nutricionales base del alimento desde la tabla alimentos
    $sqlAlimento = "SELECT nombre, calorias, proteinas, carbohidratos, grasas FROM alimentos WHERE id = ?";
    $stmtAlimento = $conexion->prepare($sqlAlimento);
    if (!$stmtAlimento) {
        throw new Exception("Error al preparar consulta de alimento");
    }
    $stmtAlimento->bind_param("i", $alimento_id_val);
    $stmtAlimento->execute();
    $resAlimento = $stmtAlimento->get_result();

    if ($resAlimento->num_rows === 0) {
        $stmtAlimento->close();
        http_response_code(404);
        echo json_encode(
            [
                "success" => false,
                "message" => "El alimento seleccionado no existe en el catálogo"
            ],
            JSON_UNESCAPED_UNICODE
        );
        exit;
    }

    $alimento = $resAlimento->fetch_assoc();
    $stmtAlimento->close();

    $nombre_alimento = $alimento["nombre"];
    $unidad_limpia = strtolower(trim((string)$unidad));

    // 8. Calcular los macronutrientes y calorías según cantidad y unidad
    if ($unidad_limpia === "gramos" || $unidad_limpia === "g" || $unidad_limpia === "mililitros" || $unidad_limpia === "ml") {
        $factor = $cantidad_val / 100.0;
    } elseif ($unidad_limpia === "taza" || $unidad_limpia === "tazas") {
        $factor = ($cantidad_val * 240.0) / 100.0;
    } else {
        // "unidad", "porción", "porcion" u otra medida directa
        $factor = $cantidad_val > 0 ? $cantidad_val : 1.0;
    }

    $calorias = round(((float)$alimento["calorias"]) * $factor, 2);
    $proteinas = round(((float)$alimento["proteinas"]) * $factor, 2);
    $carbohidratos = round(((float)$alimento["carbohidratos"]) * $factor, 2);
    $grasas = round(((float)$alimento["grasas"]) * $factor, 2);

    // 9. Insertar el consumo en las 13 columnas de la tabla consumos
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
        $usuario_id_val,
        $alimento_id_val,
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
            ],
            JSON_UNESCAPED_UNICODE
        );
    } else {
        http_response_code(500);
        echo json_encode(
            [
                "success" => false,
                "message" => "No se pudo registrar el consumo en la base de datos"
            ],
            JSON_UNESCAPED_UNICODE
        );
    }

    $stmt->close();
    $conexion->close();

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode(
        [
            "success" => false,
            "message" => "Error del servidor: " . $e->getMessage()
        ],
        JSON_UNESCAPED_UNICODE
    );
}
?>