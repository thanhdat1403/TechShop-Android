package com.example.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.shopfastfood.R;
import com.example.shopfastfood.model.GioHang;
import com.example.shopfastfood.model.SanPhamMoi;
import com.example.shopfastfood.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;

public class ChiTietActivity extends AppCompatActivity {
    TextView tensp, giasp, mota;
    Button btnthem;
    ImageView imghinhanh;
    Spinner spinner;
    Toolbar toolbar;
    SanPhamMoi sanPhamMoi;
    NotificationBadge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);
        initView();
        ActionToolbar();
        initData();
        initControl();
    }

    private void initControl() {
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themGioHang();
            }
        });
    }
    //Phương thức thêm sản phẩm trong giỏ hàng
    private void themGioHang() {
        if (Utils.manggiohang.size() > 0){ // Kiểm tra nếu giỏ hàng có sản phẩm trước đó rồi
            boolean flag = false;
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            for (int i = 0; i < Utils.manggiohang.size(); i++){ //Cho vòng lặp chạy qua từng phần tử trong mảng sản phẩm giỏ hàng
                if (Utils.manggiohang.get(i).getIdsp() == sanPhamMoi.getId()){ //Kiểm tra xem id sp đó có bị trùng sản phẩm không
                    Utils.manggiohang.get(i).setSoluong(soluong + Utils.manggiohang.get(i).getSoluong()); //set lại số lượng cho nó
                    long gia = Long.parseLong(sanPhamMoi.getGiasp()) * Utils.manggiohang.get(i).getSoluong();
                    Utils.manggiohang.get(i).setGiasp(gia);//set giá cho sp
                    //Làm trường hợp sp không có trong giỏ hàng
                    flag = true; //Nếu nó trùng thì cho là true
                }
            }
            //Nếu như nó không trùng
            if (flag == false){
                long gia = Long.parseLong(sanPhamMoi.getGiasp()) * soluong; // Tổng tiền =  giá sản phẩm * số lượng
                GioHang gioHang = new GioHang();
                gioHang.setGiasp(gia);
                gioHang.setSoluong(soluong);
                gioHang.setIdsp(sanPhamMoi.getId());
                gioHang.setTensp(sanPhamMoi.getTensp());
                gioHang.setHinhsp(sanPhamMoi.getHinhanh());
                Utils.manggiohang.add(gioHang);
            }
        }else{
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            long gia = Long.parseLong(sanPhamMoi.getGiasp()) * soluong; // Tổng tiền =  giá sản phẩm * số lượng
            GioHang gioHang = new GioHang();
            gioHang.setGiasp(gia);
            gioHang.setSoluong(soluong);
            gioHang.setIdsp(sanPhamMoi.getId());
            gioHang.setTensp(sanPhamMoi.getTensp());
            gioHang.setHinhsp(sanPhamMoi.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");

        if (sanPhamMoi != null) {
            tensp.setText(sanPhamMoi.getTensp());
            mota.setText(sanPhamMoi.getMota());
            Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imghinhanh);

            // Hiển thị giá sản phẩm với định dạng
//            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
//            giasp.setText("Giá: " + decimalFormat.format(sanPhamMoi.getGiasp()) + " VND");
            // Hiển thị giá sản phẩm với định dạng
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            try {
                long gia = Long.parseLong(sanPhamMoi.getGiasp()); // Chuyển đổi chuỗi thành số
                giasp.setText("Giá: " + decimalFormat.format(gia) + " VND");
            } catch (NumberFormatException e) {
                giasp.setText("Giá không hợp lệ");
                e.printStackTrace();
            }
        }

        // Thêm dữ liệu cho Spinner
        Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, so);
        spinner.setAdapter(adapterspin);
    }

    private void initView() {
        tensp = findViewById(R.id.txttensp);
        giasp = findViewById(R.id.txtgiasp);
        mota = findViewById(R.id.txtmotachitiet);
        btnthem = findViewById(R.id.btnthemvaogiohang);
        spinner = findViewById(R.id.spinner);
        imghinhanh = findViewById(R.id.imgchitiet);
        toolbar = findViewById(R.id.toolbar);
        badge = findViewById(R.id.menu_sl);
        FrameLayout frameLayoutgiohang = findViewById(R.id.framegiohang);
        frameLayoutgiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        if (Utils.manggiohang != null){ //set cho nó nhớ (Hiểu hình dung là: số lượng ở giỏ hàng thêm bao nhiêu soluong thì vẫn hiển thị bằng đấy số lượng trước khi đặt mua thì sẽ reset lại)
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
            }
            badge.setText(String.valueOf(totalItem));
        }
    }
    private void ActionToolbar() {
        // Thiết lập toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Xử lý sự kiện nút back (<-:toolbar trở về trang trước)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //Cập nhật lại giá trị giỏ hàng khi xóa sp mình chắc chắn
    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.manggiohang != null){ //set cho nó nhớ (Hiểu hình dung là: số lượng ở giỏ hàng thêm bao nhiêu soluong thì vẫn hiển thị bằng đấy số lượng trước khi đặt mua thì sẽ reset lại)
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
            }
            badge.setText(String.valueOf(totalItem));
        }
    }
}