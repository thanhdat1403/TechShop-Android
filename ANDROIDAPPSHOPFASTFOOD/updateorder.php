<?php
include "connect.php";

// Lấy dữ liệu từ POST
$id = $_POST['id'];
$trangthai = $_POST['trangthai'];
// Chuẩn bị truy vấn SQL
$query = "UPDATE `donhang` SET`trangthai`='$trangthai' WHERE `id` = '$id'";

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