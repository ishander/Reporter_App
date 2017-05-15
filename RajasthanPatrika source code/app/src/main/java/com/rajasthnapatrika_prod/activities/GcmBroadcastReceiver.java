package com.rajasthnapatrika_prod.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.rajasthnapatrika_prod.utils.AppUtills;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	if(AppUtills.showLogs)Log.v("GcmBroadcastReceiver", "GcmBroadcastReceiver onReceive ");
    	
    	if(AppUtills.showLogs)Log.v("GcmBroadcastReceiver", "intent is  "+intent.getExtras());

        ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());
       
        
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}