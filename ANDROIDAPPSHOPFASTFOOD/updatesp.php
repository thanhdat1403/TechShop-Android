<?php
include "connect.php";

// Lấy dữ liệu từ POST
$tensp = mysqli_real_escape_string($conn, $_POST['tensp']);
$gia = mysqli_real_escape_string($conn, $_POST['gia']);
$hinhanh = mysqli_real_escape_string($conn, $_POST['hinhanh']);
$mota = mysqli_real_escape_string($conn, $_POST['mota']);
$loai = mysqli_real_escape_string($conn, $_POST['loai']);
$id = mysqli_real_escape_string($conn, $_POST['id']);

// Chuẩn bị truy vấn SQL
$query = "UPDATE `sanphammoi` SET `tensp`='$tensp',`giasp`='$gia',`hinhanh`='$hinhanh',`mota`='$mota',`loai`='$loai' WHERE `id` = '$id'";

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
