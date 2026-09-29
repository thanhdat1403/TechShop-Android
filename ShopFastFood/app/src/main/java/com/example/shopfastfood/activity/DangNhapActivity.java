package com.example.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopfastfood.MainActivity;
import com.example.shopfastfood.R;
import com.example.shopfastfood.retrofit.ApiBanHang;
import com.example.shopfastfood.retrofit.RetrofitClient;
import com.example.shopfastfood.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    private TextView txtdangki, txtresetpass;
    private EditText email, pass;
    private AppCompatButton btndangnhap;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ApiBanHang apiBanHang;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        // Khởi tạo các view và Firebase Auth
        initView();
        initControll();

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
//        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
//            String savedEmail = Paper.book().read("email");
//            String savedPass = Paper.book().read("pass");
//
//            // Gọi phương thức đăng nhập tự động
//            autoLogin(savedEmail, savedPass);
//        }
    }
    //hàm tự động đăng nhập (bỏ cách này tạm thời vì khi dùng cách này :khi đăng xuất lại lập tức đăng nhập lại
//    private void autoLogin(String email, String pass) {
//        // Hiển thị ProgressDialog khi đăng nhập tự động
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Đang đăng nhập...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // Đăng nhập với Firebase
//        firebaseAuth.signInWithEmailAndPassword(email, pass)
//                .addOnCompleteListener(this, task -> {
//                    progressDialog.dismiss();
//                    if (task.isSuccessful()) {
//                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                        if (firebaseUser != null) {
//                            // Người dùng đã đăng nhập thành công, chuyển đến MainActivity
//                            Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        handleFirebaseAuthError(task.getException());
//                    }
//                });
//    }


    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();

        // Đọc dữ liệu từ Paper nếu có
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
        }
    }

    private void initControll() {
        txtdangki.setOnClickListener(v -> {
            Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
            startActivity(intent);
        });

        txtresetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
            startActivity(intent);
        });

        btndangnhap.setOnClickListener(v -> {
            String str_email = email.getText().toString().trim();
            String str_pass = pass.getText().toString().trim();

            // Kiểm tra thông tin nhập vào
            if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(this, "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(str_pass)) {
                Toast.makeText(this, "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Đang đăng nhập...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Firebase Authentication
            firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                    .addOnCompleteListener(this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Lưu lại email và password vào Paper
                                Paper.book().write("email", str_email);
                                Paper.book().write("pass", str_pass);
                                // Người dùng đã đăng nhập, chuyển trực tiếp tới MainActivity
                                Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d("FirebaseUser", "User Email: " + firebaseUser.getEmail());
                                // Gọi API để xác thực và vào trang chủ
                                dangNhap(str_email, str_pass);
                            } else {
                                Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            handleFirebaseAuthError(task.getException());
                        }
                    });
        });
    }

    private void dangNhap(String email, String pass) {
        compositeDisposable.add(apiBanHang.dangNhap(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                    Log.d("API_LOGIN", "Response: " + new Gson().toJson(userModel));
                    if (userModel.getResult() != null && !userModel.getResult().isEmpty()) {
                        isLogin = true;
                        // Đăng nhập thành công, chuyển sang MainActivity
                        Utils.user_current = userModel.getResult().get(0);
                        Paper.book().write("islogin", isLogin);
                        Paper.book().write("user", Utils.user_current);

                        Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Đảm bảo đóng DangNhapActivity
                    } else {
                        Toast.makeText(this, userModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> {
                    Log.e("API_LOGIN_ERROR", "Error: " + throwable.getMessage());
                    Toast.makeText(this, "Đăng nhập thất bại: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void handleFirebaseAuthError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Tài khoản không tồn tại.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Lỗi: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Mới thêm 19/2/2025
    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null){
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
