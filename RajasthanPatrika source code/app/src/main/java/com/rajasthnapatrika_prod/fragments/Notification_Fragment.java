package com.rajasthnapatrika_prod.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.rajasthnapatrika_prod.adapters.Notification_Adapter;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

public class Notification_Fragment extends Fragment {

    Activity activity;
    String TAG = "Notification_Fragment";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;
    private DBHelper dbHelper;
    View rootView;
    TextView txt_no_record_found;
    private DBHandler dbHandler;
    ArrayList<HashMap<String,String>> Notifications_list = new ArrayList<>();


    public Notification_Fragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.notification_activity, parent, false);
        swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        txt_no_record_found = (TextView) rootView.findViewById(R.id.txt_no_record_found);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dbHelper=new DBHelper(getActivity(), null, null, 0);
        dbHandler = new DBHandler();
        activity = getActivity();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

        swipeRefreshLayout.setColorSchemeColors(R.color.white_color,R.color.white_color,R.color.white_color,R.color.white_color);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.colorPrimary);

        Notifications_list.clear();

        Notifications_list = dbHelper.get_All_Notifications();

        Drawer_Activity.textView_notification.setText(""+AppUtills.getNotification_list_size(Notifications_list));

        if(Notifications_list.size()>0) {
            txt_no_record_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.GONE);
            txt_no_record_found.setVisibility(View.VISIBLE);
        }


        recyclerView.setAdapter(new Notification_Adapter(activity,Notifications_list));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    swipeRefreshLayout.setRefreshing(false);
                        Notifications_list.clear();
                    Notifications_list = dbHelper.get_All_Notifications();

                    Drawer_Activity.textView_notification.setText(""+AppUtills.getNotification_list_size(Notifications_list));

                    if(Notifications_list.size()>0) {
                        txt_no_record_found.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        if(AppUtills.isNetworkAvailable(getActivity()))
                            new excuteNotification_Async().execute();
                        else
                            AppUtills.alertDialog(getActivity(),getActivity().getResources().getString(R.string.txt_network_error));
                    }
                    recyclerView.setAdapter(new Notification_Adapter(activity,Notifications_list));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public class excuteNotification_Async extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute()
        {
            if(progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
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
        protected void onPostExecute(final String result) {

            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

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

                    new AsyncTask<Void,Void,Void>()
                    {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if(progressDialog != null && !progressDialog.isShowing())
                                progressDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {

                            try {
                                if(jsonArray.length()>0) {
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
                                        dbHandler.Sub_heading = "";
                                        dbHandler.Story_Type = ""+jsonObject.getString("breaking");
                                        dbHelper.insertDataInNotifiation_tbl(dbHandler);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppUtills.alertDialog(getActivity(), result);
                                    }
                                });
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            if(progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();


                            Notifications_list.clear();

                            Notifications_list = dbHelper.get_All_Notifications();

                            Drawer_Activity.textView_notification.setText(""+AppUtills.getNotification_list_size(Notifications_list));

                            if(Notifications_list.size()>0) {
                                txt_no_record_found.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.GONE);
                                txt_no_record_found.setVisibility(View.VISIBLE);
                            }

                            recyclerView.setAdapter(new Notification_Adapter(activity,Notifications_list));

                        }
                    }.execute();
                }else
                {
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    AppUtills.alertDialog(getActivity(), result);
                    recyclerView.setVisibility(View.GONE);
                    txt_no_record_found.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
                if(result!= null && !result.equals(""))
                    AppUtills.alertDialog(getActivity(), result);
                else
                    AppUtills.alertDialog(getActivity(),getResources().getString(R.string.txt_network_error));
            }
        }
    }
}
