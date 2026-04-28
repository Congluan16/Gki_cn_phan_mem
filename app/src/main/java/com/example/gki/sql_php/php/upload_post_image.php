<?php
header('Content-Type: application/json');
include 'db_config.php';

// Bật báo cáo lỗi để PHP tự động bắn exception khi SQL sai
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$id_user = $_POST['id_user'] ?? '';
$image_base64 = $_POST['image'] ?? ''; 

if (!empty($id_user) && !empty($image_base64)) {
    try {
        // Giải mã Base64
        $data = explode(',', $image_base64);
        $decoded_file = base64_decode($data[1]);
        $file_name = 'post_' . $id_user . '_' . time() . '.jpg';
        $path = 'img/' . $file_name;

        if (file_put_contents($path, $decoded_file)) {
            $final_url = "http://10.0.2.2/dating_app_api/" . $path;

            // FIX TẠI ĐÂY: Loại bỏ cột is_avatar vì DB không còn cột này
            $sql = "INSERT INTO up_Img (id_user, img_url) VALUES (?, ?)";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("is", $id_user, $final_url);
            
            if ($stmt->execute()) {
                echo json_encode(["status" => "success", "message" => "Đăng ảnh thành công!", "url" => $final_url]);
            }
            $stmt->close();
        } else {
            echo json_encode(["status" => "error", "message" => "Lỗi ghi file vật lý. Hãy check sudo chmod -R 777 img/"]);
        }
    } catch (Exception $e) {
        // Trả về lỗi cụ thể để Android Logcat hiển thị được
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
    }
}
$conn->close();
?>