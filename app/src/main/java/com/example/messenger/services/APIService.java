package com.example.messenger.services;

import com.example.messenger.model.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                 "Content-Type:application/json",
                    "Authorization:Key=AAAAfqFRYME:APA91bEnwGBnDRxtXbkc8juk3PpSm8IYCG1MHp5uhrB0UmM5OKJSq5mR8tkc0BTl2chPL3T0FWtl8BX7UZXRr-7xHK6XYTlRD8E1FSsVCJ9Mx89xElMq5ZbyrfJGBd1IPMKbz5IxVaBi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
