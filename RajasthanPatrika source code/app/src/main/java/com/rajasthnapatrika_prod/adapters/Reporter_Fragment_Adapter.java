package com.rajasthnapatrika_prod.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;

/**
 * Created by mshrotriya on 3/1/2016.
 */
public class Reporter_Fragment_Adapter extends RecyclerView.Adapter<Reporter_Fragment_Adapter.ViewHolder>
{
    Activity activity;

    public Reporter_Fragment_Adapter(Activity activity)
    {
            this.activity = activity;
    }

    @Override
    public Reporter_Fragment_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reporter_fragment_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Reporter_Fragment_Adapter.ViewHolder holder, int position) {


        if(position % 2 == 0)
        {
            holder.reporter_layout.setBackgroundColor(activity.getResources().getColor(R.color.white_color));
        }else
            holder.reporter_layout.setBackgroundColor(activity.getResources().getColor(R.color.gray_view_color));

    }

    @Override
    public int getItemCount() {
        return 10;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView txt_month, txt_publish, txt_upload, txt_percent;
        public final LinearLayout reporter_layout;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;

            reporter_layout          = (LinearLayout) mView.findViewById(R.id.reporter_layout);

            txt_month          = (TextView)mView.findViewById(R.id.txt_month);
            txt_publish        = (TextView)mView.findViewById(R.id.txt_publish);
            txt_upload         = (TextView)mView.findViewById(R.id.txt_upload);
            txt_percent         = (TextView)mView.findViewById(R.id.txt_percent);


        }

    }
}
