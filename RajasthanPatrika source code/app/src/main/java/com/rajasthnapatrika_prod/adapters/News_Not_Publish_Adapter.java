package com.rajasthnapatrika_prod.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.fragments.Breaking_News_Fragment;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jgupta on 1/4/2016.
 */


public class News_Not_Publish_Adapter extends RecyclerView.Adapter<News_Not_Publish_Adapter.ViewHolder> {

    DBHelper dbHelper;
    Activity activity;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
//    String Date_Time;

    public News_Not_Publish_Adapter(Activity activity, ArrayList<HashMap<String, String>> arrayList)
    {
            this.activity = activity;
            this.arrayList = arrayList;
        dbHelper = new DBHelper(activity, null, null, 0);

    }

    @Override
    public News_Not_Publish_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_not_publish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(News_Not_Publish_Adapter.ViewHolder holder, int position)
    {
        SimpleDateFormat simpleDateFormat = null;
        try {

            if(!Breaking_News_Fragment.nid_not_published.equals(""))
            {
                if(arrayList.get(position).get("Nid").equals(Breaking_News_Fragment.nid_not_published))
                {
                    holder.main.setBackgroundColor(activity.getResources().getColor(R.color.color_highlight_back));
                }
                Breaking_News_Fragment.nid_not_published = "";
            }

            holder.txt_heading.setText(arrayList.get(position).get("Heading"));
            holder.txt_sub_heading.setText(arrayList.get(position).get("Sub_heading"));
            holder.txt_status.setText(arrayList.get(position).get("Status"));
            if(arrayList.get(position).get("publish_status_print").equalsIgnoreCase("1") || arrayList.get(position).get("publish_status_tv").equalsIgnoreCase("1") || arrayList.get(position).get("publish_status_web").equalsIgnoreCase("1"))
            {
                holder.media_lay.setVisibility(View.VISIBLE);
                holder.txt_status.setVisibility(View.GONE);
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
                holder.txt_status.setVisibility(View.VISIBLE);
            }
            if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_process) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_outbox))
            {
                holder.txt_status.setTextColor(activity.getResources().getColor(R.color.red_color));

            }else  if(arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_draft) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Uploaded) || arrayList.get(position).get("Status").equalsIgnoreCase(AppController_Patrika.Story_Status_Approved))
            {
                holder.txt_status.setTextColor(activity.getResources().getColor(R.color.blue_color));
            }
            simpleDateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.ENGLISH);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        try
        {
            Date date1 = simpleDateFormat.parse(arrayList.get(position).get("Time"));
            Date date2 = simpleDateFormat.parse(AppUtills.getDate_n_Time());

            String time = AppUtills.printDifference(date1, date2);
            if(AppUtills.showLogs) Log.e("News Not Published","time|place "+time+"|"+arrayList.get(position).get("PlaceName")+"of heading  "+arrayList.get(position).get("Heading"));

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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView radio_img,web_img,tv_img;
        LinearLayout media_lay,main;
        public final TextView txt_heading, txt_sub_heading, txt_time_place,txt_status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            main                = (LinearLayout) mView.findViewById(R.id.main);
            txt_status         = (TextView)mView.findViewById(R.id.txt_status);
            txt_heading          = (TextView)mView.findViewById(R.id.txt_heading);
            txt_sub_heading        = (TextView)mView.findViewById(R.id.txt_sub_heading);
            txt_time_place     = (TextView)mView.findViewById(R.id.txt_time_place);
            tv_img          = (ImageView)mView.findViewById(R.id.tv_img);
            web_img        = (ImageView)mView.findViewById(R.id.web_img);
            radio_img            = (ImageView)mView.findViewById(R.id.radio_img);
            media_lay            = (LinearLayout)mView.findViewById(R.id.media_lay);
        }
    }
}
