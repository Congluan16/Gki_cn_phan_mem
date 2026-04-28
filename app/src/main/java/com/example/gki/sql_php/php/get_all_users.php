<?php
header('Content-Type: application/json');
include 'db_config.php';

// Lấy tất cả người dùng và JOIN với sở thích
$sql = "SELECT u.id_user, u.full_name, u.birth_date, u.height, u.weight, u.profile_img_id, h.content_hobbies AS hobbies 
        FROM Users u 
        LEFT JOIN Hobbies h ON u.id_user = h.id_user";

$result = $conn->query($sql);
$users = [];

if ($result) {
    while($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
}
echo json_encode($users); // Trả về mảng [{}, {}]
$conn->close();
?>