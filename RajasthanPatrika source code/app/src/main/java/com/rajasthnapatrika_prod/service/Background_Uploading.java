package com.rajasthnapatrika_prod.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

/**
 * Created by mshrotriya on 2/9/2016.
 */
public class Background_Uploading extends Service {
    String Media_name = "";
    public static ArrayList<HashMap<String, String>> paths = new ArrayList<>();
    public ArrayList<HashMap<String, String>> breaking_arrayList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> detail_arrayList = new ArrayList<>();
    static int increse_media = 0;
    DBHelper dbHelper;
    String TAG = "Background_Uploading";
    boolean flag = false, call_again = false;
    String News_Id;
    String Nid = "";
    File f;
    String flag_type = "";
    int detial_size = 0;
    FTPClient client;
    String MiliSec = "";
    boolean img_flag = false, vid_flag = false, aud_flag = false;
    Context context;
    AsyncTask<String, Void, Void> asyncBackground;
    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        dbHelper = new DBHelper(getApplicationContext(), null, null, 0);
//        client = new FTPClient();
        if (AppUtills.showLogs) Log.v(TAG, "onCreate called");
    }

    public void uploadFileTask(String file) {
        f = new File(file);
        client = new FTPClient();
        if (AppUtills.showLogs) Log.v("TAG", "uploadFileTask called");

        new AsyncTask<String, Void, Void>(){


            @Override
            protected Void doInBackground(String... params) {

                    try {

                        if (paths.size() > 0) {

                            Log.v("AsyncBackground doInBackground", "AsyncBackground doInBackground");

                            client.connect(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_server", ""), Integer.parseInt(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_port", "")));
                            Log.v("username", "user name  " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_name", ""));
                            Log.v("FTP_PASS", " FTP_PASS  " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_password", ""));
                            client.login(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_name", ""), AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_password", ""));
                            client.setType(FTPClient.TYPE_BINARY);
                            if (AppUtills.showLogs)
                                Log.e("TAG", "Directory path   " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));

                            try {

                                client.changeDirectory(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));

                            } catch (Exception e) {
                                String s = AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", "");

                                String array[] = s.split("/");

                                System.out.println(array.length);

                                String str = "/";
                                for (int i = 1; i < array.length; i++) {
                                    str += array[i] + "/";
                                    System.out.println(str);
                                    try {
                                        client.createDirectory(str);
                                    } catch (Exception ex) {
                                        continue;
                                    }
                                }

                                client.changeDirectory(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));

                            }

                            boolean supported = client.isResumeSupported();

                            if (AppUtills.showLogs) Log.v(TAG, "isResumeSupported   " + supported);

                            client.upload(f, new MyTransferListener());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        stopSelf();
                        if (AppUtills.isNetworkAvailable(context))
                            startService(new Intent(getApplicationContext(), Background_Uploading.class));
                        try {
                            client.logout();
                            client.disconnect(true);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            stopSelf();
                            if (AppUtills.isNetworkAvailable(context))
                                startService(new Intent(getApplicationContext(), Background_Uploading.class));
                        }
                }
                return null;
            }
        }.execute();

    }

//    class AsyncBackground extends  AsyncTask<String, Void, Void>{
//
//       public AsyncBackground(String file)
//        {
//            f = new File(file);
//            Log.v("AsyncBackground called constructor", "AsyncBackground constructor" +file);
//        }
//
//        @Override
//        protected void onCancelled(Void aVoid) {
//            super.onCancelled(aVoid);
//            Log.v("onCancelled  ", "onCancelled " );
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            Log.v("onCancelled def", "onCancelled def" );
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//
//            try {
//
//                Log.v("AsyncBackground doInBackground", "AsyncBackground doInBackground" );
//
//                client.connect(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_server", ""), Integer.parseInt(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_port", "")));
//                Log.v("username", "user name  " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_name", ""));
//                Log.v("FTP_PASS", " FTP_PASS  " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_password", ""));
//                client.login(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_name", ""), AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_password", ""));
//                client.setType(FTPClient.TYPE_BINARY);
//                if (AppUtills.showLogs)
//                    Log.e("TAG", "Directory path   " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));
//
//                try {
//
//                    client.changeDirectory(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));
//
//                } catch (Exception e) {
//                    String s = AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", "");
//
//                    String array[] = s.split("/");
//
//                    System.out.println(array.length);
//
//                    String str = "/";
//                    for (int i = 1; i < array.length; i++) {
//                        str += array[i] + "/";
//                        System.out.println(str);
//                        try {
//                            client.createDirectory(str);
//                        } catch (Exception ex) {
//                            continue;
//                        }
//                    }
//
//                    client.changeDirectory(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));
//                }
//
//                boolean supported = client.isResumeSupported();
//
//                if (AppUtills.showLogs) Log.v(TAG, "isResumeSupported   " + supported);
//
//                client.upload(f, new MyTransferListener());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                stopSelf();
//                if(AppUtills.isNetworkAvailable(context))
//                    startService(new Intent(getApplicationContext(), Background_Uploading.class));
//                try {
//                    client.logout();
//                    client.disconnect(true);
//                } catch (Exception e2) {
//                    e2.printStackTrace();
//                    stopSelf();
//                    if(AppUtills.isNetworkAvailable(context))
//                        startService(new Intent(getApplicationContext(), Background_Uploading.class));
//                }
//            }
//            return null;
//        }
//    }

    /*******
     * Used to file upload and show progress
     **********/

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            Media_name = f.getName();
            System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {

            System.out.println(" transferred ..." + length);
        }

        public void completed() {

            try {

                call_again = false;
                System.out.println(" f.getName() ..." + f.getName());
                System.out.println(" MiliSec oncompleted  " + MiliSec);
                System.out.println(" MiliSec oncompleted  " + detail_arrayList.size());
                if (detail_arrayList != null && detail_arrayList.size() > 0)
                    MiliSec = detail_arrayList.get(detial_size).get("MiliSec");
                if (!detail_arrayList.get(detial_size).get("MiliSec").equals(""))
                    client.rename(f.getName(), AppController_Patrika.getSharedPreferences().getString("userid", "") + "_" + MiliSec + "_" + f.getName());
                else {
                    if (detail_arrayList != null && detail_arrayList.size() > 0) {
                        MiliSec = detail_arrayList.get(detial_size).get("MiliSec");
                        client.rename(f.getName(), AppController_Patrika.getSharedPreferences().getString("userid", "") + "_" + MiliSec + "_" + f.getName());
                    } else {
                        detail_arrayList.clear();
                        detail_arrayList = dbHelper.getAll_News_By_Type("new", AppController_Patrika.Story_Type_Detailed);
                        if (detail_arrayList != null && detail_arrayList.size() > 0) {
                            MiliSec = detail_arrayList.get(detial_size).get("MiliSec");
                            client.rename(f.getName(), AppController_Patrika.getSharedPreferences().getString("userid", "") + "_" + MiliSec + "_" + f.getName());
                        }
                    }
                }
                System.out.println(" MiliSec oncompleted  " + MiliSec);

                System.out.println(" completed ..." + increse_media);
                System.out.println(" Media ID ..." + paths.get(increse_media).get("Id"));
                if (paths.get(increse_media).get("Id") != null)
                    dbHelper.Update_Media_Status(paths.get(increse_media).get("Id"), paths.get(increse_media).get("Type"), detail_arrayList.get(0).get("Id"));
                flag = true;
                Log.e(TAG, "detail_arrayList size in complte  " + detail_arrayList.size());
                if (detail_arrayList.size() > 0)
                    excute_send_Detailed_News_Async(detail_arrayList, 0);
                if (paths.get(increse_media).get("Id") != null)
                    if (AppUtills.showLogs)
                        Log.v(TAG, "Media_Id    " + paths.get(increse_media).get("Id"));
                if (AppUtills.showLogs) Log.v(TAG, "News_Id    " + News_Id);
                increse_media++;
//                paths = dbHelper.getAll_Images(News_Id, "queue");
//                increse_media = 0;
                if (paths.size() > increse_media) {
                    Log.e(TAG, "if par tpaths > increse_media before call to_call_upload_method" + paths.size());
                    to_call_upload_method(paths.get(increse_media).get("Path"));
                }else if (paths.size() == increse_media) {
                    Log.e(TAG, "else part paths size equals to increase media  " + paths.size());
                    Log.e(TAG, " increase media  " + increse_media);
                    paths.clear();
                    if (!News_Id.equals("")) {
                        paths = dbHelper.getAll_Images(News_Id, "queue");
                        Log.e("after get media","as news_id blank  "+paths.size());
                    }
                    if (paths.size() == 0) {
                        call_again = true;
                        increse_media = 0;
                        dbHelper.Update_processstatus(News_Id, Nid, "old", AppController_Patrika.Story_Status_Uploaded);
                        Log.e("after get media","if part paths.size() == 0 and before call submit news "+paths.size());
                        Log.e("after get media","if detail_arrayList paths.size() == 0 and before call submit news "+detail_arrayList.size());
                        stopSelf();
                        if(AppUtills.isNetworkAvailable(context))
                            startService(new Intent(getApplicationContext(), Background_Uploading.class));
                    } else {
                        stopSelf();
                        if(AppUtills.isNetworkAvailable(context))
                        startService(new Intent(getApplicationContext(), Background_Uploading.class));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void aborted() {

            System.out.println(" aborted ...");
            if (AppUtills.showLogs) Log.v(TAG, "Media_Id    " + paths.get(increse_media).get("Id"));
            if (AppUtills.showLogs) Log.v(TAG, "News_Id    " + News_Id);

            stopSelf();
            if(AppUtills.isNetworkAvailable(context))
            startService(new Intent(getApplicationContext(), Background_Uploading.class));
        }

        public void failed() {

            System.out.println(" failed ...");
            if (AppUtills.showLogs) Log.v(TAG, "Media_Id    " + paths.get(increse_media).get("Id"));
            if (AppUtills.showLogs) Log.v(TAG, "News_Id    " + News_Id);

            stopSelf();
            if(AppUtills.isNetworkAvailable(context))
            startService(new Intent(getApplicationContext(), Background_Uploading.class));
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        try {
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (AppUtills.showLogs) Log.v(TAG, "onDestroy called   ");

            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AppUtills.showLogs) Log.v(TAG, "onStartCommand");

        try {
            if (AppUtills.showLogs)
                Log.e(TAG, "News id is " + dbHelper.getAll_News_InProcessNews());

            News_Id = dbHelper.getAll_News_InProcessNews();
            paths.clear();
            if (!News_Id.equals("")) {
                paths = dbHelper.getAll_Images(News_Id, "queue");

                HashMap<String, String> hashMap = dbHelper.getListSelected_news(News_Id, AppController_Patrika.Story_Type_Detailed, "");

                if (paths.size() > 0)
                    dbHelper.Update_processstatus(News_Id, Nid, "old", AppController_Patrika.Story_Status_process);
                detail_arrayList.clear();
                detail_arrayList.add(hashMap);
                Log.e(TAG, "news detail_arrayList  size " + detail_arrayList.size());
                if (AppUtills.showLogs)
                    Log.e(TAG, "media paths  size " + paths.size());
            }
            if (paths.size() > 0) {
                flag_type = "media";
                increse_media = 0;
                to_call_upload_method(paths.get(increse_media).get("Path"));
            } else {
                submit_News();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
            if(AppUtills.isNetworkAvailable(context))
            startService(new Intent(getApplicationContext(), Background_Uploading.class));
        }

        return START_NOT_STICKY;
    }

    void submit_News() {
        try {

            Log.e(TAG, "void submit_News() called  " + detail_arrayList.size());

            detail_arrayList.clear();
            breaking_arrayList.clear();

            detail_arrayList = dbHelper.getAll_News_By_Type("new", AppController_Patrika.Story_Type_Detailed);
            breaking_arrayList = dbHelper.getAll_News_By_Type("new", AppController_Patrika.Story_Type_Breaking);

            Log.e(TAG, "detail_arrayList size in submit_News  " + detail_arrayList.size());

            flag = false;

            if (breaking_arrayList.size() != 0) {
                int j = 0;
                excute_send_Breaking_News_Async(breaking_arrayList, j);
            }
            if (detail_arrayList.size() > 0) {
                flag_type = "detail";
                increse_media = 0;
                detial_size = 0;
                MiliSec = detail_arrayList.get(detial_size).get("MiliSec");
                if (AppUtills.showLogs) Log.e(TAG, "MiliSec in submit_News " + MiliSec);
                excute_send_Detailed_News_Async(detail_arrayList, detial_size);
            }
            if (breaking_arrayList.size() == 0 && detail_arrayList.size() == 0)
                stopSelf();

        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
            if(AppUtills.isNetworkAvailable(context))
             startService(new Intent(getApplicationContext(), Background_Uploading.class));
        }
    }

    void excute_send_Breaking_News_Async(final ArrayList<HashMap<String, String>> breaking_arrayList, final int j) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... voids) {
                String Url;
                String response = "";

                try {

                    if (breaking_arrayList.get(j).get("Nid").equals(""))
                        Url = AppController_Patrika.content_create_api;
                    else
                        Url = AppController_Patrika.content_create_api + "/" + breaking_arrayList.get(j).get("Nid");

                    String new_heading = breaking_arrayList.get(j).get("Heading").replaceAll("\"", "&quote;");
                    String new_sub_heading = breaking_arrayList.get(j).get("Heading").replaceAll("\"", "&quote;");

                    Double lat = 0.0, longi = 0.0;
                    if (AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
                        lat = AppController_Patrika.Latitude;
                        longi = AppController_Patrika.Longitude;
                    } else {
                        lat = AppController_Patrika.Latitude_cell;
                        longi = AppController_Patrika.Longitude_cell;
                    }

                    String sn = " { \"title\":\"" + new_heading + "\",\"type\":\"" + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type", "") + "\",\"language\":\"und\"" +
                            ",\"field_ccms_sub_heading\":{\"und\":[{ \"value\":\"" + new_sub_heading + "\"}]}" +
                            ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"1\"}]}" +
                            ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                            ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                            "}";

                    JSONObject jsonObject = new JSONObject(sn.trim());

                    StringEntity se = new StringEntity(jsonObject.toString().trim(), HTTP.UTF_8);


                    if (AppUtills.showLogs)
                        Log.e("doInBackground", "doInBackground entity is  " + se.toString());
                    if (AppUtills.showLogs)
                        Log.e("Heading", "Heading " + breaking_arrayList.get(j).get("Heading"));
                    if (AppUtills.showLogs)
                        Log.e("Sub_heading", "Sub_heading is  " + breaking_arrayList.get(j).get("Sub_heading"));


                    if (breaking_arrayList.get(j).get("Nid").equals("")) {
                        HttpPost httppost = new HttpPost(Url);
                        httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                        httppost.setHeader("Accept", "application/json");
                        httppost.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                        httppost.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                        httppost.setEntity(se);

                        response = AppUtills.getDecodedResponse(httppost);
                    } else if (!breaking_arrayList.get(j).get("Nid").equals("")) {

                        HttpPut httpPut = new HttpPut(Url);
                        httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
                        httpPut.setHeader("Accept", "application/json");
                        httpPut.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                        httpPut.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                        httpPut.setEntity(se);

                        response = AppUtills.getDecodedResponse_Update1(httpPut);
                    }

                    if (AppUtills.showLogs) Log.v("response", "" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                    if(AppUtills.isNetworkAvailable(context))
                    startService(new Intent(getApplicationContext(), Background_Uploading.class));
                }
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                if (AppUtills.showLogs) Log.v("onPost", "onPOst breaking " + j);
                if (AppUtills.showLogs) Log.v("onPost", "onPOst breaking " + s);
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    String Nid_ = jsonObject.getString("nid");

                    dbHelper.Update_processstatus(breaking_arrayList.get(j).get("Id"), Nid_, "old", AppController_Patrika.Story_Status_Uploaded);


                    int n = j + 1;
                    if (breaking_arrayList.size() > n)
                        excute_send_Breaking_News_Async(breaking_arrayList, n);
                    else if (breaking_arrayList.size() == n) {
                        breaking_arrayList.clear();

                        if (detail_arrayList.size() > 0) {
                            int j = 0;
                            increse_media = 0;
                            excute_send_Detailed_News_Async(detail_arrayList, j);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                    if(AppUtills.isNetworkAvailable(context))
                    startService(new Intent(getApplicationContext(), Background_Uploading.class));
                }
            }
        }.execute();
    }

    void excute_send_Detailed_News_Async(final ArrayList<HashMap<String, String>> detail_arrayList, final int j) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... voids) {

                String Url;
                String response = "";

                try {

                    if (detail_arrayList.get(j).get("Nid").equals(""))
                        Url = AppController_Patrika.content_create_api;
                    else
                        Url = AppController_Patrika.content_create_api + "/" + detail_arrayList.get(j).get("Nid");

                    if (AppUtills.showLogs) Log.e(TAG, "flag is  " + flag);
                    if (AppUtills.showLogs) Log.e(TAG, "Media is  " + Media_name);
                    if (AppUtills.showLogs) Log.e(TAG, "Url is  " + Url);

                    ArrayList<HashMap<String, String>> list = dbHelper.getAll_Images(detail_arrayList.get(j).get("Id"), "");

                    ArrayList<HashMap<String, String>> Image_list = new ArrayList<>();
                    ArrayList<HashMap<String, String>> Video_list = new ArrayList<>();
                    ArrayList<HashMap<String, String>> Audio_list = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, String> hm = new HashMap<>();

                        hm.put("Path", "" + list.get(i).get("Path"));
                        hm.put("Type", list.get(i).get("Type"));
                        hm.put("Id", list.get(i).get("Id"));
                        hm.put("Status", list.get(i).get("Status"));
                        hm.put("Status_process", list.get(i).get("new"));
                        if (AppUtills.showLogs) Log.v(TAG, "image path is  " + list.get(i));

                        if (list.get(i).get("Status_process").equals("new") && list.get(i).get("Type").equals("Image") || list.get(i).get("Type").equals("Thumb")) {
                            if (list.get(i).get("Status_process").equals("new"))
                                img_flag = true;
                            Image_list.add(hm);
                        } else if (list.get(i).get("Type").equals("Audio") && list.get(i).get("Status_process").equals("new")) {
                            if (list.get(i).get("Status_process").equals("new"))
                                aud_flag = true;
                            Audio_list.add(hm);
                        } else if (list.get(i).get("Type").equals("Video") && list.get(i).get("Status_process").equals("new")) {
                            if (list.get(i).get("Status_process").equals("new"))
                                vid_flag = true;
                            Video_list.add(hm);
                        }
                    }

                    Log.e("list size", "list size is " + detail_arrayList.size());
                    Log.e("Heading Heading", "Heading " + detail_arrayList.get(j).get("Heading"));
                    Log.e("Sub_heading ", "Sub_heading " + detail_arrayList.get(j).get("Sub_heading"));
                    Log.e("Personality_topic ", "Personality_topic " + detail_arrayList.get(j).get("Personality_topic"));
                    Log.e("Category Category", "Heading " + detail_arrayList.get(j).get("Category"));

                    String personality = detail_arrayList.get(j).get("Personality_topic");
                    String intro = detail_arrayList.get(j).get("Introduction");
                    String body = detail_arrayList.get(j).get("Body");

                    String new_heading = detail_arrayList.get(j).get("Heading").replaceAll("\"", "&quote;");
                    String new_sub_heading = detail_arrayList.get(j).get("Sub_heading").replaceAll("\"", "&quote;");

                    String new_personality = personality.replaceAll("\"", "&quote;");
                    String new_intro = intro.replaceAll("\"", "&quote;");
                    String new_body = body.replaceAll("\"", "&quote;");

                    Log.v(TAG, "new_personality  " + new_personality);

                    if (detail_arrayList != null && detail_arrayList.size() > 0) {
                        MiliSec = detail_arrayList.get(detial_size).get("MiliSec");
                    }

                    Double lat = 0.0, longi = 0.0;
                    if (AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
                        lat = AppController_Patrika.Latitude;
                        longi = AppController_Patrika.Longitude;
                    } else {
                        lat = AppController_Patrika.Latitude_cell;
                        longi = AppController_Patrika.Longitude_cell;
                    }
                    String start = "";
                    if (!detail_arrayList.get(j).get("Nid").equals("")) {
                        start = " { \"title\":\"" + new_heading + "\",\"type\":\"new_article\",\"language\":\"und\"" +
                                ",\"body\":{\"und\":[{ \"value\":\"" + new_body + "\"}]}" +
                                ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\"" + new_intro + "\"}]}" +
                                ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                                ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                                ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";
                    } else {
                        start = " { \"title\":\"" + new_heading + "\",\"type\":\"new_article\",\"language\":\"und\"" +
                                ",\"body\":{\"und\":[{ \"value\":\"" + new_body + "\"}]}" +
                                ",\"field_ccms_sub_heading\":{\"und\":[{ \"value\":\"" + new_sub_heading + "\"}]}" +
                                ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\"" + new_intro + "\"}]}" +
                                ",\"field_ccms_personality\":{\"und\":[{ \"value\":\"" + new_personality + "\"}]}" +
                                ",\"field_ccms_domain\":{\"und\":[{ \"value\":\"" + detail_arrayList.get(j).get("Domain") + "\"}]}" +
                                ",\"field_ccms_category\":{\"und\":[{ \"value\":\"" + detail_arrayList.get(j).get("Category") + "\"}]}" +
                                ",\"field_ccms_scope\":{\"und\":[{ \"value\":\"" + detail_arrayList.get(j).get("Scope") + "\"}]}" +
                                ",\"field_ccms_priority\":{\"und\":[{ \"value\":\"" + detail_arrayList.get(j).get("Priority") + "\"}]}" +
                                ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                                ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                                ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";
                    }

                    String end = "}";
                    String image_start = ",\"field_ccms_uploaded_images\":{\"und\":[";
                    String img_end_part = "]}";

                    String video_start = ",\"field_ccms_uploaded_videos\":{\"und\":[";
                    String Audio_start = ",\"field_ccms_uploaded_audios\":{\"und\":[";

                    String complete_String = "";

                    boolean img_size = false;
                    boolean vid_size = false;
                    boolean aud_size = false;

                    if (Image_list.size() > 0 && img_flag)
                        img_size = true;
                    if (Video_list.size() > 0 && vid_flag)
                        vid_size = true;
                    if (Audio_list.size() > 0 && aud_flag)
                        aud_size = true;

                    int Img_size = Integer.parseInt(detail_arrayList.get(j).get("Img_size"));
                    int Vid_size = Integer.parseInt(detail_arrayList.get(j).get("Vid_size"));
                    int Aud_size = Integer.parseInt(detail_arrayList.get(j).get("Aud_size"));

                    if (AppController_Patrika.Story_Status_Uploaded.equals(detail_arrayList.get(j).get("Status"))) {
                        Img_size += Image_list.size();
                        Vid_size += Video_list.size();
                        Aud_size += Audio_list.size();
                    }

                    Log.e(TAG, "Img_size " + Img_size);
                    Log.e(TAG, "Vid_size " + Vid_size);
                    Log.e(TAG, "Aud_size " + Aud_size);

                    if (img_size && vid_size && aud_size)
                        complete_String = start + image_start + create_json_format("image", Image_list, "" + MiliSec, Img_size) + img_end_part + video_start + create_json_format("video", Video_list, "" + MiliSec, Vid_size) + img_end_part + Audio_start + create_json_format("audio", Audio_list, "" + MiliSec, Aud_size) + img_end_part + end;
                    else if (img_size && !vid_size && !aud_size)
                        complete_String = start + image_start + create_json_format("image", Image_list, "" + MiliSec, Img_size) + img_end_part + end;
                    else if (img_size && vid_size && !aud_size)
                        complete_String = start + image_start + create_json_format("image", Image_list, "" + MiliSec, Img_size) + img_end_part + video_start + create_json_format("video", Video_list, "" + MiliSec, Vid_size) + img_end_part + end;
                    else if (img_size && !vid_size && aud_size)
                        complete_String = start + image_start + create_json_format("image", Image_list, "" + MiliSec, Img_size) + img_end_part + Audio_start + create_json_format("audio", Audio_list, "" + MiliSec, Aud_size) + img_end_part + end;
                    else if (!img_size && !vid_size && !aud_size)
                        complete_String = start + end;
                    else if (!img_size && vid_size && aud_size)
                        complete_String = start + video_start + create_json_format("video", Video_list, "" + MiliSec, Vid_size) + img_end_part + Audio_start + create_json_format("audio", Audio_list, "" + MiliSec, Aud_size) + img_end_part + end;
                    else if (!img_size && !vid_size && aud_size)
                        complete_String = start + Audio_start + create_json_format("audio", Audio_list, "" + MiliSec, Aud_size) + img_end_part + end;
                    else if (!img_size && vid_size && !aud_size)
                        complete_String = start + video_start + create_json_format("video", Video_list, "" + MiliSec, Vid_size) + img_end_part + end;

                    if (AppUtills.showLogs) Log.e(TAG, "complete_String  " + complete_String);


                    JSONObject jsonObject = new JSONObject(complete_String.trim());

                    StringEntity se = new StringEntity(jsonObject.toString().trim(), HTTP.UTF_8);


                    if (detail_arrayList.get(j).get("Nid").equals("")) {
                        HttpPost httppost = new HttpPost(Url);
                        httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                        httppost.setHeader("Accept", "application/json");
                        httppost.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                        httppost.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                        httppost.setEntity(se);

                        response = AppUtills.getDecodedResponse(httppost);
                    } else if (!detail_arrayList.get(j).get("Nid").equals("")) {

                        HttpPut httpPut = new HttpPut(Url);
                        httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
                        httpPut.setHeader("Accept", "application/json");
                        httpPut.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                        httpPut.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                        httpPut.setEntity(se);

                        response = AppUtills.getDecodedResponse_Update1(httpPut);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                    if(AppUtills.isNetworkAvailable(context))
                    startService(new Intent(getApplicationContext(), Background_Uploading.class));
                }
                if (AppUtills.showLogs) Log.v("TAG", "response  " + response);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);


                    Nid = jsonObject.getString("nid");

                    if (!flag) {

//                        if(detail_arrayList.get(j).get("Nid").equals(""))
                        AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("directory_path", jsonObject.getString("field_ccms_ftp_directory")).commit();

                        detail_arrayList.get(j).put("Nid", "" + jsonObject.getString("nid"));

                        paths.clear();
                        paths = dbHelper.getAll_Images(detail_arrayList.get(j).get("Id"), "queue");

                        News_Id = detail_arrayList.get(j).get("Id");
                        increse_media = 0;

                        if (paths.size() == 0) {
                            dbHelper.Update_processstatus(detail_arrayList.get(j).get("Id"), Nid, "old", AppController_Patrika.Story_Status_Uploaded);
                            submit_News();
                        } else if (paths.size() > 0) {
                            dbHelper.Update_processstatus(detail_arrayList.get(j).get("Id"), Nid, "old", AppController_Patrika.Story_Status_process);
                            to_call_upload_method(paths.get(increse_media).get("Path"));
                        }

                        if (AppUtills.showLogs) Log.v("TAG", "onPostExecute called");
                        if (AppUtills.showLogs)
                            Log.v("TAG", "media length  " + paths.size());
                    }
                    flag = false;
                    if (call_again)
                        submit_News();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                    if(AppUtills.isNetworkAvailable(context))
                    startService(new Intent(getApplicationContext(), Background_Uploading.class));
                }
            }
        }.execute();
    }


    void to_call_upload_method(String path) {
        if (AppUtills.showLogs) Log.e(TAG, "to_call_upload_method called");
        if (paths.size() > increse_media) {
            File f = new File(path);
            if (f.exists()) {
//                    asyncBackground  =  new  AsyncBackground(path).execute();

                uploadFileTask(path);
                }
            } else {
                if_Not_Exists(path);
            }
        }

    void if_Not_Exists(String path) {
        if (AppUtills.showLogs) Log.e(TAG, "isExists  " + false);
        if (paths.get(increse_media).get("Id") != null)
            dbHelper.Update_Media_Status(paths.get(increse_media).get("Id"), paths.get(increse_media).get("Type"), "");
        increse_media++;
        if (paths.size() > increse_media)
            to_call_upload_method(paths.get(increse_media).get("Path"));
        else if (paths.size() == increse_media)
            dbHelper.Update_processstatus(News_Id, Nid, "old", AppController_Patrika.Story_Status_Uploaded);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String create_json_format(String media, ArrayList<HashMap<String, String>> list, String MiliSec, int len) {
        int new_len = 0;

        if (len > 0)
            new_len = len - 1;

        String str_json = "";
        try {

            JSONObject data_json2 = new JSONObject();
            JSONObject data_json3 = new JSONObject();

            for (int i = 0; i < list.size(); i++) {
                File file = new File(list.get(i).get("Path"));
                String strFileName = file.getName();
                JSONObject data_json1 = new JSONObject();

                data_json1.put("filename", AppController_Patrika.getSharedPreferences().getString("userid", "") + "_" + MiliSec + "_" + strFileName);

                if (list.get(i).get("Status").equals("uploaded"))
                    data_json1.put("upload_status", "1");
                else if (list.get(i).get("Status").equals("queue"))
                    data_json1.put("upload_status", "0");

                data_json2.put(media + (len + i), data_json1);
            }
            data_json3.put("value", data_json2.toString());

            String str = data_json3.toString();
            String s = str.replaceAll("\\\\", "");

            StringBuffer stringBuffer = new StringBuffer(s);

            stringBuffer.replace(9, 10, "");
            stringBuffer.replace(stringBuffer.length() - 2, stringBuffer.length() - 1, "");

            str_json = stringBuffer.toString();
            Log.e("AppUtils", "stringBuffer " + str_json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str_json;
    }
}

