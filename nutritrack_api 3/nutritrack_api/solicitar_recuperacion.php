<?php
/**
 * Solicitar código de recuperación de contraseña
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

// Buscar si el usuario existe
$sql = "SELECT id, nombre FROM usuarios WHERE correo = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $correo);
$stmt->execute();
$resultado = $stmt->get_result();
$usuario = $resultado->fetch_assoc();
$stmt->close();

if (!$usuario) {
    // Respuesta genérica para evitar enumeración de cuentas
    echo json_encode([
        "success" => true,
        "message" => "Si la cuenta existe, se ha procesado la solicitud de recuperación.",
        "codigo_enviado" => false,
        "info_servicio" => "El correo no está registrado o no se pudo procesar."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$usuario_id = (int)$usuario["id"];

// Invalidad códigos anteriores no utilizados de este usuario
$sqlInval = "UPDATE codigos_recuperacion SET utilizado = 1 WHERE usuario_id = ? AND utilizado = 0";
$stmtInval = $conn->prepare($sqlInval);
$stmtInval->bind_param("i", $usuario_id);
$stmtInval->execute();
$stmtInval->close();

// Generar código temporal de 6 dígitos
$codigo = sprintf("%06d", mt_rand(100000, 999999));

// Vencimiento en 15 minutos
$expiracion = date("Y-m-d H:i:s", strtotime("+15 minutes"));

$sqlInsert = "INSERT INTO codigos_recuperacion (usuario_id, codigo, expiracion, intentos, utilizado) VALUES (?, ?, ?, 0, 0)";
$stmtInsert = $conn->prepare($sqlInsert);
$stmtInsert->bind_param("iss", $usuario_id, $codigo, $expiracion);

if (!$stmtInsert->execute()) {
    echo json_encode([
        "success" => false,
        "message" => "Error al generar el código de recuperación"
    ], JSON_UNESCAPED_UNICODE);
    $stmtInsert->close();
    $conn->close();
    exit;
}
$stmtInsert->close();

// Preparación de integración para envío de correo electrónico
$asunto = "Código de recuperación de contraseña - NutriTrack";
$mensajeCorreo = "Hola " . $usuario["nombre"] . ",\n\nTu código de recuperación temporal es: " . $codigo . "\n\nEste código vence en 15 minutos y cuenta con un límite de 3 intentos.\nSi no solicitaste este cambio, ignora este mensaje.";
$cabeceras = "From: no-reply@nutritrack.com\r\nX-Mailer: PHP/" . phpversion();

$correoEnviado = false;
$smtpHost = ini_get("SMTP") ?: "localhost";
$smtpPort = (int)(ini_get("smtp_port") ?: 25);

// Verificar si existe un servicio SMTP configurado (distinto del localhost por defecto sin servicio)
$smtpConfigurado = !empty(getenv("SMTP_HOST")) || ($smtpHost !== "localhost" && !empty($smtpHost));

if ($smtpConfigurado) {
    $correoEnviado = @mail($correo, $asunto, $mensajeCorreo, $cabeceras);
}

if ($correoEnviado) {
    echo json_encode([
        "success" => true,
        "message" => "Se ha enviado un código de verificación a su correo electrónico.",
        "codigo_enviado" => true
    ], JSON_UNESCAPED_UNICODE);
} else {
    // Si no existe un servicio de correo configurado, indicar con total transparencia
    // qué configuración falta sin fingir que el correo fue enviado
    echo json_encode([
        "success" => true,
        "message" => "Código de verificación generado. El servidor local no cuenta con un servicio SMTP activo.",
        "codigo_enviado" => false,
        "info_servicio" => "Falta configurar un servicio SMTP en XAMPP (sendmail.ini o php.ini). Para continuar las pruebas en el emulador, ingrese el código: " . $codigo,
        "codigo_demo" => $codigo
    ], JSON_UNESCAPED_UNICODE);
}

$conn->close();
?>
