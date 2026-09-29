package com.example.shopfastfood.activity;

import static com.example.shopfastfood.utils.Utils.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.shopfastfood.MainActivity;
import com.example.shopfastfood.R;
import com.example.shopfastfood.model.GioHang;
import com.example.shopfastfood.model.NotifiSendData;
import com.example.shopfastfood.retrofit.ApiBanHang;
import com.example.shopfastfood.retrofit.ApiPushNotification;
import com.example.shopfastfood.retrofit.RetrofitClient;
import com.example.shopfastfood.retrofit.RetrofitClientNotifi;
import com.example.shopfastfood.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.momo.momo_partner.AppMoMoLib;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien, txtemail;
    EditText edtdiachi,txtsodt;
    AppCompatButton btndathang,btnmomo;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;
    int iddonhang;

    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "Thanh toán đơn hàng online";
    private String merchantCode = "MOMOC2IC20251403";//Nên tạo tài khoản Momo doanh nghiệp và mã đối tác của mik(còn trên đây chỉ là mã đối tác tham khảo)
    private String merchantNameLabel = "Nhà cung cấp ThanhDat";
    private String description = "Mua hàng online";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        // Sử dụng Gson với setLenient để xử lý JSON không hợp lệ
        //Gson gson = new GsonBuilder().setLenient().create();

        // Khởi tạo OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // Khởi tạo Gson với chế độ lenient
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Khởi tạo Retrofit với client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // Đã khai báo client, không còn lỗi
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        initView();
        countItem();
        initControl();
    }
//    public void processJsonData(String jsonString) {
//        // Kiểm tra jsonString có hợp lệ không
//        if (jsonString == null || jsonString.isEmpty()) {
//            Log.e("API_ERROR", "Dữ liệu JSON tapiBanHang.createOderrống hoặc null");
//            return;
//        }
//
//        // Khởi tạo Gson với chế độ linh hoạt
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        // Loại bỏ các ký tự escape không mong muốn
//        jsonString = jsonString.replace("\\u003d", "=")
//                .replace("\\u0026", "&");
//
//        List<GioHang> gioHang;
//        try {
//            gioHang = gson.fromJson(jsonString, new TypeToken<List<GioHang>>(){}.getType());
//            Log.d("DEBUG", "Giỏ hàng: " + gioHang);
//        } catch (JsonSyntaxException e) {
//            Log.e("API_ERROR", "Lỗi JSON: " + e.getMessage());
//        }
//    }

    //Đếm số lượng đơn hàng
    private void countItem() {
        totalItem = 0;
        for (int i = 0; i < Utils.mangmuahang.size(); i++){
            //Tính tổng sản phẩm duyệt qua mạng mua hàng
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();//Cứ 1 lần nó chạy qua là nó lấy số lượng
        }
    }

    //Get token through MoMo app
    private void requestPayment(String iddonhang) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", iddonhang); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", iddonhang); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("Thành công",data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    compositeDisposable.add(apiBanHang.updateMomo(iddonhang,token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            throwable -> {
                                Log.d("error",throwable.getMessage());
                            }
                    ));
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("Thành công","Không thành công");
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("Thành công","Không thành công");
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("Thành công","Không thành công");
                } else {
                    //TOKEN FAIL
                    Log.d("Thành công","Không thành công");
                }
            } else {
                Log.d("Thành công","Không thành công");
            }
        } else {
            Log.d("Thành công","Không thành công");
        }
    }


    private void initControl() {
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(v -> finish());
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e("ERROR", "Không tìm thấy Toolbar");
            return;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back); // Đặt icon nếu cần
        toolbar.setNavigationOnClickListener(v -> finish());


        // Định dạng tổng tiền
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien", 0);
        txttongtien.setText(decimalFormat.format(tongtien));

        // Kiểm tra user_current
//        if (Utils.user_current != null) {
//            txtemail.setText(Utils.user_current.getEmail());
//            txtsodt.setText(Utils.user_current.getMobile());
//        } else {
//            Toast.makeText(this, "Thông tin người dùng không tồn tại", Toast.LENGTH_SHORT).show();
//        }
        try {
            if (Utils.user_current != null) {
                txtemail.setText(Utils.user_current.getEmail());

                // KHÔNG lấy số điện thoại từ user_current
                // để người dùng tự nhập số điện thoại giao hàng.
                txtsodt.setText("");
            } else {
                Toast.makeText(
                        this,
                        "Thông tin người dùng không tồn tại",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            Log.e("ERROR", "Error setting user data: " + e.getMessage());
        }


//        btndathang.setOnClickListener(view -> {
//            // Kiểm tra địa chỉ
//            String str_diachi = edtdiachi.getText().toString().trim();
//            if (TextUtils.isEmpty(str_diachi)) {
//                Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Kiểm tra giỏ hàng
//            if (Utils.mangmuahang == null || Utils.mangmuahang.isEmpty()) {
//                Toast.makeText(this, "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
//            } else {
//                // Log dữ liệu giỏ hàng
//                String str_email = Utils.user_current.getEmail();
//                String str_sdt = Utils.user_current.getMobile();
//                int id = Utils.user_current.getId();
//                Log.d("test", new Gson().toJson(Utils.mangmuahang)); //chỉ để kiểm tra thôi
//                compositeDisposable.add(apiBanHang.createOder(str_email,str_sdt,String.valueOf(tongtien), id, str_diachi, totalItem,new Gson().toJson(Utils.mangmuahang))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        userModel -> {
//                            pushNotiToUser();
//                            Toast.makeText(getApplicationContext(),"Thành công", Toast.LENGTH_SHORT).show();
//                            //khi mua thành công thì cũng phải clear đi
//                            Utils.mangmuahang.clear();
//                            //chuyển màn hình
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        },
//                        throwable -> {
//                            Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                ));
//            }
//        });

        btndathang.setOnClickListener(view -> {
            String str_diachi = edtdiachi.getText().toString().trim();
            String str_dienthoai = txtsodt.getText().toString().trim();
            if (TextUtils.isEmpty(str_diachi) && TextUtils.isEmpty(str_dienthoai)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.mangmuahang == null || Utils.mangmuahang.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String str_email = Utils.user_current.getEmail();
//                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();

                    Log.d("DEBUG", "Email: " + str_email + ", Số điện thoại: " + str_dienthoai + ", Tổng tiền: " + tongtien + ", Địa chỉ: " + str_diachi);
                    Log.d("DEBUG", "Giỏ hàng: " + new Gson().toJson(Utils.mangmuahang));

                    // ====================== DEBUG TRƯỚC KHI GỬI ======================
                    Log.e("POST_DATA", "======================================");
                    Log.e("POST_DATA", "EMAIL      = " + str_email);
                    Log.e("POST_DATA", "SDT        = " + str_dienthoai);
                    Log.e("POST_DATA", "TONGTIEN   = " + tongtien);
                    Log.e("POST_DATA", "IDUSER     = " + id);
                    Log.e("POST_DATA", "DIACHI     = " + str_diachi);
                    Log.e("POST_DATA", "SOLUONG    = " + totalItem);
                    Log.e("POST_DATA", "CHITIET    = " + new Gson().toJson(Utils.mangmuahang));
                    Log.e("POST_DATA", "======================================");
                    compositeDisposable.add(apiBanHang.createOder(str_email, str_dienthoai, String.valueOf(tongtien), id, str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {

                                        Log.e("DONHANG", "========================");
                                        Log.e("DONHANG", "SUCCESS = " + messageModel.isSuccess());
                                        Log.e("DONHANG", "MESSAGE = " + messageModel.getMessage());
                                        Log.e("DONHANG", "IDDONHANG = " + messageModel.getIddonhang());
                                        Log.e("DONHANG", "========================");

                                        if (messageModel.isSuccess()) {

                                            Toast.makeText(getApplicationContext(),
                                                    "Đặt hàng thành công",
                                                    Toast.LENGTH_SHORT).show();

                                            // Tạm thời KHÔNG gửi thông báo Firebase
                                            // pushNotiToUser();

                                            Utils.mangmuahang.clear();

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {

                                            Toast.makeText(getApplicationContext(),
                                                    "Đặt hàng thất bại: " + messageModel.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    throwable -> {
                                        Log.e("DONHANG_ERROR", "Lỗi", throwable);
                                        Toast.makeText(getApplicationContext(),
                                                throwable.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                            ));
                } catch (Exception e) {
                    Log.e("ERROR", "Exception: " + e.getMessage());
                }
            }
        });

        btnmomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_diachi = edtdiachi.getText().toString().trim();
                String str_dienthoai = txtsodt.getText().toString().trim();
                if (TextUtils.isEmpty(str_diachi) && TextUtils.isEmpty(str_dienthoai)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Utils.mangmuahang == null || Utils.mangmuahang.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String str_email = Utils.user_current.getEmail();
                        //String str_sdt = Utils.user_current.getMobile();
                        int id = Utils.user_current.getId();

                        Log.d("DEBUG", "Email: " + str_email + ", Số điện thoại: " + str_dienthoai + ", Tổng tiền: " + tongtien + ", Địa chỉ: " + str_diachi);
                        Log.d("DEBUG", "Giỏ hàng: " + new Gson().toJson(Utils.mangmuahang));

                        compositeDisposable.add(apiBanHang.createOder(str_email, str_dienthoai, String.valueOf(tongtien), id, str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {

                                            Log.d("API_RESPONSE",
                                                    "success = " + messageModel.isSuccess());

                                            Log.d("API_RESPONSE",
                                                    "message = " + messageModel.getMessage());

                                            Log.d("API_RESPONSE",
                                                    "iddonhang = " + messageModel.getIddonhang());


                                            if (messageModel.isSuccess()) {

                                                Toast.makeText(
                                                        ThanhToanActivity.this,
                                                        "Đặt hàng thành công - Mã đơn: "
                                                                + messageModel.getIddonhang(),
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                pushNotiToUser();

                                                Utils.mangmuahang.clear();

                                                Intent intent = new Intent(
                                                        ThanhToanActivity.this,
                                                        MainActivity.class
                                                );

                                                startActivity(intent);
                                                finish();

                                            } else {

                                                Toast.makeText(
                                                        ThanhToanActivity.this,
                                                        "Đặt hàng thất bại: "
                                                                + messageModel.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                Log.e("API_ERROR",
                                                        "Server báo thất bại: "
                                                                + messageModel.getMessage());
                                            }
                                        },

                                        throwable -> {

                                            Log.e(
                                                    "API_ERROR",
                                                    "Error: " + throwable.getMessage(),
                                                    throwable
                                            );

                                            Toast.makeText(
                                                    ThanhToanActivity.this,
                                                    "Lỗi API: " + throwable.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                ));
                    } catch (Exception e) {
                        Log.e("ERROR", "Exception: " + e.getMessage());
                    }
                }
            }
        });


    }


    //lấy token của admin để gửi đi
    private void pushNotiToUser() {
        //lấy token :gettoken
        compositeDisposable.add(apiBanHang.gettoken(1)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                userModel -> {
                    if (userModel.isSuccess()){
                        for (int i = 0; i<userModel.getResult().size();i++){
                            //String token = "APA91bEFHYNQPCjGlQhP86zYBf8xfpr9Y45RboIOZikTQcMOIBUtyBX0zs73mw6i-ckG4f6c7x_DnT4UNUUC-mB-p8k63EjFseQVNHAP8zmlELwVdFcBskk";//đang chưa lấy được token khi đã đăng nhập thành công
                            Map<String, String> data = new HashMap<>();
                            data.put("title", "Thông báo");
                            data.put("body","Bạn đã có đơn hàng mới");
                            NotifiSendData notifiSendData = new NotifiSendData(userModel.getResult().get(i).getToken(), data);
                            ApiPushNotification apiPushNotification = RetrofitClientNotifi.getInstance().create(ApiPushNotification.class);
                            compositeDisposable.add(apiPushNotification.sendNotification(notifiSendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            notifiRespone -> {

                                            },
                                            throwable -> {
                                                Log.d("logg",throwable.getMessage());
                                            }
                                    ));
                        }
                    }
                },
                throwable -> {
                    Log.d("logg",throwable.getMessage());
                }
        ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txttongtien = findViewById(R.id.txttongtien); // TextView cho tổng tiền
        txtsodt = findViewById(R.id.txtsodienthoai); // TextView cho số điện thoại
        txtemail = findViewById(R.id.txtemail);     // TextView cho email
        edtdiachi = findViewById(R.id.editdiachi);  // EditText cho địa chỉ
        btndathang = findViewById(R.id.btndathang); // Button cho đặt hàng
        btnmomo = findViewById(R.id.btnmomo); // Button cho đặt hàng và thanh toán bằng momo
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}