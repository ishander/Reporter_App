package com.rajasthnapatrika_prod.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.rajasthnapatrika_prod.adapters.News_Publish_Adapter;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jgupta on 1/4/2016.
 */
public class News_Publised_Fragment extends Fragment
{
    ProgressDialog progressDialog;
    Activity activity;
    RecyclerView recyclerView;
    View view;
    TextView txt_no_record_found;
    DBHelper dbHelper;
    SwipeRefreshLayout swipeRefreshLayout;
    String TAG = "News_Publised_Fragment";
    ArrayList<HashMap<String,String>> News_published_data;

    public News_Publised_Fragment()
    {

    }
    @SuppressLint("ValidFragment")
   public News_Publised_Fragment(Activity activity)
   {
        this.activity =activity;
   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.publised_lst_view, container, false);

        dbHelper=new DBHelper(getActivity(), null, null, 0);
        News_published_data  = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txt_no_record_found = (TextView) view.findViewById(R.id.txt_no_record_found);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        swipeRefreshLayout  = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

        swipeRefreshLayout.setColorSchemeColors(R.color.white_color,R.color.white_color,R.color.white_color,R.color.white_color);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.colorPrimary);
        txt_no_record_found = (TextView) view.findViewById(R.id.txt_no_record_found);

        News_published_data.clear();
        News_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Breaking);
        if(News_published_data.size() == 0) {
            if (AppUtills.isNetworkAvailable(getActivity()))
                new excute_get_Breaking_News_Async().execute();
            else {
                AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                execute_process();
            }
        }else
            execute_process();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    swipeRefreshLayout.setRefreshing(false);

                        News_published_data.clear();
                        News_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Breaking);

                    if(News_published_data.size()>0)
                        execute_process();
                    else if(AppUtills.isNetworkAvailable(getActivity()))
                        new excute_get_Breaking_News_Async().execute();
                    else {
                        AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                        execute_process();
                    }
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
            if(News_published_data !=null) {
                    execute_process();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void execute_process()
    {

        News_published_data.clear();
        News_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Breaking);

        if(News_published_data.size()>0) {
                txt_no_record_found.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else {
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
            }
            recyclerView.setAdapter(new News_Publish_Adapter(activity, News_published_data));
            scroll_list();
    }

    public class excute_get_Breaking_News_Async extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids)
        {

            if(AppUtills.showLogs)Log.e(TAG,"Url for breaking news published  http://ccms.patrikatv.com/api/repapp/v1/contents?brkpublished=1");
            HttpGet httpGet = new HttpGet(AppController_Patrika.Published_Breaking_news_Api);
            httpGet.setHeader("X-CSRF-Token",""+ AppController_Patrika.getSharedPreferences().getString("token",""));
            httpGet.setHeader(HTTP.CONTENT_TYPE,"application/json");
            httpGet.setHeader("Accept","application/json");
            httpGet.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            if(AppUtills.showLogs)
                Log.v("", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
            if(AppUtills.showLogs)Log.e(TAG,"Cookie "+AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(AppUtills.showLogs)Log.v(TAG, "response ......................second  "+response);
            return response;
        }
        @Override
        protected void onPostExecute(String result) {

            if (AppUtills.showLogs) Log.v(TAG, "onPOst  " + result);

            try {

                if (result !=null)
                {
                }
                News_published_data.clear();
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject(result);

                    if (AppUtills.showLogs) Log.v("onPost", "jsonObject  " + jsonObject);
                } else if (json instanceof JSONArray) {

                    JSONArray jsonArray = new JSONArray(result);

                    if (AppUtills.showLogs)
                        Log.v("onPost", "json is array size  " + jsonArray.length());
                    News_published_data.clear();

                    if (jsonArray.length() > 0)
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();

//                        hashMap.put("Ftp_directory", jsonArray.getJSONObject(i).getString("ftp_directory"));
                            hashMap.put("Ftp_directory", "");
                            hashMap.put("User_name", jsonArray.getJSONObject(i).getString("user_name"));
                            hashMap.put("Heading", jsonArray.getJSONObject(i).getString("title"));
                            hashMap.put("Sub_heading", jsonArray.getJSONObject(i).getString("sub_heading"));
                            hashMap.put("PlaceName", AppUtills.getPlace_Name_by_lat_long(getActivity(), jsonArray.getJSONObject(i).getString("content_place_latitude"), jsonArray.getJSONObject(i).getString("content_place_longitude")));
                            hashMap.put("Time", jsonArray.getJSONObject(i).getString("content_timestamp"));
                            hashMap.put("Nid", jsonArray.getJSONObject(i).getString("nid"));
                            hashMap.put("Story_Type", AppController_Patrika.Story_Type_Breaking);
                            hashMap.put("publish_status_print", jsonArray.getJSONObject(i).getString("publish_status_print"));
                            hashMap.put("publish_status_tv", jsonArray.getJSONObject(i).getString("publish_status_tv"));
                            hashMap.put("publish_status_web", jsonArray.getJSONObject(i).getString("publish_status_web"));

                            News_published_data.add(hashMap);

                            dbHelper.discard_Stored_data(jsonArray.getJSONObject(i).getString("nid"),"publish");

                            if (AppUtills.showLogs)Log.v(TAG,"Nid is  "+jsonArray.getJSONObject(i).getString("nid"));
                            if (AppUtills.showLogs)Log.v(TAG,"title is  "+jsonArray.getJSONObject(i).getString("title"));
                            if (AppUtills.showLogs)Log.v(TAG,"content_timestamp is  "+jsonArray.getJSONObject(i).getString("content_timestamp"));

                            if(News_published_data.size() == jsonArray.length())
                                new Async_execute_db().execute();
                        }
                    }else
                    {
                        if (progressDialog !=null && progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }
                        txt_no_record_found.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    if (AppUtills.showLogs)
                        Log.v("List size", "list size  " + News_published_data.size());
                }else
                {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    AppUtills.alertDialog(getActivity(), result);
                    execute_process();
                }
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

    void scroll_list()
    {
        Log.e(TAG,"scroll_list called "+Breaking_News_Fragment.nid_published);
        if(!Breaking_News_Fragment.nid_published.equals(""))
        {
            HashMap<String,String> hashMap =   dbHelper.check_if_news_exist(Breaking_News_Fragment.nid_published,"publish",AppController_Patrika.Story_Type_Breaking);
//            Breaking_News_Fragment.nid_published = "";
            if(hashMap!=null && hashMap.size()>0) {
                if (!hashMap.get("id").equals("")) {
                    Log.e(TAG, "scroll_list id is " + Breaking_News_Fragment.nid_published);
                    if (hashMap.get("type").equals(AppController_Patrika.Story_Type_Breaking)) {
                        int j = 0;
                        for (int i = 0; i < News_published_data.size(); i++) {
                            j++;
                            if (News_published_data.get(i).get("Id").equals(hashMap.get("id")))
                                break;
                        }

                        if (j != 0) {
                            if (News_published_data.size() == j)
                                recyclerView.scrollToPosition(j - 1);
                            else if (News_published_data.size() == 1)
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

            for (int i = 0; i < News_published_data.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();

//                        hashMap.put("Ftp_directory", jsonArray.getJSONObject(i).getString("ftp_directory"));
                hashMap.put("Ftp_directory", "");
                hashMap.put("User_name", News_published_data.get(i).get("User_name"));
                hashMap.put("Heading", News_published_data.get(i).get("Heading"));
                hashMap.put("Sub_heading", News_published_data.get(i).get("Sub_heading"));

                hashMap.put("PlaceName", News_published_data.get(i).get("PlaceName"));
                hashMap.put("Time", News_published_data.get(i).get("Time"));
                hashMap.put("Nid", News_published_data.get(i).get("Nid"));
                hashMap.put("Story_Type", News_published_data.get(i).get("Story_Type"));
                hashMap.put("publish_status_print", News_published_data.get(i).get("publish_status_print"));
                hashMap.put("publish_status_tv", News_published_data.get(i).get("publish_status_tv"));
                hashMap.put("publish_status_web", News_published_data.get(i).get("publish_status_web"));
                hashMap.put("Thumbnail", ""+News_published_data.get(i).get("Thumbnail"));


              if(i==0)
                dbHelper.insert_published_News(hashMap, AppController_Patrika.Story_Type_Breaking,true);
                else if(i >0)
                  dbHelper.insert_published_News(hashMap, AppController_Patrika.Story_Type_Breaking,false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            News_published_data.clear();
            News_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Breaking);

            Drawer_Activity.textView_breaking.setText(""+News_published_data.size());

            if(News_published_data.size()>0) {
                txt_no_record_found.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else {
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
            }

            progressDialog.dismiss();
            recyclerView.setAdapter(new News_Publish_Adapter(activity, News_published_data));
            progressDialog.dismiss();
        }
    }
}
