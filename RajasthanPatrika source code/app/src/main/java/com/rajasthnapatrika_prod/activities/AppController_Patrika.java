package com.rajasthnapatrika_prod.activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mshrotriya on 1/12/2016.
 */
public class AppController_Patrika extends Application
{
    public static String content_logout_api = "http://ccms.patrikatv.com/api/repapp/v1/user/logout.json";
    public static String content_login_api = "http://ccms.patrikatv.com/api/repapp/v1/user/login.json";
    public static String content_create_api = "http://ccms.patrikatv.com/api/repapp/v1/content";
    public static String Uploaded_Breaking_news_Api  = "http://ccms.patrikatv.com/api/repapp/v1/contents?published=0&breaking=1";
    public static String Published_Breaking_news_Api  = "http://ccms.patrikatv.com/api/repapp/v1/contents?published=1&breaking=1";
    public static String Uploaded_Detailed_news_Api  = "http://ccms.patrikatv.com/api/repapp/v1/contents?published=0&breaking=0";
    public static String Published_Detailed_news_Api  = "http://ccms.patrikatv.com/api/repapp/v1/contents?published=1&breaking=0";
    public static String Notification_Api  = "http://ccms.patrikatv.com/api/repapp/v1/repapp_notification/all";
    public static String All_Config_Api  = "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/all";
    public static String Domain_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/domains";
    public static String Category_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/categories";
    public static String Scope_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/scope";
    public static String Priorities_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/priorities";
    public static String Sms_Number_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/sms_number";
    public static String Ftp_Credential_Api= "http://ccms.patrikatv.com/api/repapp/v1/repapp_config/retrieve=ftp";
    public static String Notification_ACk = "http://ccms.patrikatv.com/api/repapp/v1/repapp_notification/";
    public static String get_news_by_id = "http://ccms.patrikatv.com/api/repapp/v1/content/";

    public static final String ACTION_MULTIPLE_PICK_IMAGES = "luminous.ACTION_MULTIPLE_PICK_IMAGE";
    public static final String ACTION_MULTIPLE_PICK_VIDEOS = "luminous.ACTION_MULTIPLE_PICK_VIDEO";
    private static AppController_Patrika mInstance;
    private static SharedPreferences sharedPref_patrika ;
    private static SharedPreferences sharedPref_patrika_rememberme ;
    private static SharedPreferences sharedPref_patrika_FTP_Credentials ;
    private static ImageLoader imageLoader;
    private static DisplayImageOptions options_big;
    private static DisplayImageOptions options_circular;
    public static String PlaceName = "";
    public static Double Latitude = 0.0;
    public static Double Longitude = 0.0;
    public static Double Latitude_cell = 0.0;
    public static Double Longitude_cell = 0.0;
    public static String Story_Type_Detailed = "Detailed";
    public static String Story_Type_Breaking = "Breaking";
    public static String Story_Status_draft = "Saved as draft";
    public static String Story_Status_process = "In process";
    public static String Story_Status_outbox = "In outbox";
    public static String Story_Status_Uploaded = "Uploaded";
    public static String Story_Status_Approved = "Approved";

    public static ArrayList<HashMap<String,String>> paths = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mInstance = this;

            initSharedPreferences_FTP_Credentials();
            initSharedPreferences();
            initSharedPreferences_remember_me();
            Crittercism.initialize(getApplicationContext(), "c8ba647355b6441e85bae0ec0167a61700555300");
            initImageLoader();

            AppUtills.getCurrentLocation(getApplicationContext());

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            int cid = cellLocation.getCid();
            int lac = cellLocation.getLac();
            Log.e("Application","cid "+cid);
            Log.e("Application","lac "+lac);
            AppUtills.displayMap(cid, lac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private void initSharedPreferences() {
        try {
            sharedPref_patrika = getApplicationContext().getSharedPreferences("sharedPref_patrika", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSharedPreferences_remember_me() {
        try {
            sharedPref_patrika_rememberme = getApplicationContext().getSharedPreferences("sharedPref_patrika_rememberme", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSharedPreferences_FTP_Credentials() {
        try {
            sharedPref_patrika_FTP_Credentials = getApplicationContext().getSharedPreferences("sharedPref_patrika_FTP_Credentials", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized SharedPreferences getSharedPreferences() {
        return sharedPref_patrika;
    }

    public static synchronized SharedPreferences getSharedPreferences_remember_me() {
        return sharedPref_patrika_rememberme;
    }

    public static synchronized SharedPreferences getSharedPreferences_FTP_Credentials() {
        return sharedPref_patrika_FTP_Credentials;
    }

    private void initImageLoader() {
        try {
            imageLoader = ImageLoader.getInstance();

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnFail(R.drawable.no_media).showImageForEmptyUri(R.drawable.no_media) // this will intitate null if there is loading fail or empty url
                    .cacheInMemory(true).cacheOnDisc(true).handler(new Handler())
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getApplicationContext()).defaultDisplayImageOptions(defaultOptions).memoryCache(
                    new WeakMemoryCache());

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .denyCacheImageMultipleSizesInMemory()
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024)
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs() // Remove for release app
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ImageLoader getImageLoader() {
        return imageLoader;
    }
}
