package com.rajasthnapatrika_prod.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.rajasthnapatrika_prod.BuildConfig;
import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.service.Service_to_get_Credentials;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    TextInputLayout input_layout_username, input_layout_password;
    EditText et_username, et_password;
    CheckBox check_remember_me;
    Button btn_login;
    String device_id = "", device_name;
    ProgressDialog progressDialog;
    private DBHandler dbHandler;
    private DBHelper dbHelper;
    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        try {
            if (AppController_Patrika.getSharedPreferences().getString("reg_id", "").equals("")) {
                if (AppUtills.isNetworkAvailable(LoginActivity.this))
                    AppUtills.registerGcmInBackground(LoginActivity.this);
            }

            try {

                dbHandler = new DBHandler();
                dbHelper = new DBHelper(LoginActivity.this, null, null, 0);

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

                input_layout_username = (TextInputLayout) findViewById(R.id.input_layout_username);
                input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);

                et_username = (EditText) findViewById(R.id.et_username);
                et_password = (EditText) findViewById(R.id.et_password);

                check_remember_me = (CheckBox) findViewById(R.id.check_remember_me);
                btn_login = (Button) findViewById(R.id.btn_login);

                if (AppUtills.showLogs)
                    Log.v("user_name", "user_name " + AppController_Patrika.getSharedPreferences_remember_me().getString("user_name", ""));
                if (AppUtills.showLogs)
                    Log.v("password", "password " + AppController_Patrika.getSharedPreferences_remember_me().getString("password", ""));

                if (!AppController_Patrika.getSharedPreferences_remember_me().getString("user_name", "").equals("") &&
                        !AppController_Patrika.getSharedPreferences_remember_me().getString("password", "").equals("")) {
                    if (AppUtills.showLogs) Log.v("data found", "data found ");
                    check_remember_me.setChecked(true);
                    et_username.setText("" + AppController_Patrika.getSharedPreferences_remember_me().getString("user_name", ""));
                    et_password.setText("" + AppController_Patrika.getSharedPreferences_remember_me().getString("password", ""));
                }

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                device_id = tm.getDeviceId();
                device_name = Build.BRAND + Build.MODEL;

                if (AppUtills.showLogs) Log.v("device_id", "device_id  " + device_id);
                if (AppUtills.showLogs)
                    Log.v("SplashActivity regid", "SplashActivity regid  " + AppController_Patrika.getSharedPreferences().getString("reg_id", ""));
                if (AppUtills.showLogs) Log.v("device_name", "device_name  " + device_name);

                et_username.addTextChangedListener(removeTextWatcher(et_username, input_layout_username));
                et_password.addTextChangedListener(removeTextWatcher(et_password, input_layout_password));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            try {
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                        if (!et_username.getText().toString().trim().equals("")) {
                            if (!et_password.getText().toString().trim().equals("")) {

                                if (AppUtills.isNetworkAvailable(LoginActivity.this))
                                    new excuteLoginAsync().execute();
                                else
                                    AppUtills.alertDialog(LoginActivity.this, getResources().getString(R.string.txt_network_error));
                            } else {
                                input_layout_password.setError("password must be filled");
                            }
                        } else {
                            input_layout_username.setError("username must be filled");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                check_remember_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (AppUtills.showLogs) Log.v("checked is", "checked is  " + b);

                        if (b) {
                            AppController_Patrika.getSharedPreferences_remember_me().edit().putString("user_name", "" + et_username.getText().toString().trim())
                                    .putString("password", "" + et_password.getText().toString().trim())
                                    .commit();
                        } else if (!b) {
                            AppController_Patrika.getSharedPreferences_remember_me().edit().putString("user_name", "")
                                    .putString("password", "")
                                    .commit();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TextWatcher removeTextWatcher(final EditText editText, final TextInputLayout textInputLayout) {
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

    public class excuteLoginAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            BufferedReader in = null;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            if (AppUtills.showLogs)
                Log.v("et_username", "" + et_username.getText().toString().trim());
            if (AppUtills.showLogs)
                Log.v("et_password", "" + et_password.getText().toString().trim());
            if (AppUtills.showLogs) Log.v("device_id", "" + device_id);
            if (AppUtills.showLogs) Log.v("device_name", "" + device_name);
            if (AppUtills.showLogs)
                Log.v("reg_id", "" + AppController_Patrika.getSharedPreferences().getString("reg_id", ""));

            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;

            String versionName = BuildConfig.VERSION_NAME;

            if (AppUtills.showLogs) Log.v(TAG, "versionName " + versionName);
            if (AppUtills.showLogs) Log.v("device_name", "android - " + release + "-" + sdkVersion);

            if (AppController_Patrika.getSharedPreferences().getString("reg_id", "").equals(""))
                AppUtills.registerGcmInBackground(LoginActivity.this);

            nameValuePairs.add(new BasicNameValuePair("username", "" + et_username.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("password", "" + et_password.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("imei", "" + device_id));
            nameValuePairs.add(new BasicNameValuePair("device", "" + device_name));
            nameValuePairs.add(new BasicNameValuePair("os", "android - " + release + "-" + sdkVersion));
            nameValuePairs.add(new BasicNameValuePair("regid", "" + AppController_Patrika.getSharedPreferences().getString("reg_id", "")));
            nameValuePairs.add(new BasicNameValuePair("build", "" + versionName));

            if (AppUtills.showLogs) Log.v("nameValuePairs", "" + nameValuePairs);

            HttpPost httppost = new HttpPost(AppController_Patrika.content_login_api);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");

            String response = "";

            try {
                response = AppUtills.getDecodedResponse(httppost, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AppUtills.showLogs) Log.v(TAG, "excuteLoginAsync doinbackground" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (check_remember_me.isChecked()) {
                AppController_Patrika.getSharedPreferences_remember_me().edit().putString("user_name", "" + et_username.getText().toString().trim())
                        .putString("password", "" + et_password.getText().toString().trim())
                        .commit();
            }


            if (AppUtills.showLogs) Log.v(TAG, "excuteLoginAsync onPOst  " + s);

            JSONObject jsonObject = null;

            try {
                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONObject) {
                    jsonObject = new JSONObject(s);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        if (AppUtills.showLogs)


                            Log.v("status is", "status is  " + jsonObject.getString("status"));
                        AppController_Patrika.getSharedPreferences().edit()
                                .putString("sessid", "" + jsonObject.getString("sessid"))
                                .putString("session_name", "" + jsonObject.getString("session_name"))
                                .putString("token", "" + jsonObject.getString("token"))
                                .putString("userid", "" + jsonObject.getString("userid"))
                                .putString("name", "" + jsonObject.getString("name"))
                                .putString("pass", "" + et_password.getText().toString().trim())
                                .putBoolean("is_true", true)
                                .commit();

                        AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("password", et_password.getText().toString().trim()).commit();

                        Log.v("token at login", "token login   " + jsonObject.getString("token"));
                        Log.v("token at login", "token pref   " + AppController_Patrika.getSharedPreferences().getString("token", ""));

                        startService(new Intent(LoginActivity.this, Service_to_get_Credentials.class).putExtra("flag", "splash"));

                        Intent intent;
                        if (AppController_Patrika.getSharedPreferences().getString("pin", "").equals("")) {
                            intent = new Intent(LoginActivity.this, PinChange_Activity.class);
                            intent.putExtra("flag", "first");
                            startActivity(intent);
                            finish();
                        } else {
                            intent = new Intent(LoginActivity.this, Drawer_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    if (AppUtills.showLogs) Log.e("onPost", "jsonObject..........  " + jsonObject);
                } else if (json instanceof JSONArray) {
                    AppUtills.alertDialog(LoginActivity.this, s);
                } else {
                    AppUtills.alertDialog(LoginActivity.this, s);
                }


            } catch (Exception e) {
                if (s != null && !s.equals(""))
                    AppUtills.alertDialog(LoginActivity.this, s);
                else
                    AppUtills.alertDialog(LoginActivity.this, getResources().getString(R.string.txt_network_error));
                e.printStackTrace();
            }
        }
    }
}
