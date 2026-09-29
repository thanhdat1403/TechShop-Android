package com.example.shopfastfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.example.shopfastfood.MainActivity;
import com.example.shopfastfood.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        // Chuyển sang màn hình tiếp theo sau 2 giây
//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, DangNhapActivity.class);
//            startActivity(intent);
//            finish();
//        }, 2000); // 2000ms = 2 giây

        //Cách 2 chuyển sang màn hình tiếp theo
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception ex) {

                }finally {
                    //Kiểm tra nếu nó chưa đăng nhập thì chuyển vào màn hình đăng nhập còn chưa thì vào màn hình main
                    //Nhớ là lần chạy đầu tiên nó vẫn thông qua màn hình đăng nhập để có thể ghi lại thông tin lần chạy thứ 2 thì không cần thông quá nữa mà nó đã nhớ rồi và chạy thẳng vào màn hình chính
                    if(Paper.book().read("user") == null){
                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}
