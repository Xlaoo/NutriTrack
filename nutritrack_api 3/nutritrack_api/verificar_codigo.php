<?php
/**
 * Verificar código de recuperación de contraseña
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
$codigo = trim($_POST["codigo"] ?? "");

if (empty($correo) || empty($codigo)) {
    echo json_encode([
        "success" => false,
        "message" => "Correo y código son obligatorios"
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
        "message" => "Código no válido o expirado"
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$usuario_id = (int)$usuario["id"];

// Buscar el último código activo no utilizado
$sqlCod = "SELECT id, codigo, intentos, expiracion FROM codigos_recuperacion 
           WHERE usuario_id = ? AND utilizado = 0 
           ORDER BY id DESC LIMIT 1";
$stmtCod = $conn->prepare($sqlCod);
$stmtCod->bind_param("i", $usuario_id);
$stmtCod->execute();
$resCod = $stmtCod->get_result();
$regCodigo = $resCod->fetch_assoc();
$stmtCod->close();

if (!$regCodigo) {
    echo json_encode([
        "success" => false,
        "message" => "No hay un código activo. Solicite un nuevo código."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$idCodigo = (int)$regCodigo["id"];
$intentos = (int)$regCodigo["intentos"];
$expiracion = strtotime($regCodigo["expiracion"]);

// Verificar vencimiento
if (time() > $expiracion) {
    // Marcar como utilizado para que quede descartado
    $conn->query("UPDATE codigos_recuperacion SET utilizado = 1 WHERE id = $idCodigo");
    echo json_encode([
        "success" => false,
        "message" => "El código ha expirado. Por favor, solicite uno nuevo."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

// Verificar límite de intentos antes de evaluar
if ($intentos >= 3) {
    $conn->query("UPDATE codigos_recuperacion SET utilizado = 1 WHERE id = $idCodigo");
    echo json_encode([
        "success" => false,
        "message" => "Ha superado el límite de 3 intentos permitidos. Solicite un nuevo código."
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

// Comprobar coincidencia del código
if ($regCodigo["codigo"] !== $codigo) {
    $nuevosIntentos = $intentos + 1;
    if ($nuevosIntentos >= 3) {
        $stmtUp = $conn->prepare("UPDATE codigos_recuperacion SET intentos = ?, utilizado = 1 WHERE id = ?");
        $stmtUp->bind_param("ii", $nuevosIntentos, $idCodigo);
        $stmtUp->execute();
        $stmtUp->close();

        echo json_encode([
            "success" => false,
            "message" => "Código incorrecto. Ha alcanzado el límite de 3 intentos. Solicite un nuevo código."
        ], JSON_UNESCAPED_UNICODE);
    } else {
        $stmtUp = $conn->prepare("UPDATE codigos_recuperacion SET intentos = ? WHERE id = ?");
        $stmtUp->bind_param("ii", $nuevosIntentos, $idCodigo);
        $stmtUp->execute();
        $stmtUp->close();

        $restantes = 3 - $nuevosIntentos;
        echo json_encode([
            "success" => false,
            "message" => "Código incorrecto. Le restan " . $restantes . " intento(s)."
        ], JSON_UNESCAPED_UNICODE);
    }
    $conn->close();
    exit;
}

// Código correcto: generar token temporal seguro de reseteo
$token = bin2hex(random_bytes(24));
$stmtToken = $conn->prepare("UPDATE codigos_recuperacion SET token = ? WHERE id = ?");
$stmtToken->bind_param("si", $token, $idCodigo);

if (!$stmtToken->execute()) {
    echo json_encode([
        "success" => false,
        "message" => "Error al generar sesión de cambio de contraseña"
    ], JSON_UNESCAPED_UNICODE);
    $stmtToken->close();
    $conn->close();
    exit;
}
$stmtToken->close();

echo json_encode([
    "success" => true,
    "message" => "Código verificado correctamente.",
    "token" => $token
], JSON_UNESCAPED_UNICODE);

$conn->close();
?>
