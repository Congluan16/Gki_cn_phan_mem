<?php
header('Content-Type: application/json');
include 'db_config.php';

$id_user = $_POST['id_user'] ?? '';
$hobbies = $_POST['hobbies'] ?? '';

if (!empty($id_user)) {
    // 1. Kiểm tra xem user đã có dòng trong bảng Hobbies chưa
    $check = "SELECT * FROM Hobbies WHERE id_user = ?";
    $stmt_check = $conn->prepare($check);
    $stmt_check->bind_param("i", $id_user);
    $stmt_check->execute();
    $res = $stmt_check->get_result();

    if ($res->num_rows > 0) {
        // Đã có thì UPDATE
        $sql = "UPDATE Hobbies SET content_hobbies = ? WHERE id_user = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("si", $hobbies, $id_user);
    } else {
        // Chưa có thì INSERT
        $sql = "INSERT INTO Hobbies (id_user, content_hobbies) VALUES (?, ?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("is", $id_user, $hobbies);
    }

    if ($stmt->execute()) {
        // 2. Lấy lại hồ sơ đầy đủ để trả về cho App
        $sql_final = "SELECT u.*, h.content_hobbies AS hobbies 
                      FROM Users u 
                      LEFT JOIN Hobbies h ON u.id_user = h.id_user 
                      WHERE u.id_user = ?";
        $stmt_final = $conn->prepare($sql_final);
        $stmt_final->bind_param("i", $id_user);
        $stmt_final->execute();
        echo json_encode($stmt_final->get_result()->fetch_assoc());
    } else {
        echo json_encode(["status" => "error", "message" => $conn->error]);
    }
}
$conn->close();
?>