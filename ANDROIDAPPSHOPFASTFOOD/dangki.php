<?php
include "connect.php";

// Lấy dữ liệu từ POST và loại bỏ khoảng trắng
$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$pass = isset($_POST['pass']) ? trim($_POST['pass']) : '';
$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$mobile = isset($_POST['mobile']) ? trim($_POST['mobile']) : '';
$uid = isset($_POST['uid']) ? trim($_POST['uid']) : '';

// Kiểm tra đầu vào
if (empty($email) || empty($pass) || empty($username) || empty($mobile) || empty($uid)) {
    echo json_encode(['success' => false, 'message' => 'Vui lòng điền đầy đủ thông tin']);
    exit;
}

// Kiểm tra email đã tồn tại
$query = "SELECT * FROM user WHERE email = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
if ($result->num_rows > 0) {
    echo json_encode(['success' => false, 'message' => 'Email đã tồn tại']);
    exit;
}

// Mã hóa mật khẩu
$hashed_pass = password_hash($pass, PASSWORD_DEFAULT);

// Chèn dữ liệu vào bảng user với đúng thứ tự
$query = "INSERT INTO user (email, pass, username, mobile, uid, token, status) VALUES (?, ?, ?, ?, ?, '', 0)";
$stmt = $conn->prepare($query);

// Kiểm tra lỗi chuẩn bị câu lệnh
if (!$stmt) {
    echo json_encode(['success' => false, 'message' => 'Lỗi khi chuẩn bị truy vấn']);
    exit;
}

// Gán đúng thứ tự giá trị cho các cột
$stmt->bind_param("sssss", $email, $hashed_pass, $username, $mobile, $uid);

// Thực thi câu lệnh
if ($stmt->execute()) {
    echo json_encode(['success' => true, 'message' => 'Đăng ký thành công']);
} else {
    echo json_encode(['success' => false, 'message' => 'Đăng ký không thành công']);
}
?>
