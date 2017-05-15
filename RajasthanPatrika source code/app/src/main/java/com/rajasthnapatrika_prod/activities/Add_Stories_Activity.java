package com.rajasthnapatrika_prod.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.fragments.Breaking_Fragment;
import com.rajasthnapatrika_prod.fragments.Detailed_Fragment;
import com.rajasthnapatrika_prod.service.Service_to_get_Credentials;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Add_Stories_Activity extends AppCompatActivity {
    ActionBar mActionBar;
    public static String Id = "";
    int tab_position = 0;
    public static String page_info = "";
    public static String nid = "", status = "";
    private DBHelper dbHelper;
    GpsLocationReceiver receiver;
    AlertDialog alert;
    boolean flag = false;
    String TAG = "Add_Stories_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AppController_Patrika.getSharedPreferences().getBoolean("is_true", false)) {
            startActivity(new Intent(Add_Stories_Activity.this, LoginActivity.class));
            finish();
        }

        Log.e(TAG,"Latitude "+AppController_Patrika.Latitude);
        Log.e(TAG,"Longitude "+AppController_Patrika.Longitude);;
        Log.e(TAG,"Latitude_cell "+AppController_Patrika.Latitude_cell);
        Log.e(TAG,"Longitude_cell "+AppController_Patrika.Longitude_cell);

        setContentView(R.layout.add_stories_layout);


        registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        flag = true;
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getResources().getString(R.string.add_news_txt));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setElevation(0);

        dbHelper = new DBHelper(this, null, null, 0);
        String domain_value[]  = dbHelper.getAll_Drop_Downs_values("domain");

        if(domain_value.length == 0)
            startService(new Intent(Add_Stories_Activity.this, Service_to_get_Credentials.class).putExtra("flag", "get_drop_down"));
        if (getIntent() != null && getIntent().hasExtra("tab_position")) {
            if (getIntent().hasExtra("tab_position"))
                tab_position = Integer.parseInt(getIntent().getStringExtra("tab_position"));
            if (getIntent().hasExtra("page_info"))
                page_info = getIntent().getStringExtra("page_info");
            if (getIntent().hasExtra("Id"))
                Id = getIntent().getStringExtra("Id");
            if (getIntent().hasExtra("status"))
                status = getIntent().getStringExtra("status");
        } else if (getIntent() != null && getIntent().hasExtra("flag")) {
            if (getIntent().getStringExtra("flag").equals("content_type")) {

                if(getIntent().getStringExtra("isRead").equals("Unread")) {

                   if(AppUtills.isNetworkAvailable(this))
                    AppUtills.execute_notification_ack(this, getIntent().getStringExtra("notification_id"), getIntent().getStringExtra("Time"));
                }

                dbHelper.update_Notification_Read_status(getIntent().getStringExtra("notification_id"));
                HashMap<String,String> hashMap =   dbHelper.check_if_news_exist(getIntent().getStringExtra("nid"),"not_publish","");

                if(hashMap!=null && hashMap.size()>0)
                {
                    if (AppUtills.showLogs) Log.v(TAG, "onCreate id is  " + hashMap.get("id"));
                    Id = hashMap.get("id");
                    tab_position = 1;

                  if(hashMap.get("type").equals(AppController_Patrika.Story_Type_Detailed))
                    page_info = AppController_Patrika.Story_Type_Detailed;
                    else if(hashMap.get("type").equals(AppController_Patrika.Story_Type_Breaking))
                      page_info = AppController_Patrika.Story_Type_Breaking;

                }else
                {
                nid = getIntent().getStringExtra("content_url");
                tab_position = 1;
                page_info = "get_from_server";
                }
            }
        } else {
            tab_position = 0;
            page_info = "";
            Id = "";
        }

        ViewPager  viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Breaking_Fragment(Add_Stories_Activity.this), getResources().getString(R.string.breaking_txt));
        adapter.addFrag(new Detailed_Fragment(Add_Stories_Activity.this), getResources().getString(R.string.detail_txt));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tab_position);
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Add_Stories_Activity.this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?");
        alertDialogBuilder.setMessage("GPS ऑन नहीं है, आप इसे ऑन करना चाहते है ?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                    dialog.dismiss();
                            }
                        });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(Add_Stories_Activity.this, Drawer_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (AppUtills.isGpsOn(this)) {
            Log.e(TAG,"isGpsOn true");
            if(alert !=null && alert.isShowing())
                alert.dismiss();
            if(AppController_Patrika.Latitude == 0.0 && AppController_Patrika.Longitude == 0.0)
                 AppUtills.getCurrentLocation(this);
        } else {
            Log.e(TAG,"isGpsOn false  ");
            if(alert == null)
                showGPSDisabledAlertToUser();
            else if(alert !=null && !alert.isShowing())
                showGPSDisabledAlertToUser();
        }
    }


    public class GpsLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("GpsLocationReceiver","GpsLocationReceiver  calles  "+intent);
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
            {
                final LocationManager manager = (LocationManager)context.getSystemService    (Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    if(alert == null)
                        showGPSDisabledAlertToUser();
                    else if(alert !=null && !alert.isShowing())
                        showGPSDisabledAlertToUser();
                }else
                {
                     if(alert !=null && alert.isShowing())
                        alert.dismiss();
                    if (AppUtills.isGpsOn(Add_Stories_Activity.this)) {
                        if(AppController_Patrika.Latitude == 0.0 && AppController_Patrika.Longitude == 0.0)
                            AppUtills.getCurrentLocation(Add_Stories_Activity.this);
                    }
                }

            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(AppUtills.showLogs)Log.e(TAG,"onNewIntent called");
        if(AppUtills.showLogs)Log.e(TAG,"intent is  "+intent);

        if (intent != null && intent.hasExtra("tab_position")) {
            if (intent.hasExtra("tab_position"))
                tab_position = Integer.parseInt(intent.getStringExtra("tab_position"));
            if (intent.hasExtra("page_info"))
                page_info = intent.getStringExtra("page_info");
            if (intent.hasExtra("Id"))
                Id = intent.getStringExtra("Id");
            if (intent.hasExtra("status"))
                status = intent.getStringExtra("status");
        } else if (intent != null && intent.hasExtra("flag")) {
            if (intent.getStringExtra("flag").equals("content_type")) {
                HashMap<String,String> hashMap =   dbHelper.check_if_news_exist(intent.getStringExtra("nid"),"not_publish","");

                if(intent.getStringExtra("isRead").equals("Unread"))
                {
                   if(AppUtills.isNetworkAvailable(this))
                        AppUtills.execute_notification_ack(this,intent.getStringExtra("notification_id"), intent.getStringExtra("Time"));
                }


                if(hashMap!=null && hashMap.size()>0)
                {
                    if (AppUtills.showLogs) Log.v(TAG, "onNewIntent id is  " + hashMap.get("id"));
                    Id = hashMap.get("id");
                    tab_position = 1;

                    if(hashMap.get("type").equals(AppController_Patrika.Story_Type_Detailed))
                        page_info = AppController_Patrika.Story_Type_Detailed;
                    else if(hashMap.get("type").equals(AppController_Patrika.Story_Type_Breaking))
                        page_info = AppController_Patrika.Story_Type_Breaking;

                }else
                {
                    nid = intent.getStringExtra("content_url");
                    tab_position = 1;
                    page_info = "get_from_server";
                }
            }
        } else {
            tab_position = 0;
            page_info = "";
            Id = "";
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout  tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(tab_position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
