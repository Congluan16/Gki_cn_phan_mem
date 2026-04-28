<?php
$host = "localhost";
$user = "root"; 
$pass = "16012006"; 
$db   = "dating_app"; 

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}
$conn->set_charset("utf8");
?>