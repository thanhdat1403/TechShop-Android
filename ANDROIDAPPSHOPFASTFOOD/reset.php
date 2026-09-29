<?php

include "connect.php";

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/PHPMailer/src/Exception.php';
require 'PHPMailer/PHPMailer/src/PHPMailer.php';
require 'PHPMailer/PHPMailer/src/SMTP.php';

$email = $_POST['email'];

$query = 'SELECT * FROM `user` WHERE `email` = "' . $email . '"';
$data = mysqli_query($conn, $query);

$result = array();

while ($row = mysqli_fetch_assoc($data)) {
    $result[] = $row;
}

if (empty($result)) {

    $arr = [
        'success' => false,
        'message' => 'Email không chính xác',
        'result' => $result
    ];

} else {

    // Send reset password email
    $email = $result[0]["email"];
    $pass = $result[0]["pass"];

    $baseUrl = "http://YOUR_LOCAL_IP:8888/ANDROIDAPPSHOPFASTFOOD";

    $link = "<a href='" . $baseUrl .
        "/reset_pass.php?key=" . urlencode($email) .
        "&reset=" . urlencode($pass) .
        "'>Click To Reset password</a>";

    $mail = new PHPMailer();

    $mail->CharSet = "utf-8";
    $mail->IsSMTP();

    // Enable SMTP authentication
    $mail->SMTPAuth = true;

    // Gmail username
    // Replace with your own email when running the project locally.
    $mail->Username = "YOUR_EMAIL@example.com";

    // Gmail App Password
    // Do NOT put your real password in this file.
    $mail->Password = "YOUR_EMAIL_APP_PASSWORD";

    $mail->SMTPSecure = "ssl";

    // Gmail SMTP server
    $mail->Host = "smtp.gmail.com";

    // Gmail SMTP port
    $mail->Port = "465";

    $mail->From = "YOUR_EMAIL@example.com";
    $mail->FromName = "App Shop Fast Food";

    $mail->AddAddress($email, "receiver_name");

    $mail->Subject = "Reset Password";

    $mail->IsHTML(true);

    $mail->Body = $link;

    if ($mail->Send()) {

        $arr = [
            'success' => true,
            'message' => 'Vui lòng kiểm tra mail của bạn',
            'result' => $result
        ];

    } else {

        $arr = [
            'success' => false,
            'message' => 'Gửi mail không thành công',
            'result' => $mail->ErrorInfo
        ];
    }
}

print_r(json_encode($arr));

?>