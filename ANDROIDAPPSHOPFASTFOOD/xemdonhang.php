<?php
include "connect.php";

// Lấy dữ liệu từ POST và làm sạch để tránh SQL Injection
$iduser = mysqli_real_escape_string($conn, $_POST['iduser']);

if ($iduser == 0) {
    // get all đơn hàng
    $query = "SELECT * FROM `donhang` ORDER BY id DESC";
}else{
    $query = "SELECT * FROM `donhang` WHERE `iduser` = '$iduser' ORDER BY id DESC";
}

// Truy vấn đơn hàng của người dùng
$data = mysqli_query($conn, $query);

$result = array();

if ($data) {
    while ($row = mysqli_fetch_assoc($data)) {
        // Truy vấn chi tiết đơn hàng dựa trên iddonhang
        $iddonhang = $row['id'];
        $truyvan = "SELECT * FROM `chitietdonhang` INNER JOIN sanphammoi ON chitietdonhang.idsp = sanphammoi.id WHERE chitietdonhang.iddonhang = '$iddonhang'";
        $data1 = mysqli_query($conn, $truyvan);

        $item = array();
        if ($data1) {
            while ($row1 = mysqli_fetch_assoc($data1)) {
                $item[] = $row1; // Thêm từng mục chi tiết vào mảng $item
            }
        }
        $row['item'] = $item; // Gán chi tiết đơn hàng vào mảng $row
        $result[] = $row; // Thêm đơn hàng vào kết quả cuối
    }

    // Trả về kết quả nếu có dữ liệu
    if (!empty($result)) {
        $arr = [
            'success' => true,
            'message' => "Thành công",
            'result' => $result
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => "Không có đơn hàng",
            'result' => $result
        ];
    }
} else {
    // Lỗi khi thực hiện truy vấn đơn hàng
    $arr = [
        'success' => false,
        'message' => "Không thể truy vấn đơn hàng",
        'result' => []
    ];
}

// Trả về dữ liệu JSON
echo json_encode($arr);

?>
