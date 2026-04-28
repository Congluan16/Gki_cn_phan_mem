<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_match = $_POST['id_match'];
    $sender_id = $_POST['sender_id'];
    $content = $_POST['content'];

    // Mặc định is_read = 0 (chưa đọc)
    $sql = "INSERT INTO Messages (id_match, sender_id, content, is_read) VALUES (?, ?, ?, 0)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iis", $id_match, $sender_id, $content);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Đã gửi tin nhắn"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Lỗi: " . $conn->error]);
    }

    $stmt->close();
}
$conn->close();
?>