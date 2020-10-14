package com.example.wms_outbound_menu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    public static String getConnectionStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkStatus = cm.getActiveNetworkInfo();
        if (networkStatus != null) {
            return "online";
        } else {
            return "offline";
        }
    }
}
