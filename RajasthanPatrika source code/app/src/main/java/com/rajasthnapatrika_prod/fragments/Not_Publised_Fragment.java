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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.adapters.Not_Pub_Adapter;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Not_Publised_Fragment extends Fragment {
    String TAG = "Not_Publised_Fragment";
    Activity activity;
    ListView pub_list_view;
    DBHelper dbHelper;
    RecyclerView recyclerView;
    TextView txt_no_record_found;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    Not_Pub_Adapter adapter;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private DBHandler dbHandler;
    ProgressDialog progressDialog;
    ImageView img;
//    ArrayList<ArrayList<HashMap<String, String>>> nested_list = new ArrayList<>();

    public Not_Publised_Fragment()
    {

    }
    @SuppressLint("ValidFragment")
    public Not_Publised_Fragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_not__publised_, container, false);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            txt_no_record_found = (TextView) view.findViewById(R.id.txt_no_record_found);
            img = (ImageView) view.findViewById(R.id.img);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));
            dbHelper = new DBHelper(getActivity(), null, null, 0);
            dbHandler = new DBHandler();

            swipeRefreshLayout.setColorSchemeColors(R.color.white_color, R.color.white_color, R.color.white_color, R.color.white_color);
            swipeRefreshLayout.setProgressBackgroundColor(R.color.colorPrimary);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                        arrayList.clear();
                        arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
                        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

                        adapter = new Not_Pub_Adapter(activity, arrayList);

                        if (arrayList.size() > 0) {
                            recyclerView.setAdapter(adapter);
                            txt_no_record_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            if (AppUtills.isNetworkAvailable(getActivity()))
                                new excute_get_Detailed_News_Async().execute();
                            else {
                                AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                                txt_no_record_found.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;

    }

        /* (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.OnItemTouchListener#onInterceptTouchEvent(android.support.v7.widget.RecyclerView, android.view.MotionEvent)
     */

    @Override
    public void onResume() {
        super.onResume();
        arrayList.clear();
        arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
        if (arrayList.size() > 0) {
            txt_no_record_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            txt_no_record_found.setVisibility(View.VISIBLE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        if (AppUtills.showLogs) Log.v("arrayList", "arrayList size  " + arrayList.size());
        adapter = new Not_Pub_Adapter(activity, arrayList);

        if (arrayList.size() > 0) {
            recyclerView.setAdapter(adapter);
            scroll_list();
        } else {
            if (AppUtills.isNetworkAvailable(getActivity()))
                new excute_get_Detailed_News_Async().execute();
            else {
                if (!Detailed_News_Fragment.flag_dialog)
                    AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                txt_no_record_found.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
        Detailed_News_Fragment.flag_dialog = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (isVisibleToUser) {
                if (recyclerView != null) {
                    arrayList.clear();
                    arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
                    adapter = new Not_Pub_Adapter(activity, arrayList);

                    recyclerView.setAdapter(adapter);

                    if (arrayList.size() > 0) {
                        txt_no_record_found.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        txt_no_record_found.setVisibility(View.VISIBLE);
                    }
                    if (arrayList.size() > 0)
                        scroll_list();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void scroll_list() {
        Log.e(TAG, "scroll_list called " + Detailed_News_Fragment.nid_not_published);
        if (!Detailed_News_Fragment.nid_not_published.equals("")) {
            Log.e(TAG, "scroll_list id is " + Detailed_News_Fragment.nid_not_published);

            HashMap<String, String> hashMap = dbHelper.check_if_news_exist(Detailed_News_Fragment.nid_not_published, "not_publish", AppController_Patrika.Story_Type_Detailed);
//            Detailed_News_Fragment.nid_not_published = "";
            if(hashMap!=null && hashMap.size()>0) {
                if (!hashMap.get("id").equals("")) {
                    if (hashMap.get("type").equals(AppController_Patrika.Story_Type_Detailed)) {
                        int j = 0;
                        for (int i = 0; i < arrayList.size(); i++) {
                            j++;
                            if (arrayList.get(i).get("Id").equals(hashMap.get("id")))
                                break;
                        }

                        if (j != 0) {
                            if (arrayList.size() == j)
                                recyclerView.scrollToPosition(j - 1);
                            else if (arrayList.size() == 1)
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

    public class excute_get_Detailed_News_Async extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            if (AppUtills.showLogs) Log.v(TAG, "onPreExecute called");
            if (!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpGet httpGet = new HttpGet(AppController_Patrika.Uploaded_Detailed_news_Api);
            httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
            httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            if (AppUtills.showLogs)
                Log.v("", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AppUtills.showLogs) Log.v(TAG, "respose ..............   " + response);
            return response;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            try {

                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject(result);

                    if (AppUtills.showLogs) Log.v("onPost", "jsonObject TAG.......  " + jsonObject);
                } else if (json instanceof JSONArray) {

                    final JSONArray jsonArray = new JSONArray(result);

                    if (AppUtills.showLogs)
                        Log.v("onPost", "json is array size TAG......  " + jsonArray.length());

                    if (jsonArray.length() > 0) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {
                                if (progressDialog != null && !progressDialog.isShowing()) {
                                    progressDialog.show();
                                }
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    String before_time = "";
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        dbHandler.Status_process = "old";
                                        dbHandler.Heading = "" + jsonArray.getJSONObject(i).getString("title");
                                        dbHandler.Sub_heading = "" + jsonArray.getJSONObject(i).getString("sub_heading");
                                        dbHandler.Domain = "";
                                        dbHandler.Category = "";
                                        dbHandler.Scope = "";
                                        dbHandler.Priority = "";
                                        dbHandler.Personality_topic = "";
                                        dbHandler.Body = "";
                                        dbHandler.Introduction = "";
                                        dbHandler.Story_Type = AppController_Patrika.Story_Type_Detailed;
                                        dbHandler.Time = "" + jsonArray.getJSONObject(i).getString("content_timestamp");
                                        dbHandler.PlaceName = "" + AppUtills.getPlace_Name_by_lat_long(getActivity(), jsonArray.getJSONObject(i).getString("content_place_latitude"), jsonArray.getJSONObject(i).getString("content_place_longitude"));
                                        dbHandler.Nid = jsonArray.getJSONObject(i).getString("nid");

                                        dbHandler.latitute = jsonArray.getJSONObject(i).getString("content_place_latitude");
                                        dbHandler.longitute = jsonArray.getJSONObject(i).getString("content_place_longitude");
                                        dbHandler.publish_status_print = jsonArray.getJSONObject(i).getString("publish_status_print");
                                        dbHandler.publish_status_tv = jsonArray.getJSONObject(i).getString("publish_status_tv");
                                        dbHandler.publish_status_web = jsonArray.getJSONObject(i).getString("publish_status_web");

                                        if (jsonArray.getJSONObject(i).getString("publish_status_print").equalsIgnoreCase("1") || jsonArray.getJSONObject(i).getString("publish_status_tv").equalsIgnoreCase("1") || jsonArray.getJSONObject(i).getString("publish_status_web").equalsIgnoreCase("1"))
                                            dbHandler.Status = AppController_Patrika.Story_Status_Approved;
                                        else
                                            dbHandler.Status = AppController_Patrika.Story_Status_Uploaded;

                                        if (!jsonArray.getJSONObject(i).getString("field_ccms_thumb_image").equals("[]")) {
                                            Log.e(TAG, "field_ccms_thumb_image" + jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url"));
                                            File rootPath = new File(Environment.getExternalStoragePublicDirectory(getActivity().getResources().getString(R.string.app_name)), ".Thumbnail");
                                            DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                            if (!jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url").equals("null") && jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url") != null && !jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url").equals("")) {
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
                                                dbHandler.Thumbnail = rootPath + "/" + (new File(jsonArray.getJSONObject(i).getJSONArray("field_ccms_thumb_image").getJSONObject(0).getString("url")).getName());
                                            } else
                                                dbHandler.Thumbnail = "";
                                        } else
                                            dbHandler.Thumbnail = "";

                                        if (!jsonArray.getJSONObject(i).getString("field_ccms_uploaded_audios").equals("") && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_audios").equals("null") && jsonArray.getJSONObject(i).getString("field_ccms_uploaded_audios") != null && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_audios").equals("[]")) {

                                            JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("field_ccms_uploaded_audios"));

                                            Log.e(TAG, "field_ccms_uploaded_audios   " + jsonObject.length());


                                            dbHandler.Aud_size = "" + jsonObject.length();
                                        } else
                                            dbHandler.Aud_size = "" + 0;

                                        if (!jsonArray.getJSONObject(i).getString("field_ccms_uploaded_images").equals("") && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_images").equals("null") && jsonArray.getJSONObject(i).getString("field_ccms_uploaded_images") != null && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_images").equals("[]")) {
                                            JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("field_ccms_uploaded_images"));
                                            Log.e(TAG, " after split field_ccms_uploaded_images       " + jsonObject.length());

                                            dbHandler.Img_size = "" + (jsonObject.length());
                                        } else
                                            dbHandler.Img_size = "" + 0;

                                        if (!jsonArray.getJSONObject(i).getString("field_ccms_uploaded_videos").equals("") && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_videos").equals("null") && jsonArray.getJSONObject(i).getString("field_ccms_uploaded_videos") != null && !jsonArray.getJSONObject(i).getString("field_ccms_uploaded_videos").equals("[]")) {
                                            JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("field_ccms_uploaded_videos"));
                                            Log.e(TAG, " after split field_ccms_uploaded_videos       " + jsonObject.length());

                                            dbHandler.Vid_size = "" + jsonObject.length();

                                        } else
                                            dbHandler.Vid_size = "" + 0;

                                        dbHandler.MiliSec = System.currentTimeMillis();
                                        Log.e(TAG, "THumbnail before insert    " + dbHandler.Thumbnail);
                                        dbHelper.insertDataInAdd_News(dbHandler, "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            AppUtills.alertDialog(getActivity(), result);
                                        }
                                    });
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {

                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                arrayList.clear();
                                arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                adapter = new Not_Pub_Adapter(activity, arrayList);
                                recyclerView.setAdapter(adapter);
                                if (arrayList.size() > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txt_no_record_found.setVisibility(View.GONE);
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    txt_no_record_found.setVisibility(View.VISIBLE);
                                }
                            }
                        }.execute();
                    } else {
                        Log.v("else   ..... onPost", "json is array size TAG......  " + jsonArray.length());

                        recyclerView.setVisibility(View.GONE);
                        txt_no_record_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    AppUtills.alertDialog(getActivity(), result);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
                if (result != null && !result.equals(""))
                    AppUtills.alertDialog(getActivity(), result);
                else
                    AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
            }
        }
    }
}