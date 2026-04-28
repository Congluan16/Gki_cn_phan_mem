<?php
include 'db_config.php';

$id_match = $_GET['id_match'];

$sql = "SELECT * FROM Messages WHERE id_match = ? ORDER BY timestamp ASC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id_match);
$stmt->execute();
$result = $stmt->get_result();

$messages = array();
while($row = $result->fetch_assoc()) {
    $messages[] = $row;
}

echo json_encode($messages);

$stmt->close();
$conn->close();
?>