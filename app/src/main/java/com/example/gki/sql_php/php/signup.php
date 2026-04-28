<?php
header('Content-Type: application/json');
include 'db_config.php';

$full_name = $_POST['full_name'] ?? '';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Kiểm tra email tồn tại
$checkEmail = $conn->prepare("SELECT id_user FROM Users WHERE email = ?");
$checkEmail->bind_param("s", $email);
$checkEmail->execute();

if ($checkEmail->get_result()->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Email already exists"]);
} else {
    // Lưu vào cột password_hash theo schema
    $stmt = $conn->prepare("INSERT INTO Users (full_name, email, password_hash) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $full_name, $email, $password);
    
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Account created"]);
    } else {
        echo json_encode(["status" => "error", "message" => $conn->error]);
    }
    $stmt->close();
}
$checkEmail->close();
$conn->close();
?>