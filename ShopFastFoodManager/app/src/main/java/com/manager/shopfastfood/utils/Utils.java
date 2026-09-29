package com.manager.shopfastfood.utils;

import com.manager.shopfastfood.model.GioHang;
import com.manager.shopfastfood.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public  static final String BASE_URL="http://192.168.1.81:8888/ANDROIDAPPSHOPFASTFOOD/"; // lấy số địa chỉ này bằng cách mở cmd -> gõ ipconfig ->Wireless LAN adapter Wi-Fi: -> IPv4 Address. . . . . . . . . . . : 192.168.1.81 nhớ + cổng xampp 8888
    //còn ANDROIDAPPSHOPFASTFOOD/trong htdocs
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    //hàm giúp người dùng nhập email , pass bên đăng ký thì sang bên đăng nhập nó hiển thị nên luôn khỏi nhập lại
    public static User user_current = new User();
}
