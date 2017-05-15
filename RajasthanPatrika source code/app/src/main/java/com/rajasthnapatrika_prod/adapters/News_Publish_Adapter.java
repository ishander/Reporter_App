package com.rajasthnapatrika_prod.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
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
public class News_Publish_Adapter extends RecyclerView.Adapter<News_Publish_Adapter.ViewHolder>
{
    Activity activity;
    ArrayList<HashMap<String,String>> News_published_data;
    String TAG = "News_Publish_Adapter";

    public News_Publish_Adapter(Activity activity, ArrayList<HashMap<String,String>> News_published_data)
    {
        this.activity = activity;
        this.News_published_data = News_published_data;
    }

    @Override
    public News_Publish_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_publish_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(News_Publish_Adapter.ViewHolder holder,final int position)
    {

        if(!Breaking_News_Fragment.nid_published.equals(""))
        {
            if(News_published_data.get(position).get("Nid").equals(Breaking_News_Fragment.nid_published))
            {
                holder.main.setBackgroundColor(activity.getResources().getColor(R.color.color_highlight_back));
            }
            Breaking_News_Fragment.nid_published = "";
        }


        holder.txt_heading.setText(News_published_data.get(position).get("Heading"));
        holder.txt_sub_heading.setText(News_published_data.get(position).get("Sub_heading"));

        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.ENGLISH);

        try
        {
            Date date1 = simpleDateFormat.parse(News_published_data.get(position).get("Time"));
            Date date2 = simpleDateFormat.parse(AppUtills.getDate_n_Time());

            if(AppUtills.showLogs) Log.v(TAG,"time from server  "+News_published_data.get(position).get("Time"));
            if(AppUtills.showLogs) Log.v(TAG,"date from server  "+date1);
            if(AppUtills.showLogs) Log.v(TAG,"date from current date2  "+date2);

            String time = AppUtills.printDifference(date1, date2);

            if(!time.equals("") && !News_published_data.get(position).get("PlaceName").equals(""))
                holder.loctn_time_txt.setText(time +" | " +News_published_data.get(position).get("PlaceName"));
            else if(!time.equals("") && News_published_data.get(position).get("PlaceName").equals(""))
                holder.loctn_time_txt.setText(time);
            else if(!News_published_data.get(position).get("PlaceName").equals("") && time.equals(""))
                holder.loctn_time_txt.setText(News_published_data.get(position).get("PlaceName"));


            if(!News_published_data.get(position).get("publish_status_print").equals("0") && !News_published_data.get(position).get("publish_status_print").equals("1")) {
                holder.new_img.setBackgroundResource(R.drawable.news_paper_icon_h);

                holder.new_img.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {

                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                        share.putExtra(Intent.EXTRA_TEXT, News_published_data.get(position).get("publish_status_print"));

                        activity.startActivity(Intent.createChooser(share, "Share link!"));
                    }
                });

            }else if(News_published_data.get(position).get("publish_status_print").equals("1"))
            {
                holder.new_img.setBackgroundResource(R.drawable.news_paper_icon_h_pink);
            }else
                holder.new_img.setBackgroundResource(R.drawable.news_paper_icon);

            if(!News_published_data.get(position).get("publish_status_tv").equals("0") && !News_published_data.get(position).get("publish_status_tv").equals("1")) {
                holder.video_img.setBackgroundResource(R.drawable.player_icon_h);

                holder.video_img.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {

                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                        share.putExtra(Intent.EXTRA_TEXT, News_published_data.get(position).get("publish_status_tv"));

                        activity.startActivity(Intent.createChooser(share, "Share link!"));
                    }
                });

            }else if(News_published_data.get(position).get("publish_status_tv").equals("1"))
            {
                holder.video_img.setBackgroundResource(R.drawable.player_icon_h_pink);
            }else
                holder.video_img.setBackgroundResource(R.drawable.player_icon_gray);

            if(!News_published_data.get(position).get("publish_status_web").equals("0") && !News_published_data.get(position).get("publish_status_web").equals("1"))
            {
                holder.broswer_img.setBackgroundResource(R.drawable.broswer_icon_h);

                holder.broswer_img.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {

                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                        share.putExtra(Intent.EXTRA_TEXT, News_published_data.get(position).get("publish_status_web"));

                        activity.startActivity(Intent.createChooser(share, "Share link!"));
                    }
                });

            }else if(News_published_data.get(position).get("publish_status_web").equals("1"))
            {
                holder.broswer_img.setBackgroundResource(R.drawable.broswer_icon_h_pink);
            }else
                holder.broswer_img.setBackgroundResource(R.drawable.broswer_icon);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return News_published_data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView news_img_video_img,video_img,broswer_img,new_img;
        public final TextView txt_heading,loctn_time_txt,txt_sub_heading;
        LinearLayout main;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            main                = (LinearLayout) mView.findViewById(R.id.main);
            news_img_video_img = (ImageView)mView.findViewById(R.id.news_img_video_img);
            video_img          = (ImageView)mView.findViewById(R.id.video_img);
            broswer_img        = (ImageView)mView.findViewById(R.id.broswer_img);
            new_img            = (ImageView)mView.findViewById(R.id.new_img);
            txt_heading        = (TextView)mView.findViewById(R.id.txt_heading);
            loctn_time_txt     = (TextView)mView.findViewById(R.id.loctn_time_txt);
            txt_sub_heading     = (TextView)mView.findViewById(R.id.txt_sub_heading);
        }

    }
}
