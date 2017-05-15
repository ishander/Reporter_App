package com.rajasthnapatrika_prod.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.Add_Stories_Activity;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.adapters.News_Not_Publish_Adapter;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;
import com.rajasthnapatrika_prod.utils.ClickListener;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

public class News_Not_Publised_Fragment extends Fragment
{
    RecyclerView recyclerView;
    TextView txt_no_record_found;
    Activity activity;
    ListView pub_list_view;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    News_Not_Publish_Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
    String TAG = "News_Not_Publised_Fragment";
    private DBHandler dbHandler;

    public News_Not_Publised_Fragment()
    {
    }
    @SuppressLint("ValidFragment")
    public News_Not_Publised_Fragment(Activity activity)
    {
    this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try {
             view = inflater.inflate(R.layout.fragment_not__publised_, container, false);

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            txt_no_record_found = (TextView) view.findViewById(R.id.txt_no_record_found);
            swipeRefreshLayout  = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

            dbHelper = new DBHelper(getActivity(), null, null, 0);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));
            dbHandler=new DBHandler();
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            adapter = new News_Not_Publish_Adapter(activity, arrayList);

            swipeRefreshLayout.setColorSchemeColors(R.color.white_color,R.color.white_color,R.color.white_color,R.color.white_color);
            swipeRefreshLayout.setProgressBackgroundColor(R.color.colorPrimary);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    try
                    {
                        swipeRefreshLayout.setRefreshing(false);
                        arrayList.clear();
                        arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Breaking);
                        adapter = new News_Not_Publish_Adapter(activity, arrayList);
                        if(arrayList.size()>0) {
                            txt_no_record_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.GONE);
                            txt_no_record_found.setVisibility(View.VISIBLE);
                        }
                       if(arrayList.size()>0)
                        recyclerView.setAdapter(adapter);
                        else {
                           if(AppUtills.isNetworkAvailable(getActivity()))
                                new excute_get_Detailed_News_Async().execute();
                           else
                               AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                       }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
                /* (non-Javadoc)
                 * @see com.printland.ClickListener#onClick(android.view.View, int)
                 */
                @Override
                public void onClick(View view, int position)
                {
                    if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Uploaded))
                    {
                        if(AppUtills.showLogs) Log.e("id is ","id is on click  "+arrayList.get(position).get("Id"));
                        Intent intent = new Intent(getActivity(), Add_Stories_Activity.class);
                        intent.putExtra("tab_position", "1");
                        intent.putExtra("page_info",AppController_Patrika.Story_Type_Breaking);
                        intent.putExtra("Id",""+arrayList.get(position).get("Id"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process))
                        Toast.makeText(activity,getActivity().getResources().getString(R.string.cant_edit_process_txt),Toast.LENGTH_LONG).show();
                    else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
                        Toast.makeText(activity,getActivity().getResources().getString(R.string.cant_edit_outbox_txt),Toast.LENGTH_LONG).show();
                    else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Approved))
                        Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_approved_txt),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return view;
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                /* (non-Javadoc)
                 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp(android.view.MotionEvent)
                 */
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                /* (non-Javadoc)
                 * @see android.view.GestureDetector.SimpleOnGestureListener#onLongPress(android.view.MotionEvent)
                 */
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        /* (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.OnItemTouchListener#onInterceptTouchEvent(android.support.v7.widget.RecyclerView, android.view.MotionEvent)
     */
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        /* (non-Javadoc)
         * @see android.support.v7.widget.RecyclerView.OnItemTouchListener#onTouchEvent(android.support.v7.widget.RecyclerView, android.view.MotionEvent)
         */
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        /* (non-Javadoc)
         * @see android.support.v7.widget.RecyclerView.OnItemTouchListener#onRequestDisallowInterceptTouchEvent(boolean)
         */
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean arg0) {

        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        arrayList.clear();
        arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Breaking);

        if(arrayList.size()>0) {
            txt_no_record_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.GONE);
            txt_no_record_found.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new News_Not_Publish_Adapter(activity, arrayList);

        if(arrayList.size()>0) {
            recyclerView.setAdapter(adapter);
            scroll_list();
        }
        else {
            if(AppUtills.isNetworkAvailable(getActivity()))
                new excute_get_Detailed_News_Async().execute();
            else {
                if(!Breaking_News_Fragment.flag_dialog)
                    AppUtills.alertDialog(getActivity(), getResources().getString(R.string.txt_network_error));
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
            }
        }
        Breaking_News_Fragment.flag_dialog = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (isVisibleToUser)
            {
             if(recyclerView != null)
             {
                 arrayList.clear();
                 arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Breaking);
                 adapter = new News_Not_Publish_Adapter(activity, arrayList);
                 if (arrayList.size() > 0) {
                     adapter = new News_Not_Publish_Adapter(activity, arrayList);
                     txt_no_record_found.setVisibility(View.GONE);
                     recyclerView.setVisibility(View.VISIBLE);
                     recyclerView.setAdapter(adapter);

                     scroll_list();
                 }
             }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class excute_get_Detailed_News_Async extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            if (AppUtills.showLogs)Log.v(TAG, "onPreExecute called");
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids)
        {

            if(AppUtills.showLogs)Log.e(TAG, "doInBackground................   ");
            HttpGet httpGet = new HttpGet(AppController_Patrika.Uploaded_Breaking_news_Api);
            httpGet.setHeader("X-CSRF-Token",""+ AppController_Patrika.getSharedPreferences().getString("token",""));
            httpGet.setHeader(HTTP.CONTENT_TYPE,"application/json");
            httpGet.setHeader("Accept","application/json");
            httpGet.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            if(AppUtills.showLogs)Log.v("",""+AppController_Patrika.getSharedPreferences().getString("token",""));

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(AppUtills.showLogs)Log.e(TAG, "response..   "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (AppUtills.showLogs) Log.e("onPost", "result  " + result);
            if (result !=null)
            {
                if (AppUtills.showLogs) Log.e(TAG, "result  " + result);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }

            try {

                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject jsonObject = new JSONObject(result);

                    if (AppUtills.showLogs) Log.e("onPost...............", "jsonObject  " + jsonObject);
                } else if (json instanceof JSONArray)
                {

                    JSONArray jsonArray = new JSONArray(result);

                    if (AppUtills.showLogs)
                        Log.e("onPost", "json is array sizeffff............. not published " + jsonArray.length());

                    if (jsonArray.length()>0)
                    {
                        txt_no_record_found.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

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
                            dbHandler.Story_Type = AppController_Patrika.Story_Type_Breaking;
                            dbHandler.Time = "" + jsonArray.getJSONObject(i).getString("content_timestamp");
                            dbHandler.PlaceName = "" + AppUtills.getPlace_Name_by_lat_long(getActivity(),jsonArray.getJSONObject(i).getString("content_place_latitude"), jsonArray.getJSONObject(i).getString("content_place_longitude"));
                            dbHandler.Nid = jsonArray.getJSONObject(i).getString("nid");
                            dbHandler.Thumbnail = "";
                            dbHandler.MiliSec = System.currentTimeMillis();

                            dbHandler.latitute = jsonArray.getJSONObject(i).getString("content_place_latitude");
                            dbHandler.longitute = jsonArray.getJSONObject(i).getString("content_place_longitude");
                            dbHandler.publish_status_print = jsonArray.getJSONObject(i).getString("publish_status_print");
                            dbHandler.publish_status_tv = jsonArray.getJSONObject(i).getString("publish_status_tv");
                            dbHandler.publish_status_web = jsonArray.getJSONObject(i).getString("publish_status_web");

                            if(jsonArray.getJSONObject(i).getString("publish_status_print").equalsIgnoreCase("1") || jsonArray.getJSONObject(i).getString("publish_status_tv").equalsIgnoreCase("1") || jsonArray.getJSONObject(i).getString("publish_status_web").equalsIgnoreCase("1"))
                                dbHandler.Status = AppController_Patrika.Story_Status_Approved;
                            else
                                dbHandler.Status = AppController_Patrika.Story_Status_Uploaded;

                            dbHelper.insertDataInAdd_News(dbHandler,"");
                        }

                        arrayList.clear();
                        arrayList = dbHelper.getAll_News(AppController_Patrika.Story_Type_Breaking);
                        recyclerView.setAdapter(adapter);

                    }else
                    {
                        recyclerView.setVisibility(View.GONE);
                        txt_no_record_found.setVisibility(View.VISIBLE);
                    }


                    if (AppUtills.showLogs)
                        Log.v("onPost", "arrayList  size  " + arrayList.size());


                }else
                {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                AppUtills.alertDialog(getActivity(), result);
                    recyclerView.setVisibility(View.GONE);
                    txt_no_record_found.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                recyclerView.setVisibility(View.GONE);
                txt_no_record_found.setVisibility(View.VISIBLE);
                if(result!= null && !result.equals(""))
                    AppUtills.alertDialog(getActivity(), result);
                else
                    AppUtills.alertDialog(getActivity(),getResources().getString(R.string.txt_network_error));
            }
        }
    }

    void scroll_list()
    {
        Log.e(TAG,"scroll_list called "+Breaking_News_Fragment.nid_not_published);
        if(!Breaking_News_Fragment.nid_not_published.equals(""))
        {
            HashMap<String,String> hashMap =   dbHelper.check_if_news_exist(Breaking_News_Fragment.nid_not_published,"not_publish",AppController_Patrika.Story_Type_Breaking);
//            Breaking_News_Fragment.nid_not_published = "";
            if(hashMap!=null && hashMap.size()>0) {
                if (!hashMap.get("id").equals("")) {
                    Log.e(TAG, "scroll_list id is " + Breaking_News_Fragment.nid_not_published);
                    if (hashMap.get("type").equals(AppController_Patrika.Story_Type_Breaking)) {
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
}
