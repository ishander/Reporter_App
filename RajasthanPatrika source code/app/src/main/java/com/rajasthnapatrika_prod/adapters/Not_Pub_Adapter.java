package com.rajasthnapatrika_prod.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.Add_Stories_Activity;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.fragments.Detailed_News_Fragment;
import com.rajasthnapatrika_prod.service.Background_Uploading;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jgupta on 1/4/2016.
 */
public class Not_Pub_Adapter extends RecyclerView.Adapter<Not_Pub_Adapter.ViewHolder>
{
    Activity activity;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    DBHelper dbHelper;
    ArrayList<ArrayList<HashMap<String,String>>> nested_list;
    String TAG = "Not_Pub_Adapter";
    ProgressDialog progressDialog;
    String Nid ="";
    boolean img_flag = false, vid_flag = false, aud_flag = false;

    public Not_Pub_Adapter(Activity activity, ArrayList<HashMap<String, String>> arrayList)
    {
        this.activity = activity;
        this.arrayList = arrayList;
        dbHelper=new DBHelper(activity, null, null, 0);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.txt_please_wait));
    }

    @Override
    public Not_Pub_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.not_publish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Not_Pub_Adapter.ViewHolder holder, final int position)
    {

        if(!Detailed_News_Fragment.nid_published.equals(""))
        {
            if(arrayList.get(position).get("Nid").equals(Detailed_News_Fragment.nid_published))
            {
                holder.main.setBackgroundColor(activity.getResources().getColor(R.color.color_highlight_back));
            }
            Detailed_News_Fragment.nid_not_published = "";
        }

        holder.txt_heading.setText(arrayList.get(position).get("Heading"));
        holder.txt_process_status_below.setText(arrayList.get(position).get("Status"));

        if(arrayList.get(position).get("publish_status_print").equalsIgnoreCase("1") || arrayList.get(position).get("publish_status_tv").equalsIgnoreCase("1") || arrayList.get(position).get("publish_status_web").equalsIgnoreCase("1"))
        {
            holder.txt_process_status_below.setVisibility(View.GONE);
            holder.media_lay.setVisibility(View.VISIBLE);
            if(arrayList.get(position).get("publish_status_print").equalsIgnoreCase("1"))
                holder.radio_img.setBackgroundResource(R.drawable.news_paper_icon_h_pink);
            else
                holder.radio_img.setBackgroundResource(R.drawable.news_paper_icon);

            if(arrayList.get(position).get("publish_status_tv").equalsIgnoreCase("1"))
                holder.tv_img.setBackgroundResource(R.drawable.player_icon_h_pink);
            else
                holder.tv_img.setBackgroundResource(R.drawable.player_icon_gray);

            if(arrayList.get(position).get("publish_status_web").equalsIgnoreCase("1"))
                holder.web_img.setBackgroundResource(R.drawable.broswer_icon_h_pink);
            else
                holder.web_img.setBackgroundResource(R.drawable.broswer_icon);
        }else {
            holder.media_lay.setVisibility(View.GONE);
            holder.txt_process_status_below.setVisibility(View.VISIBLE);
        }

        if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
        {
            holder.txt_process_status_below.setTextColor(activity.getResources().getColor(R.color.red_color));

        }else  if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Uploaded) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Approved))
        {
            holder.txt_process_status_below.setTextColor(activity.getResources().getColor(R.color.blue_color));
        }

        if(AppUtills.showLogs)Log.e(TAG,"Thumbnail   "+arrayList.get(position).get("Thumbnail"));
        if(AppUtills.showLogs)Log.e(TAG,"Heading   "+arrayList.get(position).get("Heading"));
        if(AppUtills.showLogs)Log.e(TAG,"Nid   "+arrayList.get(position).get("Nid"));

        if(!arrayList.get(position).get("Thumbnail").isEmpty())
        {
            holder.news_img_video_img.setImageBitmap(AppUtills.compressBitmapImage(arrayList.get(position).get("Thumbnail")));
        }
        else
                {
                    holder.news_img_video_img.setImageResource(R.drawable.img_patrika);
                    ArrayList<HashMap<String, String>> list = dbHelper.getAll_Images(arrayList.get(position).get("Id"), "");

                  if(list.size()>0) {
                      for (int i = 0; i < list.size(); i++) {
                          if (list.get(i).get("Type").equalsIgnoreCase("Audio"))
                              holder.news_img_video_img.setImageResource(R.drawable.audio);
                      }
                  }else if(!arrayList.get(position).get("Aud_size").equals("") && Integer.parseInt(arrayList.get(position).get("Aud_size")) >0)
                      holder.news_img_video_img.setImageResource(R.drawable.audio);

                }
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.ENGLISH);

        try
        {
            Date date1 = simpleDateFormat.parse(arrayList.get(position).get("Time"));
            Date date2 = simpleDateFormat.parse(AppUtills.getDate_n_Time());

            String time = AppUtills.printDifference(date1, date2);

            if(AppUtills.showLogs)Log.e(TAG,"time|place "+time+"|"+arrayList.get(position).get("PlaceName")+"of heading  "+arrayList.get(position).get("Heading"));

          if(!time.equals("") && !arrayList.get(position).get("PlaceName").equals(""))
            holder.txt_time_place.setText(time +" | " +arrayList.get(position).get("PlaceName"));
            else if(!time.equals("") && arrayList.get(position).get("PlaceName").equals(""))
              holder.txt_time_place.setText(time);
            else if(!arrayList.get(position).get("PlaceName").equals("") && time.equals(""))
              holder.txt_time_place.setText(arrayList.get(position).get("PlaceName"));

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        holder.linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Uploaded))
              {
                  Intent intent = new Intent(activity, Add_Stories_Activity.class);
                  intent.putExtra("tab_position", "1");
                  intent.putExtra("page_info", AppController_Patrika.Story_Type_Detailed);
                  intent.putExtra("Id", "" + arrayList.get(position).get("Id"));
                  intent.putExtra("status", "" + arrayList.get(position).get("Status"));

                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                  activity.startActivity(intent);
                  activity.finish();
              }
              else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process))
                  Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_process_txt),Toast.LENGTH_LONG).show();
              else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
                  Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_outbox_txt),Toast.LENGTH_LONG).show();
                else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Approved))
                  Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_approved_txt),Toast.LENGTH_LONG).show();
            }
        });

        holder.news_img_video_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Uploaded)) {
                    Intent intent = new Intent(activity, Add_Stories_Activity.class);
                    intent.putExtra("tab_position", "1");
                    intent.putExtra("page_info", AppController_Patrika.Story_Type_Detailed);
                    intent.putExtra("Id", "" + arrayList.get(position).get("Id"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
                else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process))
                    Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_process_txt),Toast.LENGTH_LONG).show();
                else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
                    Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_outbox_txt),Toast.LENGTH_LONG).show();
                else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Approved))
                    Toast.makeText(activity,activity.getResources().getString(R.string.cant_edit_approved_txt),Toast.LENGTH_LONG).show();
                }
        });
        holder.img_three_dot.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View view)
            {
                PopupMenu popup = new PopupMenu(activity, holder.img_three_dot);

                 popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());

                MenuItem send =  popup.getMenu().findItem(R.id.send);
                MenuItem priorty = popup.getMenu().findItem(R.id.priory);
                MenuItem discard = popup.getMenu().findItem(R.id.discard);

                if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process))
                {
                    send.setVisible(false);
                    priorty.setVisible(false);
                    discard.setVisible(false);

                }else  if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft))
                {
                    send.setVisible(true);
                    priorty.setVisible(false);
                    discard.setVisible(true);
                }else if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
                {
                    send.setVisible(false);
                    priorty.setVisible(true);
                    discard.setVisible(false);
                }


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId()) {

                            case R.id.discard:

                                   dbHelper.discard_Stored_data(arrayList.get(position).get("Id"),"not_publish");
                                   dbHelper.discard_Stored_images_data(arrayList.get(position).get("Id"),"not_publish");
                                   arrayList =  dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
//                                    nested_list.clear();
//                                    for(int i=0;i<arrayList.size();i++)
//                                    {
//                                        ArrayList<HashMap<String,String>> list = dbHelper.getAll_Images(arrayList.get(i).get("Id"),"");
//                                        nested_list.add(list);
//                                    }
                                       notifyDataSetChanged();

                                break;

                            case R.id.send:

                                HashMap<String,String> hashMap = dbHelper.getListSelected_news(arrayList.get(position).get("Id"),AppController_Patrika.Story_Type_Detailed,"");

                                verifyPassword(hashMap);
                                Log.e(TAG,"hash map size is  "+hashMap.size());
                                Log.e(TAG,"hash map Nid is  "+hashMap.get("Nid"));

                                break;

                            case R.id.priory:
                                    dbHelper.Update_MiliSec(arrayList.get(position).get("Id"));
                                    Toast.makeText(activity,activity.getResources().getString(R.string.priority_set),Toast.LENGTH_LONG).show();
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView news_img_video_img,video_img,web_img,tv_img, img_three_dot,radio_img;
        public final TextView txt_heading,txt_time_place,txt_process_status_below;
        public final LinearLayout video_durtn_txt, linear_layout,media_lay,main;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            img_three_dot      = (ImageView)mView.findViewById(R.id.img_three_dot);
            news_img_video_img = (ImageView)mView.findViewById(R.id.news_img_video_img);
            video_img          = (ImageView)mView.findViewById(R.id.video_img);
            tv_img          = (ImageView)mView.findViewById(R.id.tv_img);
            web_img        = (ImageView)mView.findViewById(R.id.web_img);
            radio_img            = (ImageView)mView.findViewById(R.id.radio_img);
            media_lay            = (LinearLayout) mView.findViewById(R.id.media_lay);
            main            = (LinearLayout) mView.findViewById(R.id.main);

            txt_heading          = (TextView)mView.findViewById(R.id.txt_heading);
//            txt_process_status        = (TextView)mView.findViewById(R.id.txt_process_status);
            txt_time_place     = (TextView)mView.findViewById(R.id.txt_time_place);
            txt_process_status_below = (TextView)mView.findViewById(R.id.txt_process_status_below);
            video_durtn_txt    = (LinearLayout)mView.findViewById(R.id.video_durtn_txt);
            linear_layout    = (LinearLayout)mView.findViewById(R.id.linear_layout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

   void excuteSend_Detailed_news_Async(final HashMap<String,String> hashMap) {
      final int Img_size = Integer.parseInt(hashMap.get("Img_size"));
       final int Vid_size = Integer.parseInt(hashMap.get("Vid_size"));
       final int Aud_size = Integer.parseInt(hashMap.get("Aud_size"));
       try {
           new AsyncTask<Void, Void, String>()
           {
               @Override
               protected void onPreExecute () {
                   progressDialog.show();
               }

               @Override
               protected String doInBackground (Void...voids){

                   String Url;
                   String response = "";

                   if(hashMap.get("Nid").equals(""))
                       Url = AppController_Patrika.content_create_api;
                   else
                       Url = AppController_Patrika.content_create_api+"/"+hashMap.get("Nid");

                   try
                   {
                       ArrayList<HashMap<String,String>> list = dbHelper.getAll_Images(hashMap.get("Id"),"");

                       ArrayList<HashMap<String,String>> Image_list = new ArrayList<>();
                       ArrayList<HashMap<String,String>> Video_list = new ArrayList<>();
                       ArrayList<HashMap<String,String>> Audio_list = new ArrayList<>();
                       AppController_Patrika.paths.clear();
                       HashMap<String,String> hashMap1 = new HashMap<String, String>();
                       for (int i = 0; i < list.size(); i++)
                       {
                           HashMap<String,String> hm = new HashMap<>();

                           hm.put("Path",""+list.get(i).get("Path"));
                           hm.put("Type",list.get(i).get("Type"));
                           hm.put("Id",list.get(i).get("Id"));
                           hm.put("Status",list.get(i).get("Status"));
                           hm.put("Status_process",list.get(i).get("Status_process"));

                           if(list.get(i).get("Type").equals("Thumb"))
                           {
                               hashMap1.put("Path",""+list.get(i).get("Path"));
                               hashMap1.put("Type",list.get(i).get("Type"));
                               hashMap1.put("Id",list.get(i).get("Id"));
                               hashMap1.put("Status",list.get(i).get("Status"));
                               hashMap1.put("Status_process",list.get(i).get("new"));
                           }

                           if(AppUtills.showLogs)Log.v(TAG,"image path is  "+list.get(i));

                           if(list.get(i).get("Type").equals("Image") || list.get(i).get("Type").equals("Thumb"))
                           {
                               if(list.get(i).get("Status_process").equals("new"))
                                   img_flag = true;
                               Image_list.add(hm);
                           }
                           else if(list.get(i).get("Type").equals("Audio")) {
                               if(list.get(i).get("Status_process").equals("new"))
                                   aud_flag = true;
                               Audio_list.add(hm);
                           }else if(list.get(i).get("Type").equals("Video")) {
                               if(list.get(i).get("Status_process").equals("new"))
                                   vid_flag = true;
                               Video_list.add(hm);
                           }
                       }

                       AppController_Patrika.paths = list;

                       if(hashMap1!=null && hashMap.size()>0)
                           AppController_Patrika.paths.add(0,hashMap1);

                       String personality = hashMap.get("Personality_topic");
                       String intro = hashMap.get("Introduction");
                       String body = hashMap.get("Body");
                       String heading = hashMap.get("Heading");
                       String subheading = hashMap.get("Sub_heading");

                       String new_heading = heading.replaceAll("\"","&quote;");
                       String  new_sub_heading = subheading.replaceAll("\"","&quote;");

                       String new_personality = personality.replaceAll("\"","&quote;");
                       String new_intro = intro.replaceAll("\"","&quote;");
                       String new_body = body.replaceAll("\"","&quote;");

                       Log.v(TAG,"new_personality  "+new_personality);

                       Double lat = 0.0 , longi = 0.0;
                       if(AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
                           lat =  AppController_Patrika.Latitude;
                           longi = AppController_Patrika.Longitude;
                       }else  {
                           lat =  AppController_Patrika.Latitude_cell;
                           longi = AppController_Patrika.Longitude_cell;
                       }

                       String time = AppUtills.getDate_n_Time();

                       String start = " { \"title\":\""+new_heading+"\",\"type\":\""+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type","")+"\",\"language\":\"und\"" +
                               ",\"body\":{\"und\":[{ \"value\":\""+new_body+"\"}]}" +
                               ",\"field_ccms_sub_heading\":{\"und\":[{ \"value\":\""+new_sub_heading+"\"}]}" +
                               ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\""+new_intro+"\"}]}" +
                               ",\"field_ccms_personality\":{\"und\":[{ \"value\":\""+new_personality+"\"}]}" +
                               ",\"field_ccms_domain\":{\"und\":[{ \"value\":\""+hashMap.get("Domain")+"\"}]}" +
                               ",\"field_ccms_category\":{\"und\":[{ \"value\":\""+hashMap.get("Category")+"\"}]}" +
                               ",\"field_ccms_scope\":{\"und\":[{ \"value\":\""+hashMap.get("Scope")+"\"}]}" +
                               ",\"field_ccms_priority\":{\"und\":[{ \"value\":\""+hashMap.get("Priority")+"\"}]}" +
                               ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\""+lat+"\",\"lon\":\""+longi+"\"}}]}" +
                               ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\""+time+"\"}}]}" +
                               ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";

                       hashMap.put("latitute",""+lat);
                       hashMap.put("longitute",""+longi);
                       hashMap.put("Time",""+time);

                       String end = "}";
                       String image_start = ",\"field_ccms_uploaded_images\":{\"und\":[";
                       String img_end_part = "]}";

                       String video_start = ",\"field_ccms_uploaded_videos\":{\"und\":[";
                       String Audio_start = ",\"field_ccms_uploaded_audios\":{\"und\":[";
                       String complete_String = "";
                       boolean img_size = false;
                       boolean vid_size = false;
                       boolean aud_size = false;

                       if(Image_list.size()>0 && img_flag)
                           img_size = true;
                       if(Video_list.size()>0 && vid_flag)
                           vid_size = true;
                       if(Audio_list.size()>0 && aud_flag)
                           aud_size = true;

                       if(img_size && vid_size && aud_size)
                           complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+hashMap.get("MiliSec"),Img_size)+img_end_part+video_start+AppUtills.create_json_format("video",Video_list,""+hashMap.get("MiliSec"),Vid_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+hashMap.get("MiliSec"),Aud_size)+img_end_part+end;
                       else if(img_size && !vid_size && !aud_size)
                           complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+hashMap.get("MiliSec"),Img_size)+img_end_part+end;
                       else  if(img_size && vid_size && !aud_size)
                           complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+hashMap.get("MiliSec"),Img_size)+img_end_part+video_start+AppUtills.create_json_format("video",Video_list,""+hashMap.get("MiliSec"),Vid_size)+img_end_part+end;
                       else  if(img_size && !vid_size && aud_size)
                           complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+hashMap.get("MiliSec"),Img_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+hashMap.get("MiliSec"),Aud_size)+img_end_part+end;
                       else  if(!img_size && !vid_size && !aud_size)
                           complete_String =  start+end;
                       else  if(!img_size && vid_size && aud_size)
                           complete_String =  start+video_start+AppUtills.create_json_format("video",Video_list,""+hashMap.get("MiliSec"),Vid_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+hashMap.get("MiliSec"),Aud_size)+img_end_part+end;
                       else  if(!img_size && !vid_size && aud_size)
                           complete_String =  start+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+hashMap.get("MiliSec"),Aud_size)+img_end_part+end;
                       else  if(!img_size && vid_size && !aud_size)
                           complete_String =  start+video_start+AppUtills.create_json_format("video",Video_list,""+hashMap.get("MiliSec"),Vid_size)+img_end_part+end;


                       if(AppUtills.showLogs)Log.e(TAG,"complete_String  "+complete_String);


                       JSONObject jsonObject = new JSONObject(complete_String.toString().trim());

                       StringEntity se = new StringEntity(jsonObject.toString().trim(),HTTP.UTF_8);

                       if (hashMap.get("Nid").equals("")) {
                           HttpPost httppost = new HttpPost(Url);
                           httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                           httppost.setHeader("Accept", "application/json");
                           httppost.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                           httppost.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                           httppost.setEntity(se);

                           response = AppUtills.getDecodedResponse(httppost);
                       } else if (!hashMap.get("Nid").equals("")) {

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
                   }

                   if (AppUtills.showLogs) Log.v("response", "" + response);
                   return response;
               }

               @Override
               protected void onPostExecute (String s){
                   progressDialog.dismiss();

                   if (AppUtills.showLogs) Log.v("onPost", "onPOst  " + s);

                   JSONObject jsonObject = null;
                   try {
                       Object json = new JSONTokener(s).nextValue();
                       if (json instanceof JSONObject) {
                           jsonObject = new JSONObject(s);

                           Nid = jsonObject.getString("nid");
                           AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("directory_path", jsonObject.getString("field_ccms_ftp_directory")).commit();

                           if (img_flag || vid_flag || aud_flag) {
                               if (AppUtills.isNetworkAvailable(activity)) {
                                   if (!AppUtills.isMyServiceRunning(Background_Uploading.class, activity)) {
                                       insertIntoDb(hashMap, AppController_Patrika.Story_Status_process, "old");
                                       if (AppUtills.showLogs)
                                           Log.e("onPostExecute", "Service is not running");

                                       activity.startService(new Intent(activity, Background_Uploading.class));
                                   } else {
                                       insertIntoDb(hashMap, AppController_Patrika.Story_Status_outbox, "new");
                                   }
                               }
                           } else  {
                               insertIntoDb(hashMap, AppController_Patrika.Story_Status_Uploaded, "old");
                           }

                           Toast.makeText(activity, activity.getResources().getString(R.string.submit_news_txt), Toast.LENGTH_SHORT).show();
                       }else
                           AppUtills.alertDialog(activity,s);
                   } catch (JSONException e) {
                       AppUtills.alertDialog(activity,s);
                       e.printStackTrace();
                   }
               }
           }.execute(null, null, null);
       }catch (Exception e)
       {
           e.printStackTrace();
       }
   }
    void verifyPassword(final HashMap<String,String> hashMap)
    {
        final Dialog dialog = new Dialog(activity, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_password);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        final EditText txt_password = (EditText) dialog.findViewById(R.id.txt_password);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        txt_password.setFilters(FilterArray);
        txt_password.setHint(activity.getResources().getString(R.string.hint_pin_name));
        final TextInputLayout input_layout_pass = (TextInputLayout) dialog.findViewById(R.id.input_layout_pass);
        txt_password.addTextChangedListener(removeTextWatcher(txt_password, input_layout_pass));
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.btn_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(txt_password.getText().toString().trim().length()>0)
                        {
                            if (AppController_Patrika.getSharedPreferences().getString("pin","").equals(txt_password.getText().toString().trim())) {
                                dialog.dismiss();
                                if (AppUtills.isNetworkAvailable(activity)) {
                                     excuteSend_Detailed_news_Async(hashMap);
                                } else
                                    openDialog(hashMap);
                                if(AppUtills.showLogs)Log.v(TAG,"password on submit is  "+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("password",""));
                            }else {
                                input_layout_pass.setError(activity.getResources().getString(R.string.hint_correct_password));
                                if(AppUtills.showLogs)Log.v(TAG,"password on submit is  "+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("password",""));
                            }
                        }else {
                            input_layout_pass.setError(activity.getResources().getString(R.string.hint_pin_name));
                        }
                    }
                });
        dialog.show();
        txt_password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    if(s.length()>0) {
                        txt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        txt_password.setSelection(txt_password.getText().length());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,	int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }
    void openDialog(final HashMap<String,String> hashMap)
    {
        final Dialog dialog = new Dialog(activity, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_two);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView dialog4all_txt = (TextView) dialog
                .findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(activity.getResources().getString(R.string.send_sms_text));
        dialog.findViewById(R.id.btn_Dialog_no).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        insertIntoDb(hashMap,AppController_Patrika.Story_Status_outbox,"new");
                    }
                });

        dialog.findViewById(R.id.btn_Dialog_yes).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        String phoneNo = ""+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number","");
                        String message = "H:"+hashMap.get("Heading")+"SH:"+hashMap.get("Sub_heading")+"D:"+hashMap.get("Domain")+"C:"+hashMap.get("Category")
                                +"S:"+hashMap.get("Scope")+"P:"+hashMap.get("Priority")+"PE:"+hashMap.get("Personality_topic")+"I:"+hashMap.get("Introduction")
                                +"B:"+hashMap.get("Body");

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, message, null, null);
                            Toast.makeText(activity, activity.getResources().getString(R.string.sms_sent_txt), Toast.LENGTH_LONG).show();
                            insertIntoDb(hashMap,AppController_Patrika.Story_Status_outbox,"new");
                        }
                        catch (Exception e) {
                            Toast.makeText(activity,activity.getResources().getString(R.string.sms_failed_txt), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    TextWatcher removeTextWatcher(final EditText editText, final TextInputLayout textInputLayout)
    {
        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                if(AppUtills.showLogs)Log.v("onTextChanged", "onTextChanged");
                editText.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        };
        return textWatcher;
    }

    public void insertIntoDb(HashMap<String,String> map, String Status, String Status_process)
    {
        if(AppUtills.showLogs)Log.e("insertIntoDb called","insertIntoDb called");

        dbHelper.Update_processstatus(map.get("Id"), Nid, Status_process,Status);
        AppController_Patrika.paths.clear();
        AppController_Patrika.paths = dbHelper.getAll_Images(map.get("Id"),"");

        arrayList.clear();
        arrayList =  dbHelper.getAll_News(AppController_Patrika.Story_Type_Detailed);
//        nested_list.clear();
//        for(int i=0;i<arrayList.size();i++)
//        {
//            ArrayList<HashMap<String,String>> list = dbHelper.getAll_Images(arrayList.get(i).get("Id"),"");
//            nested_list.add(list);
//        }
        notifyDataSetChanged();
    }

}
