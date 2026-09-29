<?php
include "connect.php";

// Lấy dữ liệu từ POST
$id = mysqli_real_escape_string($conn, $_POST['id']);

// Chuẩn bị truy vấn SQL
$query = "DELETE FROM `sanphammoi` WHERE `id` = '$id'";

// Thực thi truy vấn
$data = mysqli_query($conn, $query);

// Kiểm tra kết quả
if ($data) {
    $arr = [
        'success' => true,
        'message' => 'Xóa thành công',
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Xóa không thành công',
    ];
}

// Trả về kết quả dưới dạng JSON
echo json_encode($arr);
?>
