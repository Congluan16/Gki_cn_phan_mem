<?php
header('Content-Type: application/json');
include 'db_config.php';

$id_user = $_POST['id_user'] ?? '';
$image_base64 = $_POST['image'] ?? ''; 

if (!empty($id_user) && !empty($image_base64)) {
    // 1. Giải mã Base64 và lưu file
    $data = explode(',', $image_base64);
    $decoded_file = base64_decode($data[1]);
    $file_name = 'avatar_' . $id_user . '_' . time() . '.jpg';
    $path = 'img/' . $file_name;

    if (file_put_contents($path, $decoded_file)) {
        $final_url = "http://10.0.2.2/dating_app_api/" . $path;

        // 2. Cập nhật link vào cột profile_img_id
        $sql = "UPDATE Users SET profile_img_id = ? WHERE id_user = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("si", $final_url, $id_user);
        
        // SỬA LỖI: Dùng đúng biến $stmt đã khai báo ở trên
        if ($stmt->execute()) {
            // 3. Lấy lại dữ liệu mới nhất (đã bỏ img_url vì cột đó không tồn tại)
            $sql_user = "SELECT u.id_user, u.full_name, u.profile_img_id, h.content_hobbies AS hobbies 
                         FROM Users u 
                         LEFT JOIN Hobbies h ON u.id_user = h.id_user 
                         WHERE u.id_user = ?";
            $stmt_final = $conn->prepare($sql_user);
            $stmt_final->bind_param("i", $id_user);
            $stmt_final->execute();
            echo json_encode($stmt_final->get_result()->fetch_assoc());
            $stmt_final->close();
        } else {
            echo json_encode(["error" => "MySQL Update Failed: " . $conn->error]);
        }
        $stmt->close();
    } else {
        echo json_encode(["error" => "Failed to write file. Check folder permissions."]);
    }
}
$conn->close();
?>