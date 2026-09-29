package com.manager.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.manager.shopfastfood.MainActivity;
import com.manager.shopfastfood.R;
import com.manager.shopfastfood.retrofit.ApiBanHang;
import com.manager.shopfastfood.retrofit.RetrofitClient;
import com.manager.shopfastfood.utils.Utils;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToanActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtTongTien, txtSoDienThoai, txtEmail;
    private EditText edtDiaChi;
    private AppCompatButton btnDatHang;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiBanHang apiBanHang;
    private long tongTien;
    private int totalItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // Khởi tạo các thành phần giao diện
        initView();

        // Đếm số lượng sản phẩm trong giỏ hàng
        countItem();

        // Gán giá trị vào các thành phần giao diện và xử lý sự kiện
        initControl();
    }

    private void countItem() {
        totalItem = 0;
        if (Utils.mangmuahang != null) {
            for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                totalItem += Utils.mangmuahang.get(i).getSoluong(); // Tổng số lượng sản phẩm
            }
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Định dạng tổng tiền
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongTien = getIntent().getLongExtra("tongtien", 0);
        txtTongTien.setText(decimalFormat.format(tongTien));

        // Kiểm tra thông tin người dùng và hiển thị
        if (Utils.user_current != null) {
            String email = Utils.user_current.getEmail() != null ? Utils.user_current.getEmail() : "Chưa có email";
            String mobile = (Utils.user_current.getMobile() != null && !Utils.user_current.getMobile().isEmpty() && !Utils.user_current.getMobile().equals("null"))
                    ? Utils.user_current.getMobile()
                    : "Số điện thoại không có sẵn";

            txtEmail.setText(email);
            txtSoDienThoai.setText(mobile);

            Log.d("UserInfo", "Email: " + email);
            Log.d("UserInfo", "Mobile: " + mobile);
        } else {
            Toast.makeText(this, "Thông tin người dùng không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Xử lý sự kiện khi bấm nút đặt hàng
        btnDatHang.setOnClickListener(view -> {
            String strDiaChi = edtDiaChi.getText().toString().trim();
            if (TextUtils.isEmpty(strDiaChi)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.mangmuahang == null || Utils.mangmuahang.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuẩn bị dữ liệu để gửi đến API
            String strEmail = Utils.user_current.getEmail();
            String strSdt = Utils.user_current.getMobile();
            int id = Utils.user_current.getId();

            Log.d("OrderData", "Email: " + strEmail);
            Log.d("OrderData", "Số điện thoại: " + strSdt);
            Log.d("OrderData", "Tổng tiền: " + tongTien);
            Log.d("OrderData", "Địa chỉ: " + strDiaChi);
            Log.d("OrderData", "Tổng sản phẩm: " + totalItem);
            Log.d("OrderData", "Giỏ hàng: " + new Gson().toJson(Utils.mangmuahang));

            // Tạo đối tượng Gson với lenient mode để xử lý lỗi JSON
            Gson gson = new GsonBuilder().setLenient().create();

            // Kiểm tra lại dữ liệu JSON
            String jsonCart = gson.toJson(Utils.mangmuahang);
            Log.d("OrderData", "Giỏ hàng JSON: " + jsonCart);

            // Gọi API tạo đơn hàng
            compositeDisposable.add(apiBanHang.createOder(strEmail, strSdt, String.valueOf(tongTien), id, strDiaChi, totalItem, jsonCart)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                Toast.makeText(getApplicationContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                Utils.mangmuahang.clear();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            },
                            throwable -> {
                                Log.e("API Error", "Error: " + throwable.getMessage());
                                Toast.makeText(getApplicationContext(), "Đặt hàng thất bại: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txtTongTien = findViewById(R.id.txttongtien);
        txtSoDienThoai = findViewById(R.id.txtsodienthoai);
        txtEmail = findViewById(R.id.txtemail);
        edtDiaChi = findViewById(R.id.editdiachi);
        btnDatHang = findViewById(R.id.btndathang);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
