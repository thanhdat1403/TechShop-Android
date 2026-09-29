<?php
include "connect.php";

// Lấy dữ liệu từ POST
$tensp = mysqli_real_escape_string($conn, $_POST['tensp']);
$gia = mysqli_real_escape_string($conn, $_POST['gia']);
$hinhanh = mysqli_real_escape_string($conn, $_POST['hinhanh']);
$mota = mysqli_real_escape_string($conn, $_POST['mota']);
$loai = mysqli_real_escape_string($conn, $_POST['loai']);

// Chuẩn bị truy vấn SQL
$query = "INSERT INTO `sanphammoi`(`tensp`, `giasp`, `hinhanh`, `mota`, `loai`) 
          VALUES ('$tensp', '$gia', '$hinhanh', '$mota', '$loai')";

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
