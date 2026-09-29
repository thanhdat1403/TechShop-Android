package com.example.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.shopfastfood.R;
import com.example.shopfastfood.adapter.DienThoaiAdapter;
import com.example.shopfastfood.model.SanPhamMoi;
import com.example.shopfastfood.retrofit.ApiBanHang;
import com.example.shopfastfood.retrofit.RetrofitClient;
import com.example.shopfastfood.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LaptopActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ApiBanHang apiBanHang;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int page = 1;
    private int loai;
    private DienThoaiAdapter adapterDt;
    private List<SanPhamMoi> sanPhamMoiList;
    private LinearLayoutManager linearLayoutManager;
    private Handler handler = new Handler();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dien_thoai);

        // Initialize API and fetch "loai" from Intent
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai", 2); // Default to "2" if no data passed

        // Initialize views and setup
        initializeViews();
        setupToolbar();
        fetchData(page);
        setupLoadMoreEvent();
    }

    private void setupLoadMoreEvent() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && linearLayoutManager.findLastCompletelyVisibleItemPosition() == sanPhamMoiList.size() - 1) {
                    isLoading = true;
                    loadMoreData();
                }
            }
        });
    }

    private void loadMoreData() {
        handler.post(() -> {
            sanPhamMoiList.add(null); // Add null item for loading indicator
            adapterDt.notifyItemInserted(sanPhamMoiList.size() - 1);
        });

        handler.postDelayed(() -> {
            sanPhamMoiList.remove(sanPhamMoiList.size() - 1); // Remove loading indicator
            adapterDt.notifyItemRemoved(sanPhamMoiList.size());
            page++;
            fetchData(page);
            isLoading = false;
        }, 2000); // Simulated delay for loading
    }

    private void fetchData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterDt == null) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    adapterDt = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterDt);
                                } else {
                                    int startPosition = sanPhamMoiList.size();
                                    sanPhamMoiList.addAll(sanPhamMoiModel.getResult());
                                    adapterDt.notifyItemRangeInserted(startPosition, sanPhamMoiModel.getResult().size());
                                }
                            } else {
                                Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Log.e("ERROR", "Error loading data", throwable);
                            Toast.makeText(this, "Không thể kết nối server", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Dynamically set toolbar title based on "loai"
            String title = (loai == 1) ? "Điện thoại" : "Máy tính";
            getSupportActionBar().setTitle(title);
        }

        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_dt);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear(); // Prevent memory leaks
        super.onDestroy();
    }
}