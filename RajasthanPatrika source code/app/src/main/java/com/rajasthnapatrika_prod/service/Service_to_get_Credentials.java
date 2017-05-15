package com.rajasthnapatrika_prod.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.activities.Drawer_Activity;
import com.rajasthnapatrika_prod.activities.MyAlarmReceiver;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Calendar;
import java.util.Iterator;

import it.sauronsoftware.ftp4j.FTPClient;

/**
 * Created by mshrotriya on 2/9/2016.
 */
public class Service_to_get_Credentials extends Service
{
    static int increse_media = 0;
    boolean flag_detail = false;
    int detail_count = 0;
    FTPClient client;
    DBHelper dbHelper;
    String TAG = "Service_to_get_Credentials";
    String flag = "";
    String News_Id;
    private DBHandler dbHandler;
    Handler handler;

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
        if(AppUtills.showLogs)Log.v(TAG,"onCreate called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(AppUtills.showLogs)Log.v(TAG, "onStartCommand");


        try
        {
            dbHandler = new DBHandler();
            dbHelper = new DBHelper(getApplicationContext(), null, null, 0);

            if(intent.getStringExtra("flag").equals("splash"))
            {
                flag = intent.getStringExtra("flag");

                excute_get_All_Credential();

                new excuteNotification_Async().execute();

//                delete_old_data();
            }
            if(intent.getStringExtra("flag").equals("get_drop_down"))
            {
                flag = intent.getStringExtra("flag");

                excute_get_All_Credential();

            }else if(intent.getStringExtra("flag").equals("domains"))
            {
                flag = intent.getStringExtra("flag");
                    excute_get_Domains(intent.getStringExtra("json"));

            }else if(intent.getStringExtra("flag").equals("categories"))
            {
                flag = intent.getStringExtra("flag");
                excute_get_categories(intent.getStringExtra("json"));
            }
            else if(intent.getStringExtra("flag").equals("scope"))
            {
                flag = intent.getStringExtra("flag");
                excute_get_scope(intent.getStringExtra("json"));
            }
            else if(intent.getStringExtra("flag").equals("priorities"))
            {
                flag = intent.getStringExtra("flag");
                excute_get_priority(intent.getStringExtra("json"));
            }
            else if(intent.getStringExtra("flag").equals("ftp"))
            {
                flag = intent.getStringExtra("flag");
                    excute_get_Ftp_Credential();
            }
            else if(intent.getStringExtra("flag").equals("sms_number"))
            {
                flag = intent.getStringExtra("flag");
                excute_get_get_Number_for_sms();
            }
            else if(intent.getStringExtra("flag").equals("alarm"))
            {
                delete_old_data();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public void delete_old_data() {
        if(AppUtills.showLogs)Log.e(TAG,"Alaram manager scheduled");
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),
                0, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, 1);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        AppController_Patrika.getSharedPreferences().edit().putBoolean("is_alarm_started", true).commit();
        // Tell the user about what we did.
    }

    void excute_get_Ftp_Credential() {

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected void onPreExecute ()
            {
                if(AppUtills.showLogs)Log.v(TAG,"excute_get_Ftp_Credential onPreExecute called");
            }

            @Override
            protected String doInBackground (Void...voids)
            {
                String response = "";

                try {
                    HttpGet httpGet = new HttpGet(AppController_Patrika.Ftp_Credential_Api);
                    httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpGet.setHeader("Accept", "application/json");
                    httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                    httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                    response = AppUtills.getDecodedResponse(httpGet);

                    if (AppUtills.showLogs) Log.v(TAG, "response" + response);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute (String s)
            {
                try {

                    if(AppUtills.showLogs)Log.v(TAG,"ftp is   "+s);

                    JSONObject jsonObject = new JSONObject(s);

                    if(AppUtills.showLogs)Log.v(TAG,"jsonObject is   "+jsonObject.getJSONObject("ftp"));
                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().clear().commit();
                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("ftp_server",""+jsonObject.getJSONObject("ftp").getString("ftp_server"))
                            .putString("ftp_user_name",""+jsonObject.getJSONObject("ftp").getString("ftp_user_name"))
                            .putString("ftp_user_password",""+jsonObject.getJSONObject("ftp").getString("ftp_user_password"))
                            .putString("ftp_port",""+jsonObject.getJSONObject("ftp").getString("ftp_port"))
                            .commit();

                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
            }
        }.execute();
    }


    void excute_get_Domains(String s) {

//        new AsyncTask<Void, Void, String>()
//        {
//            @Override
//            protected void onPreExecute ()
//            {
//                if(AppUtills.showLogs)Log.v(TAG,"excute_get_Domains onPreExecute called");
//            }
//
//            @Override
//            protected String doInBackground (Void...voids)
//            {
//                String response = "";
//
//                try {
//                    HttpGet httpGet = new HttpGet(AppController_Patrika.Domain_Api);
//                    httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
//                    httpGet.setHeader("Accept", "application/json");
//                    httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
//                    httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));
//
//                    response = AppUtills.getDecodedResponse(httpGet);
//
//                    if (AppUtills.showLogs) Log.v(TAG, "response" + response);
//                }catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                return response;
//            }
//
//            @Override
//            protected void onPostExecute (String s)
//            {
                try {

                    if(AppUtills.showLogs)Log.v(TAG,"domains is   "+s);

                    JSONObject jsonObject = new JSONObject(s);

                    if(AppUtills.showLogs)Log.v(TAG,"jsonObject is   "+jsonObject.getJSONObject("domains"));
                    dbHelper.deleteDropDowns_Data("domain");
                    Iterator<String> iter = jsonObject.getJSONObject("domains").keys();
                    if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.getJSONObject("domains").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("domain",""+value);

                        } catch (JSONException e) {
                            stopSelf();
                        }
                    }
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
//            }
//        }.execute();
    }


    void excute_get_categories(String s) {

//        new AsyncTask<Void, Void, String>()
//        {
//            @Override
//            protected void onPreExecute ()
//            {
//                if(AppUtills.showLogs)Log.v(TAG,"excute_get_categories onPreExecute called");
//            }
//
//            @Override
//            protected String doInBackground (Void...voids)
//            {
//                String response = "";
//
//                try {
//                    HttpGet httpGet = new HttpGet(AppController_Patrika.Category_Api);
//                    httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
//                    httpGet.setHeader("Accept", "application/json");
//                    httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
//                    httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));
//
//                    response = AppUtills.getDecodedResponse(httpGet);
//
//                    if (AppUtills.showLogs) Log.v(TAG, "response" + response);
//                }catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                return response;
//            }
//
//            @Override
//            protected void onPostExecute (String s)
//            {
                try {

                    if(AppUtills.showLogs)Log.v(TAG,"data is   "+s);

                    JSONObject jsonObject = new JSONObject(s);

                    if(AppUtills.showLogs)Log.v(TAG,"jsonObject is   "+jsonObject.getJSONObject("categories"));
                    dbHelper.deleteDropDowns_Data("category");
                    Iterator<String> iter = jsonObject.getJSONObject("categories").keys();
                    if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.getJSONObject("categories").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("category",""+value);

                        } catch (JSONException e) {
                            stopSelf();
                        }
                    }
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
//            }
//        }.execute();
    }

    void excute_get_scope(String s)
    {
                try {

                    if(AppUtills.showLogs)Log.v(TAG,"scope is   "+s);

                    JSONObject jsonObject = new JSONObject(s);

                    if(AppUtills.showLogs)Log.v(TAG,"jsonObject is   "+jsonObject.getJSONObject("scope"));
                    dbHelper.deleteDropDowns_Data("scope");
                    Iterator<String> iter = jsonObject.getJSONObject("scope").keys();
                    if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.getJSONObject("scope").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("scope",""+value);

                        } catch (JSONException e) {
                            stopSelf();
                        }
                    }
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
    }

    void excute_get_priority(String s) {

                try {
                    if(AppUtills.showLogs)Log.v(TAG,"priorities is   "+s);
                    JSONObject jsonObject = new JSONObject(s);
                    if(AppUtills.showLogs)Log.v(TAG,"jsonObject priorities is   "+jsonObject.getJSONObject("priorities"));
                    dbHelper.deleteDropDowns_Data("priority");
                    Iterator<String> iter = jsonObject.getJSONObject("priorities").keys();
                    if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.getJSONObject("priorities").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("priority",""+value);

                        } catch (JSONException e) {
                            stopSelf();
                        }
                    }
                    stopSelf();
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopSelf();
                }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);

        try {
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            stopSelf();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            stopSelf();
        }
    }

    void excute_get_get_Number_for_sms() {

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected void onPreExecute ()
            {
                if(AppUtills.showLogs)Log.v(TAG,"excute_get_Ftp_Credential onPreExecute called");
            }

            @Override
            protected String doInBackground (Void...voids)
            {
                String response = "";

                try {
                    HttpGet httpGet = new HttpGet(AppController_Patrika.Sms_Number_Api);
                    httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpGet.setHeader("Accept", "application/json");
                    httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                    httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                    response = AppUtills.getDecodedResponse(httpGet);

                    if (AppUtills.showLogs) Log.v(TAG, "response" + response);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute (String s)
            {
                try {

                    if(AppUtills.showLogs)Log.v(TAG,"data is   "+s);

                    if(AppUtills.showLogs)Log.v(TAG,"sms_number is   "+new JSONObject(s).getString("sms_number"));

                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("sms_number",new JSONObject(s).getString("sms_number")).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    void excute_get_All_Credential() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                if (AppUtills.showLogs) Log.v(TAG, "excute_get_All_Credential onPreExecute called");
            }

            @Override
            protected String doInBackground(Void... voids) {
                String response = "";

                try {
                    HttpGet httpGet = new HttpGet(AppController_Patrika.All_Config_Api);
                    httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpGet.setHeader("Accept", "application/json");
                    httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                    httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                    response = AppUtills.getDecodedResponse(httpGet);

                    if (AppUtills.showLogs) Log.v(TAG, "response" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String s) {


                if (AppUtills.showLogs) Log.v(TAG, "onPostExecute called response is   " + s);


                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject json_cofig = jsonObject.getJSONObject("configuration");

                    if (AppUtills.showLogs) Log.v(TAG, "ftp length is   " + json_cofig.getJSONObject("ftp").length());
                    if (AppUtills.showLogs) Log.v(TAG, "domains length is   " + json_cofig.getJSONObject("domains").length());
                    if (AppUtills.showLogs) Log.v(TAG, " sms_number is   " + json_cofig.getString("sms_number"));
                    if (AppUtills.showLogs) Log.v(TAG, "content_type is   " + json_cofig.getString("content_type"));

                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("ftp_server",""+json_cofig.getJSONObject("ftp").getString("ftp_server"))
                            .putString("ftp_user_name",""+json_cofig.getJSONObject("ftp").getString("ftp_user_name"))
                            .putString("ftp_user_password",""+json_cofig.getJSONObject("ftp").getString("ftp_user_password"))
                            .putString("ftp_port",""+json_cofig.getJSONObject("ftp").getString("ftp_port"))
                            .commit();


                    if(AppUtills.showLogs)Log.v(TAG,"ftp_server is pref  "+ AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_server",""));
                    if(AppUtills.showLogs)Log.v(TAG,"ftp_server is server  "+ json_cofig.getJSONObject("ftp").getString("ftp_server"));


                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("sms_number",json_cofig.getString("sms_number"))
                            .putString("content_type",json_cofig.getString("content_type"))
                            .putString("webview_url",json_cofig.getString("webview_url"))
                            .commit();


                    dbHelper.deleteDropDowns_Data("domain");
                    try {
                        if(AppUtills.showLogs)Log.v(TAG,"jsonObject domains is   "+json_cofig.getJSONObject("domains"));
                        Iterator<String> iter = json_cofig.getJSONObject("domains").keys();
                        if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                        while (iter.hasNext()) {
                            String key = iter.next();
                                Object value = json_cofig.getJSONObject("domains").get(key);

                                if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                                dbHelper.insertDrop_Downs_values("domain",""+value);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stopSelf();
                    }
                    dbHelper.deleteDropDowns_Data("category");
                    try {
                        if(AppUtills.showLogs)Log.v(TAG,"jsonObject categories is   "+json_cofig.getJSONObject("categories"));
                        Iterator<String> iter = json_cofig.getJSONObject("categories").keys();
                        if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                        while (iter.hasNext()) {
                            String key = iter.next();
                            Object value = json_cofig.getJSONObject("categories").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("category",""+value);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stopSelf();
                    }
                    dbHelper.deleteDropDowns_Data("scope");
                    try {
                        if(AppUtills.showLogs)Log.v(TAG,"jsonObject scope is   "+json_cofig.getJSONObject("scope"));
                        Iterator<String> iter = json_cofig.getJSONObject("scope").keys();
                        if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                        while (iter.hasNext()) {
                            String key = iter.next();
                            Object value = json_cofig.getJSONObject("scope").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("scope",""+value);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stopSelf();
                    }
                    dbHelper.deleteDropDowns_Data("priority");
                    try {
                        if(AppUtills.showLogs)Log.v(TAG,"jsonObject priorities is   "+json_cofig.getJSONObject("priorities"));
                        Iterator<String> iter = json_cofig.getJSONObject("priorities").keys();
                        if(AppUtills.showLogs) Log.e(TAG,"iter size  "+iter.hasNext());
                        while (iter.hasNext()) {
                            String key = iter.next();
                            Object value = json_cofig.getJSONObject("priorities").get(key);

                            if(AppUtills.showLogs)Log.v(TAG,"value is   "+value);
                            dbHelper.insertDrop_Downs_values("priority",""+value);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stopSelf();
                    }

                }catch(Exception e)
                {
                    e.printStackTrace();
                }


            }
        }.execute();
    }


    public class excuteNotification_Async extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpGet httpGet = new HttpGet(AppController_Patrika.Notification_Api);
            httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
            httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AppUtills.showLogs)
                Log.v(TAG, "excuteNotification_Async doinbackground" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if (AppUtills.showLogs) Log.v(TAG, "excuteNotification_Async onPOst  " + result);

            try {

                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject(result);

                    if (AppUtills.showLogs) Log.v("onPost", "jsonObject  " + jsonObject);
                } else if (json instanceof JSONArray) {

                   final JSONArray jsonArray = new JSONArray(result);

                    if (AppUtills.showLogs)
                        Log.v("onPost", "json is array size  " + jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        dbHandler.notification_id = ""+jsonObject.getString("notification_id");
                        dbHandler.Nid = "" + jsonObject.getString("nid");
                        dbHandler.Content_url = "" + jsonObject.getString("content_url");
                        dbHandler.Heading = "" + jsonObject.getString("title");
                        dbHandler.Time = "" + jsonObject.getString("notification_time");
                        dbHandler.PlaceName = "";
                        dbHandler.Required_more = "" + jsonObject.getString("type_detail");
                        dbHandler.isRead = "Unread";
                        dbHandler.Story_Type = ""+jsonObject.getString("breaking");
                        dbHelper.insertDataInNotifiation_tbl(dbHandler);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(Drawer_Activity.textView_notification != null)
                                Drawer_Activity.textView_notification.setText(""+jsonArray.length());
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

