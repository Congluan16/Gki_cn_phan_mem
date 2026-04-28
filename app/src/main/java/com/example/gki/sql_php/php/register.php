<?php
include 'db_config.php';

// Nhận dữ liệu từ ứng dụng Android
$full_name  = $_POST['full_name'] ?? '';
$email      = $_POST['email'] ?? '';
$password   = $_POST['password'] ?? ''; // Mật khẩu thô từ người dùng
$birth_date = $_POST['birth_date'] ?? '';
$height     = $_POST['height'] ?? '';
$weight     = $_POST['weight'] ?? '';
$gender     = $_POST['gender'] ?? 'Male';

// 1. Mã hóa mật khẩu
$password_hash = password_hash($password, PASSWORD_DEFAULT);

// 2. Chuẩn bị câu lệnh SQL
// Lưu ý: Lưu mật khẩu thô vào cột 'password' và mật khẩu đã mã hóa vào 'password_hash'
$sql = "INSERT INTO Users (email, password, password_hash, full_name, birth_date, height, weight, gender, profile_img_id) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);
$profile_img_default = "1";

$stmt->bind_param("sssssssss", 
    $email, 
    $password, 
    $password_hash, 
    $full_name, 
    $birth_date, 
    $height, 
    $weight, 
    $gender, 
    $profile_img_default
);

if ($stmt->execute()) {
    echo json_encode([
        "id_user" => $conn->insert_id,
        "full_name" => $full_name,
        "status" => "success"
    ]);
} else {
    echo json_encode(["id_user" => 0, "message" => "Lỗi: " . $conn->error]);
}

$stmt->close();
$conn->close();
?>