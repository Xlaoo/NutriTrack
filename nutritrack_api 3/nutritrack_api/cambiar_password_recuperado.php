<?php
/**
 * Cambiar contraseña tras verificación de código
 *
 * PHP version 8.0+
 */

header("Content-Type: application/json; charset=UTF-8");

require_once "conexion.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Método no permitido"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$correo = trim($_POST["correo"] ?? "");
$token = trim($_POST["token"] ?? "");
$nuevo_password = $_POST["nuevo_password"] ?? "";

if (empty($correo) || empty($token) || empty($nuevo_password)) {
    echo json_encode([
        "success" => false,
        "message" => "Todos los campos son obligatorios"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

if (strlen($nuevo_password) < 8) {
    echo json_encode([
        "success" => false,
        "message" => "La nueva contraseña debe tener al menos 8 caracteres"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// Buscar usuario
$sql = "SELECT id FROM usuarios WHERE correo = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $correo);
$stmt->execute();
$resUsuario = $stmt->get_result();
$usuario = $resUsuario->fetch_assoc();
$stmt->close();

if (!$usuario) {
    echo json_encode([
        "success" => false,
        "message" => "Usuario no encontrado"
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$usuario_id = (int)$usuario["id"];

// Validar token activo no utilizado
$sqlToken = "SELECT id, expiracion FROM codigos_recuperacion 
             WHERE usuario_id = ? AND token = ? AND utilizado = 0 
             ORDER BY id DESC LIMIT 1";
$stmtToken = $conn->prepare($sqlToken);
$stmtToken->bind_param("is", $usuario_id, $token);
$stmtToken->execute();
$resToken = $stmtToken->get_result();
$regToken = $resToken->fetch_assoc();
$stmtToken->close();

if (!$regToken) {
    echo json_encode([
        "success" => false,
        "message" => "El permiso de recuperación es inválido o ya fue utilizado."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$idCodigo = (int)$regToken["id"];
$expiracion = strtotime($regToken["expiracion"]);

if (time() > $expiracion) {
    $conn->query("UPDATE codigos_recuperacion SET utilizado = 1 WHERE id = $idCodigo");
    echo json_encode([
        "success" => false,
        "message" => "La sesión de recuperación ha expirado. Por favor, vuelva a solicitar un código."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

// Generar hash seguro con password_hash()
$hashPassword = password_hash($nuevo_password, PASSWORD_DEFAULT);

// Actualizar EXCLUSIVAMENTE la columna 'password' del usuario
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

// Marcar código/token como utilizado de forma irreversible
$conn->query("UPDATE codigos_recuperacion SET utilizado = 1 WHERE id = $idCodigo");

echo json_encode([
    "success" => true,
    "message" => "Contraseña actualizada correctamente"
], JSON_UNESCAPED_UNICODE);

$conn->close();
?>
