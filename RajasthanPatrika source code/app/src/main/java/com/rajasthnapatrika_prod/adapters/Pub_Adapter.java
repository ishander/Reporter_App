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

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.fragments.Detailed_News_Fragment;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jgupta on 1/4/2016.
 */
public class Pub_Adapter extends RecyclerView.Adapter<Pub_Adapter.ViewHolder>
{
    Activity activity;
    String TAG = "Pub_Adapter";
    DBHelper dbHelper;

    ArrayList<HashMap<String,String>> Detailed_published_data;

    public Pub_Adapter(Activity activity, ArrayList<HashMap<String,String>> Detailed_published_data)
    {

        this.activity = activity;
        dbHelper=new DBHelper(activity, null, null, 0);
        this.Detailed_published_data = Detailed_published_data;

    }

    @Override
    public Pub_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Pub_Adapter.ViewHolder holder, final int position) {

//        holder.news_img_video_img.setBackgroundResource(images[position]);

        if(!Detailed_News_Fragment.nid_published.equals(""))
        {
            if(Detailed_published_data.get(position).get("Nid").equals(Detailed_News_Fragment.nid_published))
            {
                holder.main.setBackgroundColor(activity.getResources().getColor(R.color.color_highlight_back));
            }
            Detailed_News_Fragment.nid_published = "";
        }

        holder.new_msg_txt.setText(Detailed_published_data.get(position).get("Heading"));
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.ENGLISH);
        try
        {
            Date date1 = null;
         if(Detailed_published_data.get(position).get("Time") != null)
             date1 = simpleDateFormat.parse(Detailed_published_data.get(position).get("Time"));

            Date date2 = simpleDateFormat.parse(AppUtills.getDate_n_Time());
            String time = "";

            if(date1 != null)
                time = AppUtills.printDifference(date1, date2);

            if(!time.equals("") && !Detailed_published_data.get(position).get("PlaceName").equals(""))
                holder.loctn_time_txt.setText(time +" | " +Detailed_published_data.get(position).get("PlaceName"));
            else if(!time.equals("") && Detailed_published_data.get(position).get("PlaceName").equals(""))
                holder.loctn_time_txt.setText(time);
            else if(!Detailed_published_data.get(position).get("PlaceName").equals("") && time.equals(""))
                holder.loctn_time_txt.setText(Detailed_published_data.get(position).get("PlaceName"));

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(AppUtills.showLogs)Log.e(TAG,"Thumbnail   "+Detailed_published_data.get(position).get("Thumbnail"));

        if(!Detailed_published_data.get(position).get("Thumbnail").equals("") && Detailed_published_data.get(position).get("Thumbnail") != null)
        {
            AppController_Patrika.getImageLoader().displayImage("file://" + Detailed_published_data.get(position).get("Thumbnail"),
                    holder.news_img_video_img, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
//                                        img.setImageResource(R.drawable.no_media);
                            super.onLoadingStarted(imageUri, view);
                        }
                    });
        }

        Log.e("publish_status_print","publish_status_print  "+Detailed_published_data.get(position).get("publish_status_print"));
        Log.e("publish_status_tv","publish_status_tv  "+Detailed_published_data.get(position).get("publish_status_tv"));
        Log.e("publish_status_web","publish_status_web  "+Detailed_published_data.get(position).get("publish_status_web"));

        if(!Detailed_published_data.get(position).get("publish_status_print").equalsIgnoreCase("0") && !Detailed_published_data.get(position).get("publish_status_print").equalsIgnoreCase("1")) {
            holder.radio_img.setBackgroundResource(R.drawable.news_paper_icon_h);

            holder.radio_img.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                    share.putExtra(Intent.EXTRA_TEXT, Detailed_published_data.get(position).get("publish_status_print"));

                    activity.startActivity(Intent.createChooser(share, "Share link!"));
                }
            });

        }else if(Detailed_published_data.get(position).get("publish_status_print").equalsIgnoreCase("1"))
        {
            holder.radio_img.setBackgroundResource(R.drawable.news_paper_icon_h_pink);
        }
        else
            holder.radio_img.setBackgroundResource(R.drawable.news_paper_icon);

        if(!Detailed_published_data.get(position).get("publish_status_tv").equalsIgnoreCase("0") && !Detailed_published_data.get(position).get("publish_status_tv").equalsIgnoreCase("1")) {
            holder.tv_img.setBackgroundResource(R.drawable.player_icon_h);

            holder.tv_img.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                    share.putExtra(Intent.EXTRA_TEXT, Detailed_published_data.get(position).get("publish_status_tv"));

                    activity.startActivity(Intent.createChooser(share, "Share link!"));
                }
            });

        }else if(Detailed_published_data.get(position).get("publish_status_tv").equalsIgnoreCase("1"))
        {
            holder.tv_img.setBackgroundResource(R.drawable.player_icon_h_pink);
        }
        else
            holder.tv_img.setBackgroundResource(R.drawable.player_icon_gray);

        if(!Detailed_published_data.get(position).get("publish_status_web").equalsIgnoreCase("0") && !Detailed_published_data.get(position).get("publish_status_web").equalsIgnoreCase("1"))
        {
            holder.web_img.setBackgroundResource(R.drawable.broswer_icon_h);

            holder.web_img.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Patrika");
                    share.putExtra(Intent.EXTRA_TEXT, Detailed_published_data.get(position).get("publish_status_web"));

                    activity.startActivity(Intent.createChooser(share, "Share link!"));
                }
            });

        }else if(Detailed_published_data.get(position).get("publish_status_web").equalsIgnoreCase("1"))
        {
            holder.web_img.setBackgroundResource(R.drawable.broswer_icon_h_pink);

        }else
            holder.web_img.setBackgroundResource(R.drawable.broswer_icon);

        if(AppUtills.showLogs) Log.v(TAG,"Title is  "+Detailed_published_data.get(position).get("Heading"));

    }

    @Override
    public int getItemCount() {
        return Detailed_published_data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView news_img_video_img,tv_img,web_img,radio_img;
        public final TextView durtn_txt,new_msg_txt,loctn_time_txt;
        public final LinearLayout video_durtn_txt,main;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            main = (LinearLayout)mView.findViewById(R.id.main);
            news_img_video_img = (ImageView)mView.findViewById(R.id.news_img_video_img);
            tv_img          = (ImageView)mView.findViewById(R.id.tv_img);
            web_img        = (ImageView)mView.findViewById(R.id.web_img);
            radio_img            = (ImageView)mView.findViewById(R.id.radio_img);

            durtn_txt          = (TextView)mView.findViewById(R.id.durtn_txt);
            new_msg_txt        = (TextView)mView.findViewById(R.id.new_msg_txt);
            loctn_time_txt     = (TextView)mView.findViewById(R.id.loctn_time_txt);

            video_durtn_txt    = (LinearLayout)mView.findViewById(R.id.video_durtn_txt);

        }

    }
}
