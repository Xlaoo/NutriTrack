
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

require_once "conexion.php";

// Solo permitir solicitudes POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {

    echo json_encode(
        [
            "success" => false,
            "message" => "Método no permitido"
        ]
    );

    exit;
}

// Recibir datos enviados desde Android
$correo = trim($_POST["correo"] ?? "");
$password = $_POST["password"] ?? "";

// Validar campos
if (empty($correo) || empty($password)) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Ingrese correo y contraseña"
        ]
    );

    exit;
}

// Buscar usuario por correo
$sql = "SELECT id, nombre, apellido, correo, password, rol
        FROM usuarios
        WHERE correo = ?";

$stmt = $conn->prepare($sql);

if (!$stmt) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Error al preparar la consulta"
        ]
    );

    exit;
}

$stmt->bind_param("s", $correo);

$stmt->execute();

$resultado = $stmt->get_result();

// Verificar si existe el usuario
if ($resultado->num_rows === 0) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Correo o contraseña incorrectos"
        ]
    );

    $stmt->close();
    $conn->close();

    exit;
}

// Obtener usuario
$usuario = $resultado->fetch_assoc();

// Verificar contraseña
if (!password_verify($password, $usuario["password"])) {

    echo json_encode(
        [
            "success" => false,
            "message" => "Correo o contraseña incorrectos"
        ]
    );

    $stmt->close();
    $conn->close();

    exit;
}

// Login correcto
echo json_encode(
    [
        "success" => true,
        "message" => "Inicio de sesión correcto",
        "usuario" => [
            "id" => (int) $usuario["id"],
            "nombre" => $usuario["nombre"],
            "apellido" => $usuario["apellido"],
            "correo" => $usuario["correo"],
            "rol" => $usuario["rol"] ?? "usuario"
        ]
    ]
);

$stmt->close();
$conn->close();

?>

