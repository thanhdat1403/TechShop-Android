package com.manager.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.manager.shopfastfood.R;
import com.manager.shopfastfood.adapter.DienThoaiAdapter;
import com.manager.shopfastfood.model.SanPhamMoi;
import com.manager.shopfastfood.retrofit.ApiBanHang;
import com.manager.shopfastfood.retrofit.RetrofitClient;
import com.manager.shopfastfood.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DienThoaiActivity extends AppCompatActivity {
     Toolbar toolbar;
     RecyclerView recyclerView;
     ApiBanHang apiBanHang;
     CompositeDisposable compositeDisposable = new CompositeDisposable();
     int page = 1;
     int loai;
     DienThoaiAdapter adapterDt;
     List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    //Hàm này có nghĩa là cần tạo ra các phương thước con mà mình muốn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dien_thoai);

        // Khởi tạo API và lấy loại sản phẩm
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai", 1);

        // Ánh xạ và thiết lập ban đầu
        Anhxa();
        ActionToolbar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading == false){ //Nếu Loading mà false thì cho nó tìm đến vị trí cuối cùng
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == sanPhamMoiList.size()-1){
                        isLoading = true;
                        loadMove();
                    }
                }
            }
        });
    }

    private void loadMove() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.add(null);
                adapterDt.notifyItemInserted(sanPhamMoiList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remover null
                sanPhamMoiList.remove(sanPhamMoiList.size()-1);
                adapterDt.notifyItemRemoved(sanPhamMoiList.size());
                page = page+1;
                getData(page); //get dữ liệu từ trang mới
                adapterDt.notifyDataSetChanged();
                isLoading = false; // thì phải set về false lại để tiếp tục load
            }
        },2000);
    }

    private void getData(int page) {
        // Lấy dữ liệu từ API
        compositeDisposable.add(apiBanHang.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterDt == null){
                                    sanPhamMoiList = sanPhamMoiModel.getResult();

                                    // Log dữ liệu để kiểm tra
                                    Log.d("DEBUG", "Data loaded: " + sanPhamMoiList.size());

                                    // Thiết lập adapter nếu dữ liệu thành công
                                    adapterDt = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterDt);
                                }else{
                                    int vitri = sanPhamMoiList.size()-1;
                                    int soluongadd = sanPhamMoiModel.getResult().size();
                                    for (int i = 0; i<soluongadd; i++){
                                        sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                    }
                                    adapterDt.notifyItemRangeInserted(vitri,soluongadd);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Không có dữ liệu", Toast.LENGTH_LONG).show();
                                isLoading = true;
                            }
                        },
                        throwable -> {
                            Log.e("ERROR", "Error loading data", throwable);
                            Toast.makeText(getApplicationContext(), "Không thể kết nối server", Toast.LENGTH_LONG).show();
                        }
                )
        );
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

    private void Anhxa() {
        // Ánh xạ các view
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_dt);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        // Khởi tạo danh sách sản phẩm mới
        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        // Xóa compositeDisposable để tránh rò rỉ bộ nhớ
        compositeDisposable.clear();
        super.onDestroy();
    }
}