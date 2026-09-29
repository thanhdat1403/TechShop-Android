package com.example.shopfastfood.retrofit;

import com.example.shopfastfood.model.DonHangModel;
import com.example.shopfastfood.model.LoaiSpModel;
import com.example.shopfastfood.model.MessageModel;
import com.example.shopfastfood.model.SanPhamMoiModel;
import com.example.shopfastfood.model.UserModel;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );

    //Viết để post dữ liệu nên
    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    //Viết để post dữ liệu nên
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder(
            @Field("email") String email,
            @Field("sodienthoai") String sodienthoai,
            @Field("tongtien") String tongtien,
            @Field("iduser") int iduser,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken(
            @Field("status") int status
    );

    @POST("xoa.php")
    @FormUrlEncoded
    Observable<MessageModel> xoaSanPham(
            @Field("id") int id
    );

    @POST("insertsp.php")
    @FormUrlEncoded
    Observable<MessageModel> insertSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int loai
    );

    //    @POST("updatesp.php")
//    @FormUrlEncoded
//    Observable<MessageModel> updatesp(
//            @Field("tensp") String tensp,
//            @Field("gia") String gia,
//            @Field("hinhanh") String hinhanh,
//            @Field("mota") String mota,
//            @Field("loai") int loai,
//            @Field("id") int id
//    );
    @POST("updatesp.php")
    @FormUrlEncoded
    Observable<MessageModel> updatesp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int loai,
            @Field("id") int id
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("updatemomo.php")
    @FormUrlEncoded
    Observable<MessageModel> updateMomo(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("updateorder.php")
    @FormUrlEncoded
    Observable<MessageModel> updateOrder(
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );


    @Multipart
    @POST("upload.php")
    Call<MessageModel> uploadFile(
            @Part MultipartBody.Part file
    );

}
