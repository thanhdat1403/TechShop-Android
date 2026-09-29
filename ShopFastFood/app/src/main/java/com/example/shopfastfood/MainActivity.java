package com.example.shopfastfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.example.shopfastfood.activity.DangNhapActivity;
import com.example.shopfastfood.activity.DienThoaiActivity;
import com.example.shopfastfood.activity.GioHangActivity;
import com.example.shopfastfood.activity.LaptopActivity;
import com.example.shopfastfood.activity.SearchActivity;
import com.example.shopfastfood.activity.XemDonActivity;
import com.example.shopfastfood.adapter.LoaiSpAdapter;
import com.example.shopfastfood.adapter.SanPhamMoiAdapter;
import com.example.shopfastfood.model.LoaiSp;
import com.example.shopfastfood.model.SanPhamMoi;
import com.example.shopfastfood.model.User;
import com.example.shopfastfood.retrofit.ApiBanHang;
import com.example.shopfastfood.retrofit.RetrofitClient;
import com.example.shopfastfood.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    //bắt sự kiện cho giỏ hàng(số lượng sp) ở ngoài trang chủ
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);

        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Nếu người dùng đã đăng nhập, lấy thông tin người dùng từ FirebaseUser
            String userId = firebaseUser.getUid();  // Lấy UID của người dùng
            String userEmail = firebaseUser.getEmail();  // Lấy email của người dùng
            String userName = firebaseUser.getDisplayName();  // Lấy tên người dùng nếu có
            String userPhone = Utils.user_current.getMobile();  // Lấy số điện thoại từ đối tượng User
            //String userPhone = firebaseUser.getPhoneNumber();  // Lấy số điện thoại từ Firebase

            // Kiểm tra nếu userPhone là null
            if (userPhone == null) {
                userPhone = "Số điện thoại không có sẵn";  // Hoặc xử lý theo cách bạn muốn
            }

            // Cập nhật đối tượng User trong Utils bằng cách sử dụng setter
            Utils.user_current = new User();
            Utils.user_current.setUid(userId);
            Utils.user_current.setEmail(userEmail);
            Utils.user_current.setUsername(userName);
            Utils.user_current.setMobile(userPhone);
        } else {
            // Nếu người dùng chưa đăng nhập, khởi tạo đối tượng User rỗng
            Utils.user_current = new User();
        }


        getToken();
        Anhxa();
        // Chạy quảng cáo
        ActionBar();
        ActionViewFlipper();

        // Kiểm tra kết nối internet
        if (isConnected(this)) {
            Toast.makeText(getApplicationContext(), "Đã kết nối internet", Toast.LENGTH_LONG).show(); //LENGTH_SHORT: hiển thị ngắn, LENGTH_LONG: hiển thị dài
            ActionViewFlipper();
            // Kết nối với PHP
            getLoaiSanPham();
            getSpMoi();
            // Bắt sự kiện cho menu khi click (điều hướng)
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "Không có internet", Toast.LENGTH_LONG).show();
        }
    }


    //Hàm lấy thông báo getToken
//    private void getToken(){
//        FirebaseMessaging.getInstance().getToken()
//                .addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        if (!TextUtils.isEmpty(s)){
//                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    messageModel -> {
//
//                                    },
//                                    throwable -> {
//                                        Log.d("log", throwable.getMessage());
//                                    }
//                            ));
//                        }
//                    }
//                });
//    }
    private void getToken() {
        // Đảm bảo Firebase đã được khởi tạo
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        // Kiểm tra xem token có hợp lệ không
                        if (!TextUtils.isEmpty(token)) {
                            // Gửi token đến backend của bạn
                            Log.d("FirebaseToken", "Token đã được lấy: " + token);
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(), token)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {
                                                // Log khi thành công
                                                Log.d("TokenSuccess", "Token đã được cập nhật thành công");
                                            },
                                            throwable -> {
                                                // Log khi có lỗi
                                                Log.d("TokenError", "Lỗi khi gửi token: " + throwable.getMessage());
                                            }
                                    ));

                        } else {
                            Log.d("TokenError", "Token rỗng");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Ghi lại lỗi nếu việc lấy token thất bại
                    Log.e("TokenFailure", "Lấy token Firebase thất bại", e);
                });
    }

    //Hàm bắt sự kiện khi click vào hàng cần điều hướng sự ở thanh menu thì nó sẽ tự chuyển giao diện đến sự kiện đó
    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai", 1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), LaptopActivity.class); //Vì điện thoại và laptop giống nhau nên dùng chung luôn DienThoaiActivity và khác nhau mỗi ở chỗ là Điện thoại loại = 1, còn Laptop loại = 2
                        //Hoặc là có thể ctrl + A toàn bộ code của DienThoaiActivity và ctrl + V vào LaptopActivity hay lớp activity nào đó và gắn cho Intent laptop = new Intent(getApplicationContext(), LaptopActivity.class); để sử dụng
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case  6:
                        Intent quanly = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(quanly);
                        break;
                    case 7:
                        // Xóa thông tin người dùng khỏi Paper và đăng xuất Firebase
                        Paper.book().delete("user");
                        Paper.book().delete("islogin");  // Đảm bảo xóa trạng thái đăng nhập

                        FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase

                        // Chuyển hướng đến màn hình đăng nhập
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();  // Đảm bảo không quay lại trang chủ sau khi đăng xuất
                        break;

                }

            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(),mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever" + throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()){
//                        Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                                //thêm dữ liệu vào cho ListView
                                mangloaisp = loaiSpModel.getResult();
                                //mangloaisp.add(new LoaiSp("Đăng xuất", "https://cdn.pixabay.com/photo/2017/05/29/23/02/logging-out-2355227_1280.png"));
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }

    // Phương thức này tạo để chạy quảng cáo
    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://cdn.tgdd.vn/Files/2015/12/03/751514/tgdd-online-friday-800-300-1.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2015/04/25/635920/banner-home.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2016/11/18/915169/2209a8e7cb17f54fc6e9a0ee207a027d9c04a18292f560f201pimgpsh_fullsize_distr_800x300.png");

        for (int i = 0; i < mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);

        // Tạo và gán Animation(Hình hoạt để chuyển động ảnh)
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khởi tạo list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else { //Nếu nó không null thì cho hiển thị badge số lượng sản phẩm ở giao diện trang chủ
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        //Tìm kiếm sản phẩm
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
        }
        badge.setText(String.valueOf(totalItem));
    }

    //Hàm kiểm tra xem thiết bị có kết nối internet hay không
//    private boolean isConnected(Context context){
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //Nhớ thêm quyền vào không bị lỗi
//        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())){
//            return true;
//        }else {
//            return false;
//        }
//    }
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
