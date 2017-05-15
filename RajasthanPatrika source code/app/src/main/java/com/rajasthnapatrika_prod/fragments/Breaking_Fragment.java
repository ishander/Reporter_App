package com.rajasthnapatrika_prod.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.activities.Drawer_Activity;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.utils.AppUtills;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by jgupta on 1/4/2016.
 */
public class Breaking_Fragment extends Fragment
{
    EditText et_heading, et_sub_heading;
    /* for record voice*/
    MyRecognitionListener listener;
    SpeechRecognizer sr;
    AlertDialog.Builder builder;
    String flag = "";
    String Nid = "";
    AppCompatActivity activity;
    String TAG = "Breaking_Fragment";
    String et_text = "";
    Button submit_btn;
    DBHelper dbHelper;
    private DBHandler dbHandler;
//    String Date_Time;
    ImageView img_heading_mic, img_sub_heading_mic;
    boolean flag_toast = false;
    TextInputLayout input_layout_heading, input_layout_subheading;
    ProgressDialog progressDialog;
    boolean is_password_verified = false;
    boolean isGPSDialogShown=false;
    String status = "";

    public Breaking_Fragment()
    {

    }
    @SuppressLint("ValidFragment")
    public Breaking_Fragment(AppCompatActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.breaking_news_layout, container, false);

        try {
        setHasOptionsMenu(true);
        dbHandler=new DBHandler();
        dbHelper=new DBHelper(getActivity(), null, null, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

        sr = SpeechRecognizer.createSpeechRecognizer(activity);
        listener = new MyRecognitionListener();
        sr.setRecognitionListener(listener);
        builder = new AlertDialog.Builder(activity);

        et_heading = (EditText) view.findViewById(R.id.et_heading);
        et_sub_heading = (EditText) view.findViewById(R.id.et_sub_heading);

        input_layout_heading = (TextInputLayout) view.findViewById(R.id.input_layout_heading);
        input_layout_subheading = (TextInputLayout) view.findViewById(R.id.input_layout_subheading);

        et_heading.addTextChangedListener(AppUtills.removeTextWatcher(et_heading, input_layout_heading));
        et_sub_heading.addTextChangedListener(AppUtills.removeTextWatcher(et_sub_heading, input_layout_subheading));

        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        img_heading_mic = (ImageView) view.findViewById(R.id.img_heading_mic);
        img_sub_heading_mic = (ImageView) view.findViewById(R.id.img_sub_heading_mic);

        onBackPress(et_heading);
        onBackPress(et_sub_heading);

            et_heading.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (et_heading.getRight() - et_heading.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            showToast();
                            Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                            intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                            sr.startListening(intent);
                            et_text = "heading";
                            return false;
                        }
                    }
                    return false;
                }
            });


            et_sub_heading.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (et_sub_heading.getRight() - et_sub_heading.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                                    showToast();
                                    Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                                    intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                                    sr.startListening(intent);
                                    et_text = "sub_heading";
                            return false;
                        }
                    }
                    return false;
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!et_heading.getText().toString().trim().equals(""))
                    {
                        if (!et_sub_heading.getText().toString().trim().equals(""))
                        {
                            verifyPassword();
                        } else {
                            input_layout_subheading.setError("Please enter Sub Heading");
                        }
                    } else {
                        input_layout_heading.setError("Please enter Heading");
                    }
                }
            });

            img_heading_mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    showToast();
                    Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                        intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                        sr.startListening(intent);
                        et_text = "heading";
                }
            });


            img_sub_heading_mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    showToast();
                    Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                    intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                    sr.startListening(intent);
                    et_text = "sub_heading";
                }
            });
        }catch(Exception e)
        {
            e.printStackTrace();
        }



        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.add_story,menu);

        MenuItem draft = menu.findItem(R.id.draft);
        MenuItem photo = menu.findItem(R.id.photo);
        MenuItem video = menu.findItem(R.id.video);
        MenuItem audio = menu.findItem(R.id.audio);

        photo.setVisible(false);
        video.setVisible(false);
        audio.setVisible(false);
    }

    void showToast()
    {
        if(AppUtills.isNetworkAvailable(getActivity()))
        {
            Toast toast = Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.please_speak_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else
            Toast.makeText(getActivity(), ""+getResources().getString(R.string.txt_network_error), Toast.LENGTH_SHORT).show();
    }

    public void onBackPress(final EditText editText)
    {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    Log.e(TAG,"KeyEvent.ACTION_UP if called");
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.e(TAG,"KeyEvent.KEYCODE_BACK called");

                        flag_toast = true;
                        boolean flag_check = false;
                        flag = "draft";
                        if (!et_heading.getText().toString().trim().equals("")) {
                            if (!et_sub_heading.getText().toString().trim().equals("")) {

                                flag_check = true;
                                insertIntoDb("old");
                            }else
                            {
                                flag_check = false;
                            }
                        }

                        if(!flag_check)
                        {
                            if (!et_heading.getText().toString().trim().equals("") || !et_sub_heading.getText().toString().trim().equals("")){
                                openDialog_to_notify_about_data();
                                return true;
                            }
                            else{
                                startActivity_Main();
                                return true;
                            }
                        }

                        return false;
                    }
                }else
                    Log.e(TAG,"KeyEvent.ACTION_UP else called");
                return false;
            }
        });
    }

    public void insertIntoDb(String Status_process)
    {
            dbHandler = new DBHandler();
//        if(Add_Stories_Activity.page_info.equals("")) {
            if (flag.equals("draft"))
                dbHandler.Status = AppController_Patrika.Story_Status_draft;
            else if(flag.equals("process"))
                dbHandler.Status = AppController_Patrika.Story_Status_process;
            else if(flag.equals("outbox"))
                dbHandler.Status = AppController_Patrika.Story_Status_outbox;
            else if(flag.equals("uploaded"))
                dbHandler.Status = AppController_Patrika.Story_Status_Uploaded;
//        }else
//            dbHandler.Status = status;

            dbHandler.Status_process = Status_process;
            dbHandler.Heading = "" + et_heading.getText().toString().trim();
            dbHandler.Sub_heading = "" + et_sub_heading.getText().toString().trim();
            dbHandler.Domain = "";
            dbHandler.Category = "";
            dbHandler.Scope = "";
            dbHandler.Priority = "";
            dbHandler.Personality_topic = "";
            dbHandler.Body = "";
            dbHandler.Introduction = "";
            dbHandler.Story_Type = AppController_Patrika.Story_Type_Breaking;
            dbHandler.Time = "" + AppUtills.getDate_n_Time();
            dbHandler.PlaceName = "" + AppController_Patrika.PlaceName;
            dbHandler.Nid = Nid;
            dbHandler.Thumbnail = "";
            dbHandler.MiliSec = System.currentTimeMillis();
            dbHandler.publish_status_print = "0";
            dbHandler.publish_status_tv = "0";
            dbHandler.publish_status_web = "0";
        if(AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
            dbHandler.latitute = "" + AppController_Patrika.Latitude;
            dbHandler.longitute = "" + AppController_Patrika.Longitude;
        }else  {
            dbHandler.latitute = "" + AppController_Patrika.Latitude_cell;
            dbHandler.longitute = "" + AppController_Patrika.Longitude_cell;
        }

            dbHelper.insertDataInAdd_News(dbHandler,"");


        try {
            if (!flag_toast)
            {
                if (flag.equals("draft"))
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.save_draft_txt), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), Drawer_Activity.class);
                intent.putExtra("page_info", "breaking");
                intent.putExtra("tab_position", "1");
                startActivity(intent);
                getActivity().finish();
             }else
            {
                startActivity_Main();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                flag_toast = true;
                boolean flag_check = false;
                if (!et_heading.getText().toString().trim().equals("")) {
                    if (!et_sub_heading.getText().toString().trim().equals("")) {
                        flag = "draft";
                        flag_check = true;
                        insertIntoDb("old");
                    }
                }
                if(!flag_check)
                {
                    if (!et_heading.getText().toString().trim().equals("") || !et_sub_heading.getText().toString().trim().equals(""))
                        openDialog_to_notify_about_data();
                    else
                        startActivity_Main();
                }

                break;
            case R.id.draft:
                if (!et_heading.getText().toString().trim().equals(""))
                {
                    if (!et_sub_heading.getText().toString().trim().equals(""))
                    {
                        flag = "draft";
                        status = AppController_Patrika.Story_Status_draft;
                        insertIntoDb("old");
                    } else {
                        input_layout_subheading.setError("Please enter Sub Heading");
                    }
                } else {
                    input_layout_heading.setError("Please enter Heading");
                }
                break;
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    void startActivity_Main()
    {
        Intent intent = new Intent(getActivity(),Drawer_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        getActivity().finish();
    }

    class MyRecognitionListener
            implements RecognitionListener
    {

        public void onBeginningOfSpeech()
        {
        }

        public void onBufferReceived(byte abyte0[])
        {
        }

        public void onEndOfSpeech()
        {
        }

        public void onError(int i)
        {
        }

        public void onEvent(int i, Bundle bundle)
        {
        }

        public void onPartialResults(Bundle bundle)
        {
        }

        public void onReadyForSpeech(Bundle bundle)
        {
        }

        public void onResults(Bundle bundle)
        {
            final ArrayList<String> list = bundle.getStringArrayList("results_recognition");
            CharSequence acharsequence[] = (CharSequence[])list.toArray(new CharSequence[list.size()]);
            builder.setTitle("Choose one:");

            builder.setItems(acharsequence,   new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    if(!et_text.equals(""))
                    {
                        if(et_text.equals("heading"))
                            et_heading.append((new StringBuilder(String.valueOf(((String)list.get(which)).toString()))).append(" ").toString());
                            else if(et_text.equals("sub_heading"))
                                et_sub_heading.append((new StringBuilder(String.valueOf(((String)list.get(which)).toString()))).append(" ").toString());

                    }
                }
            });
            builder.show();
        }

        public void onRmsChanged(float f)
        {
        }
    }

    public class excute_send_Breaking_News_Async extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids)
        {

            String Url;
            String response = "";
            try {
            if(Nid.equals(""))
                Url = AppController_Patrika.content_create_api;
            else
                Url = AppController_Patrika.content_create_api+"/"+Nid;

            if(AppUtills.showLogs)Log.e("Url is","Url is  "+Url);
                if(AppUtills.showLogs)Log.e("Url is","Url is  "+et_heading.getText().toString().trim());

                String  new_heading = et_heading.getText().toString().trim().replaceAll("\"","&quote;");
                String  new_sub_heading = et_sub_heading.getText().toString().trim().replaceAll("\"","&quote;");

                Double lat = 0.0 , longi = 0.0;
                if(AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
                    lat =  AppController_Patrika.Latitude;
                    longi = AppController_Patrika.Longitude;
                }else  {
                    lat =  AppController_Patrika.Latitude_cell;
                    longi = AppController_Patrika.Longitude_cell;
                }

                String  sn =  " { \"title\":\""+new_heading+"\",\"type\":\""+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type","")+"\",\"language\":\"und\"" +
                        ",\"field_ccms_sub_heading\":{\"und\":[{ \"value\":\""+new_sub_heading+"\"}]}" +
                        ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"1\"}]}" +
                        ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\""+lat+"\",\"lon\":\""+longi+"\"}}]}" +
                        ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\""+AppUtills.getDate_n_Time()+"\"}}]}" +
                        "}";

                JSONObject jsonObject = new JSONObject(sn.trim());
                Log.e(TAG,"jsonObject  "+jsonObject.toString(4));
                StringEntity se = new StringEntity(jsonObject.toString().trim(),HTTP.UTF_8);

                Log.v(TAG,"json is  "+jsonObject.toString());

            if(AppUtills.showLogs)Log.e("Breaking_Fragment","entity is  "+se.toString());
                if(AppUtills.showLogs)Log.e("Breaking_Fragment","Latitude is  "+lat);
                if(AppUtills.showLogs)Log.e("Breaking_Fragment","Latitude is  "+longi);

                if(Nid.equals("")) {
                HttpPost httppost = new HttpPost(Url);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                httppost.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                if(AppUtills.showLogs)Log.e("HttpPost is","HttpPost is called "+Url);
                httppost.setEntity(se);

                response = AppUtills.getDecodedResponse(httppost);
            }
            else if(!Nid.equals(""))
                {
                HttpPut httpPut = new HttpPut(Url);
                httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httpPut.setHeader("Accept", "application/json");
                httpPut.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                httpPut.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                httpPut.setEntity(se);

                if(AppUtills.showLogs)Log.e("HttpPost is","HttpPost is called "+Url);
                response = AppUtills.getDecodedResponse_Update1(httpPut);
            }

            if(AppUtills.showLogs)Log.v("response", ""+response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s)
        {
            progressDialog.dismiss();

            if(AppUtills.showLogs)Log.v("onPost","onPOst  "+s);

            JSONObject jsonObject = null;

            try
            {
                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONObject) {
                    jsonObject = new JSONObject(s);

                    Nid = jsonObject.getString("nid");
                    flag = "uploaded";
                    status = AppController_Patrika.Story_Status_Uploaded;
                    insertIntoDb("old");

                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.submit_news_txt), Toast.LENGTH_SHORT).show();
                }else
                    AppUtills.alertDialog(getActivity(),s);

            }catch (Exception e)
            {
                if(s!= null && !s.equals(""))
                    AppUtills.alertDialog(getActivity(), s);
                else {
                    if(!AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number","").equalsIgnoreCase("0"))
                        openDialog();
                    else {
                        flag = "outbox";
                        status = AppController_Patrika.Story_Status_outbox;
                        insertIntoDb("new");
                        Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_internet_saved_to_outbox), Toast.LENGTH_LONG).show();
                    }
                }
                e.printStackTrace();
            }
        }
    }

    void openDialog()
    {
        final Dialog dialog = new Dialog(getActivity(), R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_two);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView dialog4all_txt = (TextView) dialog
                .findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(getResources().getString(R.string.send_sms_text));
        dialog.findViewById(R.id.btn_Dialog_no).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        flag = "outbox";
                        status = AppController_Patrika.Story_Status_outbox;
                        insertIntoDb("new");
                    }
                });

        dialog.findViewById(R.id.btn_Dialog_yes).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        flag = "outbox";
                            status = AppController_Patrika.Story_Status_outbox;
                        insertIntoDb("new");
                        String phoneNo = ""+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number","");
                        String message = "H:"+et_heading.getText().toString()+" SH:"+et_sub_heading.getText().toString();

                        Log.e(TAG,"message to be sent is  "+message);
                        Log.e(TAG,"phoneNo to be sent is  "+phoneNo);
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> parts = smsManager.divideMessage(message);
                            for(int i=0;i<parts.size();i++)
                            {
                                smsManager.sendTextMessage(phoneNo, null, parts.get(i), null, null);
                            }
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.sms_sent_txt), Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.sms_failed_txt), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    void verifyPassword()
    {
        final Dialog dialog = new Dialog(getActivity(), R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_password);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        final EditText txt_password = (EditText) dialog.findViewById(R.id.txt_password);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        txt_password.setFilters(FilterArray);
        txt_password.setHint(getActivity().getResources().getString(R.string.hint_pin_name));
        final TextInputLayout input_layout_pass = (TextInputLayout) dialog.findViewById(R.id.input_layout_pass);
        txt_password.addTextChangedListener(AppUtills.removeTextWatcher(txt_password, input_layout_pass));
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        is_password_verified = false;
                    }
                });

        dialog.findViewById(R.id.btn_submit).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        if(txt_password.getText().toString().trim().length()>0)
                        {
                            if (AppController_Patrika.getSharedPreferences().getString("pin","").equals(txt_password.getText().toString().trim()))
                            {
                                is_password_verified = true;
                                dialog.dismiss();
                                if (AppUtills.isNetworkAvailable(getActivity())) {
                                    new excute_send_Breaking_News_Async().execute();
                                }
                                else {
                                    if(!AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number","").equalsIgnoreCase("0"))
                                        openDialog();
                                    else {
                                        flag = "outbox";
                                        status = AppController_Patrika.Story_Status_outbox;
                                        insertIntoDb("new");
                                        Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_internet_saved_to_outbox), Toast.LENGTH_LONG).show();
                                    }
                                }
                                if(AppUtills.showLogs)Log.v(TAG,"password on submit is  "+AppController_Patrika.getSharedPreferences().getString("pass", ""));
                            }else {
                                is_password_verified = false;
                                input_layout_pass.setError(getResources().getString(R.string.hint_correct_password));
                                if(AppUtills.showLogs)Log.v(TAG,"password on submit is  "+AppController_Patrika.getSharedPreferences().getString("pass", ""));
                            }
                        }else {
                            is_password_verified = false;

                        }input_layout_pass.setError(getResources().getString(R.string.hint_pin_name));
                    }
                });
        dialog.show();

        txt_password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    if(s.length()>0) {
                        txt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        txt_password.setSelection(txt_password.getText().length());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,	int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }


    void openDialog_to_notify_about_data()
    {
        final Dialog dialog = new Dialog(getActivity(), R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_two);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView dialog4all_txt = (TextView) dialog
                .findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText("क्या आप ड्राफ्ट में सेव करना चाहते है ? यदि हाँ तो कृपया आवश्यक डेटा भरें.");
        dialog.findViewById(R.id.btn_Dialog_no).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        startActivity_Main();
                    }
                });

        dialog.findViewById(R.id.btn_Dialog_yes).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }
    }
