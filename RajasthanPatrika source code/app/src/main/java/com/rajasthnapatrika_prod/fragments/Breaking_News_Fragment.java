package com.rajasthnapatrika_prod.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.Add_Stories_Activity;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.util.ArrayList;
import java.util.List;

public class Breaking_News_Fragment extends Fragment {

    String TAG = "Breaking_News_Fragment";
    /*tabs layout*/
    TabLayout tabLayout;

    /* floatiing Action Button*/
    FloatingActionButton fab;

    Toolbar toolbar;

    /* view pager*/
    public static ViewPager viewPager;

    /* view pager adapter*/
    ViewPagerAdapter adapter;
    public static String Id = "";
    ProgressDialog progressDialog;
    private DBHelper dbHelper;
    View rootView;
    int tab_position = 0;
    public static String page_info = "";
    public static String flag = "";
    public static boolean flag_dialog = false;
    public static String nid_published = "",nid_not_published = "";
  public  Breaking_News_Fragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_main, parent, false);

        try {

            dbHelper=new DBHelper(getActivity(), null, null, 0);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));
            if(AppUtills.showLogs) Log.v(TAG,"Breaking_News_Fragment onCreateView called");
            flag_dialog = true;

          if(getArguments() !=null) {

              if(getArguments().getString("tab_position")!=null)
                tab_position = Integer.parseInt(getArguments().getString("tab_position"));
              else  if(getArguments().getString("flag")!=null)
              {
                  flag = getArguments().getString("flag");
                  tab_position = Integer.parseInt(getArguments().getString("position"));
                  page_info = "";

                  if(tab_position == 0)
                      nid_published = getArguments().getString("nid");
                  else if(tab_position == 1)
                      nid_not_published = getArguments().getString("nid");
              }
          }
          else
            {
                tab_position = 0;
                page_info = "";
            }

            viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent add_story_intent = new Intent(getActivity(), Add_Stories_Activity.class);
                    startActivity(add_story_intent);
                    getActivity().finish();

                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return rootView;
    }
    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new News_Publised_Fragment(getActivity()), getActivity().getResources().getString(R.string.published_txt));
        adapter.addFrag(new News_Not_Publised_Fragment(getActivity()),getActivity().getResources().getString(R.string.not_published_txt));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tab_position);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
