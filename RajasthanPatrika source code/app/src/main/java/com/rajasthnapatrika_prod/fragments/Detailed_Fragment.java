package com.rajasthnapatrika_prod.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.Add_Stories_Activity;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.activities.Drawer_Activity;
import com.rajasthnapatrika_prod.database.DBHandler;
import com.rajasthnapatrika_prod.database.DBHelper;
import com.rajasthnapatrika_prod.pick.CustomGallery;
import com.rajasthnapatrika_prod.pick.GalleryAdapter;
import com.rajasthnapatrika_prod.service.Background_Uploading;
import com.rajasthnapatrika_prod.utils.AppUtills;
import com.rajasthnapatrika_prod.utils.HorizontalListView;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by jgupta on 1/4/2016.
 */
public class Detailed_Fragment extends Fragment {
    AppCompatActivity activity;

    public Detailed_Fragment() {

    }
    @SuppressLint("ValidFragment")
    public Detailed_Fragment(AppCompatActivity activity) {
        this.activity = activity;
    }

    /* for record voice*/
    CustomArrayAdapter customArrayAdapter;
    HorizontalListView listview;
    boolean flag_thumbnail = false;
    MediaRecorder myAudioRecorder;
    String category_value[];
    String scope_value[];
    String priority_value[];
    String domain_value[];
    double startTime = 0;
    double finalTime = 0;
    long record_Id = 0;
    String Nid = "";
    MyRecognitionListener listener;
    SpeechRecognizer sr;
    AlertDialog.Builder builder;
    String et_text = "";
    EditText et_heading, et_sub_heading, et_intro, et_body, et_personality_topic;
    TextInputLayout input_layout_heading, input_layout_subheading, input_layout_intro, input_layout_body, input_personality_topic,
            input_domain, input_category, input_scope, input_priorty;
    ScrollView scollview;
    LinearLayout ll_photo, ll_video, ll_audio, ll;
    ImageView img_photo, img_video, img_audio;
    ImageView img_personality_topic_mic, img_heading_mic, img_sub_heading_mic, img_intro_mic, img_body_mic;
    TextView txt_photo, txt_video, txt_audio;
    Button submit_btn;
    GalleryAdapter adapter;
    EditText et_domain, et_category, et_scope, et_priorty;
    private DBHandler dbHandler;
    private DBHelper dbHelper;
    final static String TAG = "Detailed_Fragment";
    boolean flag_toast = false,is_called = false;
    String flag = "";
    /* for capture image from camera and gallary */
    static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static final int GALLERY_CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 300;
    Uri fileUri; // file url to store image.
    static final int MEDIA_TYPE_IMAGE = 1;
    static final int MEDIA_TYPE_Thumbnail = 4;
    int oneTimeOnly = 0;
    //    TextView txt_increase_img;
    public static ArrayList<HashMap<String,String>> Image_list = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> Audio_list = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> Video_list = new ArrayList<>();
    boolean img_flag = false, vid_flag = false, aud_flag = false;
    String Image = "Image", Audio = "Audio", Video = "Video";
    ProgressDialog progressDialog;
    String UI_Control = "photo";
    private static final int SELECT_AUDIO = 3;
    private static final int SELECT_VIDEO = 2;
    String status = AppController_Patrika.Story_Status_draft,Thumbnail = "",Db_Contains_db = "";
    /*video string*/
    public static final int MEDIA_TYPE_VIDEO = 2;
    long MiliSec = 0;
     int Img_size = 0;
     int Vid_size = 0;
     int Aud_size = 0;

    MenuItem photo;
    MenuItem video;
    MenuItem audio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.detailed_fragment_layout, container, false);
        try {
            setHasOptionsMenu(true);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.txt_please_wait));

            dbHandler = new DBHandler();
            dbHelper = new DBHelper(getActivity(), null, null, 0);

            domain_value = dbHelper.getAll_Drop_Downs_values("domain");
            category_value = dbHelper.getAll_Drop_Downs_values("category");
            scope_value = dbHelper.getAll_Drop_Downs_values("scope");
            priority_value = dbHelper.getAll_Drop_Downs_values("priority");

            AppController_Patrika.paths.clear();
            Image_list.clear();
            Video_list.clear();
            Audio_list.clear();
            initializeResouces(view);
            setListener();

        /*to record voice*/
            sr = SpeechRecognizer.createSpeechRecognizer(activity);
            listener = new MyRecognitionListener();
            sr.setRecognitionListener(listener);
            builder = new AlertDialog.Builder(activity);
            customArrayAdapter = new CustomArrayAdapter();
            listview.setVisibility(View.GONE);
            /*As Intent received*/
            if (!Add_Stories_Activity.page_info.equals("")) {
                if (Add_Stories_Activity.page_info.equalsIgnoreCase(AppController_Patrika.Story_Type_Detailed)) {

                    HashMap<String, String> hashMap = dbHelper.getListSelected_news(Add_Stories_Activity.Id, Add_Stories_Activity.page_info,"");

                    Nid = hashMap.get("Nid");
                    if (AppUtills.showLogs) Log.e(TAG, " Add_Stories_Activity.Id is " +  Add_Stories_Activity.Id);

                    et_heading.setText(hashMap.get("Heading"));
                    et_sub_heading.setText(hashMap.get("Sub_heading"));
                    et_intro.setText(hashMap.get("Introduction"));
                    et_body.setText(hashMap.get("Body"));
                    et_personality_topic.setText(hashMap.get("Personality_topic"));
                    et_domain.setText(hashMap.get("Domain"));
                    et_category.setText(hashMap.get("Category"));
                    et_scope.setText(hashMap.get("Scope"));
                    et_priorty.setText(hashMap.get("Priority"));
                    status = hashMap.get("Status");

                    if(!hashMap.get("Thumbnail").equals(""))
                    {
                       if(status.equals(AppController_Patrika.Story_Status_Uploaded)) {
                           Db_Contains_db = hashMap.get("Thumbnail");
                       }
                        if(new File(hashMap.get("Thumbnail")).exists())
                            Thumbnail = hashMap.get("Thumbnail");
                        else
                            Thumbnail = "";
                    }

                    if (AppUtills.showLogs) Log.e(TAG, "HasgMap Nid is " + Nid);
                    if (AppUtills.showLogs) Log.e(TAG, "HasgMap status is " + status);
                    if (AppUtills.showLogs) Log.e(TAG, "HasgMap Thumbnail is " + Thumbnail);

                    if(AppUtills.showLogs)Log.e(TAG,"status local is "+status);
                    if(AppUtills.showLogs)Log.e(TAG,"AppController_Patrika status is "+AppController_Patrika.Story_Status_draft);
                    if (!status.equals(AppController_Patrika.Story_Status_draft))
                    {
                        et_heading.setEnabled(false);
                        et_sub_heading.setEnabled(false);
                        et_personality_topic.setEnabled(false);
                        et_domain.setEnabled(false);
                        et_category.setEnabled(false);
                        et_scope.setEnabled(false);
                        et_priorty.setEnabled(false);
                        et_intro.requestFocus();

                        img_heading_mic.setEnabled(false);
                        img_sub_heading_mic.setEnabled(false);
                        img_personality_topic_mic.setEnabled(false);
                    }
                    if (et_domain.getText().toString().trim().equals("")) {

                        if (!Nid.equals("")) {
                            Add_Stories_Activity.nid = AppController_Patrika.get_news_by_id + Nid;

                            if (AppUtills.isNetworkAvailable(getActivity()))
                                new excuteGet_Detailed_news_Async().execute();
                        }
                    }

                    ArrayList<HashMap<String, String>> list = dbHelper.getAll_Images(Add_Stories_Activity.Id, "");

                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, String> hm = new HashMap<>();

                        hm.put("Path", "" + list.get(i).get("Path"));
                        hm.put("Type", list.get(i).get("Type"));
                        hm.put("Id", list.get(i).get("Id"));
                        hm.put("Status",list.get(i).get("Status"));
                        hm.put("Status_process",list.get(i).get("Status_process"));

                        if (AppUtills.showLogs) Log.v(TAG, "image path is  " + list.get(i));

                         if(!list.get(i).get("Type").equals("Thumb"))
                                AppController_Patrika.paths.add(hm);

                        if (list.get(i).get("Type").equals("Image"))
                            Image_list.add(hm);
                        else if (list.get(i).get("Type").equals("Audio")) {
                            Audio_list.add(hm);
                        } else if (list.get(i).get("Type").equals("Video")) {
                            Video_list.add(hm);
                        }
                    }

                    if(AppController_Patrika.Story_Status_Uploaded.equals(status))
                    {
                            Img_size = Integer.parseInt(hashMap.get("Img_size"));
                            Vid_size = Integer.parseInt(hashMap.get("Vid_size"));
                             Aud_size = Integer.parseInt(hashMap.get("Aud_size"));

                        if(AppUtills.showLogs)Log.e(TAG,"insertIntoDb Img_size "+Img_size);
                        if(AppUtills.showLogs)Log.e(TAG,"insertIntoDb Vid_size "+Vid_size);
                        if(AppUtills.showLogs)Log.e(TAG,"insertIntoDb Aud_size "+Aud_size);
                    }

                    if (Image_list.size() > 0) {
                        listview.setVisibility(View.VISIBLE);
                        listview.setAdapter(customArrayAdapter);
                    }
                } else if (Add_Stories_Activity.page_info.equalsIgnoreCase(AppController_Patrika.Story_Type_Breaking)) {
                    HashMap<String, String> hashMap;

                    hashMap = dbHelper.getListSelected_news(Add_Stories_Activity.Id, Add_Stories_Activity.page_info,"");

                    Nid = hashMap.get("Nid");
                    status = hashMap.get("Status");
                    et_heading.setText(hashMap.get("Heading"));
                    et_sub_heading.setText(hashMap.get("Sub_heading"));

                    if (!status.equals(AppController_Patrika.Story_Status_draft)) {
                        et_heading.setEnabled(false);
                        et_sub_heading.setEnabled(false);

                        img_heading_mic.setEnabled(false);
                        img_sub_heading_mic.setEnabled(false);
                    }

                } else if (Add_Stories_Activity.page_info.equals("get_from_server")) {
                    Log.e(TAG, "get_from_server is type");
                    if (AppUtills.isNetworkAvailable(getActivity()))
                        new excuteGet_Detailed_news_Async().execute();
                }
            }
            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!et_domain.getText().toString().trim().equals("")) {
                        if (!et_category.getText().toString().trim().equals("")) {
                            if (!et_scope.getText().toString().trim().equals("")) {
                                if (!et_priorty.getText().toString().trim().equals("")) {
                                    if (!et_personality_topic.getText().toString().trim().equals("")) {
                                        if (!et_heading.getText().toString().trim().equals("")) {
                                            if (!et_sub_heading.getText().toString().trim().equals("")) {
                                                if (!et_body.getText().toString().trim().equals("")) {
                                                    verifyPassword();
                                                } else {
                                                    et_body.requestFocus();
                                                    input_layout_body.setError("Please enter body");
                                                }
                                            } else {
                                                et_sub_heading.requestFocus();
                                                input_layout_subheading.setError("Please enter Sub heading");
                                            }
                                        } else {
                                            et_heading.requestFocus();
                                            input_layout_heading.setError("Please enter heading");
                                        }
                                    } else {
                                        et_personality_topic.requestFocus();
                                        input_personality_topic.setError("Please enter personality/topic");
                                    }
                                } else {
                                    et_priorty.requestFocus();
                                    input_priorty.setError("Please select priority");
                                }
                            } else {
                                et_scope.requestFocus();
                                input_scope.setError("Please select scope");
                            }
                        } else {
                            et_category.requestFocus();
                            input_category.setError("Please select category");
                        }
                    } else {
                        et_domain.requestFocus();
                        input_domain.setError("Please select domain");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallary"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (!AppUtills.checkDeviceSupportCamera(getActivity())) {
                        AppUtills.alertDialog(activity, getResources().getString(R.string.error_camera));
                    } else {
                        captureCameraImage();
                    }
                } else if (items[item].equals("Choose from Gallary")) {
                    Intent i = new Intent(AppController_Patrika.ACTION_MULTIPLE_PICK_IMAGES);
                    startActivityForResult(i, GALLERY_CAPTURE_IMAGE_REQUEST_CODE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void captureCameraImage() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = AppUtills.getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "Patrika_app");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (AppUtills.showLogs) Log.v("Patrika_app ", "captureimage...." + fileUri);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {

                    if (AppUtills.showLogs) Log.v("fileUri.getPath()", "" + fileUri.getPath());
                    setCapturedImage(fileUri.getPath());
                }
            } else if (requestCode == SELECT_AUDIO) {
                Uri selectedImageUri = data.getData();
                Log.e("selectedImageUri", "selectedImageUri  " + selectedImageUri);

                Log.e("path", "path  " + selectedImageUri.getPath());

                if (!selectedImageUri.getPath().equals("")) {
                    if (new File(selectedImageUri.getPath()).exists()) {
                        AppUtills.scanFile(getActivity(),new File(selectedImageUri.getPath()).getAbsolutePath());
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("Path", "" + selectedImageUri.getPath());
                        hm.put("Type", Audio);
                        hm.put("Status", "queue");
                        hm.put("Status_process", "new");
                        AppController_Patrika.paths.add(hm);
                        Audio_list.add(hm);
                    } else {
                        Log.e("is not exist", "is not exist");

                        String path = _getRealPathFromURI(getActivity(), data.getData());

                        if (new File(path).exists()) {
                            AppUtills.scanFile(getActivity(),new File(path).getAbsolutePath());
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("Path", "" + path);
                            hm.put("Type", Audio);
                            hm.put("Status", "queue");
                            hm.put("Status_process", "new");
                            AppController_Patrika.paths.add(hm);
                            Audio_list.add(hm);
                        } else {
                            Log.e("is not exist", "is not exist");
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (Audio_list.size() > 0)
                {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);

            } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {

                    if (fileUri.getPath() != null) {
                        if (new File(fileUri.getPath()).exists()) {
                            AppUtills.scanFile(getActivity(),new File(fileUri.getPath()).getAbsolutePath());
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("Path", "" + fileUri.getPath());
                            hm.put("Type", Video);
                            hm.put("Status", "queue");
                            hm.put("Status_process", "new");
                            AppController_Patrika.paths.add(hm);
                            Video_list.add(hm);
                        } else
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT).show();
                    }

                    if (Video_list.size() > 0) {
                        listview.setVisibility(View.VISIBLE);
                        listview.setAdapter(customArrayAdapter);
                    } else
                        listview.setVisibility(View.GONE);
                }
            } else if (requestCode == GALLERY_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    String[] all_path = data.getStringArrayExtra("all_path");

                    for (int i= 0;i<Image_list.size();i++) {
                        if (new File(Image_list.get(i).get("Path")).exists())
                        {

                        } else
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT).show();
                    }

                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly before Image_list size is  "+Image_list.size());

                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly data size is  "+all_path.length);
                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly Image_list size is  "+Image_list.size());

                    if(Image_list.size()>0) {
                        listview.setVisibility(View.VISIBLE);
                        listview.setAdapter(customArrayAdapter);
                    }else
                        listview.setVisibility(View.GONE);

                        if(AppUtills.showLogs)Log.v("paths ", "paths size  " + AppController_Patrika.paths.size());
                }
            } else if (requestCode == SELECT_VIDEO) {
                if (resultCode == Activity.RESULT_OK) {
                    String[] all_path = data.getStringArrayExtra("all_path");

                    for (String string : all_path) {
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = string;

                        if (new File(item.sdcardPath).exists()) {
                            AppUtills.scanFile(getActivity(),new File(item.sdcardPath).getAbsolutePath());
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("Path", "" + item.sdcardPath);
                            hm.put("Type", Video);
                            hm.put("Status", "queue");
                            hm.put("Status_process", "new");
                            AppController_Patrika.paths.add(hm);
                        } else
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT).show();
                    }
                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly before Video_list size is  "+Video_list.size());

                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly data size is  "+all_path.length);
                    if(AppUtills.showLogs)Log.e(TAG,"onActivity Resuly Video_list size is  "+Video_list.size());

                    if(Video_list.size()>0) {
                        listview.setVisibility(View.VISIBLE);
                        listview.setAdapter(customArrayAdapter);
                    }else
                        listview.setVisibility(View.GONE);

                    if (AppUtills.showLogs)
                        Log.v("paths ", "paths size  " + AppController_Patrika.paths.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCapturedImage(String pathName) {
        try {
            if (!pathName.equals("")) {
                if (new File(pathName).exists()) {

                    AppUtills.scanFile(getActivity(),new File(pathName).getAbsolutePath());

                    if (AppUtills.showLogs) Log.d("setCapturedImage", "setCap" +
                            "turedImage called " + pathName);
                    View view1 = (View) this.activity.getLayoutInflater().inflate(R.layout.image_view_smail_to_add_layout, null);
                    ImageView img = (ImageView) view1.findViewById(R.id.img);
                    img.setImageBitmap(AppUtills.compressBitmapImage(pathName));
                    ll.addView(view1);

                    HashMap<String, String> hm = new HashMap<>();
                    hm.put("Path", "" + pathName);
                    hm.put("Type", Image);
                    hm.put("Status", "queue");
                    hm.put("Status_process", "new");
                    AppController_Patrika.paths.add(hm);
                    Image_list.add(hm);
                } else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT).show();
            }
            if(Image_list.size()>0) {
                listview.setVisibility(View.VISIBLE);
                listview.setAdapter(customArrayAdapter);
            }else
                listview.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniflate_add_Next_img() {
        try {
            if (UI_Control.equals("photo"))
                if (Image_list.size() < 10)
                    selectImage();
                else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.cant_share_more_image), Toast.LENGTH_SHORT).show();

            else if (UI_Control.equals("audio")) {
                final CharSequence[] items = {"Record Audio", "Choose from file"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Audio");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Record Audio")) {
                            final Dialog dialog1 = new Dialog(getActivity());
                            dialog1.setContentView(R.layout.audio_layout);
                            dialog1.setTitle("Record Audio");
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            Window window = dialog1.getWindow();
                            lp.copyFrom(window.getAttributes());
                            //This makes the dialog take up the full width
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            window.setAttributes(lp);

                            final Button done = (Button) dialog1.findViewById(R.id.btn_done);
                            final Button stop = (Button) dialog1.findViewById(R.id.btn_stop);
                            final Button record = (Button) dialog1.findViewById(R.id.btn_record);
                            done.setEnabled(false);
                            record.setEnabled(true);
                            fileUri = null;
                            record.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        fileUri = AppUtills.getOutputMediaFileUri(3, "Patrika_app");
                                        if (AppUtills.showLogs)
                                            record.setEnabled(false);
                                        done.setEnabled(true);

                                        Log.e(TAG, "uri is  " + fileUri.getPath());
                                        myAudioRecorder = new MediaRecorder();
                                        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                                        myAudioRecorder.setOutputFile(fileUri.getPath());
                                        myAudioRecorder.prepare();
                                        myAudioRecorder.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.recording_started), Toast.LENGTH_SHORT).show();
                                }
                            });

                            stop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        stop.setEnabled(false);
                                        record.setEnabled(true);
                                        myAudioRecorder.stop();
                                        myAudioRecorder.reset();
                                        myAudioRecorder.release();
                                        myAudioRecorder = null;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.audio_recorded), Toast.LENGTH_SHORT).show();
                                }
                            });

                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (fileUri != null) {
                                        record.setEnabled(true);
                                        done.setEnabled(false);
                                        myAudioRecorder.stop();
                                        myAudioRecorder.reset();
                                        myAudioRecorder.release();
                                        myAudioRecorder = null;

                                        if (new File(fileUri.getPath()).exists()) {
                                            AppUtills.scanFile(getActivity(),new File(fileUri.getPath()).getAbsolutePath());
                                            HashMap<String, String> hm = new HashMap<>();
                                            hm.put("Path", "" + fileUri.getPath());
                                            hm.put("Type", Audio);
                                            hm.put("Status", "queue");
                                            hm.put("Status_process", "new");
                                            AppController_Patrika.paths.add(hm);
                                            Audio_list.add(hm);
                                            if (Audio_list.size() > 0) {
                                                listview.setVisibility(View.VISIBLE);
                                                listview.setAdapter(customArrayAdapter);
                                            } else
                                                listview.setVisibility(View.GONE);
                                        } else
                                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_media_txt), Toast.LENGTH_SHORT);
                                    }
                                    dialog1.dismiss();
                                }
                            });
                            dialog1.show();
                        } else if (items[item].equals("Choose from file")) {
                            Intent intent = new Intent();
                            intent.setType("audio/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Audio"), SELECT_AUDIO);

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            } else if (UI_Control.equals("video")) {
                if (Video_list.size() < 20) {
                    final CharSequence[] items = {"Record Video", "Choose from file"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Add Video");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items[item].equals("Record Video")) {
                                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                                fileUri = AppUtills.getOutputMediaFileUri(MEDIA_TYPE_VIDEO, "Patrika_app");

                                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

                                startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);

                            } else if (items[item].equals("Choose from file")) {

                                Intent i = new Intent(AppController_Patrika.ACTION_MULTIPLE_PICK_VIDEOS);
                                startActivityForResult(i, SELECT_VIDEO);
                                if (!progressDialog.isShowing())
                                    progressDialog.show();

                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                } else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.cant_share_more_video), Toast.LENGTH_SHORT).show();
            }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPress(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (AppUtills.showLogs) Log.e("onBackPress called", "onBackPress called");

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        flag_toast = true;
                        flag = "draft";
                        boolean flag_check = false;
                        if (!et_domain.getText().toString().trim().equals("")) {
                            if (!et_category.getText().toString().trim().equals("")) {
                                if (!et_scope.getText().toString().trim().equals("")) {
                                    if (!et_priorty.getText().toString().trim().equals("")) {
                                        if (!et_personality_topic.getText().toString().trim().equals("")) {

                                            if (!et_heading.getText().toString().trim().equals("")) {
                                                if (!et_sub_heading.getText().toString().trim().equals("")) {
                                                    if (!et_body.getText().toString().trim().equals("")) {

                                                        flag_check = true;
                                                        if (!Add_Stories_Activity.page_info.equals("get_from_server"))
                                                            insertIntoDb("old");
                                                        else {
                                                            startActivity_Main();
                                                        }
                                                    }

                                                }

                                            }

                                        }

                                    }

                                }

                            }
                        }
                        if (!flag_check) {
                            if (!et_heading.getText().toString().trim().equals("") || !et_sub_heading.getText().toString().trim().equals("")
                                    || !et_body.getText().toString().trim().equals("") || !et_domain.getText().toString().trim().equals("")
                                    || !et_scope.getText().toString().trim().equals("") || !et_category.getText().toString().trim().equals("")
                                    || !et_priorty.getText().toString().trim().equals("") || !et_personality_topic.getText().toString().trim().equals("")) {
                                openDialog_to_notify_about_data();
                                return true;
                            }
                            else {
                                startActivity_Main();
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return false;
            }
        });
    }

    public void insertIntoDb(String Status_process) {
        if (AppUtills.showLogs) Log.e("insertIntoDb called", "insertIntoDb called");

        dbHandler = new DBHandler();

        if(!is_called)
        {
            if(Thumbnail.equals(""))
            {
                addThumbnail();

            }else
            {
                if(!status.equals(AppController_Patrika.Story_Status_Uploaded))
                {
                    if (Image_list.size() == 0 && Video_list.size() == 0)
                    {
                        Thumbnail = "";
                    }
                }
            }
            if(AppUtills.showLogs)Log.v(TAG,"Img_size before "+Img_size);
            if(AppUtills.showLogs)Log.v(TAG,"Vid_size before "+Vid_size);
            if(AppUtills.showLogs)Log.v(TAG,"Aud_size before "+Aud_size);
            prepare_media_list();

            if(AppUtills.showLogs)Log.v(TAG,"after prepare_media_list called  "+Thumbnail);
        }

        if (Add_Stories_Activity.page_info.equals("")) {
            if (flag.equals("draft"))
                dbHandler.Status = AppController_Patrika.Story_Status_draft;
            else if (flag.equals("process"))
                dbHandler.Status = AppController_Patrika.Story_Status_process;
            else if (flag.equals("outbox"))
                dbHandler.Status = AppController_Patrika.Story_Status_outbox;
            else if (flag.equals("uploaded"))
                dbHandler.Status = AppController_Patrika.Story_Status_Uploaded;
        } else
            dbHandler.Status = status;

        if (AppUtills.showLogs) Log.e(TAG, "NID before insert  " + Nid);

        dbHandler.Status_process = Status_process;
        dbHandler.Heading = "" + et_heading.getText().toString().trim();
        dbHandler.Sub_heading = "" + et_sub_heading.getText().toString().trim();
        dbHandler.Domain = "" + et_domain.getText().toString().trim();
        dbHandler.Category = "" + et_category.getText().toString().trim();
        dbHandler.Scope = "" + et_scope.getText().toString().trim();
        dbHandler.Priority = "" + et_priorty.getText().toString().trim();
        dbHandler.Personality_topic = "" + et_personality_topic.getText().toString().trim();
        dbHandler.Body = "" + et_body.getText().toString().trim();
        dbHandler.Introduction = "" + et_intro.getText().toString().trim();
        dbHandler.Story_Type = AppController_Patrika.Story_Type_Detailed;
        dbHandler.Time = "" + AppUtills.getDate_n_Time();
        dbHandler.PlaceName = "" + AppController_Patrika.PlaceName;
        dbHandler.Nid = Nid;
        if(MiliSec != 0)
            dbHandler.MiliSec = MiliSec;
        else
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

        dbHandler.Thumbnail = ""+Thumbnail;
        dbHandler.Img_size = ""+Img_size;
        dbHandler.Vid_size = ""+Vid_size;
        dbHandler.Aud_size = ""+Aud_size;


        if (!Add_Stories_Activity.page_info.equals("")
                && !Add_Stories_Activity.Id.equals("")) {
            record_Id = dbHelper.insertDataInAdd_News(dbHandler, Add_Stories_Activity.Id);
        } else
            record_Id = dbHelper.insertDataInAdd_News(dbHandler, "");

        if (!Add_Stories_Activity.page_info.equals("") && Add_Stories_Activity.page_info.equalsIgnoreCase("detailed")
                && !Add_Stories_Activity.Id.equals("")) {
            dbHelper.insertDataInAdd_image_tbl(AppController_Patrika.paths, Add_Stories_Activity.Id, -1, Nid);
        } else {
            dbHelper.insertDataInAdd_image_tbl(AppController_Patrika.paths, "", record_Id, Nid);
        }

        if (!flag_toast) {

            if (flag.equals("draft"))
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.save_draft_txt), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getActivity(), Drawer_Activity.class);
            intent.putExtra("tab_position", "1");
            intent.putExtra("page_info", "detail");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else
            startActivity_Main();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                flag_toast = true;
                flag = "draft";
                boolean flag_check = false;
                if (!et_domain.getText().toString().trim().equals("")) {
                    if (!et_category.getText().toString().trim().equals("")) {
                        if (!et_scope.getText().toString().trim().equals("")) {
                            if (!et_priorty.getText().toString().trim().equals("")) {
                                if (!et_personality_topic.getText().toString().trim().equals("")) {

                                    if (!et_heading.getText().toString().trim().equals("")) {
                                        if (!et_sub_heading.getText().toString().trim().equals("")) {
                                            if (!et_body.getText().toString().trim().equals("")) {

                                                flag_check = true;
                                                if (!Add_Stories_Activity.page_info.equals("get_from_server"))
                                                    insertIntoDb("old");
                                                else
                                                    startActivity_Main();
                                            }

                                        }

                                    }

                                }

                            }

                        }

                    }
                }
                if (!flag_check) {
                    if (!et_heading.getText().toString().trim().equals("") || !et_sub_heading.getText().toString().trim().equals("")
                            || !et_body.getText().toString().trim().equals("") || !et_domain.getText().toString().trim().equals("")
                            || !et_scope.getText().toString().trim().equals("") || !et_category.getText().toString().trim().equals("")
                            || !et_priorty.getText().toString().trim().equals("") || !et_personality_topic.getText().toString().trim().equals(""))
                        openDialog_to_notify_about_data();
                    else
                        startActivity_Main();
                }
                break;
            case R.id.draft:
                flag_toast = false;
                flag = "draft";
                if (!et_domain.getText().toString().trim().equals("")) {
                    if (!et_category.getText().toString().trim().equals("")) {
                        if (!et_scope.getText().toString().trim().equals("")) {
                            if (!et_priorty.getText().toString().trim().equals("")) {
                                if (!et_personality_topic.getText().toString().trim().equals("")) {

                                    if (!et_heading.getText().toString().trim().equals("")) {
                                        if (!et_sub_heading.getText().toString().trim().equals("")) {
                                            if (!et_body.getText().toString().trim().equals("")) {
                                                if (Add_Stories_Activity.page_info.equals("get_from_server"))
                                                    status = "draft";

                                                insertIntoDb("old");
                                            } else {
                                                et_body.requestFocus();
                                                input_layout_body.setError("Please enter body");
                                            }
                                        } else {
                                            et_sub_heading.requestFocus();
                                            input_layout_subheading.setError("Please enter Sub heading");
                                        }
                                    } else {
                                        et_heading.requestFocus();
                                        input_layout_heading.setError("Please enter heading");
                                    }
                                } else {
                                    et_personality_topic.requestFocus();
                                    input_personality_topic.setError("Please enter personality/topic");
                                }
                            } else {
                                et_priorty.requestFocus();
                                input_priorty.setError("Please select priority");
                            }
                        } else {
                            et_scope.requestFocus();
                            input_scope.setError("Please select scope");
                        }
                    } else {
                        et_category.requestFocus();
                        input_category.setError("Please select category");
                    }
                } else {
                    et_domain.requestFocus();
                    input_domain.setError("Please select domain");
                }
                break;
            case R.id.photo:
                photo.setIcon(R.drawable.camera_selected);
                video.setIcon(R.drawable.video_normal);
                audio.setIcon(R.drawable.audio_normal);

                UI_Control = "photo";
                if (Image_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();
                break;
            case R.id.video:
                photo.setIcon(R.drawable.camera_normal);
                video.setIcon(R.drawable.video_selected);
                audio.setIcon(R.drawable.audio_normal);

                UI_Control = "video";
                if (Video_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();
                break;
            case R.id.audio:
                photo.setIcon(R.drawable.camera_normal);
                video.setIcon(R.drawable.video_normal);
                audio.setIcon(R.drawable.audio_selected);

                UI_Control = "audio";
                if (Audio_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();
                break;
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    void startActivity_Main() {
        Intent intent = new Intent(getActivity(), Drawer_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    class MyRecognitionListener
            implements RecognitionListener {

        public void onBeginningOfSpeech() {
        }

        public void onBufferReceived(byte abyte0[]) {
        }

        public void onEndOfSpeech() {
        }

        public void onError(int i) {
        }

        public void onEvent(int i, Bundle bundle) {
        }

        public void onPartialResults(Bundle bundle) {
        }

        public void onReadyForSpeech(Bundle bundle) {
        }

        public void onResults(Bundle bundle) {
            final ArrayList<String> list = bundle.getStringArrayList("results_recognition");
            CharSequence acharsequence[] = (CharSequence[]) list.toArray(new CharSequence[list.size()]);
            builder.setTitle("Choose one:");

            Log.v("speech recognition", "speech recognition   " + list);

            builder.setItems(acharsequence, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    if (!et_text.equals("")) {
                        if (et_text.equals("heading"))
                            et_heading.append((new StringBuilder(String.valueOf(((String) list.get(which)).toString()))).append(" ").toString());
                        else if (et_text.equals("sub_heading"))
                            et_sub_heading.append((new StringBuilder(String.valueOf(((String) list.get(which)).toString()))).append(" ").toString());
                        else if (et_text.equals("pers_topic"))
                            et_personality_topic.append((new StringBuilder(String.valueOf(((String) list.get(which)).toString()))).append(" ").toString());
                        else if (et_text.equals("intro"))
                            et_intro.append((new StringBuilder(String.valueOf(((String) list.get(which)).toString()))).append(" ").toString());
                        else if (et_text.equals("body"))
                            et_body.append((new StringBuilder(String.valueOf(((String) list.get(which)).toString()))).append(" ").toString());

                    }
                }
            });
            builder.show();
        }

        public void onRmsChanged(float f) {
        }
    }

    public class excuteSend_Detailed_news_Async extends AsyncTask<Void,Void,String>
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

                if(AppUtills.showLogs)Log.e(TAG,"Url is  "+Url);

                if(Thumbnail.equals(""))
                {
                    addThumbnail();
                }else
                {
                    if(!status.equals(AppController_Patrika.Story_Status_Uploaded))
                    {
                        if (Image_list.size() == 0 && Video_list.size() == 0)
                        {
                            Thumbnail = "";
                        }
                    }
                }
                Log.v(TAG,"Img_size before "+Img_size);
                Log.v(TAG,"Vid_size before "+Vid_size);
                Log.v(TAG,"Aud_size before "+Aud_size);
                prepare_media_list();
                is_called = true;

                MiliSec = System.currentTimeMillis();
                String personality = et_personality_topic.getText().toString().trim();
                String intro = et_intro.getText().toString().trim();
                String body = et_body.getText().toString().trim();
                String heading = et_heading.getText().toString().trim();
                String subheading = et_sub_heading.getText().toString().trim();

                String new_heading = heading.replaceAll("\"","&quote;");
                String new_subheading = subheading.replaceAll("\"","&quote;");

                String new_personality = personality.replaceAll("\"","&quote;");
                String new_intro = intro.replaceAll("\"","&quote;");
                String new_body = body.replaceAll("\"","&quote;");

                Log.v(TAG,"new_personality  "+new_personality);

                String end = "}";
                String image_start = ",\"field_ccms_uploaded_images\":{\"und\":[";
                String img_end_part = "]}";

                String video_start = ",\"field_ccms_uploaded_videos\":{\"und\":[";
                String Audio_start = ",\"field_ccms_uploaded_audios\":{\"und\":[";
                String start = "";
                String complete_String = "";

                Double lat = 0.0 , longi = 0.0;
                if(AppController_Patrika.Latitude != 0.0 && AppController_Patrika.Longitude != 0.0) {
                    lat =  AppController_Patrika.Latitude;
                    longi = AppController_Patrika.Longitude;
                }else  {
                    lat =  AppController_Patrika.Latitude_cell;
                    longi = AppController_Patrika.Longitude_cell;
                }

                if(status.equals(AppController_Patrika.Story_Status_Uploaded)) {
                    if (!et_domain.isEnabled()) {
                        start =  "{ \"body\":{\"und\":[{ \"value\":\"" + new_body + "\"}]}" +
                                ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\"" + new_intro + "\"}]}" +
                                ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                                ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                                ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";


                    }else if(!et_heading.isEnabled() && et_domain.isEnabled())
                    {
                        start = " { \"type\":\"" + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type", "") + "\",\"language\":\"und\"" +
                                ",\"body\":{\"und\":[{ \"value\":\"" + new_body + "\"}]}" +
                                ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\"" + new_intro + "\"}]}" +
                                ",\"field_ccms_personality\":{\"und\":[{ \"value\":\"" + new_personality + "\"}]}" +
                                ",\"field_ccms_domain\":{\"und\":[{ \"value\":\"" + et_domain.getText().toString().trim() + "\"}]}" +
                                ",\"field_ccms_category\":{\"und\":[{ \"value\":\"" + et_category.getText().toString().trim() + "\"}]}" +
                                ",\"field_ccms_scope\":{\"und\":[{ \"value\":\"" + et_scope.getText().toString().trim() + "\"}]}" +
                                ",\"field_ccms_priority\":{\"und\":[{ \"value\":\"" + et_priorty.getText().toString().trim() + "\"}]}" +
                                ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                                ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                                ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";

                    }
                }else {
                    start = " { \"title\":\"" + new_heading + "\",\"type\":\"" + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("content_type", "") + "\",\"language\":\"und\"" +
                            ",\"body\":{\"und\":[{ \"value\":\"" + new_body + "\"}]}" +
                            ",\"field_ccms_sub_heading\":{\"und\":[{ \"value\":\"" + new_subheading + "\"}]}" +
                            ",\"field_ccms_introduction\":{\"und\":[{ \"value\":\"" + new_intro + "\"}]}" +
                            ",\"field_ccms_personality\":{\"und\":[{ \"value\":\"" + new_personality + "\"}]}" +
                            ",\"field_ccms_domain\":{\"und\":[{ \"value\":\"" + et_domain.getText().toString().trim() + "\"}]}" +
                            ",\"field_ccms_category\":{\"und\":[{ \"value\":\"" + et_category.getText().toString().trim() + "\"}]}" +
                            ",\"field_ccms_scope\":{\"und\":[{ \"value\":\"" + et_scope.getText().toString().trim() + "\"}]}" +
                            ",\"field_ccms_priority\":{\"und\":[{ \"value\":\"" + et_priorty.getText().toString().trim() + "\"}]}" +
                            ",\"field_ccms_content_place\":{\"und\":[{\"geom\":{ \"lat\":\"" + lat + "\",\"lon\":\"" + longi + "\"}}]}" +
                            ",\"field_ccms_content_timestamp\":{\"und\":[{\"value\":{ \"date\":\"" + AppUtills.getDate_n_Time() + "\"}}]}" +
                            ",\"field_ccms_breaking\":{\"und\":[{ \"value\":\"0\"}]}";
                }

                boolean img_size = false;
                boolean vid_size = false;
                boolean aud_size = false;

                if(Image_list.size()>0 && img_flag)
                    img_size = true;
                if(Video_list.size()>0 && vid_flag)
                    vid_size = true;
                if(Audio_list.size()>0 && aud_flag)
                    aud_size = true;

                if(img_size && vid_size && aud_size)
                    complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+MiliSec,Img_size)+img_end_part+video_start+AppUtills.create_json_format("video",Video_list,""+MiliSec,Vid_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+MiliSec,Aud_size)+img_end_part+end;
                else if(img_size && !vid_size && !aud_size)
                    complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+MiliSec,Img_size)+img_end_part+end;
                else  if(img_size && vid_size && !aud_size)
                    complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+MiliSec,Img_size)+img_end_part+video_start+AppUtills.create_json_format("video",Video_list,""+MiliSec,Vid_size)+img_end_part+end;
                else  if(img_size && !vid_size && aud_size)
                    complete_String =  start+image_start+AppUtills.create_json_format("image",Image_list,""+MiliSec,Img_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+MiliSec,Aud_size)+img_end_part+end;
                else  if(!img_size && !vid_size && !aud_size)
                    complete_String =  start+end;
                else  if(!img_size && vid_size && aud_size)
                    complete_String =  start+video_start+AppUtills.create_json_format("video",Video_list,""+MiliSec,Vid_size)+img_end_part+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+MiliSec,Aud_size)+img_end_part+end;
                else  if(!img_size && !vid_size && aud_size)
                    complete_String =  start+Audio_start+AppUtills.create_json_format("audio",Audio_list,""+MiliSec,Aud_size)+img_end_part+end;
                else  if(!img_size && vid_size && !aud_size)
                    complete_String =  start+video_start+AppUtills.create_json_format("video",Video_list,""+MiliSec,Vid_size)+img_end_part+end;

                if(AppUtills.showLogs)Log.e(TAG,"complete_String  "+complete_String);

                JSONObject jsonObject = new JSONObject(complete_String.trim());

                StringEntity se = new StringEntity(jsonObject.toString().trim(),HTTP.UTF_8);

                if (Nid.equals("")) {
                    HttpPost httppost = new HttpPost(Url);
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                    httppost.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                    httppost.setEntity(se);

                    response = AppUtills.getDecodedResponse(httppost);
                } else if (!Nid.equals("")) {

                    HttpPut httpPut = new HttpPut(Url);
                    httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpPut.setHeader("Accept", "application/json");
                    httpPut.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                    httpPut.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                    httpPut.setEntity(se);

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

            try
            {
                if(AppUtills.showLogs)Log.v("onPostExecute", "onPostExecute called  "+s);

                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject(s);

                    if (AppUtills.showLogs)
                        Log.e(TAG, "ftp_directory is  " + jsonObject.getString("field_ccms_ftp_directory"));
                    AppController_Patrika.getSharedPreferences_FTP_Credentials().edit().putString("directory_path", jsonObject.getString("field_ccms_ftp_directory")).commit();
                    if (AppUtills.showLogs)
                        Log.e(TAG, "ftp_directory is  " + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("directory_path", ""));

                    Nid = jsonObject.getString("nid");
                    Log.e(TAG, "onPostExecute AppController_Patrika.paths.size()  "+AppController_Patrika.paths.size());
                    if (img_flag || vid_flag || aud_flag) {
                        if (AppUtills.isNetworkAvailable(getActivity())) {
                            if (!AppUtills.isMyServiceRunning(Background_Uploading.class, getActivity())) {
                                flag = "process";
                                status = AppController_Patrika.Story_Status_process;
                                insertIntoDb("old");
                                if (AppUtills.showLogs)
                                    Log.e(TAG, "onPostExecute Service is not running");

                                getActivity().startService(new Intent(getActivity(), Background_Uploading.class));
                            } else {
                                Log.e(TAG, "onPostExecute Service is  running");
                                flag = "outbox";
                                status = AppController_Patrika.Story_Status_outbox;
                                insertIntoDb("new");
                            }
                        } else {
                            flag = "outbox";
                            status = AppController_Patrika.Story_Status_outbox;
                            insertIntoDb("new");
                        }
                    } else  {
                        flag = "uploaded";
                        status = AppController_Patrika.Story_Status_Uploaded;
                        insertIntoDb("old");
                    }

                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.submit_news_txt), Toast.LENGTH_SHORT).show();
                }else
                    AppUtills.alertDialog(getActivity(),s);
            }catch (Exception e)
            {
                if(s!= null && !s.equals(""))
                    AppUtills.alertDialog(getActivity(), s);
                else {
                    if (!AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number", "").equalsIgnoreCase("0"))
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

    void initializeResouces(View view) {
        listview = (HorizontalListView) view.findViewById(R.id.hlvSimpleList);
        ll_photo = (LinearLayout) view.findViewById(R.id.ll_photo);
        ll_video = (LinearLayout) view.findViewById(R.id.ll_video);
        ll_audio = (LinearLayout) view.findViewById(R.id.ll_audio);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        img_photo = (ImageView) view.findViewById(R.id.img_photo);
        img_video = (ImageView) view.findViewById(R.id.img_video);
        img_audio = (ImageView) view.findViewById(R.id.img_audio);
        txt_photo = (TextView) view.findViewById(R.id.txt_photo);
        txt_video = (TextView) view.findViewById(R.id.txt_video);
        txt_audio = (TextView) view.findViewById(R.id.txt_audio);

        et_heading = (EditText) view.findViewById(R.id.et_heading);
        et_sub_heading = (EditText) view.findViewById(R.id.et_sub_heading);
        et_intro = (EditText) view.findViewById(R.id.et_intro);
        et_body = (EditText) view.findViewById(R.id.et_body);
        et_personality_topic = (EditText) view.findViewById(R.id.et_personality_topic);

        et_domain = (EditText) view.findViewById(R.id.et_domain);
        et_category = (EditText) view.findViewById(R.id.et_category);
        et_scope = (EditText) view.findViewById(R.id.et_scope);
        et_priorty = (EditText) view.findViewById(R.id.et_priorty);

        input_layout_heading = (TextInputLayout) view.findViewById(R.id.input_layout_heading);
        input_layout_subheading = (TextInputLayout) view.findViewById(R.id.input_layout_subheading);
        input_layout_intro = (TextInputLayout) view.findViewById(R.id.input_layout_intro);
        input_layout_body = (TextInputLayout) view.findViewById(R.id.input_layout_body);
        input_personality_topic = (TextInputLayout) view.findViewById(R.id.input_personality_topic);
        input_domain = (TextInputLayout) view.findViewById(R.id.input_domain);
        input_category = (TextInputLayout) view.findViewById(R.id.input_category);
        input_scope = (TextInputLayout) view.findViewById(R.id.input_scope);
        input_priorty = (TextInputLayout) view.findViewById(R.id.input_priorty);

        et_heading.addTextChangedListener(AppUtills.removeTextWatcher(et_heading, input_layout_heading));
        et_sub_heading.addTextChangedListener(AppUtills.removeTextWatcher(et_sub_heading, input_layout_subheading));
        et_intro.addTextChangedListener(AppUtills.removeTextWatcher(et_intro, input_layout_intro));
        et_body.addTextChangedListener(AppUtills.removeTextWatcher(et_body, input_layout_body));
        et_personality_topic.addTextChangedListener(AppUtills.removeTextWatcher(et_personality_topic, input_personality_topic));
        et_category.addTextChangedListener(AppUtills.removeTextWatcher(et_category, input_category));
        et_scope.addTextChangedListener(AppUtills.removeTextWatcher(et_scope, input_scope));
        et_domain.addTextChangedListener(AppUtills.removeTextWatcher(et_domain, input_domain));
        et_priorty.addTextChangedListener(AppUtills.removeTextWatcher(et_priorty, input_priorty));


        scollview = (ScrollView) view.findViewById(R.id.scollview);


        img_personality_topic_mic = (ImageView) view.findViewById(R.id.img_personality_topic_mic);
        img_heading_mic = (ImageView) view.findViewById(R.id.img_heading_mic);
        img_sub_heading_mic = (ImageView) view.findViewById(R.id.img_sub_heading_mic);
        img_intro_mic = (ImageView) view.findViewById(R.id.img_intro_mic);
        img_body_mic = (ImageView) view.findViewById(R.id.img_body_mic);

        submit_btn = (Button) view.findViewById(R.id.submit_btn);

        img_photo.setImageResource(R.drawable.camera_selected);
        txt_photo.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    void setListener() {
        ll_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_photo.setImageResource(R.drawable.camera_selected);
                txt_photo.setTextColor(getResources().getColor(R.color.colorPrimary));

                img_video.setImageResource(R.drawable.video_gray);
                txt_video.setTextColor(getResources().getColor(R.color.gray_txt_color));

                img_audio.setImageResource(R.drawable.audio_gray);
                txt_audio.setTextColor(getResources().getColor(R.color.gray_txt_color));

                UI_Control = "photo";
                if (Image_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();
            }
        });


        ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_video.setImageResource(R.drawable.video_selected);
                txt_video.setTextColor(getResources().getColor(R.color.colorPrimary));

                img_photo.setImageResource(R.drawable.camera_gray);
                txt_photo.setTextColor(getResources().getColor(R.color.gray_txt_color));

                img_audio.setImageResource(R.drawable.audio_gray);
                txt_audio.setTextColor(getResources().getColor(R.color.gray_txt_color));

                UI_Control = "video";
                if (Video_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();

            }
        });

        ll_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_audio.setImageResource(R.drawable.audio_selected);
                txt_audio.setTextColor(getResources().getColor(R.color.colorPrimary));

                img_photo.setImageResource(R.drawable.camera_gray);
                txt_photo.setTextColor(getResources().getColor(R.color.gray_txt_color));

                img_video.setImageResource(R.drawable.video_gray);
                txt_video.setTextColor(getResources().getColor(R.color.gray_txt_color));

                UI_Control = "audio";
                if (Audio_list.size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    listview.setAdapter(customArrayAdapter);
                } else
                    listview.setVisibility(View.GONE);
                iniflate_add_Next_img();
            }
        });

        img_heading_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
                Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                sr.startListening(intent);
                et_text = "heading";
            }
        });

        img_sub_heading_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
                Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                sr.startListening(intent);
                et_text = "sub_heading";

            }
        });

        img_personality_topic_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
                Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                sr.startListening(intent);
                et_text = "pers_topic";

            }
        });

        img_intro_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
                Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                sr.startListening(intent);
                et_text = "intro";

            }
        });

        img_body_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
                Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                intent.putExtra("android.speech.extra.LANGUAGE", "hi-IN");
                sr.startListening(intent);
                et_text = "body";

            }
        });

        onBackPress(et_heading);
        onBackPress(et_sub_heading);
        onBackPress(et_intro);
        onBackPress(et_body);
        onBackPress(et_personality_topic);


        et_domain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Domain");
                builder.setItems(domain_value, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        et_domain.setText(domain_value[position].toString());
                    }
                });
                builder.create().show();
            }
        });

        et_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Category");
                builder.setItems(category_value, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        et_category.setText(category_value[position].toString());
                    }
                });
                builder.create().show();
            }
        });

        et_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Scope");
                builder.setItems(scope_value, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        et_scope.setText(scope_value[position].toString());
                    }
                });
                builder.create().show();
            }
        });

        et_priorty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Priority");
                builder.setItems(priority_value, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        et_priorty.setText(priority_value[position].toString());
                    }
                });
                builder.create().show();
            }
        });

    }

    void showToast() {
        if (AppUtills.isNetworkAvailable(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.please_speak_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else
            Toast.makeText(getActivity(), "" + getResources().getString(R.string.txt_network_error), Toast.LENGTH_SHORT).show();
    }

    void openDialog() {
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
                        status = AppController_Patrika.Story_Status_outbox;
                        flag = "outbox";
                        insertIntoDb("new");
                    }
                });

        dialog.findViewById(R.id.btn_Dialog_yes).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String phoneNo = "" + AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number", "");
                        String message = "H:" + et_heading.getText().toString().trim() + " SH:" + et_sub_heading.getText().toString().trim() + " D:" + et_domain.getText().toString().trim() + " C:" + et_category.getText().toString().trim()
                                + " S:" + et_scope.getText().toString().trim() + " P:" + et_priorty.getText().toString().trim() + " PE:" + et_personality_topic.getText().toString().trim() + " I:" + et_intro.getText().toString().trim()
                                + " B:" + et_body.getText().toString().trim();

                        Log.e(TAG,"message to be sent is  "+message);
                        Log.e(TAG,"phoneNo to be sent is  "+phoneNo);
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> parts = smsManager.divideMessage(message);
                            Log.e(TAG,"parts to be sent is  "+parts);
                            for(int i=0;i<parts.size();i++)
                            {
                                smsManager.sendTextMessage(phoneNo, null, parts.get(i), null, null);
                            }
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.sms_sent_txt), Toast.LENGTH_LONG).show();
                            status = AppController_Patrika.Story_Status_outbox;
                            flag = "outbox";
                            insertIntoDb("new");
                        } catch (Exception e) {
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.sms_failed_txt), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    void verifyPassword() {
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
                    }
                });

        dialog.findViewById(R.id.btn_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txt_password.getText().toString().trim().length() > 0) {
                            if (AppController_Patrika.getSharedPreferences().getString("pin", "").equals(txt_password.getText().toString().trim())) {
                                dialog.dismiss();
                                if (AppUtills.isNetworkAvailable(getActivity())) {
                                    new excuteSend_Detailed_news_Async().execute();
                                } else {
                                    if (!AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("sms_number", "").equalsIgnoreCase("0"))
                                        openDialog();
                                    else {
                                        flag = "outbox";
                                        status = AppController_Patrika.Story_Status_outbox;
                                        insertIntoDb("new");
                                        Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_internet_saved_to_outbox), Toast.LENGTH_LONG).show();
                                    }
                                }

                                if (AppUtills.showLogs)
                                    Log.v(TAG, "password on submit is  " + AppController_Patrika.getSharedPreferences().getString("pass", ""));
                            } else {
                                input_layout_pass.setError(getResources().getString(R.string.hint_correct_password));
                                if (AppUtills.showLogs)
                                    Log.v(TAG, "password on submit is  " + AppController_Patrika.getSharedPreferences().getString("pass", ""));
                            }
                        } else {
                            input_layout_pass.setError(getResources().getString(R.string.hint_pin_name));
                        }
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

    public class excuteGet_Detailed_news_Async extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.e(TAG, "onPreExecute of excuteGet_Detailed_news_Async");
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }


        @Override
        protected String doInBackground(Void... voids) {
            String response = "";

            try {
                Log.e(TAG, "content_url  " + Add_Stories_Activity.nid);
                HttpGet httpGet = new HttpGet(Add_Stories_Activity.nid);
                httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("X-CSRF-Token", "" + AppController_Patrika.getSharedPreferences().getString("token", ""));
                httpGet.setHeader("Cookie", AppController_Patrika.getSharedPreferences().getString("session_name", "") + "=" + AppController_Patrika.getSharedPreferences().getString("sessid", ""));

                response = AppUtills.getDecodedResponse(httpGet);

                if (AppUtills.showLogs) Log.v("response", "" + response);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                if (AppUtills.showLogs) Log.v("onPostExecute", "onPostExecute called  " + s);
                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject(s);

                    Nid = jsonObject.getString("nid");
                    et_heading.setText("" + jsonObject.getString("title"));

                    if (check_instance_type("field_ccms_sub_heading") && !jsonObject.getString("field_ccms_sub_heading").equals("[]"))
                        et_sub_heading.setText("" + new JSONObject(jsonObject.getString("field_ccms_sub_heading")).getJSONArray("und").getJSONObject(0).getString("value"));

                    if (check_instance_type("body") && !jsonObject.getString("body").equals("[]"))
                        et_body.setText("" + new JSONObject(jsonObject.getString("body")).getJSONArray("und").getJSONObject(0).getString("value"));

                    if (check_instance_type("field_ccms_introduction") && !jsonObject.getString("field_ccms_introduction").equals("[]"))
                        et_intro.setText("" + new JSONObject(jsonObject.getString("field_ccms_introduction")).getJSONArray("und").getJSONObject(0).getString("value"));

                    if (check_instance_type("field_ccms_category") && !jsonObject.getString("field_ccms_category").equals("[]"))
                        et_category.setText("" + new JSONObject(jsonObject.getString("field_ccms_category")).getJSONArray("und").getJSONObject(0).getString("value"));

                    if (check_instance_type("field_ccms_scope") && !jsonObject.getString("field_ccms_scope").equals("[]"))
                        et_scope.setText("" + new JSONObject(jsonObject.getString("field_ccms_scope")).getJSONArray("und").getJSONObject(0).getString("value"));
                    if (check_instance_type("field_ccms_domain") && !jsonObject.getString("field_ccms_domain").equals("[]"))
                        et_domain.setText("" + new JSONObject(jsonObject.getString("field_ccms_domain")).getJSONArray("und").getJSONObject(0).getString("value"));
                    if (check_instance_type("field_ccms_priority") && !jsonObject.getString("field_ccms_priority").equals("[]"))
                        et_priorty.setText("" + new JSONObject(jsonObject.getString("field_ccms_priority")).getJSONArray("und").getJSONObject(0).getString("value"));
                    if (check_instance_type("field_ccms_personality") && !jsonObject.getString("field_ccms_personality").equals("[]"))
                        et_personality_topic.setText("" + new JSONObject(jsonObject.getString("field_ccms_personality")).getJSONArray("und").getJSONObject(0).getString("value"));

                    JSONObject innerJson = new JSONObject(jsonObject.getString("field_ccms_sub_heading"));
                    Log.e(TAG, "sunheading convert to json  " + innerJson.toString());
                    Log.e(TAG, "und convert to json  " + innerJson.getJSONArray("und").getJSONObject(0).getString("value"));


                    if (!et_scope.getText().toString().trim().equals("")) {
                        et_heading.setEnabled(false);
                        et_sub_heading.setEnabled(false);
                        et_personality_topic.setEnabled(false);
                        et_domain.setEnabled(false);
                        et_category.setEnabled(false);
                        et_scope.setEnabled(false);
                        et_priorty.setEnabled(false);
                        et_intro.requestFocus();

                        img_heading_mic.setEnabled(false);
                        img_sub_heading_mic.setEnabled(false);
                        img_personality_topic_mic.setEnabled(false);
                    } else {
                        et_heading.setEnabled(false);
                        et_sub_heading.setEnabled(false);
                        img_heading_mic.setEnabled(false);
                        img_sub_heading_mic.setEnabled(false);
                    }
                }else
                    AppUtills.alertDialog(getActivity(),s);
            } catch (Exception e) {
                AppUtills.alertDialog(getActivity(),s);
                e.printStackTrace();
            }
        }
    }

    boolean check_instance_type(String type) {
        try {
            Object json = new JSONTokener(type).nextValue();

            if (json instanceof JSONObject)
                return false;
            else if (json instanceof JSONArray)
                return false;
            else
                return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addThumbnail() {

        Log.e(TAG, "addThumbnail called  ");
        String time =  ""+System.currentTimeMillis();
        FileOutputStream fos = null;
        File rootPath = new File(Environment.getExternalStoragePublicDirectory(getActivity().getResources().getString(R.string.app_name)), ".Thumbnail");
        if (!rootPath.exists())
            rootPath.mkdirs();

        Bitmap bitmap = null;

        if (Image_list.size() > 0 || Video_list.size() > 0) {
            String path = "";

            if (Image_list.size() > 0)
                path = Image_list.get(0).get("Path");
            else if (Video_list.size() > 0)
                path = Video_list.get(0).get("Path");
            if (!path.equals("")) {
                if (Image_list.size() > 0)
                    bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),100, 100);
                else if (Video_list.size() > 0)
                    bitmap = ThumbnailUtils.createVideoThumbnail(path,MediaStore.Video.Thumbnails.MICRO_KIND);

                Log.e("time", "path  " + path);
                Log.e("time", "bitmap  " + bitmap);

                try {
                    Log.e("time", "time  " + time);
                    String snapshotImage = time + "THUMB.jpg";
                    File snapshotFile = new File(rootPath, snapshotImage);
                    fos = new FileOutputStream(snapshotFile);

                    Thumbnail = rootPath + "/" + snapshotImage;
                    Log.e("snapshotFile", "snapshotFile  " + rootPath + snapshotImage);
                    AppUtills.scanFile(getActivity(),new File(Thumbnail).getAbsolutePath());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, bytes);

                    fos.write(bytes.toByteArray());

                    if (AppUtills.showLogs)
                        Log.e(TAG, "File bytes  " + bytes.toByteArray().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }

    void openDialog_to_notify_about_data() {
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
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

        dialog.show();
    }

    public class CustomArrayAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        CustomArrayAdapter() {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            int size = 0;
            if (UI_Control.equals("photo"))
                size = Image_list.size();
            else if (UI_Control.equals("audio"))
                size = Audio_list.size();
            else if (UI_Control.equals("video"))
                size = Video_list.size();

            return size;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.image_view_smail_to_add_layout, parent, false);
            final FrameLayout linear = (FrameLayout) convertView.findViewById(R.id.linear);
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            ImageView cross = (ImageView) convertView.findViewById(R.id.cross);
            if (UI_Control.equals("photo")) {
                Log.e(TAG,"Image_list.get(position)   "+Image_list.get(position));
                img.setImageBitmap(AppUtills.compressBitmapImage(Image_list.get(position).get("Path")));
            } else if (UI_Control.equals("video"))
                img.setImageBitmap(AppUtills.getBitmapFromPath(Video_list.get(position).get("Path")));
            else if (UI_Control.equals("audio"))
                img.setImageResource(R.drawable.audio);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog_media_preview);
                    final VideoView videoView = (VideoView) dialog.findViewById(R.id.video_view);
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
                    RelativeLayout audio_layout = (RelativeLayout) dialog.findViewById(R.id.audio_layout);

                    if(UI_Control.equals("audio"))
                    {
                        videoView.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.VISIBLE);
                        Uri uri = Uri.parse(Audio_list.get(position).get("Path"));
                        final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(),uri);
                        mediaPlayer.start();
                        dialog.show();

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if(mediaPlayer.isPlaying())
                                    mediaPlayer.stop();
                            }
                        });
                    }

                }
            });

            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(getActivity(), R.style.ThemeDialogCustom);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog_two);
                    TextView dialog4all_txt = (TextView) dialog
                            .findViewById(R.id.txt_DialogTitle);
                    dialog4all_txt.setText("क्या आप इस मीडिया को डिलीट करना चाहते है ?");

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
                                    if (UI_Control.equals("photo")) {
                                        if (Image_list.size() != 0) {
                                            Image_list.remove(position);
                                            if (Image_list.size() == 0)
                                            {
                                                listview.setVisibility(View.GONE);

                                            }
                                            if(!status.equals(AppController_Patrika.Story_Status_Uploaded))
                                                Thumbnail = "";
                                            if(AppUtills.showLogs)Log.e(TAG,"Image_list size "+Image_list.size());
                                                notifyDataSetChanged();
                                        }
                                    } else if (UI_Control.equals("video")) {
                                        if (Video_list.size() != 0) {
                                            Video_list.remove(position);
                                            if (Video_list.size() == 0) {
                                                listview.setVisibility(View.GONE);
                                            }
                                            if(!status.equals(AppController_Patrika.Story_Status_Uploaded))
                                                Thumbnail = "";
                                            if(AppUtills.showLogs)Log.e(TAG,"Video_list size "+Video_list.size());
                                            notifyDataSetChanged();
                                        }
                                    } else if (UI_Control.equals("audio")) {
                                        if (Audio_list.size() != 0) {
                                            Audio_list.remove(position);
                                            if (Audio_list.size() == 0)
                                                listview.setVisibility(View.GONE);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                    dialog.show();
                }
            });

            return convertView;
        }
        /** View holder for the views we need access to */
    }

    void prepare_media_list() {

        if (AppUtills.showLogs) Log.v(TAG, "prepare_media_list called");
        if (AppUtills.showLogs) Log.v(TAG, "Db_Contains_db "+Db_Contains_db);
        AppController_Patrika.paths.clear();
        HashMap<String, String> hashMap1 = new HashMap<>();
        if (!Thumbnail.equals("")) {
            if (Db_Contains_db.equals("")) {
                hashMap1.put("Path", "" + Thumbnail);
                hashMap1.put("Type", "Thumb");
                hashMap1.put("Status", "queue");
                hashMap1.put("Status_process", "new");
                Db_Contains_db = Thumbnail;
            }
        }

        for (int i = 0; i < Image_list.size(); i++) {
            HashMap<String, String> hashMap = Image_list.get(i);
            hashMap.put("Path", "" + Image_list.get(i).get("Path"));
            hashMap.put("Type", Image);

            if(Image_list.get(i).get("Status").equalsIgnoreCase("uploaded")) {
                hashMap.put("Status_process", "old");
                hashMap.put("Status", "uploaded");
            }else {
                hashMap.put("Status_process", "new");
                hashMap.put("Status", "queue");
                img_flag = true;
            }

            Image_list.set(i,hashMap);

            AppController_Patrika.paths.add(hashMap);
    }

        for (int i = 0; i < Video_list.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Path", "" + Video_list.get(i).get("Path"));
            hashMap.put("Type", Video);

            if(Video_list.get(i).get("Status").equalsIgnoreCase("uploaded")) {
                hashMap.put("Status_process", "old");
                hashMap.put("Status", "uploaded");
            }else {
                hashMap.put("Status_process", "new");
                hashMap.put("Status", "queue");
                vid_flag = true;
            }

            Video_list.set(i,hashMap);

            AppController_Patrika.paths.add(hashMap);
        }

        for (int i = 0; i < Audio_list.size(); i++) {
             HashMap<String, String> hashMap = new HashMap<>();
             hashMap.put("Path", "" + Audio_list.get(i).get("Path"));
             hashMap.put("Type", Audio);
             hashMap.put("Status", "queue");

            if(Audio_list.get(i).get("Status").equalsIgnoreCase("uploaded")) {
                hashMap.put("Status_process", "old");
                hashMap.put("Status", "uploaded");
            }else {
                hashMap.put("Status_process", "new");
                hashMap.put("Status", "queue");
                aud_flag = true;
            }

            Audio_list.set(i,hashMap);

            AppController_Patrika.paths.add(hashMap);
        }

        if (hashMap1 != null && hashMap1.size() > 0 && hashMap1.get("Type").equals("Thumb"))
        {
                img_flag = true;
                Image_list.add(0,hashMap1);

            AppController_Patrika.paths.add(0, hashMap1);
        }
        if (AppUtills.showLogs) Log.v(TAG, "Db_Contains_db "+Db_Contains_db);
        if(AppUtills.showLogs)Log.v(TAG,"Thumbnail in detials  "+Thumbnail);
        if(AppUtills.showLogs)Log.v(TAG,"Image_list in detials  "+Image_list);
        if(AppUtills.showLogs)Log.v(TAG,"hashMap1 in detials  "+hashMap1);
        if(AppUtills.showLogs)Log.v(TAG,"paths in detials  "+AppController_Patrika.paths);
        if(AppUtills.showLogs)Log.e(TAG,"prepare_media_list Img_size "+Img_size);
        if(AppUtills.showLogs)Log.e(TAG,"prepare_media_list Vid_size "+Vid_size);
        if(AppUtills.showLogs)Log.e(TAG,"prepare_media_list Aud_size "+Aud_size);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.add_story,menu);
        MenuItem draft = menu.findItem(R.id.draft);
        photo = menu.findItem(R.id.photo);
        video = menu.findItem(R.id.video);
        audio = menu.findItem(R.id.audio);

        photo.setVisible(true);
        video.setVisible(true);
        audio.setVisible(true);

        photo.setIcon(R.drawable.camera_selected);
        UI_Control = "photo";
        if (Image_list.size() > 0) {
            listview.setVisibility(View.VISIBLE);
            listview.setAdapter(customArrayAdapter);
        } else
            listview.setVisibility(View.GONE);
    }
}