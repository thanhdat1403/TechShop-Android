<?php
include "connect.php";

// Lấy dữ liệu từ POST và làm sạch để tránh SQL Injection
$search = mysqli_real_escape_string($conn, $_POST['search']);
if (empty($search)) {
        $arr = [
        'success' => false,
        'message' => "Không tìm thấy sản phẩm"
        ];
}else{
    // Truy vấn đơn hàng của người dùng
    $query = "SELECT * FROM `sanphammoi` WHERE `tensp` LIKE '%$search%'";
    $data = mysqli_query($conn, $query);

    $result = array();

    while($row = mysqli_fetch_assoc($data)){
        $result[] = $row; // Thêm kết quả vào mảng
    }

    // Kiểm tra nếu có kết quả và trả về phản hồi
    if (!empty($result)) {
        $arr = [
            'success' => true,
            'message' => "Thành công",
            'result' => $result
        ];
    } else {
        // Khi không tìm thấy sản phẩm
        $arr = [
            'success' => false,
            'message' => "Không tìm thấy sản phẩm",
            'result' => []
        ];
    }
}

// Trả về kết quả dưới dạng JSON
echo json_encode($arr);
?>
