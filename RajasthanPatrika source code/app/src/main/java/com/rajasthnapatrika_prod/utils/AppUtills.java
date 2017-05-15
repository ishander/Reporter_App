package com.rajasthnapatrika_prod.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.database.DBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Specifies the Utility functionality checks the pattern match, location
 * provider, Camera support, GPS status checks Play Services, Network
 * Availability etc.
 *
 * @author dgupta
 *
 */
public class AppUtills {
	/** Required variable declaration start here */
	static ConnectivityManager connectivityManager;
	static NetworkInfo wifiInfo, mobileInfo;
	static boolean connected = false;
    String TAG = "AppUtills";
	public static boolean showLogs = true;
	public static boolean service_showLogs = false;
	static boolean showresponse = true;
	public static boolean showMail = false;
	public static boolean address_updated = true;
	public static final String IMAGE_DIRECTORY_NAME = "Patrika_img";
	public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;
    public static final int MEDIA_TYPE_Thumbnail = 4;
	public static Context context;
   static String placeby_lat_long = "";
	/** variable declaration end here */

	public AppUtills getInstance() {
		return this.getInstance();
	}


	public static boolean isGpsOn(Context ctx) {
		LocationManager locationManager = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static long value(String string)
	{
	    string = string.trim();
	    if( string.contains( "." ))
	    {
	        final int index = string.lastIndexOf( "." );
	        return value( string.substring( 0, index ))* 100 + value( string.substring( index + 1 ));
	    }
	    else
	    {
	        return Long.valueOf( string );
	    }
	}


	public static boolean checkDeviceSupportCamera(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		try {
			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			connected = networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected();
			return connected;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connected;
	}

	public static void alertDialog(Context context, String message)
    {
		final Dialog dialog = createDialog(context, true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		TextView dialog4all_txt = (TextView) dialog
				.findViewById(R.id.txt_DialogTitle);
		dialog4all_txt.setText(message);
		dialog.findViewById(R.id.btn_DialogOk).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		dialog.show();
	}

	public static Dialog createDialog(Context context, boolean single) {
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (single)
            dialog.setContentView(R.layout.custom_dialog_one);
		else
            dialog.setContentView(R.layout.custom_dialog_two);


		return dialog;
	}


public static String getDecodedResponse(HttpPost httppost,List<NameValuePair> nameValuePairs)
{
    BufferedReader in = null;
    HttpResponse response = null;
//    HttpParams httpParams = new BasicHttpParams();
//    HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
//    HttpConnectionParams.setSoTimeout(httpParams, 7000);
    DefaultHttpClient httpclient = new DefaultHttpClient();

    try {
        if(nameValuePairs != null)
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

    try {
        Log.v("httppost request","httppost request  "+httppost);
        Log.v("httppost request","httppost request  "+httppost.toString());

        response = httpclient.execute(httppost);
        if(AppUtills.showLogs)Log.v("Response:" , response.toString());


        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("Cookie: " + cookie.toString());
            Log.v("Cookie","cookie name    "+cookie.getName());
            Log.v("Cookie","cookie value   "+ cookie.getValue().toString());

        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    try
    {
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    StringBuffer sb = new StringBuffer("");
    String line = "";
    String NL = System.getProperty("line.separator");

    try
    {
        while ((line = in.readLine()) != null)
        {
            sb.append(line + NL);
        }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }

    try
    {
        in.close();
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    return sb.toString();
}


    public static String getDecodedResponse_Update1(HttpPut httpput)
    {
        BufferedReader in = null;
        HttpResponse response = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();


        CookieStore cookieStore = new BasicCookieStore();

        BasicClientCookie cookie1 = new BasicClientCookie("SESSb559b3765afb61317d78bad77b795d28", "JeqB0eokBC_yvMqaDTah5ya-GrEzAOblzIBYBqgKVJ4");
        cookieStore.addCookie(cookie1);


        // Create local HTTP context
        HttpContext localContext = new BasicHttpContext();
        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);


        try {
            Log.v("httppost request","httppost request  "+httpput);
            Log.v("httppost request","httppost request  "+httpput.toString());

            response = httpclient.execute(httpput);
            if(AppUtills.showLogs)Log.v("Response:" , response.toString());


            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            for (Cookie cookie : cookies) {
                System.out.println("Cookie: " + cookie.toString());
                Log.v("Cookie","cookie name    "+cookie.getName());
                Log.v("Cookie","cookie value   "+ cookie.getValue().toString());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");

        try
        {
            while ((line = in.readLine()) != null)
            {
                sb.append(line + NL);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static String getDecodedResponse(HttpPost httppost)
    {
        BufferedReader in = null;
        HttpResponse response = null;
        HttpClient httpclient=new DefaultHttpClient();

        try {
            Log.v("httppost request","httppost request  "+httppost);
            Log.v("httppost request","httppost request  "+httppost.toString());

            response = httpclient.execute(httppost);
            if(AppUtills.showLogs)Log.v("Response:" , response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");

        try
        {
            while ((line = in.readLine()) != null)
            {
                sb.append(line + NL);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static String getDecodedResponse(HttpGet httpGet)
    {
        BufferedReader in = null;
        HttpResponse response = null;
        HttpClient httpclient=new DefaultHttpClient();

        try {
            Log.v("httppost request","httppost request  "+httpGet);
            Log.v("httppost request","httppost request  "+httpGet.toString());

            response = httpclient.execute(httpGet);
            if(AppUtills.showLogs)Log.v("Response:" , response.toString());
            Log.e("Response Code","Response Code  "+response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");

        try
        {
            while ((line = in.readLine()) != null)
            {
                sb.append(line + NL);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getDate_n_Time()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
       String Date_Time = sdf.format(cal.getTime());
        if(AppUtills.showLogs)Log.v("Date_Time","Date_Time  "+Date_Time);
        return Date_Time;
    }



    public static String printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
        if(AppUtills.showLogs)Log.v("time difference", ""+elapsedDays+elapsedHours+elapsedMinutes+elapsedSeconds);

        if(elapsedDays != 0) {
            if(elapsedDays == 1)
                return elapsedDays + " day ago";
            else
                return elapsedDays + " days ago";
        }else if (elapsedHours != 0)
            return elapsedHours+" hour ago";
        else if(elapsedMinutes != 0) {
            if (elapsedMinutes == 1)
                return elapsedMinutes + " min ago";
            else
                return elapsedMinutes + " min ago";
        }else if(elapsedSeconds != 0) {
            if (elapsedSeconds == 1)
                return elapsedSeconds + " sec ago";
            else
                return elapsedSeconds + " sec ago";
        }
        return elapsedSeconds + " sec ago";
    }

    public static Uri getOutputMediaFileUri(int type, String PageName) {
        // External sdcard location
        Log.v(PageName + "  type is ", "type  "+ type);
        File mediaStorageDir = null;

        if(type == MEDIA_TYPE_AUDIO)
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Patrika Reporter_Prod/Audio");
        else  if(type == MEDIA_TYPE_VIDEO)
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Patrika Reporter_Prod/Video");
        else  if(type == MEDIA_TYPE_IMAGE)
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Patrika Reporter_Prod/Image");
        else  if(type == MEDIA_TYPE_Thumbnail)
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Patrika Reporter_Prod/Thumbnail");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                if (AppUtills.showLogs)
                    Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                            + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String time =  ""+System.currentTimeMillis();
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    +time+ "IMG.jpg");
        }else if(type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    +time+ "AUD.mp3");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    +time+"VID.mp4");
        }
        else
        {
            return null;
        }

        if (AppUtills.showLogs)
            Log.v(PageName + "  mediaFile", "" + mediaFile);
        if (AppUtills.showLogs)
            Log.v(PageName + "  uri is", "" + Uri.fromFile(mediaFile));
        if (AppUtills.showLogs)
            Log.v(PageName + "  uri is path", ""
                + Uri.fromFile(mediaFile).getPath());

        return Uri.fromFile(mediaFile);
    }


    public static String create_json_format(String media,ArrayList<HashMap<String,String>> list,String  MiliSec,int len)
    {

        String str_json = "";
        try {

            JSONObject data_json2 = new JSONObject();
            JSONObject  data_json3 = new JSONObject();

            int j = 0;
            for(int i=0;i<list.size();i++)
            {
               if(list.get(i).get("Status").equalsIgnoreCase("queue") && list.get(i).get("Status_process").equals("new")) {
                   File file = new File(list.get(i).get("Path"));
                   String strFileName = file.getName();
                   JSONObject data_json1 = new JSONObject();

                   data_json1.put("filename", AppController_Patrika.getSharedPreferences().getString("userid", "") + "_" + MiliSec + "_" + strFileName);

                   data_json1.put("upload_status", "0");

                   data_json2.put(media + (len+j), data_json1);
                   j++;
               }
            }
            data_json3.put("value", data_json2.toString());

            String str = data_json3.toString();
            String s = str.replaceAll("\\\\","");

            StringBuffer stringBuffer = new StringBuffer(s);

            stringBuffer.replace(9,10,"");
            stringBuffer.replace(stringBuffer.length()-2,stringBuffer.length()-1,"");

            str_json = stringBuffer.toString();
            Log.e("AppUtils","stringBuffer "+str_json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str_json;
    }

    /*get BItmaip from path*/

    public static Bitmap getBitmapFromPath(String path)
    {
        Bitmap bmThumbnail;
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        return bmThumbnail;
    }
	/**
	 * Used to compress bitmap images
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap compressBitmapImage(String filePath) {
		Bitmap scaledBitmap = null;

		if (new File(filePath).length() > 0) {
			if (AppUtills.showLogs)
				Log.v("Home ", ""
						+ new File(filePath).length());

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

			int actualHeight = options.outHeight;
			int actualWidth = options.outWidth;
			float maxHeight = 816.0f;
			float maxWidth = 612.0f;
			float imgRatio = actualWidth / actualHeight;
			float maxRatio = maxWidth / maxHeight;

			if (actualHeight > maxHeight || actualWidth > maxWidth) {
				if (imgRatio < maxRatio) {
					imgRatio = maxHeight / actualHeight;
					actualWidth = (int) (imgRatio * actualWidth);
					actualHeight = (int) maxHeight;
				} else if (imgRatio > maxRatio) {
					imgRatio = maxWidth / actualWidth;
					actualHeight = (int) (imgRatio * actualHeight);
					actualWidth = (int) maxWidth;
				} else {
					actualHeight = (int) maxHeight;
					actualWidth = (int) maxWidth;
				}
			}

			options.inSampleSize = calculateInSampleSize(options, actualWidth,
					actualHeight);
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[16 * 1024];

			try {
				bmp = BitmapFactory.decodeFile(filePath, options);
			} catch (OutOfMemoryError exception) {
				exception.printStackTrace();

			}
			try {
				scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
						Bitmap.Config.ARGB_8888);
			} catch (OutOfMemoryError exception) {
				exception.printStackTrace();
			}

			float ratioX = actualWidth / (float) options.outWidth;
			float ratioY = actualHeight / (float) options.outHeight;
			float middleX = actualWidth / 2.0f;
			float middleY = actualHeight / 2.0f;

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
					middleY - bmp.getHeight() / 2, new Paint(
							Paint.FILTER_BITMAP_FLAG));

			ExifInterface exif;
			try {
				exif = new ExifInterface(filePath);

				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 0);
				if (AppUtills.showLogs)
					Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
					if (AppUtills.showLogs)
						Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 3) {
					matrix.postRotate(180);
					if (AppUtills.showLogs)
						Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 8) {
					matrix.postRotate(270);
					if (AppUtills.showLogs)
						Log.d("EXIF", "Exif: " + orientation);
				}
				scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
						scaledBitmap.getWidth(), scaledBitmap.getHeight(),
						matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return scaledBitmap;
	}



	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;

		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

    public static void getPlace_Name(final Context context)
    {
       try
       {
           if(isNetworkAvailable(context) || isGpsOn(context)) {
              final  Location location = null;
               if(location != null)
               {
//                  new AsyncTask<Void,Void,String>()
//                  {
//                      @Override
//                      protected String doInBackground(Void... voids) {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {

                            String place = "";
                          try {
                              AppController_Patrika.Latitude = location.getLatitude();
                              AppController_Patrika.Longitude = location.getLongitude();
                              if (AppUtills.showLogs)Log.v("Latitude", "Latitude  " + AppController_Patrika.Latitude);
                              if (AppUtills.showLogs)Log.v("Longitude", "Longitude  " +  AppController_Patrika.Longitude);
                              if(AppUtills.isNetworkAvailable(context)) {
                                  Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                  List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                  if (addresses != null && addresses.size() > 0) {
                                      Address address = addresses.get(0);

                                      if (address.getSubAdminArea() == null) {
                                          place = address.getLocality();
                                      } else
                                      {
                                          place = address.getSubAdminArea();
                                      }
                                  }
                              }
                              if (AppUtills.showLogs)
                                  Log.v("CityName", "CityName  " + place);
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                       }
                   });
//                          return place;
//                      }
//
//                      @Override
//                      protected void onPostExecute(String s) {
//                          AppController_Patrika.PlaceName = s;
//                      }
//                  }.execute();
               }
           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }
    }

    public static String getPlace_Name_by_lat_long(Context context,final String lat,final String longi)
    {
        try
        {
            if(isNetworkAvailable(context) || isGpsOn(context)) {
                if(!lat.equals("") && !longi.equals(""))
                {
                            try {
                                AppController_Patrika.Latitude = Double.parseDouble(lat);
                                AppController_Patrika.Longitude = Double.parseDouble(longi);
                                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(longi), 1);
                                String countryName = addresses.get(0).getAddressLine(2);
                                placeby_lat_long = addresses.get(0).getLocality();

                                if (AppUtills.showLogs)Log.e("countryName", "countryName  " + placeby_lat_long);
                                if (AppUtills.showLogs)Log.e("getAdminArea", "getAdminArea  " + addresses.get(0).getAdminArea());
                                if (AppUtills.showLogs)Log.e("getCountryName", "getCountryName  " + addresses.get(0).getCountryName());
                                if (AppUtills.showLogs)Log.e("getLocality", "getLocality  " + addresses.get(0).getLocality());
                                if (AppUtills.showLogs)Log.e("getSubLocality", "getSubLocality  " + addresses.get(0).getSubLocality());

                                if (AppUtills.showLogs)Log.e("CityName", "CityName  " + placeby_lat_long);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else
                {
                    if (AppUtills.showLogs)Log.v("lat", "lat  " + lat);
                    if (AppUtills.showLogs)Log.v("longi", "longi  " + longi);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return placeby_lat_long;
    }



	public static  void  getCurrentLocation(final Context context) {
       new AsyncTask<String,String,Location>()
       {
           @Override
           protected Location doInBackground(String... strings) {
               Location location = null;
//               try {
//                   LocationManager locationManager = (LocationManager) context
//                           .getSystemService(Context.LOCATION_SERVICE);
//                   if (locationManager != null) {
//                       boolean networkIsEnabled = locationManager
//                               .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                       boolean gpsIsEnabled = locationManager
//                               .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                       if (AppUtills.showLogs)
//                           Log.v("Loc", "networkIsEnabled  " + networkIsEnabled);
//                       if (AppUtills.showLogs)
//                           Log.v("Location " , "gpsIsEnabled   " + gpsIsEnabled);
//
//                       if (networkIsEnabled) {
//                           location = locationManager
//                                   .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                           if (location == null) {
//                               if (gpsIsEnabled) {
//                                   location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                               }
//                           }
//                       } else if (gpsIsEnabled) {
//                           location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                       }
//                   } else {
//                       if (AppUtills.showLogs)
//                           Log.v("Location Service", "Location Manager is null");
//                   }
//               } catch (Exception e) {
//                   e.printStackTrace();
//               }

               try {
                   LocationManager    locationManager = (LocationManager) context
                           .getSystemService(Context.LOCATION_SERVICE);

                   // getting GPS status
                 boolean  isGPSEnabled = locationManager
                           .isProviderEnabled(LocationManager.GPS_PROVIDER);

                   // getting network status
                   boolean isNetworkEnabled = locationManager
                           .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                   if (!isGPSEnabled && !isNetworkEnabled) {
                       // no network provider is enabled
                   } else {
                       // First get location from Network Provider
                       if (isNetworkEnabled) {
//                           locationManager.requestLocationUpdates(
//                                   LocationManager.NETWORK_PROVIDER,
//                                   1000 * 60 * 1,
//                                   10, this);
                           Log.d("Network", "Network");
                           if (locationManager != null) {
                               location = locationManager
                                       .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                           }
                           // if GPS Enabled get lat/long using GPS Services
                           if (isGPSEnabled) {
                               if (location == null) {
//                                   locationManager.requestLocationUpdates(
//                                           LocationManager.GPS_PROVIDER,
//                                           1000 * 60 * 1,
//                                           10, context);
                                   Log.d("GPS Enabled", "GPS Enabled");
                                   if (locationManager != null) {
                                       location = locationManager
                                               .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                   }
                               }
                           }
                       }
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }

               Log.v("Location is ", "Location  is "+location);
               return location;
           }

           @Override
           protected void onPostExecute(final Location location)
           {
               new AsyncTask<Void,Void,String>()
                  {
                      @Override
                      protected String doInBackground(Void... voids) {
                          String place = "";
                          if(location != null) {
                              try {
                                  AppController_Patrika.Latitude = location.getLatitude();
                                  AppController_Patrika.Longitude = location.getLongitude();
                                  if (AppUtills.showLogs)
                                      Log.v("Latitude", "Latitude  " + AppController_Patrika.Latitude);
                                  if (AppUtills.showLogs)
                                      Log.v("Longitude", "Longitude  " + AppController_Patrika.Longitude);
                                  if (AppUtills.isNetworkAvailable(context)) {
                                      Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                      List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                      if (addresses != null && addresses.size() > 0) {
                                          Address address = addresses.get(0);

                                          if (address.getSubAdminArea() == null) {
                                              place = address.getLocality();
                                          } else {
                                              place = address.getSubAdminArea();
                                          }
                                      }
                                  }
                                  if (AppUtills.showLogs)
                                      Log.v("CityName", "CityName  " + place);
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                          }
                          return place;
                      }

                      @Override
                      protected void onPostExecute(String s)
                      {
                          AppController_Patrika.PlaceName = s;
                      }
                  }.execute();
           }
       }.execute();
	}


	public static boolean isMyServiceRunning(Class<?> serviceClass,
			Context context) {
		try {
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager
					.getRunningServices(Integer.MAX_VALUE)) {
				if (serviceClass.getName().equals(
						service.service.getClassName())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    public static int getNotification_list_size(ArrayList<HashMap<String,String>> hashMaps)
    {
        int i = 0;
        for(int j=0; j<hashMaps.size();j++)
        {
            if(hashMaps.get(j).get("isRead").equals("Unread")) {
                i++;
                if(AppUtills.showLogs)Log.e("isRead count","isRead count is  "+i);
            }
        }

        return i;
    }

    public static void registerGcmInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... params) {
                 GoogleCloudMessaging gcm = null;
                String msg = "";
                String  regid = "";
                try {

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging
                                .getInstance(context);
                    }

                    regid = gcm.register(context.getResources().getString(R.string.sender_id));
                    Log.v("one regid is = ", "" + regid);
                    msg = "Device registered, registration ID=" + regid;

                } catch (Exception e) {
                    msg = "Error :" + e.getMessage();

                }
                return regid;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v(" regid is = ", "" + msg);

                AppController_Patrika.getSharedPreferences().edit().putString("reg_id",""+msg).commit();
            }
        }.execute(null, null, null);
    }
    public static String getPlace_Name1(final Context context, String lat, String longi)
    {
        String place = "";
        try
        {
            if(AppUtills.isNetworkAvailable(context) || AppUtills.isGpsOn(context)) {
                if(!lat.equals(""))
                {
                    final double latitute = Double.parseDouble(lat);
                    final double longitute = Double.parseDouble(longi);
                            try {
                                if(AppUtills.isNetworkAvailable(context)) {
                                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(latitute, longitute, 1);

                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);

                                        if (address.getSubAdminArea() == null) {
                                            place = address.getLocality();
                                        } else
                                        {
                                            place = address.getSubAdminArea();
                                        }
                                    }
                                }
                                if (AppUtills.showLogs)
                                    Log.v("CityName", "CityName  " + place);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return place;
    }

    public static void scanFile(Context context,String path) {

        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

  public static  TextWatcher removeTextWatcher(final EditText editText, final TextInputLayout textInputLayout) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                if (AppUtills.showLogs) Log.v("onTextChanged", "onTextChanged");
                editText.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        };
        return textWatcher;
    }

    public static void execute_notification_ack(final Context context,final String notification_id,final String time)
    {
        if (AppUtills.showLogs) Log.v("AppUtils ", "execute_notification_ack called");
        new AsyncTask<Void, Void, String>()

        {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... voids) {

                HttpPut httpPut = new HttpPut(AppController_Patrika.Notification_ACk+notification_id);
                httpPut.setHeader("X-CSRF-Token", AppController_Patrika.getSharedPreferences().getString("token", ""));
                httpPut.setHeader("Content-Type", "application/json");
                httpPut.setHeader("cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));
                httpPut.setHeader("Accept", "application/json");

                if (AppUtills.showLogs) Log.v("Notification ACk", "notif ACk" + AppController_Patrika.Notification_ACk+notification_id);
                if (AppUtills.showLogs) Log.v("notif time", "notif time" + time);


                String response = "";

                try {
                    JSONObject jsonObject = new JSONObject(" { \"ack_time\":\""+time+"\"}");
                    StringEntity stringEntity = new StringEntity(jsonObject.toString(), HTTP.UTF_8) ;
                    if (AppUtills.showLogs) Log.v("jsonobject request", "jsonObject request" + jsonObject.toString());
                    httpPut.setEntity(stringEntity);
                    response = AppUtills.getDecodedResponse_Update1(httpPut);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (AppUtills.showLogs) Log.v("sb.toString", "" + response);

                return response;
            }

            @Override
            protected void onPostExecute(String s) {

                if (AppUtills.showLogs) Log.v("onPost", "onPOst  " + s);

                try
                {
                    Object json = new JSONTokener(s).nextValue();
                    if (json instanceof JSONObject)
                    {
                        DBHelper  dbHelper = new DBHelper(context, null, null, 0);
                        dbHelper.update_Notification_Read_status(notification_id);
                    }

                }catch (Exception e)
                {

                }
            }
        }.execute();
    }

    public static List<File> getRemovabeStorages(Context context) throws Exception {
        List<File> storages = new ArrayList<File>();

        Method getService = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
        if (!getService.isAccessible()) getService.setAccessible(true);
        IBinder service = (IBinder) getService.invoke(null, "mount");

        Method asInterface = Class.forName("android.os.storage.IMountService$Stub").getDeclaredMethod("asInterface", IBinder.class);
        if (!asInterface.isAccessible()) asInterface.setAccessible(true);
        Object mountService = asInterface.invoke(null, service);

        Object[] storageVolumes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            int uid = context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.uid;
            Method getVolumeList = mountService.getClass().getDeclaredMethod("getVolumeList", int.class, String.class, int.class);
            if (!getVolumeList.isAccessible()) getVolumeList.setAccessible(true);
            storageVolumes = (Object[]) getVolumeList.invoke(mountService, uid, packageName, 0);
        } else {
            Method getVolumeList = mountService.getClass().getDeclaredMethod("getVolumeList");
            if (!getVolumeList.isAccessible()) getVolumeList.setAccessible(true);
            storageVolumes = (Object[]) getVolumeList.invoke(mountService, (Object[]) null);
        }

        for (Object storageVolume : storageVolumes) {
            Class<?> cls = storageVolume.getClass() ;
            Method isRemovable = cls.getDeclaredMethod("isRemovable");
            if (!isRemovable.isAccessible()) isRemovable.setAccessible(true);
            if ((boolean) isRemovable.invoke(storageVolume, (Object[]) null)) {
                Method getState = cls.getDeclaredMethod("getState");
                if (!getState.isAccessible()) getState.setAccessible(true);
                String state = (String) getState.invoke(storageVolume, (Object[]) null);
                if (state.equals("mounted")) {
                    Method getPath = cls.getDeclaredMethod("getPath");
                    if (!getPath.isAccessible()) getPath.setAccessible(true);
                    String path = (String) getPath.invoke(storageVolume, (Object[]) null);
                    storages.add(new File(path));
                    Log.e("path", "" + path);
                }
            }
        }

        return storages;
    }

    public static File getRemovabeStorageDir(Context context) {
        try {
            List<File> storages = getRemovabeStorages(context);
            if (!storages.isEmpty()) {
                return storages.get(0);
            }

        } catch (Exception ignored) {
        }


        final String SECONDARY_STORAGE = System.getenv("SECONDARY_STORAGE");
        if (SECONDARY_STORAGE != null) {
            return new File(SECONDARY_STORAGE.split(":")[0]);
        }

        return null;
    }


    public static boolean isMemoryCardExist(Context context){
        boolean isExist=false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<File> files = null;
            try {
                files = getRemovabeStorages(context);
                files.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (files.size() == 0) {
                isExist = false;
            } else if (files.size() == 1) {
                isExist = true;
            }
        }else {
            if( getRemovabeStorageDir(context).length()>0){
                isExist = true;
            }else {
                isExist = false;
            }
        }
        return isExist;
    }

    public static boolean displayMap(int cellID, int lac) throws Exception {

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String urlString = "http://www.google.com/glm/mmap";

        //---open a connection to Google Maps API---
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.connect();

        //---write some custom data to Google Maps API---
        OutputStream outputStream = httpConn.getOutputStream();
        WriteData(outputStream, cellID, lac);

        //---get the response---
        InputStream inputStream = httpConn.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        //---interpret the response obtained---
        dataInputStream.readShort();
        dataInputStream.readByte();
        int code = dataInputStream.readInt();
        if (code == 0) {
            double lat = (double) dataInputStream.readInt() / 1000000D;
            double lng = (double) dataInputStream.readInt() / 1000000D;
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readUTF();

            AppController_Patrika.Latitude_cell = lat;
            AppController_Patrika.Longitude_cell = lng;
            //---display Google Maps---
            String uriString = lat + "," + lng;
            Log.e("uriString  ", " " + uriString);
//		textViewCoord.setText(uriString);
            return true;
        } else {
            return false;
        }
    }

    private static void WriteData(OutputStream out, int cellID, int lac) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cellID);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }
}