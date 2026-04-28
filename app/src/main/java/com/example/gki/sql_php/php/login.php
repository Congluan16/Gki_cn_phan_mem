<?php
header('Content-Type: application/json');
include 'db_config.php';

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// SỬA TẠI ĐÂY: Dùng LEFT JOIN để lấy sở thích từ bảng Hobbies ngay khi đăng nhập
$sql = "SELECT u.id_user, u.full_name, u.birth_date, u.height, u.weight, u.profile_img_id, h.content_hobbies AS hobbies 
        FROM Users u 
        LEFT JOIN Hobbies h ON u.id_user = h.id_user 
        WHERE u.email = ? AND u.password = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    echo json_encode($user); 
} else {
    http_response_code(401);
    echo json_encode(["error" => "Invalid email or password"]);
}

$stmt->close();
$conn->close();
?>