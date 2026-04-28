<?php
header('Content-Type: application/json');
include 'db_config.php';

// Bật báo cáo lỗi để kiểm tra nếu có vấn đề về SQL
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$action = $_POST['action'] ?? ''; 
$my_id = (int)($_POST['my_id'] ?? 0);
$target_id = (int)($_POST['target_id'] ?? 0);

if ($my_id == 0 || $target_id == 0) {
    echo json_encode(["status" => "error", "message" => "ID không hợp lệ"]);
    exit;
}

// Luôn sắp xếp ID để tìm đúng cặp duy nhất trong CSDL
$u1 = min($my_id, $target_id);
$u2 = max($my_id, $target_id);

try {
    if ($action == 'send') {
        // KIỂM TRA TRƯỚC KHI GỬI
        $check = $conn->prepare("SELECT * FROM Matches WHERE user_one_id = ? AND user_two_id = ?");
        $check->bind_param("ii", $u1, $u2);
        $check->execute();
        
        if ($check->get_result()->num_rows == 0) {
            $sql = "INSERT INTO Matches (user_one_id, user_two_id, sender_id, status) VALUES (?, ?, ?, 0)";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("iii", $u1, $u2, $my_id);
            if ($stmt->execute()) {
                echo json_encode(["status" => "success", "message" => "Đã gửi lời mời"]);
            }
        } else {
            echo json_encode(["status" => "exists", "message" => "Yêu cầu đã tồn tại"]);
        }

    } else if ($action == 'accept') {
        // ĐỒNG Ý (Cập nhật status thành 1)
        $sql = "UPDATE Matches SET status = 1 WHERE user_one_id = ? AND user_two_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ii", $u1, $u2);
        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Đã trở thành bạn bè"]);
        }

    } else if ($action == 'decline') {
        // TỪ CHỐI HOẶC HỦY (Xóa dòng dữ liệu để có thể gửi lại sau này)
        $sql = "DELETE FROM Matches WHERE user_one_id = ? AND user_two_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ii", $u1, $u2);
        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Đã xóa yêu cầu"]);
        }
    }
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}

$conn->close();
?>