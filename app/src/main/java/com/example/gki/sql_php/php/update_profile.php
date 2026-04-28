<?php
header('Content-Type: application/json');
include 'db_config.php';

$id_user = $_POST['id_user'] ?? '';
$full_name = $_POST['full_name'] ?? '';

if (!empty($id_user) && !empty($full_name)) {
    $sql = "UPDATE Users SET full_name = ? WHERE id_user = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("si", $full_name, $id_user);

    if ($stmt->execute()) {
        // Lấy lại hồ sơ kèm profile_img_id (KHÔNG lấy img_url vì cột đó không tồn tại)
        $sql_final = "SELECT u.id_user, u.full_name, u.email, u.profile_img_id, h.content_hobbies AS hobbies 
                      FROM Users u 
                      LEFT JOIN Hobbies h ON u.id_user = h.id_user 
                      WHERE u.id_user = ?";
        $stmt_final = $conn->prepare($sql_final);
        $stmt_final->bind_param("i", $id_user);
        $stmt_final->execute();
        echo json_encode($stmt_final->get_result()->fetch_assoc());
        $stmt_final->close();
    } else {
        echo json_encode(["status" => "error", "message" => $conn->error]);
    }
    $stmt->close();
}
$conn->close();
?>