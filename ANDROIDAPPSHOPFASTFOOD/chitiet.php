<?php
include "connect.php";

// Lấy thông tin từ POST request
$page = isset($_POST['page']) ? intval($_POST['page']) : 1;
$total = 30; // Lấy 5 sản phẩm mỗi trang
$pos = ($page - 1) * $total; // Tính vị trí bắt đầu
$loai = isset($_POST['loai']) ? intval($_POST['loai']) : 1;

// Dùng prepared statement để tránh SQL Injection
$stmt = $conn->prepare("SELECT * FROM `sanphammoi` WHERE `loai` = ? LIMIT ?, ?");
$stmt->bind_param("iii", $loai, $pos, $total);
$stmt->execute();
$result = $stmt->get_result();

// Lấy dữ liệu từ kết quả trả về
$output = array();
while ($row = $result->fetch_assoc()) {
    $output[] = $row;
}

// Kiểm tra dữ liệu và tạo thông tin JSON để trả về
if (!empty($output)) {
    $arr = [
        'success' => true,
        'message' => "Thành công",
        'result' => $output
    ];
} else {
    $arr = [
        'success' => false,
        'message' => "Không có dữ liệu",
        'result' => []
    ];
}

// Gửi dữ liệu về JSON
header('Content-Type: application/json');
echo json_encode($arr);
?>
