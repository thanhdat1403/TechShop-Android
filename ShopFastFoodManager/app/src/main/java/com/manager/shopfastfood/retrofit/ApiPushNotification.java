package com.manager.shopfastfood.retrofit;

import com.manager.shopfastfood.model.NotifiRespone;
import com.manager.shopfastfood.model.NotifiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=YOUR_FCM_SERVER_KEY"
            }
    )
    @POST("fcm/send")
    Observable<NotifiRespone> sendNotification(@Body NotifiSendData data);
}
