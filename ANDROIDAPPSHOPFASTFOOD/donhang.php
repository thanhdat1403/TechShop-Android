<?php

error_reporting(0);
ini_set('display_errors', 0);

header('Content-Type: application/json; charset=utf-8');

include "connect.php";


if(!$conn){

    echo json_encode([
        "success"=>false,
        "message"=>"Không kết nối MySQL"
    ]);

    exit();

}


// nhận dữ liệu Android gửi lên

$sodienthoai = $_POST['sodienthoai'];
$email       = $_POST['email'];
$tongtien    = $_POST['tongtien'];
$iduser      = $_POST['iduser'];
$diachi      = $_POST['diachi'];
$soluong     = $_POST['soluong'];
$chitiet     = $_POST['chitiet'];



// thêm đơn hàng

$sql = "
INSERT INTO donhang
(
iduser,
diachi,
sodienthoai,
email,
soluong,
tongtien,
trangthai,
momo
)
VALUES
(
'$iduser',
'$diachi',
'$sodientthoai',
'$email',
'$soluong',
'$tongtien',
0,
''
)
";



$result = mysqli_query($conn,$sql);



if($result){


    // lấy id đơn hàng vừa tạo

    $iddonhang = mysqli_insert_id($conn);



    // lưu chi tiết đơn hàng

    $array=json_decode($chitiet,true);


    foreach($array as $item){


        $idsp=$item['idsp'];
        $soluongsp=$item['soluong'];
        $gia=$item['giasp'];


        $sqlct="
        INSERT INTO chitietdonhang
        (
        iddonhang,
        idsp,
        soluong,
        gia
        )
        VALUES
        (
        '$iddonhang',
        '$idsp',
        '$soluongsp',
        '$gia'
        )
        ";


        mysqli_query($conn,$sqlct);


    }



    echo json_encode([

        "success"=>true,
        "message"=>"Đặt hàng thành công"

    ],JSON_UNESCAPED_UNICODE);



}
else{


    echo json_encode([

        "success"=>false,
        "message"=>"Đặt hàng thất bại",
        "error"=>mysqli_error($conn)

    ],JSON_UNESCAPED_UNICODE);


}


?>