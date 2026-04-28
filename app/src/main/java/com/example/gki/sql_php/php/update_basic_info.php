<?php
header('Content-Type: application/json');
include 'db_config.php';

// Bật báo cáo lỗi để dễ dàng kiểm tra nếu có vấn đề về SQL
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$id_user = $_POST['id_user'] ?? '';
$birth_date = $_POST['birth_date'] ?? '';
$height = $_POST['height'] ?? '';
$weight = $_POST['weight'] ?? '';

if (!empty($id_user)) {
    try {
        // 1. Cập nhật thông tin cơ bản vào bảng Users
        $sql_update = "UPDATE Users SET birth_date = ?, height = ?, weight = ? WHERE id_user = ?";
        $stmt = $conn->prepare($sql_update);
        $stmt->bind_param("sssi", $birth_date, $height, $weight, $id_user);
        
        if ($stmt->execute()) {
            // 2. Sau khi cập nhật thành công, lấy lại dữ liệu mới nhất của User để trả về cho App
            // Sửa đoạn SELECT cuối file PHP của bạn thành:
            // Tìm đoạn SELECT ở cuối file update_basic_info.php của bạn và thay bằng đoạn này:
            $sql_select = "SELECT u.id_user, u.email, u.full_name, u.birth_date, u.height, u.weight, 
                                u.profile_img_id, h.content_hobbies AS hobbies 
                        FROM Users u 
                        LEFT JOIN Hobbies h ON u.id_user = h.id_user 
                        WHERE u.id_user = ?";

            $stmt_select = $conn->prepare($sql_select);
            $stmt_select->bind_param("i", $id_user);
            $stmt_select->execute();
            $result = $stmt_select->get_result();
            $user = $result->fetch_assoc();

            // Trả về dữ liệu đầy đủ cho App
            echo json_encode($user);
        } else {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Không thể cập nhật dữ liệu."]);
        }
        $stmt->close();
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
    }
} else {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Thiếu ID người dùng."]);
}

$conn->close();
?>