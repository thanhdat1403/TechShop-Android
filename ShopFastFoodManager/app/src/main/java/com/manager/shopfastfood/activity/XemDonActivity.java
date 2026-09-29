package com.manager.shopfastfood.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.manager.shopfastfood.R;
import com.manager.shopfastfood.adapter.DonHangAdapter;
import com.manager.shopfastfood.model.DonHang;
import com.manager.shopfastfood.model.EventBus.DonHangEvent;
import com.manager.shopfastfood.retrofit.ApiBanHang;
import com.manager.shopfastfood.retrofit.RetrofitClient;
import com.manager.shopfastfood.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don);
        initView();
        initToolbar();
        getOrder();

    }

    private void getOrder() {
        compositeDisposable.add(apiBanHang.xemDonHang(0)
        .subscribeOn(Schedulers.io()) // Đảm bảo yêu cầu mạng chạy trên background thread
        .observeOn(AndroidSchedulers.mainThread()) // Kết quả sẽ được trả về trên main thread
        .subscribe(
                donHangModel -> {
                    DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                    redonhang.setAdapter(adapter);
                },
                throwable -> {
                    // Xử lý lỗi
                    Toast.makeText(getApplicationContext(), "Lỗi khi lấy dữ liệu: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
        ));
    }



    private void initToolbar() {
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

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toobar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void evenDoHang(DonHangEvent event){
        if (event != null){
            donHang = event.getDonHang();
            showCustumDialog();
        }
    }

    private void showCustumDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dohang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);
        List<String> list = new ArrayList<>();
        list.add("Đơn hàng đang được xử lý");
        list.add("Đơn hàng đã được chấp nhận");
        list.add("Đơn hàng đang giao cho đơn vị vận chuyển");
        list.add("Đơn hàng giao thành công");
        list.add("Đơn hàng đã hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tinhtrang = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capNhapDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }
    //cập nhật tình trạng đơn hàng
    private void capNhapDonHang() {
        compositeDisposable.add(apiBanHang.updateOrder(donHang.getId(),tinhtrang)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                messageModel -> {
                    getOrder();
                    dialog.dismiss();
                },
                throwable -> {

                }
        ));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}