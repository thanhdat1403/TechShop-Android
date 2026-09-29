<?php
include "connect.php";

// Lấy dữ liệu từ form
$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$pass = isset($_POST['pass']) ? trim($_POST['pass']) : '';

// Kiểm tra đầu vào
if (empty($email) || empty($pass)) {
    echo json_encode(['success' => false, 'message' => 'Vui lòng nhập email và mật khẩu']);
    exit;
}

// Truy vấn để kiểm tra thông tin người dùng
$query = "SELECT `pass`, `uid` FROM `user` WHERE `email` = ?";
$stmt = $conn->prepare($query);

// Kiểm tra chuẩn bị truy vấn thành công
if (!$stmt) {
    echo json_encode(['success' => false, 'message' => 'Lỗi hệ thống']);
    exit;
}

// Gán tham số và thực thi
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

// Kiểm tra kết quả truy vấn
if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $stored_password = $row['pass'];
    $user_id = $row['uid'];

    // So sánh mật khẩu đã mã hóa
    if (password_verify($pass, $stored_password)) {
        echo json_encode(['success' => true, 'message' => 'Đăng nhập thành công', 'uid' => $user_id]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Mật khẩu không đúng']);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Email không tồn tại']);
}

// Đóng kết nối
$stmt->close();
$conn->close();
?>
