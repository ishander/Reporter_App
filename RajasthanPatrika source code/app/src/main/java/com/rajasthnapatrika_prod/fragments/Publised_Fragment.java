package com.rajasthnapatrika_prod.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.activities.Drawer_Activity;
import com.rajasthnapatrika_prod.adapters.Pub_Adapter;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jgupta on 1/4/2016.
 */
public class Publised_Fragment extends Fragment
{
    String TAG = "Publised_Fragment";
    Activity activity;
    ProgressDialog progressDialog;
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<HashMap<String,String>> Detailed_published_data = new ArrayList<>();
    DBHelper dbHelper;
    TextView txt_no_record_found;

    public Publised_Fragment()
    {

    }
    @SuppressLint("ValidFragment")
   public Publised_Fragment(Activity activity)
   {
        this.activity =activity;
   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.publised_lst_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefreshLayout  = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        swipeRefreshLayout.setColorSchemeColors(R.color.white_color,R.color.white_color,R.color.white_color,R.color.white_color);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.colorPrimary);
        txt_no_record_found = (TextView) view.findViewById(R.id.txt_no_record_found);

        dbHelper=new DBHelper(getActivity(), null, null, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

        if(AppUtills.showLogs)Log.v(TAG,"Detailed_published_data size is   "+Detailed_published_data.size());

        Detailed_published_data.clear();
        Detailed_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Detailed);

        if(Detailed_published_data.size()>0)
            execute_process();
        else {
            if(AppUtills.isNetworkAvailable(getActivity()))
                new excute_get_Detailed_News_Async().execute();
            else {
                    AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                    execute_process();
            }
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    swipeRefreshLayout.setRefreshing(false);

                    Detailed_published_data.clear();
                    Detailed_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Detailed);

                    if(AppUtills.showLogs)Log.e(TAG,"list  "+Detailed_published_data);

                    if(Detailed_published_data.size()>0)
                        execute_process();
                     else if(AppUtills.isNetworkAvailable(getActivity()))
                    {
                    if (AppUtills.showLogs)Log.v(TAG, "swipeRefreshLayout swipe");
                        new excute_get_Detailed_News_Async().execute();
                    }else
                    {
                        AppUtills.alertDialog(getActivity(),getResources().getString(R.string.txt_network_error));
                        execute_process();}
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (isVisibleToUser)
            {
                if(recyclerView != null)
                {
                    Detailed_published_data.clear();
                    Detailed_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Detailed);
                    if(AppUtills.showLogs)Log.e(TAG,"Detailed_published_data list "+Detailed_published_data);
                    if (Detailed_published_data.size() > 0) {
                        execute_process();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        txt_no_record_found.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void execute_process() {

        if (Detailed_published_data.size() > 0) {
            txt_no_record_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            txt_no_record_found.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(new Pub_Adapter(activity, Detailed_published_data));
        scroll_list();
    }

    void scroll_list()
    {
        Log.e(TAG,"scroll_list called "+Detailed_News_Fragment.nid_published);
        if(!Detailed_News_Fragment.nid_published.equals(""))
        {
            Log.e(TAG,"scroll_list id is "+Detailed_News_Fragment.nid_published);

            HashMap<String,String> hashMap =   dbHelper.check_if_news_exist(Detailed_News_Fragment.nid_published,"publish",AppController_Patrika.Story_Type_Detailed);
//            Detailed_News_Fragment.nid_published = "";
            if(hashMap!=null && hashMap.size()>0) {
                if (!hashMap.get("id").equals("")) {
                    if (hashMap.get("type").equals(AppController_Patrika.Story_Type_Detailed)) {
                        int j = 0;
                        for (int i = 0; i < Detailed_published_data.size(); i++) {
                            j++;
                            if (Detailed_published_data.get(i).get("Id").equals(hashMap.get("id")))
                                break;
                        }

                        if (j != 0) {
                            if (Detailed_published_data.size() == j)
                                recyclerView.scrollToPosition(j - 1);
                            else if (Detailed_published_data.size() == 1)
                                recyclerView.scrollToPosition(j);
                            else
                                recyclerView.scrollToPosition(j - 1);
                        }
                        Log.e(TAG, "scroll_list j is " + j);
                    }
                }
            }
        }
    }
    public class excute_get_Detailed_News_Async extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            if (AppUtills.showLogs)Log.v(TAG, "onPreExecute called");

            if(progressDialog !=null && !progressDialog.isShowing())
                    progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            HttpGet httpGet = new HttpGet(AppController_Patrika.Published_Detailed_news_Api);
            httpGet.setHeader("X-CSRF-Token",""+ AppController_Patrika.getSharedPreferences().getString("token",""));
            httpGet.setHeader(HTTP.CONTENT_TYPE,"application/json");
            httpGet.setHeader("Accept","application/json");
            httpGet.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            if(AppUtills.showLogs)Log.v(TAG,"token  "+AppController_Patrika.getSharedPreferences().getString("token",""));
            if(AppUtills.showLogs)Log.e(TAG,"Cookie "+AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(AppUtills.showLogs)Log.v(TAG, "in BAckground  "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (AppUtills.showLogs) Log.v(TAG, "onPOst  " + result);
            if (result !=null)
            {
            }

            try {
                Detailed_published_data.clear();
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject jsonObject = new JSONObject(result);

                    if (AppUtills.showLogs) Log.e(TAG, "jsonObject..........  " + jsonObject);
                } else if (json instanceof JSONArray) {

                    JSONArray jsonArray = new JSONArray(result);

                    if (AppUtills.showLogs)
                        Log.e(TAG, "onPOst json is array size..............  " + jsonArray.length());

                    if (jsonArray.length() > 0) {
                        String before_time = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("Ftp_directory", "");
                            hashMap.put("User_name", jsonArray.getJSONObject(i).getString("user_name"));
                            hashMap.put("Heading", jsonArray.getJSONObject(i).getString("title"));
                            hashMap.put("Sub_heading", jsonArray.getJSONObject(i).getString("sub_heading"));

                            hashMap.put("PlaceName", AppUtills.getPlace_Name_by_lat_long(getActivity(), jsonArray.getJSONObject(i).getString("content_place_latitude"), jsonArray.getJSONObject(i).getString("content_place_longitude")));
                            hashMap.put("Time", jsonArray.getJSONObject(i).getString("content_timestamp"));
                            hashMap.put("Nid", jsonArray.getJSONObject(i).getString("nid"));
                            hashMap.put("Story_Type", AppController_Patrika.Story_Type_Detailed);
                            hashMap.put("publish_status_print", jsonArray.getJSONObject(i).getString("publish_status_print"));
                            hashMap.put("publish_status_tv", jsonArray.getJSONObject(i).getString("publish_status_tv"));
                            hashMap.put("publish_status_web", jsonArray.getJSONObject(i).getString("publish_status_web"));

                            if(!jsonArray.getJSONObject(i).getString("field_ccms_thumb_image").equals("[]")) {
                                Log.e(TAG, "field_ccms_thumb_image" + jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).get("url"));

                                DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                File rootPath = new File(Environment.getExternalStoragePublicDirectory(getActivity().getResources().getString(R.string.app_name)), ".Thumbnail");
                              if(!jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url").equals("null") && jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url") != null && !jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url").equals("")) {
                                  Uri downloadUri = Uri.parse(jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url"));
                                  DownloadManager.Request request = new DownloadManager.Request(
                                          downloadUri);
                                  request.addRequestHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));
                                  request.setAllowedNetworkTypes(
                                          DownloadManager.Request.NETWORK_WIFI
                                                  | DownloadManager.Request.NETWORK_MOBILE)
                                          .setAllowedOverRoaming(false).setTitle("Patrika Reporter")
                                          .setDescription("Downloading Thumbnail")
                                          .setDestinationInExternalPublicDir("/" + getActivity().getResources().getString(R.string.app_name) + "/.Thumbnail", (new File(jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url")).getName()));

                                  mgr.enqueue(request);

                                  Log.e(TAG, "[ath in    " + jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url"));
                                  Log.e(TAG, "THumbnail in loop insert    " + rootPath + "/" + (new File(jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url")).getName()));
                                  hashMap.put("Thumbnail", rootPath + "/" + (new File(jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url")).getName()));
                              }else
                                  hashMap.put("Thumbnail", "");
                            }else
                                hashMap.put("Thumbnail",""+dbHelper.get_Thumbnail(jsonArray.getJSONObject(i).getString("nid")));

                            Detailed_published_data.add(hashMap);
                            if (AppUtills.showLogs)Log.v(TAG, "Nid is  " + jsonArray.getJSONObject(i).getString("nid"));
                            if (AppUtills.showLogs)Log.v(TAG, "Thumbnail is  " + dbHelper.get_Thumbnail(jsonArray.getJSONObject(i).getString("nid")));

                            dbHelper.discard_Stored_images_data(jsonArray.getJSONObject(i).getString("nid"), "publish");
                            dbHelper.discard_Stored_data(jsonArray.getJSONObject(i).getString("nid"), "publish");


                            if (Detailed_published_data.size() == jsonArray.length())
                                new Async_execute_db().execute();

                        }

                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        recyclerView.setVisibility(View.GONE);
                        txt_no_record_found.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    execute_process();
                    if(result!= null && !result.equals(""))
                        AppUtills.alertDialog(getActivity(), result);
                    else
                        AppUtills.alertDialog(getActivity(),getResources().getString(R.string.txt_network_error));
                }if (AppUtills.showLogs)Log.v("List size", "list size  " + Detailed_published_data.size());
            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                execute_process();
                if(result!= null && !result.equals(""))
                    AppUtills.alertDialog(getActivity(), result);
                else
                    AppUtills.alertDialog(getActivity(),getResources().getString(R.string.txt_network_error));
                e.printStackTrace();
            }
        }
    }


    public class Async_execute_db extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < Detailed_published_data.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();

//                        hashMap.put("Ftp_directory", jsonArray.getJSONObject(i).getString("ftp_directory"));
                hashMap.put("Ftp_directory", "");
                hashMap.put("User_name", Detailed_published_data.get(i).get("User_name"));
                hashMap.put("Heading", Detailed_published_data.get(i).get("Heading"));
                hashMap.put("Sub_heading", Detailed_published_data.get(i).get("Sub_heading"));

                hashMap.put("PlaceName", Detailed_published_data.get(i).get("PlaceName"));
                hashMap.put("Time", Detailed_published_data.get(i).get("Time"));
                hashMap.put("Nid", Detailed_published_data.get(i).get("Nid"));
                hashMap.put("Story_Type", Detailed_published_data.get(i).get("Story_Type"));
                hashMap.put("publish_status_print", Detailed_published_data.get(i).get("publish_status_print"));
                hashMap.put("publish_status_tv", Detailed_published_data.get(i).get("publish_status_tv"));
                hashMap.put("publish_status_web", Detailed_published_data.get(i).get("publish_status_web"));
                hashMap.put("Thumbnail", ""+Detailed_published_data.get(i).get("Thumbnail"));

                if(i==0)
                    dbHelper.insert_published_News(hashMap, AppController_Patrika.Story_Type_Detailed,true);
                else if(i >0)
                    dbHelper.insert_published_News(hashMap, AppController_Patrika.Story_Type_Detailed,false);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Detailed_published_data.clear();
            Detailed_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Detailed);

            Drawer_Activity.textView_detail.setText(""+Detailed_published_data.size());

            execute_process();

            if(progressDialog !=null && progressDialog.isShowing())
                    progressDialog.dismiss();
        }
    }
}