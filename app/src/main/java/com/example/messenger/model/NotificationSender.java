package com.example.messenger.model;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.messenger.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {
    private NotificationData data;
    private String receiver;
    private Context mContext;
    private Activity mActivity;
    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAAfqFRYME:APA91bEnwGBnDRxtXbkc8juk3PpSm8IYCG1MHp5uhrB0UmM5OKJSq5mR8tkc0BTl2chPL3T0FWtl8BX7UZXRr-7xHK6XYTlRD8E1FSsVCJ9Mx89xElMq5ZbyrfJGBd1IPMKbz5IxVaBi";

    public NotificationSender() {
    }

    public NotificationSender(NotificationData data, String receiver, Context mContext, Activity mActivity) {
        this.data = data;
        this.receiver = receiver;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", receiver);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", data.getTitle());
            notificationObject.put("body", data.getMessage());
            notificationObject.put("icon", R.drawable.ic_messenger);

            mainObj.put("notification", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            requestQueue.add(request);

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
