<?php
header('Content-Type: application/json');
include 'db_config.php';

$userId = isset($_GET['id']) ? intval($_GET['id']) : 0;

// Sử dụng Prepared Statement để bảo mật
$stmt = $conn->prepare("SELECT u.id_user, u.full_name, u.birth_date, u.height, u.weight, 
                       u.profile_img_id, h.content_hobbies as hobbies 
                FROM Users u
                LEFT JOIN Hobbies h ON u.id_user = h.id_user
                WHERE u.id_user = ? 
                LIMIT 1");

$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode($result->fetch_assoc());
} else {
    echo json_encode(["error" => "User not found"]);
}

$stmt->close();
$conn->close();
?>