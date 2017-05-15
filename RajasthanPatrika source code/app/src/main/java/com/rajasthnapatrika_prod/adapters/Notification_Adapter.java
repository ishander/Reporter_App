package com.rajasthnapatrika_prod.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.Add_Stories_Activity;
import com.rajasthnapatrika_prod.activities.Drawer_Activity;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jgupta on 1/4/2016.
 */
public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.ViewHolder>
{
    Activity activity;
    ArrayList<HashMap<String,String>> Notifications_list;
    DBHelper dbHelper;
    String TAG = "Notification_Adapter";
    public Notification_Adapter(Activity activity, ArrayList<HashMap<String,String>> Notifications_list)
    {
        this.Notifications_list = Notifications_list;
        this.activity = activity;
        dbHelper=new DBHelper(activity, null, null, 0);
    }

    @Override
    public Notification_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_acticity_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Notification_Adapter.ViewHolder holder, final int position) {


        holder.heading.setText(Notifications_list.get(position).get("Heading"));

        if(!Notifications_list.get(position).get("Sub_heading").equals("")) {
            holder.sub_heading.setVisibility(View.VISIBLE);
            holder.sub_heading.setText(Notifications_list.get(position).get("Sub_heading"));
        }else
            holder.sub_heading.setVisibility(View.GONE);

        if(Notifications_list.get(position).get("isRead").equals("Unread")) {
            holder.heading.setTypeface(holder.heading.getTypeface(), Typeface.BOLD);
            holder.notification_icon.setImageResource(R.drawable.circle_notification);
        }
        else  if(Notifications_list.get(position).get("isRead").equals("Read")) {
            holder.notification_icon.setImageResource(R.drawable.circle_notification_yellow);
            holder.heading.setTypeface(holder.heading.getTypeface(), Typeface.NORMAL);
        }

        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.ENGLISH);

        if(AppUtills.showLogs) Log.v(TAG,"read status  "+Notifications_list.get(position).get("isRead"));

        try
        {
            Date date1 = simpleDateFormat.parse(Notifications_list.get(position).get("Time"));
            Date date2 = simpleDateFormat.parse(AppUtills.getDate_n_Time());

            String time = AppUtills.printDifference(date1, date2);

            holder.txt_time_place.setText(time);

            holder.notification_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(Notifications_list.get(position).get("Required_more").equalsIgnoreCase("0") || Notifications_list.get(position).get("Required_more").equalsIgnoreCase("1") || Notifications_list.get(position).get("Required_more").equalsIgnoreCase("2"))
                    {
                        Intent moveIntent = new Intent(activity, Add_Stories_Activity.class);
                        moveIntent.putExtra("flag", "content_type");
                        moveIntent.putExtra("nid", "" + Notifications_list.get(position).get("Nid"));
                        moveIntent.putExtra("notification_id", ""+Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("Time", ""+Notifications_list.get(position).get("Time"));
                        moveIntent.putExtra("isRead", ""+Notifications_list.get(position).get("isRead"));
                        moveIntent.putExtra("content_url", "" + Notifications_list.get(position).get("Content_url"));
                        moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        activity.startActivity(moveIntent);
                    }else if(Notifications_list.get(position).get("Required_more").equalsIgnoreCase("published"))
                    {
                        Intent moveIntent = new Intent(activity, Drawer_Activity.class);
//                        dbHelper.update_Notification_Read_status(Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("position", "0");
                        moveIntent.putExtra("notification_id", ""+Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("Time", ""+Notifications_list.get(position).get("Time"));
                        moveIntent.putExtra("isRead", ""+Notifications_list.get(position).get("isRead"));
                        moveIntent.putExtra("nid", "" + Notifications_list.get(position).get("Nid"));
                        moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        if(Notifications_list.get(position).get("Story_Type").equalsIgnoreCase("0"))
                            moveIntent.putExtra("flag", "detail");
                        else if(Notifications_list.get(position).get("Story_Type").equalsIgnoreCase("1"))
                            moveIntent.putExtra("flag", "breaking");

                        if(AppUtills.showLogs)Log.e(TAG,"time is  "+Notifications_list.get(position).get("Time"));
                        if(AppUtills.showLogs)Log.e(TAG,"Nid is  "+Notifications_list.get(position).get("Nid"));

                        activity.startActivity(moveIntent);
                    }else if(Notifications_list.get(position).get("Required_more").equalsIgnoreCase("approved"))
                    {
                        Intent moveIntent = new Intent(activity, Drawer_Activity.class);
//                        dbHelper.update_Notification_Read_status(Notifications_list.get(position).get("Nid"));
                        moveIntent.putExtra("position", "1");
                        moveIntent.putExtra("notification_id", ""+Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("Time", ""+Notifications_list.get(position).get("Time"));
                        moveIntent.putExtra("isRead", ""+Notifications_list.get(position).get("isRead"));
                        moveIntent.putExtra("nid", "" + Notifications_list.get(position).get("Nid"));
                        moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        if(Notifications_list.get(position).get("Story_Type").equalsIgnoreCase("0"))
                        {
                            moveIntent.putExtra("flag", "detail");

                            HashMap<String,String> hm =   dbHelper.check_if_news_exist(Notifications_list.get(position).get("Nid"),"not_publish","");
                            if(AppUtills.showLogs)Log.e("GCMINTENt service ","hm size if exist "+hm.size());
                            if(hm!=null && hm.size()>0)
                            {
                                    moveIntent.putExtra("position", "1");
                            }else
                            {
                                moveIntent.putExtra("position", "0");
                            }
                        }

                        else if(Notifications_list.get(position).get("Story_Type").equalsIgnoreCase("1"))
                        {
                            moveIntent.putExtra("flag", "breaking");

                            HashMap<String,String> hm =   dbHelper.check_if_news_exist(Notifications_list.get(position).get("Nid"),"not_publish","");
                            if(AppUtills.showLogs)Log.e("GCMINTENt service ","hm size if exist "+hm.size());
                            if(hm!=null && hm.size()>0)
                            {
                                    moveIntent.putExtra("position", "1");
                            }else
                            {
                                moveIntent.putExtra("position", "0");
                            }
                        }

                        activity.startActivity(moveIntent);
                    }
                    else if(Notifications_list.get(position).get("Required_more").equalsIgnoreCase("rejected"))
                    {
                        Intent moveIntent = new Intent(activity, Drawer_Activity.class);
//                      dbHelper.update_Notification_Read_status(Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("position", "0");
                        moveIntent.putExtra("flag", "detail");
                        moveIntent.putExtra("notification_id", ""+Notifications_list.get(position).get("notification_id"));
                        moveIntent.putExtra("Time", ""+Notifications_list.get(position).get("Time"));
                        moveIntent.putExtra("isRead", ""+Notifications_list.get(position).get("isRead"));
                        moveIntent.putExtra("nid", "" + Notifications_list.get(position).get("Nid"));
                        moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        activity.startActivity(moveIntent);
                    }
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return Notifications_list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView notification_icon;
        public final TextView heading,txt_time_place,sub_heading;
        public final LinearLayout video_durtn_txt,notification_list;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            notification_icon = (ImageView)mView.findViewById(R.id.notification_icon);
            notification_list = (LinearLayout) mView.findViewById(R.id.notification_list);
            heading          = (TextView)mView.findViewById(R.id.heading);
            txt_time_place     = (TextView)mView.findViewById(R.id.txt_time_place);
            sub_heading     = (TextView)mView.findViewById(R.id.sub_heading);

            video_durtn_txt    = (LinearLayout)mView.findViewById(R.id.video_durtn_txt);

        }

    }
}
