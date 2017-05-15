package com.rajasthnapatrika_prod.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.service.Background_Uploading;
import com.rajasthnapatrika_prod.utils.AppUtills;


public class SplashActivity extends AppCompatActivity {

    private GoogleCloudMessaging gcm = null;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //    public static String regid = "";
    DBHelper dbHelper;
    int Media_id;
    String TAG = "SplashActivity";
    static final int REQUEST_LOCATION_SERVICE = 200;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        image = (ImageView) findViewById(R.id.image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            populateAutoComplete();
        } else {
            mComplete();
        }

    }

    void startService() {

        if (AppUtills.isNetworkAvailable(this)) {
            if (!AppUtills.isMyServiceRunning(Background_Uploading.class, SplashActivity.this)) {
                Intent intent = new Intent(SplashActivity.this, Background_Uploading.class);
                startService(intent);
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            Log.e("populateAutoComplete", "called if part");
            return;
        } else {
            mComplete();
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            mComplete();
            return true;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)
                && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                && shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
//                && shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NETWORK_STATE)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_WIFI_STATE)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                && shouldShowRequestPermissionRationale(Manifest.permission.WAKE_LOCK)
                && shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                && shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
//            Snackbar snackbar =
            Snackbar.make(image, "Please provide permission", Snackbar.LENGTH_INDEFINITE).setAction("UNDO", new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    requestPermissions(new String[]{Manifest.permission.INTERNET
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.CAMERA
//                            , Manifest.permission.GET_ACCOUNTS
                            , Manifest.permission.ACCESS_NETWORK_STATE
                            , Manifest.permission.ACCESS_WIFI_STATE
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WAKE_LOCK
                            , Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.SEND_SMS
                    }, REQUEST_LOCATION_SERVICE);
                }
            }).show();
            Toast.makeText(SplashActivity.this, "Please provide permission", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{
                            Manifest.permission.INTERNET
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.CAMERA
                            , Manifest.permission.GET_ACCOUNTS
                            , Manifest.permission.ACCESS_NETWORK_STATE
                            , Manifest.permission.ACCESS_WIFI_STATE
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WAKE_LOCK
                            , Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.SEND_SMS
                    }, REQUEST_LOCATION_SERVICE
            );

        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && mayRequestContacts()) {
                mComplete();
                Log.e(TAG, "called..grantResults  .  " + grantResults[0]);
            }
        }
    }

    private void mComplete() {
        try {


            dbHelper = new DBHelper(SplashActivity.this, null, null, 0);

//          if(!AppController_Patrika.getSharedPreferences().getBoolean("is_alarm_started",false)) {
//              Intent moveIntent = new Intent(SplashActivity.this, Service_to_get_Credentials.class);
//              moveIntent.putExtra("flag", "alarm");
//              startService(moveIntent);
//          }

            if (AppUtills.isNetworkAvailable(this)) {

                if (checkPlayServices()) {
                    AppUtills.registerGcmInBackground(SplashActivity.this);
                } else {
                    Toast.makeText(this, "no valid play services found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "not connected to internet", Toast.LENGTH_SHORT).show();
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!AppController_Patrika.getSharedPreferences().getBoolean("is_true", false)) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        startService();
                        Intent intent;
                        if (AppController_Patrika.getSharedPreferences().getString("pin", "").equals("")) {
                            intent = new Intent(SplashActivity.this, PinChange_Activity.class);
                            intent.putExtra("flag", "first");
                        } else {
                            intent = new Intent(SplashActivity.this, Drawer_Activity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }
            }, 3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
