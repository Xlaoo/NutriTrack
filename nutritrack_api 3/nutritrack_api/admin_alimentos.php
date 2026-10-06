<?php
/**
 * Listar alimentos del sistema
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
            calorias,
            proteinas,
            carbohidratos,
            grasas
        FROM alimentos
        ORDER BY id DESC
    ";

    $resultado = $conn->query($sql);

    if (!$resultado) {
        throw new Exception(
            "Error en la consulta: " . $conn->error
        );
    }

    $alimentos = [];

    while ($fila = $resultado->fetch_assoc()) {

        $alimentos[] = [
            "id" => $fila["id"],
            "nombre" => $fila["nombre"],
            "calorias" => $fila["calorias"],
            "proteinas" => $fila["proteinas"],
            "carbohidratos" => $fila["carbohidratos"],
            "grasas" => $fila["grasas"]
        ];
    }

    echo json_encode(
        [
            "success" => true,
            "alimentos" => $alimentos
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