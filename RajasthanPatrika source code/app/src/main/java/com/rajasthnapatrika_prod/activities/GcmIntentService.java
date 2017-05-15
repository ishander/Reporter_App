package com.rajasthnapatrika_prod.activities;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.service.Service_to_get_Credentials;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.util.HashMap;

public class GcmIntentService extends IntentService 
{	
	NotificationManager mNotificationManager;
	Intent moveIntent;
	Intent notif_intent;
	String FirstName;
	private DBHandler dbHandler;
	private DBHelper dbHelper;
	Handler handler;
	public GcmIntentService()
	{
		super("GcmIntentService");
		if(AppUtills.showLogs)Log.v("GcmIntentService", "GcmIntentService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{	
		try 
		{
			handler = new Handler();
			dbHandler = new DBHandler();
			dbHelper = new DBHelper(getApplicationContext(), null, null, 0);

			if(AppUtills.showLogs)Log.v("GcmIntentService", "onHandleIntent : "+intent.getExtras().toString());

            GcmBroadcastReceiver.completeWakefulIntent(intent);
			// for getting notification message and then to fire.
			notif_intent  = intent;
			onMessage(intent);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 		
	}

	private void onMessage(Intent notificationIntent)
    {    	
    	try {
			if (AppUtills.showLogs) Log.v("GcmIntentService", "onMessage called...");

			ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
			if (AppUtills.showLogs)
				Log.v("GcmIntentService", "notification is : " + notificationIntent.getExtras());

			moveIntent = new Intent();
			moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			moveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			moveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (notificationIntent.getExtras().getString("type_detail") != null) {
				if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("published")) {
					moveIntent = new Intent(this, Drawer_Activity.class);
					Intent intent_service = new Intent(this, Service_to_get_Credentials.class);
					moveIntent.putExtra("position", "0");
					moveIntent.putExtra("notification_id", ""+notificationIntent.getExtras().getString("notification_id"));
					moveIntent.putExtra("isRead", "Unread");
					moveIntent.putExtra("Time", ""+notificationIntent.getExtras().getString("notification_time"));
					moveIntent.putExtra("nid", "" + notificationIntent.getExtras().getString("nid"));
					if (notificationIntent.getExtras().getString("breaking").equalsIgnoreCase("0"))
					{
						HashMap<String, String> hashMap = dbHelper.getListSelected_news("",AppController_Patrika.Story_Type_Detailed,notificationIntent.getExtras().getString("nid"));
						moveIntent.putExtra("flag", "detail");
						exchange_Uploded_news(hashMap,AppController_Patrika.Story_Type_Detailed,notificationIntent);
					} else if (notificationIntent.getExtras().getString("breaking").equalsIgnoreCase("1"))
					{
						HashMap<String, String> hashMap = dbHelper.getListSelected_news("",AppController_Patrika.Story_Type_Breaking,notificationIntent.getExtras().getString("nid"));
						moveIntent.putExtra("flag", "breaking");
						exchange_Uploded_news(hashMap,AppController_Patrika.Story_Type_Breaking,notificationIntent);
					}
					show_Notification_based_on(notificationIntent);
//					startService(intent_service);
			} else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("approved"))
				{
					moveIntent = new Intent(this, Drawer_Activity.class);
					Intent intent_service = new Intent(this, Service_to_get_Credentials.class);
					moveIntent.putExtra("notification_id", ""+notificationIntent.getExtras().getString("notification_id"));
					moveIntent.putExtra("isRead", "Unread");
					moveIntent.putExtra("Time", ""+notificationIntent.getExtras().getString("notification_time"));
					moveIntent.putExtra("nid", "" + notificationIntent.getExtras().getString("nid"));
					if (notificationIntent.getExtras().getString("breaking").equalsIgnoreCase("0")) {
						moveIntent.putExtra("flag", "detail");
						intent_service.putExtra("flag", "detail");
						HashMap<String, String> hm = dbHelper.check_if_news_exist(notificationIntent.getExtras().getString("nid"), "not_publish", "");
						HashMap<String, String> hashMap = dbHelper.getListSelected_news("", AppController_Patrika.Story_Type_Detailed, notificationIntent.getExtras().getString("nid"));

						if (AppUtills.showLogs)
							Log.e("GCMINTENt service ", "hm size if exist now " + hm.size());

						if (hm != null && hm.size() > 0) {
							moveIntent.putExtra("position", "1");
							update_approved_news(hashMap, AppController_Patrika.Story_Type_Detailed, notificationIntent);
						} else {
							moveIntent.putExtra("position", "0");
							exchange_Uploded_news(hashMap, AppController_Patrika.Story_Type_Detailed, notificationIntent);
					}

					} else if (notificationIntent.getExtras().getString("breaking").equalsIgnoreCase("1"))
					{

						moveIntent.putExtra("flag", "breaking");

						intent_service.putExtra("flag", "breaking");
						HashMap<String, String> hashMap = dbHelper.getListSelected_news("",AppController_Patrika.Story_Type_Breaking,notificationIntent.getExtras().getString("nid"));
						HashMap<String,String> hm =   dbHelper.check_if_news_exist(notificationIntent.getExtras().getString("nid"),"not_publish","");
						if(AppUtills.showLogs)Log.e("GCMINTENt service ","hm size if exist "+hm.size());
						if(hm!=null && hm.size()>0)
						{
							moveIntent.putExtra("position", "1");
							update_approved_news(hashMap,AppController_Patrika.Story_Type_Breaking,notificationIntent);
						}else{
							moveIntent.putExtra("position", "0");
							exchange_Uploded_news(hashMap,AppController_Patrika.Story_Type_Breaking,notificationIntent);
						}
					}
					show_Notification_based_on(notificationIntent);
				}
				else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("0") || notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("1") || notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("2")) {
					moveIntent = new Intent(this, Add_Stories_Activity.class);
					moveIntent.putExtra("flag", "content_type");
					moveIntent.putExtra("notification_id", "" + notificationIntent.getExtras().getString("notification_id"));
					moveIntent.putExtra("isRead", "Unread");
					moveIntent.putExtra("Time", ""+notificationIntent.getExtras().getString("notification_time"));
					moveIntent.putExtra("nid", "" + notificationIntent.getExtras().getString("nid"));
					moveIntent.putExtra("content_url", "" + notificationIntent.getExtras().getString("content_url"));
					show_Notification_based_on(notificationIntent);
				}else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("rejected")) {
					moveIntent = new Intent(this, Drawer_Activity.class);
					moveIntent.putExtra("flag", "detail");
					moveIntent.putExtra("position", "0");
					moveIntent.putExtra("notification_id", ""+notificationIntent.getExtras().getString("notification_id"));
					moveIntent.putExtra("isRead", "Unread");
					moveIntent.putExtra("Time", ""+notificationIntent.getExtras().getString("notification_time"));
					moveIntent.putExtra("nid", "" + notificationIntent.getExtras().getString("nid"));
					show_Notification_based_on(notificationIntent);
				}
				else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("ftp")) {
					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false)) {
						AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("ftp_server", "" + notificationIntent.getExtras().getString("ftp_server"))
								.putString("ftp_user_name", "" + notificationIntent.getExtras().getString("ftp_user_name"))
								.putString("ftp_user_password", "" + notificationIntent.getExtras().getString("ftp_user_password"))
								.putString("ftp_port", "" + notificationIntent.getExtras().getString("ftp_port"))
								.commit();
						if(AppUtills.showLogs)Log.e("GCMIntentService","ftp_server	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_server",""));
						if(AppUtills.showLogs)Log.e("GCMIntentService","ftp_user_name	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_name",""));
						if(AppUtills.showLogs)Log.e("GCMIntentService","ftp_user_password	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_user_password",""));
						if(AppUtills.showLogs)Log.e("GCMIntentService","ftp_port	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("ftp_port",""));
					}
				} else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("domains")) {
					if (AppController_Patrika.getSharedPreferences().getBoolean("is_true", false))
					{
						if(AppUtills.showLogs)Log.e("intentService","domains  "+notificationIntent.getExtras().getString("domains"));
						moveIntent = new Intent(this, Service_to_get_Credentials.class);
						moveIntent.putExtra("flag", "domains");
						moveIntent.putExtra("json", notificationIntent.getExtras().getString("domains"));
						startService(moveIntent);
					}
				} else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("categories")) {
					if (AppController_Patrika.getSharedPreferences().getBoolean("is_true", false))
					{
						if(AppUtills.showLogs)Log.e("intentService","domains  "+notificationIntent.getExtras().getString("categories"));
						moveIntent = new Intent(this, Service_to_get_Credentials.class);
						moveIntent.putExtra("flag", "categories");
						moveIntent.putExtra("json", notificationIntent.getExtras().getString("categories"));
						startService(moveIntent);
					}
				} else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("scope")) {
					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false)) {
						if(AppUtills.showLogs)Log.e("intentService","scope  "+notificationIntent.getExtras().getString("scope"));
						moveIntent = new Intent(this, Service_to_get_Credentials.class);
						moveIntent.putExtra("json", notificationIntent.getExtras().getString("scope"));
						moveIntent.putExtra("flag", "scope");
						startService(moveIntent);
					}
				} else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("priorities")) {
					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false)) {
						if(AppUtills.showLogs)Log.e("intentService","priorities  "+notificationIntent.getExtras().getString("priorities"));
						moveIntent = new Intent(this, Service_to_get_Credentials.class);
						moveIntent.putExtra("flag", "priorities");
						moveIntent.putExtra("json", notificationIntent.getExtras().getString("priorities"));
						startService(moveIntent);
					}
				}else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("sms_number")) {
					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false)) {
						if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
							AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("sms_number",notificationIntent.getExtras().getString("sms_number")).commit();

						if(AppUtills.showLogs)Log.e("GCMIntentService","sms_number	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number",""));

					}
				}else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("password")) {
					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
						AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("password",notificationIntent.getExtras().getString("password")).commit();

					if(AppUtills.showLogs)Log.e("GCMIntentService","password	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("password",""));
				}
				else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("content_type")) {

					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
						AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("content_type",notificationIntent.getExtras().getString("content_type")).commit();

					if(AppUtills.showLogs)Log.e("GCMIntentService","content_type	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type",""));

				}else if (notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("webview_url")) {

					if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
						AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("webview_url",notificationIntent.getExtras().getString("webview_url")).commit();

					if(AppUtills.showLogs)Log.e("GCMIntentService","webview_url	"+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("webview_url",""));
				}
			}
		}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
   	}

	void show_Notification_based_on(Intent notificationIntent)
	{
		if(AppController_Patrika.getSharedPreferences().getBoolean("is_true",false))
		{
			String require_more ="";
//			String subtitle = "";
			if(notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("0") || notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("1") || notificationIntent.getExtras().getString("type_detail").equalsIgnoreCase("2")) {
				require_more = "content_moreinfo";
			}else {
				require_more = "content_status";
			}

			dbHandler.notification_id = ""+notificationIntent.getExtras().getString("notification_id");
			dbHandler.Nid = ""+notificationIntent.getExtras().getString("nid");
			dbHandler.Content_url = ""+notificationIntent.getExtras().getString("content_url");
			dbHandler.Heading = ""+notificationIntent.getExtras().getString("gcm.notification.title");
			dbHandler.Time = ""+notificationIntent.getExtras().getString("notification_time");
			dbHandler.PlaceName = "";
			dbHandler.Required_more = ""+notificationIntent.getExtras().getString("type_detail");
			dbHandler.isRead = "Unread";
			dbHandler.Story_Type = ""+notificationIntent.getExtras().getString("breaking");

		if(notificationIntent.getExtras().getString("gcm.notification.body") != null)
			dbHandler.Sub_heading = notificationIntent.getExtras().getString("gcm.notification.body");
		else
			dbHandler.Sub_heading = "";

			dbHelper.insertDataInNotifiation_tbl(dbHandler);
			showActionNotification(notificationIntent,require_more);
		}
	}

//	 public int getNotificationIcon() {
//	        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//	        return whiteIcon ? R.drawable.app_icon_logo_rj : R.drawable.app_icon_logo_rj;
//	    }
	
	private void showActionNotification(Intent notificationIntent,String require_more)
    {    	
		try 
		{
			if(AppUtills.showLogs)Log.v("GcmIntentService", "showActionNotification called...");

			int icon = 0;

			if(require_more.equals("content_status"))
				icon = R.drawable.status36_yellow;
			else if(require_more.equals("content_moreinfo"))
				icon = R.drawable.moreinfo36_yellow;


			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,moveIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setAutoCancel(true)  			
			.setContentTitle(notificationIntent.getExtras().getString("gcm.notification.title"))
//			.setSubText(notificationIntent.getExtras().getString("gcm.notification.body"))

			.setContentText(notificationIntent.getExtras().getString("gcm.notification.body"))
			.setSmallIcon(icon)
//			.setLargeIcon(largeIcon)
			.setContentIntent(pendingIntent)			
			//.addAction(R.drawable.firstname_icon, "Follow to",pendingIntent)
			.addAction(android.R.drawable.sym_action_email, "Message to",pendingIntent);

			generateNotification(mBuilder, notificationIntent);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
	private void generateNotification(NotificationCompat.Builder mBuilder,Intent intent)
	{
		try 
		{
			if(AppUtills.showLogs)Log.v("GcmIntentService", "generateNotification called...");
			
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);			
			Notification notification = mBuilder.build();
//
			if(intent.getExtras().getString("type_detail").equalsIgnoreCase("0"))
			{
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_LIGHTS;

				show_Lights();

			}else if(intent.getExtras().getString("type_detail").equalsIgnoreCase("1"))
			{
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_LIGHTS;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				show_Lights();
			}else if(intent.getExtras().getString("type_detail").equalsIgnoreCase("2"))
			{
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_LIGHTS;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				notification.defaults |= Notification.DEFAULT_SOUND;
				show_Lights();
			}else
			{
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_LIGHTS;
	//				notification.defaults |= Notification.DEFAULT_VIBRATE;
				notification.defaults |= Notification.DEFAULT_SOUND;
				show_Lights();
			}
				mNotificationManager.notify((int)System.currentTimeMillis(), notification);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public void show_Lights()
	{
		PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);

		boolean isScreenOn = pm.isScreenOn();

		if(isScreenOn==false)
		{

			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");

			wl.acquire(10000);
			PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

			wl_cpu.acquire(10000);
		}
	}

	void exchange_Uploded_news(HashMap<String, String> hashMap1, String story_type, Intent notificationIntent)
	{
		if(AppUtills.showLogs)Log.e("GCM_Intent_Service","exchange_Uploded_news called map size is  "+hashMap1.size());
		if(AppUtills.showLogs)Log.e("GCM_Intent_Service","hashMap1    "+hashMap1);
		HashMap<String, String> hashMap = new HashMap<>();

		if(hashMap1.size()>0)
		{
			hashMap.put("Heading", hashMap1.get("Heading"));
			hashMap.put("Sub_heading", hashMap1.get("Sub_heading"));
			hashMap.put("PlaceName", hashMap1.get("PlaceName"));
			hashMap.put("Time", notificationIntent.getExtras().getString("notification_time"));
			hashMap.put("Thumbnail", hashMap1.get("Thumbnail"));

			hashMap.put("publish_status_tv", hashMap1.get("publish_status_tv"));
			hashMap.put("publish_status_web", hashMap1.get("publish_status_web"));
			hashMap.put("publish_status_print", hashMap1.get("publish_status_print"));

			if(AppUtills.showLogs)Log.e("GCM_Intent_Service","Thumbnail	"+hashMap1.get("Thumbnail"));
		}else
		{
			hashMap.put("Heading", notificationIntent.getExtras().getString("gcm.notification.title"));
			hashMap.put("Sub_heading", "");
			hashMap.put("PlaceName", "");
			hashMap.put("Time", ""+notificationIntent.getExtras().getString("notification_time"));
			hashMap.put("Thumbnail", "");

			hashMap.put("publish_status_tv", "0");
			hashMap.put("publish_status_web", "0");
			hashMap.put("publish_status_print", "0");
		}

		hashMap.put("Ftp_directory", "");
		hashMap.put("User_name", "");
		hashMap.put("Nid", notificationIntent.getExtras().getString("nid"));
		hashMap.put("Story_Type", story_type);

		if(notificationIntent.getExtras().getString("published_media_type")!=null) {

			if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("tv")) {
				if (notificationIntent.getExtras().getString("published_media_url").equalsIgnoreCase(""))
					hashMap.put("publish_status_tv","1");
				else
					hashMap.put("publish_status_tv", notificationIntent.getExtras().getString("published_media_url"));
			}else if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("web")) {
				if (notificationIntent.getExtras().getString("published_media_url").equalsIgnoreCase(""))
					hashMap.put("publish_status_web","1");
				else
				hashMap.put("publish_status_web", notificationIntent.getExtras().getString("published_media_url"));
			}else if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("print"))
			{
				if (notificationIntent.getExtras().getString("published_media_url").equalsIgnoreCase(""))
				hashMap.put("publish_status_print","1");
			else
				hashMap.put("publish_status_print", notificationIntent.getExtras().getString("published_media_url"));
			}
		}

		if(AppUtills.showLogs)Log.e("GCM_Intent_Service","hashMap    "+hashMap);

		dbHelper.insert_published_News(hashMap, AppController_Patrika.Story_Type_Detailed,false);

		dbHelper.discard_Stored_images_data(notificationIntent.getExtras().getString("nid"), "publish");
		dbHelper.discard_Stored_data(notificationIntent.getExtras().getString("nid"),"publish");
	}

	void update_approved_news(HashMap<String, String> hashMap1, String story_type, Intent notificationIntent)
	{
		if(AppUtills.showLogs)Log.e("GCM_Intent_Service","update_approved_news called map size is  "+hashMap1.size());
		if(AppUtills.showLogs)Log.e("GCM_Intent_Service","hashMap1    "+hashMap1);
		dbHandler = new DBHandler();
		if(hashMap1.size()>0)
		{
			dbHandler.Heading = "" + hashMap1.get("Heading");
			dbHandler.Sub_heading = "" + hashMap1.get("Sub_heading");
			dbHandler.PlaceName = "" + hashMap1.get("PlaceName");
			dbHandler.Time = "" +notificationIntent.getExtras().getString("notification_time");
			dbHandler.Thumbnail = "" + hashMap1.get("Thumbnail");
			dbHandler.Domain = "" + hashMap1.get("Domain");
			dbHandler.Category = "" + hashMap1.get("Category");
			dbHandler.Scope = "" + hashMap1.get("Scope");
			dbHandler.Priority = "" + hashMap1.get("Priority");
			dbHandler.Personality_topic = "" + hashMap1.get("Personality_topic");
			dbHandler.Body = "" + hashMap1.get("Body");
			dbHandler.Introduction = "" + hashMap1.get("Introduction");
			dbHandler.MiliSec = Long.parseLong(hashMap1.get("MiliSec"));
			dbHandler.Nid =  hashMap1.get("Nid");
			dbHandler.latitute = ""+hashMap1.get("latitute");
			dbHandler.longitute = ""+hashMap1.get("longitute");

			dbHandler.publish_status_tv = hashMap1.get("publish_status_tv");
			dbHandler.publish_status_print = hashMap1.get("publish_status_print");
			dbHandler.publish_status_web = hashMap1.get("publish_status_web");
			if(AppUtills.showLogs)Log.e("GCM_Intent_Service","Thumbnail	"+hashMap1.get("Thumbnail"));

		}else
		{
			dbHandler.Heading = notificationIntent.getExtras().getString("gcm.notification.title");
			dbHandler.Sub_heading = "" + "";
			dbHandler.PlaceName = "" + "";
			dbHandler.Time = "" +notificationIntent.getExtras().getString("notification_time");
			dbHandler.Thumbnail = "" + "";
			dbHandler.Domain = "";
			dbHandler.Category = "";
			dbHandler.Scope = "";
			dbHandler.Priority = "";
			dbHandler.Personality_topic = "";
			dbHandler.Body = "";
			dbHandler.Introduction = "";
			dbHandler.MiliSec = 0;
			dbHandler.Nid = ""+notificationIntent.getExtras().getString("nid");
			dbHandler.latitute = ""+AppController_Patrika.Latitude;
			dbHandler.longitute = ""+AppController_Patrika.Longitude;

				dbHandler.publish_status_web = "0";
				dbHandler.publish_status_tv = "0";
				dbHandler.publish_status_print = "0";
		}
		dbHandler.Status = AppController_Patrika.Story_Status_Approved;
		dbHandler.Status_process = "old";
		dbHandler.Story_Type = ""+story_type;

		if(notificationIntent.getExtras().getString("published_media_type")!=null) {

			if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("tv")) {
				dbHandler.publish_status_tv = "1";
			}else if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("web")) {
				dbHandler.publish_status_web = "1";
			}else if (notificationIntent.getExtras().getString("published_media_type").equalsIgnoreCase("print"))
			{
				dbHandler.publish_status_print = "1";
			}
		}
		if(hashMap1.get("Id") != null)
			dbHelper.insertDataInAdd_News(dbHandler, hashMap1.get("Id"));
		else
			dbHelper.insertDataInAdd_News(dbHandler, "");
	}
}

