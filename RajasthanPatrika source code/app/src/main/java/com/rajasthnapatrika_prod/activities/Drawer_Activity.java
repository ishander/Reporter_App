package com.rajasthnapatrika_prod.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rajasthnapatrika_prod.BuildConfig;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.fragments.Breaking_News_Fragment;
import com.rajasthnapatrika_prod.fragments.Detailed_News_Fragment;
import com.rajasthnapatrika_prod.fragments.Notification_Fragment;
import com.rajasthnapatrika_prod.fragments.Reports_Web_view_Fragment;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Drawer_Activity extends AppCompatActivity {

    String TAG = "Drawer_Activity";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ProgressDialog progressDialog;
    private DBHelper dbHelper;
    boolean back_flag = false;
    Fragment fragment = null;
    String tab_position = "";
    public static String page_info = "";
    FragmentManager fragManager;
    TextView txt_title;
    public static TextView textView_detail,textView_breaking,textView_notification;
    MenuItem video_camera;
    private int position = 0;
    Uri fileUri;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AppController_Patrika.getSharedPreferences().getBoolean("is_true", false)) {
            startActivity(new Intent(Drawer_Activity.this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_drawer_);

        if(AppUtills.showLogs)Log.e(TAG,"oncreate called");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_title = (TextView) findViewById(R.id.txt_title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(Drawer_Activity.this, null, null, 0);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));
        fragManager = getSupportFragmentManager();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if(getIntent().hasExtra("tab_position")) {
            if (getIntent().hasExtra("tab_position"))
                tab_position = getIntent().getStringExtra("tab_position");
            if (getIntent().hasExtra("page_info"))
                page_info = getIntent().getStringExtra("page_info");

            if (page_info.equals("breaking")) {
                fragment = new Breaking_News_Fragment();
                if(!fragment.isAdded()) {
                    Bundle bundle = new Bundle();
                    if (video_camera != null)
                        video_camera.setVisible(false);
                    bundle.putString("tab_position", tab_position);
                    if (!fragment.isAdded())
                        fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.breaking_news_txt));
                    navigationView.getMenu().getItem(1).setChecked(true);
                    position = 1;
                }
            }
            else if(page_info.equals("detail"))
            {
                fragment = new Detailed_News_Fragment();
                if(!fragment.isAdded()) {
                    if (video_camera != null)
                        video_camera.setVisible(true);
                    Bundle bundle = new Bundle();
                    bundle.putString("tab_position", tab_position);
                    if (!fragment.isAdded())
                        fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
                    navigationView.getMenu().getItem(0).setChecked(true);
                    position = 0;
                }
            }
        }else if(getIntent().hasExtra("flag"))
        {

            if (getIntent().getStringExtra("flag").equals("detail")) {
                fragment = new Detailed_News_Fragment();

                if(getIntent().getStringExtra("isRead").equals("Unread")) {
                    if(AppUtills.isNetworkAvailable(this))
                        AppUtills.execute_notification_ack(this, getIntent().getStringExtra("notification_id"), getIntent().getStringExtra("Time"));
                }

                if(!fragment.isAdded()) {
                    if(video_camera !=null)
                        video_camera.setVisible(true);
                Bundle bundle = new Bundle();
                bundle.putString("flag", "detail");
                bundle.putString("position", getIntent().getStringExtra("position"));
                bundle.putString("nid", getIntent().getStringExtra("nid"));
                if (!fragment.isAdded())
                    fragment.setArguments(bundle);
                addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
                navigationView.getMenu().getItem(0).setChecked(true);
                position = 0;
            }
            }
            else if(getIntent().getStringExtra("flag").equals("breaking"))
            {
                fragment = new Breaking_News_Fragment();

                if(getIntent().getStringExtra("isRead").equals("Unread")) {
                    if(AppUtills.isNetworkAvailable(this))
                        AppUtills.execute_notification_ack(this, getIntent().getStringExtra("notification_id"), getIntent().getStringExtra("Time"));
                }

                if(!fragment.isAdded()) {
                    if (video_camera != null)
                        video_camera.setVisible(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "breaking");
                    bundle.putString("position", getIntent().getStringExtra("position"));
                    bundle.putString("nid", getIntent().getStringExtra("nid"));
                    if (!fragment.isAdded())
                        fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.breaking_news_txt));
                    navigationView.getMenu().getItem(1).setChecked(true);
                    position = 1;
                }
            }
        }
        else
        {
            Log.e("intent is null","intent is null");
            tab_position = "";
            page_info = "";
            fragment = new Detailed_News_Fragment();
            addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
            navigationView.getMenu().getItem(0).setChecked(true);
         if(video_camera !=null)
            video_camera.setVisible(true);
            position = 0;
        }

//        Log.e(TAG,"isMemoryCardExist    "+AppUtills.isMemoryCardExist(this));
//
//        if(AppUtills.isMemoryCardExist(this))
//        {
//            File f = AppUtills.getRemovabeStorageDir(this);
//            if(f.exists()) {
//                String path = f.getPath();
//                Log.e(TAG, "file exists   " + path);
//                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//                if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP){
//                    // Do something for lollipop and above versions
//                    Log.e(TAG, "fle is Created lower than L  " + new File(path+"/hello_demo").mkdirs());
//                }else if (currentapiVersion == android.os.Build.VERSION_CODES.LOLLIPOP)
//                {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                    startActivityForResult(intent, 42);
//                    Log.e(TAG, "it Lollipop ");
//                }
//            }
//        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked stsate
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate actions
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.detail:
                        fragment = new Detailed_News_Fragment();
                        addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
                        navigationView.getMenu().getItem(0).setChecked(true);
                        if(video_camera !=null)
                            video_camera.setVisible(true);
                        position = 0;
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.breaking:
                        fragment = new Breaking_News_Fragment();
                        addFragment(fragment, getResources().getString(R.string.breaking_news_txt));
                        navigationView.getMenu().getItem(1).setChecked(true);
                        if(video_camera !=null)
                            video_camera.setVisible(false);
                        position = 1;
                        return true;
                    case R.id.reports:
                        fragment = new Reports_Web_view_Fragment();
                        addFragment(fragment, getResources().getString(R.string.reports_txt));
                        navigationView.getMenu().getItem(2).setChecked(true);
                        if(video_camera !=null)
                            video_camera.setVisible(false);
                        position = 2;
                        return true;
                    case R.id.notification:
                        fragment = new Notification_Fragment();
                        addFragment(fragment, getResources().getString(R.string.notification_txt));
                        navigationView.getMenu().getItem(3).setChecked(true);
                        if(video_camera !=null)
                            video_camera.setVisible(false);
                        position = 3;
                        return true;
                    case R.id.logout:
                        show_Logout_Dialog(dbHelper.check_News_Availability());
                        navigationView.getMenu().getItem(4).setChecked(true);
                        if(video_camera !=null)
                            video_camera.setVisible(false);
                        position = 4;
                        return true;
                    case R.id.pin_change:
                        startActivity(new Intent(Drawer_Activity.this,PinChange_Activity.class).putExtra("flag","second"));
                        return true;
                    default:
                        return true;

                }
            }
        });
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        back_flag = false;
                    }
                });
            }

        }, 0, 3000);

        setMenuCounter();

    }

    public  void addFragment(Fragment fragment,String title) {
        txt_title.setText(title);
        fragManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    private void setMenuCounter()
    {
        MenuItem menu  = navigationView.getMenu().findItem(R.id.logout);
        MenuItem video_camera  = navigationView.getMenu().findItem(R.id.video_camera);
        video_camera.setVisible(false);

        menu.setTitle(getResources().getString(R.string.logout_txt)+" ("+AppController_Patrika.getSharedPreferences().getString("name","")+") ( v"+ BuildConfig.VERSION_NAME+" )");

        ArrayList<HashMap<String,String>> Detailed_published_data = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Detailed);
        LinearLayout view = (LinearLayout) navigationView.getMenu().findItem(R.id.detail).getActionView();
        textView_detail = (TextView) view.findViewById(R.id.counter_news);
        textView_detail.setText(""+Detailed_published_data.size());

        ArrayList<HashMap<String,String>> Breaking_list = dbHelper.get_Published_news(AppController_Patrika.Story_Type_Breaking);
        Log.e(TAG,"Breaking_list size  "+Breaking_list.size());
        LinearLayout view1 = (LinearLayout) navigationView.getMenu().findItem(R.id.breaking).getActionView();
        textView_breaking = (TextView) view1.findViewById(R.id.counter_news);
        textView_breaking.setText(""+Breaking_list.size());

        ArrayList<HashMap<String,String>>  Notifications_list = dbHelper.get_All_Notifications();
        LinearLayout view2 = (LinearLayout) navigationView.getMenu().findItem(R.id.notification).getActionView();
        textView_notification = (TextView) view2.findViewById(R.id.counter_notification);
        textView_notification.setText(""+AppUtills.getNotification_list_size(Notifications_list));

    }

    void show_Logout_Dialog(boolean having_news) {
        final Dialog dialog = new Dialog(Drawer_Activity.this, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_two);
        TextView dialog4all_txt = (TextView) dialog
                .findViewById(R.id.txt_DialogTitle);
      if(!having_news)
        dialog4all_txt.setText(getResources().getString(R.string.txt_logout_message));
        else
          dialog4all_txt.setText(getResources().getString(R.string.txt_logout_message_queue));

        dialog.findViewById(R.id.btn_Dialog_no).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.btn_Dialog_yes).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (AppUtills.isNetworkAvailable(Drawer_Activity.this))
                            new excuteLogoutAsync().execute();
                        else
                            AppUtills.alertDialog(Drawer_Activity.this, getResources().getString(R.string.txt_network_error));

                    }
                });
        dialog.show();
    }

    public class excuteLogoutAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            Log.e("token in main", "toke in main    " + AppController_Patrika.getSharedPreferences().getString("token", ""));

            Log.v("seesion name & di is", "name & id  " + AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

            HttpPost httppost = new HttpPost(AppController_Patrika.content_logout_api);
            httppost.setHeader("X-CSRF-Token", AppController_Patrika.getSharedPreferences().getString("token", ""));
            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));
            httppost.setHeader("Accept", "application/json");

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httppost);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AppUtills.showLogs) Log.v("sb.toString", "" + response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();

            if (AppUtills.showLogs) Log.v("onPost", "onPOst  " + s);

            try {
                if (s.contains("[true]")) {
                    dbHelper.discard_all_data();
                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().clear().commit();
                    AppController_Patrika.getSharedPreferences().edit().clear().commit();
                    clearApplicationData();
                    startActivity(new Intent(Drawer_Activity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();

                 NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                } else {
//                    String newString = s.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\.", "").replaceAll("\"", "");
                    if(s!= null && !s.equals(""))
                        AppUtills.alertDialog(Drawer_Activity.this, s);
                    else
                        AppUtills.alertDialog(Drawer_Activity.this,getResources().getString(R.string.txt_network_error));
                }

            } catch (Exception e) {
                if(s!= null && !s.equals(""))
                    AppUtills.alertDialog(Drawer_Activity.this, s);
                else
                    AppUtills.alertDialog(Drawer_Activity.this,getResources().getString(R.string.txt_network_error));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (!back_flag) {
                back_flag = true;
                Toast.makeText(Drawer_Activity.this, "ऐप से बाहर निकलने के लिए दो बार बैक करें", Toast.LENGTH_SHORT).show();
            } else
                super.onBackPressed();
        } else
            drawerLayout.closeDrawer(Gravity.LEFT);

    }
    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);

        MenuItem reports = menu.findItem(R.id.reports);
        MenuItem detail = menu.findItem(R.id.detail);
        MenuItem breaking = menu.findItem(R.id.breaking);
        MenuItem notification = menu.findItem(R.id.notification);
        MenuItem logout = menu.findItem(R.id.logout);
        video_camera = menu.findItem(R.id.video_camera);
        MenuItem pin_change = menu.findItem(R.id.pin_change);

        reports.setVisible(false);
        detail.setVisible(false);
        breaking.setVisible(false);
        notification.setVisible(false);
        logout.setVisible(false);
        pin_change.setVisible(false);

        if(position == 0)
            video_camera.setVisible(true);
        else
            video_camera.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.video_camera:
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                fileUri = AppUtills.getOutputMediaFileUri(2, "Patrika_app");

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

                startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult called");
                if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                    Toast.makeText(this,getResources().getString(R.string.video_recorded),Toast.LENGTH_LONG).show();
                        if (fileUri.getPath() != null) {
                                AppUtills.scanFile(Drawer_Activity.this,new File(fileUri.getPath()).getAbsolutePath());
                        }
                }
                if (requestCode == 42) {
                    Uri treeUri = data.getData();
                    DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

                    // List all existing files inside picked directory
                    for (DocumentFile file : pickedDir.listFiles()) {
                        Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
                    }

                    // Create a new file and write into it
                    try {
                        DocumentFile newFile = pickedDir.createDirectory("Patrika Reporter_Prod");
                        OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
                        out.write("A long time ago...".getBytes());
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppUtills.showLogs)Log.e(TAG,"onResume called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(AppUtills.showLogs)Log.e(TAG,"onRestart called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AppUtills.showLogs)Log.e(TAG,"onStart called");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(AppUtills.showLogs)Log.e(TAG,"onNewIntent called");

        if(intent.hasExtra("tab_position")) {
            if (intent.hasExtra("tab_position"))
                tab_position = intent.getStringExtra("tab_position");
            if (intent.hasExtra("page_info"))
                page_info = intent.getStringExtra("page_info");

            if (page_info.equals("breaking")) {
                fragment = new Breaking_News_Fragment();
                if(!fragment.isAdded()) {
                Bundle bundle = new Bundle();
                if (video_camera != null)
                    video_camera.setVisible(false);
                bundle.putString("tab_position", tab_position);
                if (!fragment.isAdded())
                    fragment.setArguments(bundle);
                addFragment(fragment, getResources().getString(R.string.breaking_news_txt));
                navigationView.getMenu().getItem(1).setChecked(true);
                position = 1;
            }
            }
            else if(page_info.equals("detail"))
            {
                fragment = new Detailed_News_Fragment();
                if(!fragment.isAdded()) {
                    if (video_camera != null)
                        video_camera.setVisible(true);
                    Bundle bundle = new Bundle();
                    bundle.putString("tab_position", tab_position);
                    if (!fragment.isAdded())
                        fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
                    navigationView.getMenu().getItem(0).setChecked(true);
                    position = 0;
                }
            }
        }else if(intent.hasExtra("flag"))
        {
            if (intent.getStringExtra("flag").equals("detail")) {
                fragment = new Detailed_News_Fragment();
                    if(intent.getStringExtra("isRead").equals("Unread")) {
                        if(AppUtills.isNetworkAvailable(this))
                            AppUtills.execute_notification_ack(this, intent.getStringExtra("notification_id"), intent.getStringExtra("Time"));
                    }

                if(!fragment.isAdded()) {
                    if(video_camera !=null)
                        video_camera.setVisible(true);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "detail");
                    bundle.putString("position", intent.getStringExtra("position"));
                    bundle.putString("nid", intent.getStringExtra("nid"));
                    fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
                    navigationView.getMenu().getItem(0).setChecked(true);
                    position = 0;
                }
            }
            else if(intent.getStringExtra("flag").equals("breaking"))
            {
                fragment = new Breaking_News_Fragment();
                if(intent.getStringExtra("isRead").equals("Unread")) {
                    if(AppUtills.isNetworkAvailable(this))
                        AppUtills.execute_notification_ack(this, intent.getStringExtra("notification_id"), intent.getStringExtra("Time"));
                }
                if(!fragment.isAdded()) {
                    if(video_camera !=null)
                        video_camera.setVisible(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "breaking");
                    bundle.putString("position", intent.getStringExtra("position"));
                    bundle.putString("nid", intent.getStringExtra("nid"));
                    fragment.setArguments(bundle);
                    addFragment(fragment, getResources().getString(R.string.breaking_news_txt));
                    navigationView.getMenu().getItem(1).setChecked(true);
                    position = 1;
                }
            }
        }
        else
        {
            Log.e("intent is null","intent is null");
            tab_position = "";
            page_info = "";
            fragment = new Detailed_News_Fragment();
            addFragment(fragment, getResources().getString(R.string.detailed_news_txt));
            navigationView.getMenu().getItem(0).setChecked(true);
            if(video_camera !=null)
                video_camera.setVisible(true);
            position = 0;
        }
    }
}