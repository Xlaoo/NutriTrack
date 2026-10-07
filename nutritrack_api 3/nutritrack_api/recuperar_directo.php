<?php
/**
 * Recuperación directa de contraseña para entorno LOCAL DE PRUEBAS
 * 
 * SEGURIDAD:
 * Este endpoint permite probar el flujo de cambio de contraseña en entorno local
 * sin necesidad de servicio de correo SMTP ni códigos temporales.
 * 
 * Por seguridad está protegido mediante:
 * 1. Bandera MODO_PRUEBAS_LOCAL_RECUPERACION (fácilmente conmutable a false para producción).
 * 2. Validación estricta de IP de origen (solo localhost o emulador Android 10.0.2.2).
 * 
 * PHP version 8.0+
 */

header("Content-Type: application/json; charset=UTF-8");

require_once "conexion.php";

// =============================================================================
// INTERRUPTOR DE SEGURIDAD PARA PRUEBAS LOCALES
// Cambiar a FALSE antes de publicar o desplegar en producción.
// =============================================================================
const MODO_PRUEBAS_LOCAL_RECUPERACION = true;

// 1. Bloqueo si el modo de pruebas está desactivado
if (!MODO_PRUEBAS_LOCAL_RECUPERACION) {
    http_response_code(403);
    echo json_encode([
        "success" => false,
        "message" => "El modo de recuperación directa está deshabilitado en este servidor."
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// 2. Bloqueo estricto por IP: solo peticiones locales (localhost o emulador Android)
$ipRemota = $_SERVER["REMOTE_ADDR"] ?? "";
$ipsPermitidas = ["127.0.0.1", "::1", "10.0.2.2"];

if (!in_array($ipRemota, $ipsPermitidas, true)) {
    http_response_code(403);
    echo json_encode([
        "success" => false,
        "message" => "Acceso denegado: este endpoint solo está autorizado en entorno local de pruebas."
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// 3. Validar método POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Método no permitido"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$accion = trim($_POST["accion"] ?? "");
$correo = trim($_POST["correo"] ?? "");

if (empty($correo)) {
    echo json_encode([
        "success" => false,
        "message" => "Ingrese su correo electrónico"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "success" => false,
        "message" => "El formato del correo electrónico no es válido"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// =============================================================================
// ACCIÓN 1: VERIFICAR SI EL CORREO EXISTE EN LA TABLA usuarios
// =============================================================================
if ($accion === "verificar") {
    $sql = "SELECT id, nombre FROM usuarios WHERE correo = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $correo);
    $stmt->execute();
    $resultado = $stmt->get_result();
    $usuario = $resultado->fetch_assoc();
    $stmt->close();

    if (!$usuario) {
        echo json_encode([
            "success" => false,
            "message" => "No se encontró una cuenta con ese correo"
        ], JSON_UNESCAPED_UNICODE);
        $conn->close();
        exit;
    }

    echo json_encode([
        "success" => true,
        "message" => "Cuenta encontrada. Ingrese su nueva contraseña.",
        "nombre" => $usuario["nombre"]
    ], JSON_UNESCAPED_UNICODE);

    $conn->close();
    exit;
}

// =============================================================================
// ACCIÓN 2: ACTUALIZAR LA CONTRASEÑA UTILIZANDO password_hash()
// =============================================================================
if ($accion === "actualizar") {
    $nuevo_password = $_POST["nuevo_password"] ?? "";

    if (empty($nuevo_password)) {
        echo json_encode([
            "success" => false,
            "message" => "La nueva contraseña es obligatoria"
        ], JSON_UNESCAPED_UNICODE);
        $conn->close();
        exit;
    }

    if (strlen($nuevo_password) < 8) {
        echo json_encode([
            "success" => false,
            "message" => "La contraseña debe tener al menos 8 caracteres"
        ], JSON_UNESCAPED_UNICODE);
        $conn->close();
        exit;
    }

    // Verificar que el usuario exista antes de actualizar
    $sqlCheck = "SELECT id FROM usuarios WHERE correo = ?";
    $stmtCheck = $conn->prepare($sqlCheck);
    $stmtCheck->bind_param("s", $correo);
    $stmtCheck->execute();
    $resCheck = $stmtCheck->get_result();
    $userCheck = $resCheck->fetch_assoc();
    $stmtCheck->close();

    if (!$userCheck) {
        echo json_encode([
            "success" => false,
            "message" => "No se encontró una cuenta con ese correo"
        ], JSON_UNESCAPED_UNICODE);
        $conn->close();
        exit;
    }

    $usuario_id = (int)$userCheck["id"];

    // Encriptar la nueva contraseña con password_hash()
    $hashPassword = password_hash($nuevo_password, PASSWORD_DEFAULT);

    // Actualizar EXCLUSIVAMENTE la columna 'password'
    $sqlUpdate = "UPDATE usuarios SET password = ? WHERE id = ?";
    $stmtUpdate = $conn->prepare($sqlUpdate);
    $stmtUpdate->bind_param("si", $hashPassword, $usuario_id);

    if (!$stmtUpdate->execute()) {
        echo json_encode([
            "success" => false,
            "message" => "Error al actualizar la contraseña en la base de datos"
        ], JSON_UNESCAPED_UNICODE);
        $stmtUpdate->close();
        $conn->close();
        exit;
    }
    $stmtUpdate->close();

    echo json_encode([
        "success" => true,
        "message" => "Contraseña actualizada correctamente"
    ], JSON_UNESCAPED_UNICODE);

    $conn->close();
    exit;
}

// Acción desconocida
echo json_encode([
    "success" => false,
    "message" => "Acción no reconocida"
], JSON_UNESCAPED_UNICODE);

$conn->close();
?>
