<?php
/**
 * Página de planes y precios
 * Muestra los planes disponibles y permite gestionar suscripciones
 *
 * PHP version 8.0+
 *
 * @category HR
 * @package  Puente
 * @author   Cognition <devin@cognition.com>
 * @license  http://opensource.org/licenses/MIT MIT License
 * @link     http://puente.com
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

// Recibir datos
$nombre = trim($_POST["nombre"] ?? "");
$apellido = trim($_POST["apellido"] ?? "");
$correo = trim($_POST["correo"] ?? "");
$password = $_POST["password"] ?? "";
$edad = $_POST["edad"] ?? null;
$sexo = trim($_POST["sexo"] ?? "");
$peso = $_POST["peso"] ?? null;
$altura = $_POST["altura"] ?? null;

// Validar campos obligatorios
if (empty($nombre)
    || empty($apellido)
    || empty($correo)
    || empty($password)
) {
    echo json_encode(
        [
            "success" => false,
            "message" => "Completa todos los campos obligatorios"
        ]
    );
    exit;
}

// Validar correo
if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(
        [
            "success" => false,
            "message" => "El correo electrónico no es válido"
        ]
    );
    exit;
}

// Comprobar si el correo ya existe
$sql = "SELECT id FROM usuarios WHERE correo = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $correo);
$stmt->execute();
$resultado = $stmt->get_result();

if ($resultado->num_rows > 0) {
    echo json_encode(
        [
            "success" => false,
            "message" => "El correo ya está registrado"
        ]
    );
    exit;
}

$stmt->close();

// Encriptar contraseña
$passwordHash = password_hash($password, PASSWORD_DEFAULT);

// Insertar usuario
$sql = "INSERT INTO usuarios 
        (nombre, apellido, correo, password, edad, sexo, peso, altura)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);

$stmt->bind_param(
    "ssssisdd",
    $nombre,
    $apellido,
    $correo,
    $passwordHash,
    $edad,
    $sexo,
    $peso,
    $altura
);

// Ejecutar
if ($stmt->execute()) {

    echo json_encode(
        [
            "success" => true,
            "message" => "Usuario registrado correctamente",
            "id" => $stmt->insert_id
        ]
    );

} else {

    echo json_encode(
        [
            "success" => false,
            "message" => "Error al registrar el usuario"
        ]
    );
}

$stmt->close();
$conn->close();

?>