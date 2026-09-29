<?php
include "connect.php";

// Lấy dữ liệu từ POST
$token = $_POST['token'];
$id = $_POST['id'];

// Chuẩn bị truy vấn SQL
$query = "UPDATE `user` SET `token`='$token' WHERE `id` = '$id'";

// Thực thi truy vấn
$data = mysqli_query($conn, $query);

// Kiểm tra kết quả
if ($data) {
    $arr = [
        'success' => true,
        'message' => 'Thành công',
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Không thành công: ' . mysqli_error($conn),
    ];
}

// Trả về kết quả dưới dạng JSON
echo json_encode($arr);
?>