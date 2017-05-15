package com.rajasthnapatrika_prod.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.adapters.Reporter_Fragment_Adapter;
import com.rajasthnapatrika_prod.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class Reports_Fragment extends Fragment {

    String TAG = "Reports_Fragment";
    DBHelper dbHelper;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView;
    TextView txt_no_record_found;
    ArrayList<HashMap<String,String>>Reports_data_list = new ArrayList<>();


    public Reports_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.reporter_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        txt_no_record_found = (TextView) rootView.findViewById(R.id.txt_no_record_found);
        swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        dbHelper=new DBHelper(getActivity(), null, null, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,R.color.colorPrimary,R.color.colorPrimary,R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.color_transparent);
        setupRecyclerView(recyclerView);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    private void setupRecyclerView(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Reporter_Fragment_Adapter(getActivity()));
    }
}
