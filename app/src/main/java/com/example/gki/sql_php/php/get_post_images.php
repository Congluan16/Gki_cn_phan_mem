<?php
header('Content-Type: application/json');
include 'db_config.php';

$id_user = $_GET['id_user'] ?? '';

if (!empty($id_user)) {
    // Lấy cả id_img và img_url
    $sql = "SELECT id_img, img_url, time_upimg AS created_at FROM up_Img WHERE id_user = ? ORDER BY id_img DESC";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $id_user);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $images = [];
    while ($row = $result->fetch_assoc()) {
        $images[] = [
            "id_img" => $row['id_img'],
            "img_url" => $row['img_url'],
            "created_at" => $row['created_at']
        ];
    }
    echo json_encode($images);
}
$conn->close();
?>