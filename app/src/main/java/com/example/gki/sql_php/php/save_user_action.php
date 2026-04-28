<?php
header('Content-Type: application/json');
include 'db_config.php';

$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
$target_user_id = isset($_POST['target_user_id']) ? intval($_POST['target_user_id']) : 0;
$action_type = $_POST['action_type'] ?? '';

if ($user_id == 0 || $target_user_id == 0 || $action_type == '') {
    echo json_encode([
        "status"=>"error",
        "message"=>"Thiếu dữ liệu"
    ]);
    exit;
}

$sql = "INSERT INTO User_Actions(user_id,target_user_id,action_type)
VALUES(?,?,?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("iis",$user_id,$target_user_id,$action_type);

if($stmt->execute()){
    echo json_encode([
        "status"=>"success"
    ]);
}else{
    echo json_encode([
        "status"=>"error",
        "message"=>$stmt->error
    ]);
}
?>