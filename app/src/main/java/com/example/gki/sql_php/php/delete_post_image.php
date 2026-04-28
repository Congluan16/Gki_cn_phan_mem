<?php
header('Content-Type: application/json');
include 'db_config.php';

$id_img = $_POST['id_img'] ?? '';

if (!empty($id_img)) {
    $sql = "DELETE FROM up_Img WHERE id_img = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $id_img);
    
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Đã xóa ảnh"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Không thể xóa"]);
    }
    $stmt->close();
}
$conn->close();
?>