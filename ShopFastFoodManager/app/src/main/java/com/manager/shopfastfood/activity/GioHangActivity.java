package com.manager.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.manager.shopfastfood.R;
import com.manager.shopfastfood.adapter.GioHangAdapter;
import com.manager.shopfastfood.model.EventBus.TinhTongEvent;
import com.manager.shopfastfood.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

public class GioHangActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Button btnmuahang;
    GioHangAdapter adapter;
    long tongtiensp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();

        if (Utils.mangmuahang != null){
            Utils.mangmuahang.clear();
        }
        tinhTongTien();
    }

    private void tinhTongTien() {
        tongtiensp = 0;
        //Duyệt qua mảng giỏ hàng để có thể tính tổng tiền
        for (int i = 0; i <Utils.mangmuahang.size(); i++){
            tongtiensp = tongtiensp + (Utils.mangmuahang.get(i).getGiasp() * Utils.mangmuahang.get(i).getSoluong());
        }
        //Định dạng tổng tiền
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien.setText(decimalFormat.format(tongtiensp) + " VND");

    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (Utils.manggiohang.size() == 0){ //Nếu giỏ hàng trống
            giohangtrong.setVisibility(View.VISIBLE);
        }else { //Nếu giỏ hàng k trống thì chúng ta tạo cho nó cái adapter
            adapter = new GioHangAdapter(getApplicationContext(),Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }
        //khi bấm vào nút mua hàng thì đổi sang màn hình thanh toán
        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("tongtien",tongtiensp);
                //Sau khi bấm mua sản phẩm xong thì sẽ clear hết sản phẩm có trong giỏ hàng đi
                Utils.manggiohang.clear();
                startActivity(intent);
            }
        });
    }

    private void initView() {
        giohangtrong = findViewById(R.id.txtgiohangtrong);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleviewgiohang);
        btnmuahang = findViewById(R.id.btnmuahang);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    //Bắt sự kiện bên kia gửi qua
    @Subscribe(sticky = true, threadMode =  ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if (event != null){
            tinhTongTien();
        }
    }
}