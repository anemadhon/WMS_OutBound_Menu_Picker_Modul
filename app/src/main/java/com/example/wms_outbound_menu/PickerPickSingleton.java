package com.example.wms_outbound_menu;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class PickerPickSingleton {
    private static PickerPickSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private PickerPickSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized PickerPickSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new PickerPickSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

