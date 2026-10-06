<?php
/**
 * Listar usuarios del sistema
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
header("Access-Control-Allow-Origin: *");

require_once "conexion.php";

try {

    $sql = "
        SELECT
            id,
            nombre,
            apellido,
            correo,
            edad,
            sexo,
            peso,
            altura,
            rol,
            fecha_registro
        FROM usuarios
        ORDER BY id DESC
    ";

    $resultado = $conn->query($sql);

    if (!$resultado) {
        throw new Exception(
            "Error en la consulta: " . $conn->error
        );
    }

    $usuarios = [];

    while ($fila = $resultado->fetch_assoc()) {

        $usuarios[] = [
            "id" => $fila["id"],
            "nombre" => $fila["nombre"],
            "apellido" => $fila["apellido"],
            "correo" => $fila["correo"],
            "edad" => $fila["edad"],
            "sexo" => $fila["sexo"],
            "peso" => $fila["peso"],
            "altura" => $fila["altura"],
            "rol" => $fila["rol"],
            "fecha_registro" => $fila["fecha_registro"]
        ];
    }

    echo json_encode(
        [
            "success" => true,
            "usuarios" => $usuarios
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (Exception $e) {

    http_response_code(500);

    echo json_encode(
        [
            "success" => false,
            "message" => $e->getMessage()
        ],
        JSON_UNESCAPED_UNICODE
    );
}

$conn->close();

?>