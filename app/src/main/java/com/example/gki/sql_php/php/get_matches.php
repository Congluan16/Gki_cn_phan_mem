<?php
header('Content-Type: application/json');
include 'db_config.php';
$my_id = (int)($_GET['my_id'] ?? 0);

if ($my_id > 0) {
    // Sửa dòng bind_param trong get_matches.php
    $sql = "SELECT * FROM Matches WHERE user_one_id = ? OR user_two_id = ?";
    $stmt = $conn->prepare($sql);

    // PHẢI TRUYỀN 2 LẦN biến $my_id vì có 2 dấu ?
    $stmt->bind_param("ii", $my_id, $my_id); 
    $stmt->execute();
    
    $result = $stmt->get_result();
    $matches = [];
    while($row = $result->fetch_assoc()) { 
        $matches[] = $row; 
    }
    echo json_encode($matches);
} else {
    echo json_encode([]);
}
$conn->close();
?>