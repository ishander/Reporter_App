package com.rajasthnapatrika_prod.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.service.Background_Uploading;
import com.rajasthnapatrika_prod.utils.AppUtills;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private DBHelper dbHelper;
    int Media_id;

    @Override
public void onReceive(Context context, Intent intent) {
    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        dbHelper = new DBHelper(context, null, null, 0);

    boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
            mobile != null && mobile.isConnectedOrConnecting();
    if (isConnected) {
        Log.d("Network Available ", "YES");
        AppUtills.getPlace_Name(context);
        if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
        {
           if(!AppUtills.isMyServiceRunning(Background_Uploading.class,context))
           {
               context.startService(new Intent(context, Background_Uploading.class));
           }
        }
    } else {
        Log.d("Network Available ", "NO");
        context.stopService(new Intent(context,Background_Uploading.class));
    }
}}