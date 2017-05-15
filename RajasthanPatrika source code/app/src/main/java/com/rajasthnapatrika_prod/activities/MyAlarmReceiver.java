package com.rajasthnapatrika_prod.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.service.Service_to_get_Credentials;

/**
 * Created by mshrotriya on 4/7/2016.
 */
public class MyAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        DBHelper  dbHelper = new DBHelper(context, null, null, 0);
        dbHelper.delete_Old_PublishedData();

       Intent moveIntent = new Intent(context, Service_to_get_Credentials.class);
        moveIntent.putExtra("flag", "alarm");
        context.startService(moveIntent);
    }
}
