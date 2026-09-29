package com.example.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.shopfastfood.R;
import com.example.shopfastfood.activity.DangNhapActivity;
import com.example.shopfastfood.retrofit.ApiBanHang;
import com.example.shopfastfood.retrofit.RetrofitClient;
import com.example.shopfastfood.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKyActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        initView();
        initControll();
    }

    private void initControll() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_username = username.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();

        if (TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
            Toast.makeText(getApplicationContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_username)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mobile)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_repass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa xác nhận lại mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (!str_pass.equals(str_repass)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu của bạn không khớp", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(str_email, str_pass)
                    .addOnCompleteListener(DangKyActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                postData(str_email, str_pass, str_username, str_mobile, user.getUid());
                            }
                        } else {
                            Log.e("DangKyActivity", "Firebase Error: " + task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


//    private void dangKi() {
//        String str_username = username.getText().toString().trim(); //trim() mục đích là cắt chuỗi ở 2 đầu
//        String str_email = email.getText().toString().trim(); //trim() mục đích là cắt chuỗi ở 2 đầu
//        String str_mobile  = mobile.getText().toString().trim(); //trim() mục đích là cắt chuỗi ở 2 đầu
//        String str_pass = pass.getText().toString().trim(); //trim() mục đích là cắt chuỗi ở 2 đầu
//        String str_repass = repass.getText().toString().trim(); //trim() mục đích là cắt chuỗi ở 2 đầu
//        if (TextUtils.isEmpty(str_email)){
//            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
//        }else if (TextUtils.isEmpty(str_username)){
//            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
//        }else if (TextUtils.isEmpty(str_mobile)){
//            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
//        }else if (TextUtils.isEmpty(str_pass)){
//            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
//        }else if (TextUtils.isEmpty(str_repass)){
//            Toast.makeText(getApplicationContext(), "Bạn chưa xác nhận lại mật khẩu", Toast.LENGTH_SHORT).show();
//        }else {
//            //Kiểm tra pass và repass có giống nhau không
//            if (str_pass.equals(str_repass)){
//                firebaseAuth = FirebaseAuth.getInstance();
//                firebaseAuth.createUserWithEmailAndPassword(str_email,str_pass)
//                        .addOnCompleteListener(DangKyActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()){
//                                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                                    if (user != null){
//                                        postData( str_email,str_pass, str_username, str_mobile, user.getUid());
//                                    }
//                                }else {
//                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//
//            }else {
//                Toast.makeText(getApplicationContext(), "Mật khẩu của bạn không khớp", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //    private void postData(String str_email, String str_pass, String str_username,String str_mobile, String uid){
//        //post data
//        compositeDisposable.add(apiBanHang.dangKi(str_email, str_username,str_mobile,str_pass, uid)
//                .observeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        userModel -> {
//                            if (userModel.isSuccess()){
//                                //Tự động lấy email vs pass để điền luôn
//                                Utils.user_current.setEmail(str_email);
//                                Utils.user_current.setPass(str_pass);
//                                Toast.makeText(getApplicationContext(), "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
//                                startActivity(intent);
//                                finish(); // Kết thúc màn hình đăng ký
//                            }else {
//                                Toast.makeText(getApplicationContext(),userModel.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        },
//                        throwable -> {
//                            Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                ));
//    }
    private void postData(String str_email, String str_pass, String str_username, String str_mobile, String uid) {
        compositeDisposable.add(apiBanHang.dangKi(str_email,str_pass, str_username, str_mobile, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                //mới thêm
                                Utils.user_current.setEmail(str_email);
                                Utils.user_current.setPass(str_pass);
                                // Chuyển sang màn hình đăng nhập
                                Log.d("DangKyActivity", "Đăng ký thành công!"); // Log để kiểm tra
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("DangKyActivity", "API Error: " + userModel.getMessage()); // Log lỗi từ API
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.e("DangKyActivity", "API Exception: " + throwable.getMessage()); // Log lỗi từ server
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        username = findViewById(R.id.username);
        mobile = findViewById(R.id.mobile);
        repass = findViewById(R.id.repass);
        button = findViewById(R.id.btndangki);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}