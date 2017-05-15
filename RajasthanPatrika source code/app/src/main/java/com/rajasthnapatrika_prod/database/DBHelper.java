package com.rajasthnapatrika_prod.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase database;
    Context context;
    private static final String DATABASE_NAME = "patrika.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "DBHelper";

    //CREATE_Add_Story_Table
    public static final String Id_Ref = "Id_Ref";
    public static final String Id = "Id";
    public static final String Status = "Status";
    public static final String Status_process = "Status_process";
    public static final String Story_Type = "Story_Type";
    public static final String notification_id = "notification_id";
    public static final String Heading = "Heading";
    public static final String Sub_heading = "Sub_heading";
    public static final String Body = "Body";
    public static final String Introduction = "Introduction";

    public static final String Personality_topic = "Personality_topic";
    public static final String Priority = "Priority";
    public static final String Thumbnail = "Thumbnail";
    public static final String Scope = "Scope";
    public static final String Category = "Category";
    public static final String Domain = "Domain";
    public static final String Time = "Time";
    public static final String PlaceName = "PlaceName";
    public static final String Nid = "Nid";
    public static final String KEY_IMAGE = "images";
    public static final String MiliSec = "MiliSec";

    public static final String Type = "Type";
    public static final String tbl_add_news = "tbl_add_news";
    public static final String tbl_images = "tbl_images";

    public static final String tbl_drop_downs_values = "tbl_drop_downs_values";
    public static final String tbl_add_notification = "tbl_add_notification";
    public static final String tbl_add_news_dynamic = "tbl_add_news_dynamic";
    public static final String Ftp_directory = "Ftp_directory";
    public static final String User_name = "User_name";
    public static final String Type_drop_downs_values = "Type_drop_downs_values";
    public static final String Value_drop_downs_values = "Value_drop_downs_values";

    public static final String Content_url = "Content_url";
    public static final String isRead = "isRead";
    public static final String publish_status_web = "publish_status_web";

    public static final String publish_status_print = "publish_status_print";
    public static final String publish_status_tv = "publish_status_tv";
    public static final String Required_more = "Required_more";

    public static final String latitute = "latitute";
    public static final String longitute = "longitute";
    public static final String Img_size = "Img_size";
    public static final String Vid_size = "Vid_size";
    public static final String Aud_size = "Aud_size";


    String Add_news_tbl_dynamic = "CREATE TABLE " + tbl_add_news_dynamic + "("
            + Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Story_Type + " TEXT ,"
            + Heading + " TEXT ,"
            + Sub_heading + " TEXT ,"
            + Ftp_directory + " TEXT ,"
            + User_name + " TEXT ,"
            + Time + " TEXT ,"
            + PlaceName + " TEXT ,"
            + Nid + " TEXT ,"
            + publish_status_print + " TEXT ,"
            + publish_status_tv + " TEXT ,"
            + publish_status_web + " TEXT ,"
            + Thumbnail + " TEXT "
            + ")";


    String Add_news_tbl_Notification = "CREATE TABLE " + tbl_add_notification + "("
            + Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Nid + " TEXT ,"
            + Content_url + " TEXT ,"
            + Heading + " TEXT ,"
            + Time + " TEXT ,"
            + PlaceName + " TEXT ,"
            + Required_more + " TEXT ,"
            + isRead + " TEXT ,"
            + Sub_heading + " TEXT ,"
            + Story_Type + " TEXT ,"
            + notification_id + " TEXT "
            + ")";


    String Detailed_Drop_Downs = "CREATE TABLE " + tbl_drop_downs_values + "("
            + Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Type_drop_downs_values + " TEXT ,"
            + Value_drop_downs_values + " TEXT "
            + ")";

    String Add_news_tbl = "CREATE TABLE " + tbl_add_news + "("
            + Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Status + " TEXT ,"
            + Status_process + " TEXT ,"
            + Story_Type + " TEXT ,"
            + Heading + " TEXT ,"
            + Sub_heading + " TEXT ,"
            + Body + " TEXT ,"
            + Introduction + " TEXT ,"
            + Personality_topic + " TEXT ,"
            + Priority + " TEXT ,"
            + Scope + " TEXT ,"
            + Category + " TEXT ,"
            + Domain + " TEXT ,"
            + Time + " TEXT ,"
            + PlaceName + " TEXT ,"
            + Nid + " TEXT ,"
            + MiliSec + " INTEGER ,"
            + Thumbnail + " TEXT ,"
            + publish_status_print + " TEXT ,"
            + publish_status_tv + " TEXT ,"
            + publish_status_web + " TEXT ,"
            + latitute + " TEXT ,"
            + longitute + " TEXT ,"
            + Img_size + " TEXT ,"
            + Vid_size + " TEXT ,"
            + Aud_size + " TEXT "
            + ")";

    String Add_images_tbl = "CREATE TABLE " + tbl_images + "("
            + Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Id_Ref + " TEXT ,"
            + Type + " TEXT ,"
            + KEY_IMAGE + " TEXT ,"
            + Status + " TEXT ,"
            + Nid + " TEXT ,"
            + Status_process + " TEXT "
            + ")";

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        database = getWritableDatabase();
        if (AppUtills.showLogs) Log.v(TAG, "DBHelper Constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            if (AppUtills.showLogs) Log.v(TAG, "onCreate method called");

            db.execSQL(Add_news_tbl);
            db.execSQL(Add_news_tbl_dynamic);
            db.execSQL(Add_images_tbl);
            db.execSQL(Detailed_Drop_Downs);
            db.execSQL(Add_news_tbl_Notification);
            Toast.makeText(context, "Table Created Successfully", Toast.LENGTH_LONG);
            if (AppUtills.showLogs) Log.v(TAG, "Table Created Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDataInAdd_image_tbl(ArrayList<HashMap<String, String>> paths, String id, long key, String news_id) {
        if (AppUtills.showLogs) Log.v(TAG, "insertDataInAdd_image_tbl called ");

        try {
            database = this.getWritableDatabase();

            if (!id.equals("")) {
                database.delete(tbl_images, "Id_Ref = " + id, null);
                if (AppUtills.showLogs) Log.v("Row Deleted", "Row Deleted Successfully");
            }


            for (int i = 0; i < paths.size(); i++) {
                ContentValues contentValues = new ContentValues();
                if (!id.equals(""))
                    contentValues.put(Id_Ref, "" + id);
                else if (id.equals(""))
                    contentValues.put(Id_Ref, "" + key);

                contentValues.put(Type, "" + paths.get(i).get("Type"));
                contentValues.put(KEY_IMAGE, "" + paths.get(i).get("Path"));
                contentValues.put(Status, paths.get(i).get("Status"));
                contentValues.put(Nid, news_id);
                contentValues.put(Status_process, paths.get(i).get("Status_process"));

                database.insert(tbl_images, null, contentValues);

                if (AppUtills.showLogs) Log.v(TAG, "id  " + key);
                if (AppUtills.showLogs) Log.v(TAG, "id  " + id);
            }
            if (AppUtills.showLogs) Log.v(TAG, "Data Inserted Successfully in  " + tbl_images);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void discard_Stored_images_data(String id, String flag) {
        int Ids = Integer.parseInt(id);
        database = this.getWritableDatabase();

        try {

            if (flag.equals("publish")) {
//                String   query = "SELECT * FROM " + tbl_images+" where Nid = '"+Id+"'" ;

                String query = "SELECT * FROM " + tbl_images + " where Nid = " + Ids;

                Cursor csr = database.rawQuery(query, null);
                if (AppUtills.showLogs)
                    Log.v("Cursor length", "cursor length of image is is  " + csr.getCount());
                if (csr.moveToFirst()) {
                    do {

                        File file = new File(csr.getString(3));
                        if (!csr.getString(2).equals("Thumb"))
                            file.delete();

                        AppUtills.scanFile(context,new File(csr.getString(3)).getAbsolutePath());

                        if (csr.getString(2).equals("Audio")) {
                            Uri rootUri = MediaStore.Audio.Media.getContentUriForPath(csr.getString(3));

                            context.getContentResolver().delete(rootUri,
                                    MediaStore.MediaColumns.DATA + "=?", new String[]{csr.getString(3)});
                        }

                        if (AppUtills.showLogs)Log.v(TAG, "File deleted from storage  " + csr.getString(3));

                    } while (csr.moveToNext());
                }
            }

            if (flag.equals("not_publish"))
                database.delete(tbl_images, "Id_Ref = " + Ids, null);
            else if (flag.equals("publish"))
                database.delete(tbl_images, "Nid = " + Ids, null);
            if (AppUtills.showLogs) Log.v("Row Deleted", "Row Deleted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public void deleteDropDowns_Data(String type) {
        try {
            database = this.getWritableDatabase();
            database.delete(tbl_drop_downs_values, "Type_drop_downs_values = '" + type + "'", null);
            if (AppUtills.showLogs) Log.v("Row Deleted", "Row Deleted Successfully");
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDrop_Downs_values(String type, String value) {
        if (AppUtills.showLogs) Log.v(TAG, "insertDataInAdd_image_tbl called ");

        try {
            database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Type_drop_downs_values, "" + type);
            contentValues.put(Value_drop_downs_values, "" + value);

            database.insert(tbl_drop_downs_values, null, contentValues);

            if (AppUtills.showLogs)
                Log.v(TAG, "Data Inserted in DropDowns Successfully in  " + tbl_images);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getAll_Drop_Downs_values(String type) {
//        ArrayList<String> values_list = new ArrayList<>();
        String values_list[] = null;
        int i = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String query = "SELECT * FROM " + tbl_drop_downs_values + " where Type_drop_downs_values = '" + type + "'";

            Cursor csr = db.rawQuery(query, null);
            values_list = new String[csr.getCount()];
            if (AppUtills.showLogs)
                Log.v("Cursor length", "cursor length of image is is  " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
//                    values_list.add(csr.getString(2));
                    values_list[i] = (csr.getString(2));
                    i++;
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values_list;
    }

    public void delete_Old_PublishedData() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
            query = "SELECT Time FROM " + tbl_add_news_dynamic;

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs)
                Log.v("Cursor length", "delete_Old_PublishedData cursor length   " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    String time = csr.getString(0);
                    Log.v("Time is", "delete_Old_PublishedData Time is   " + csr.getString(0));
                    Log.v("Time is", " Time in milisecond is   "+getMiliSec_from_date(csr.getString(0)));
                    Log.v("Time is", " cuurnt milisecond is   "+System.currentTimeMillis());
                    Log.v("Time is", " 48 hour milisecond is   "+(1000*60*60*48));
                    Log.v("Time is", " after minus milisecond is   "+(System.currentTimeMillis() - (1000*60*60*48)));

                    if((System.currentTimeMillis() - (1000*60*60*48)) > getMiliSec_from_date(csr.getString(0)))
                    {
                        int i = database.delete(tbl_add_news_dynamic, "Time = '"+time+"'", null);
                        Log.e("Dynamic table deleted","Successfully "+i);
                    }

                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getMiliSec_from_date(String givenDateString) {
        long timeInMilliseconds = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
    try

    {
        Date mDate = sdf.parse(givenDateString);
        timeInMilliseconds = mDate.getTime();
        System.out.println("Date in milli :: " + timeInMilliseconds);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    return  timeInMilliseconds;
}
    public void Update_processstatus(String id, String Nids,String Status_process1,String Status1)
    {
        database = this.getWritableDatabase();

        try
        {
            int Ids = Integer.parseInt(id);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Status_process, Status_process1);
            contentValues.put(Status, Status1);
          if(!Nids.equals(""))
            contentValues.put(Nid, Nids);

            String query = "select latitute, longitute from "+tbl_add_news+" where Id = "+Ids;

            Cursor csr = database.rawQuery(query, null);

            if(AppUtills.showLogs)Log.v("csr.length","csr.length  Update_processstatus "+csr.getCount());

            if (csr.moveToFirst()) {
                do {

                   String place =  AppUtills.getPlace_Name1(context,csr.getString(0),csr.getString(1));

                    contentValues.put(PlaceName, place);
                    database.update(tbl_add_news, contentValues, "Id = " + Ids, null);

                } while (csr.moveToNext());
            }
            csr.close();


            database.update(tbl_add_news, contentValues, "Id = " + Ids, null);

            if(AppUtills.showLogs)Log.v("News Row Deleted","News Row Updated Successfully");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            database.close();
        }
    }

    public boolean check_News_Availability()
    {

        boolean having_news = false;

        database = getWritableDatabase();
        String query = "";

        query = "SELECT * FROM " +tbl_add_news+" where Status = '"+AppController_Patrika.Story_Status_process+"' OR Status = '"+AppController_Patrika.Story_Status_outbox+"' OR Status = '"+AppController_Patrika.Story_Status_draft+"'";

        Cursor csr = database.rawQuery(query, null);

        if(csr.getCount()>0)
                having_news = true;
            else  if(csr.getCount() == 0)
                having_news = false;


        if(AppUtills.showLogs)Log.v("check_News_Availability","check_News_Availability  "+having_news);

        return having_news;
    }


    public void Update_MiliSec(String id)
    {
        database = this.getWritableDatabase();
        try
        {
           String query = "SELECT MiliSec FROM " + tbl_add_news+" where MiliSec in (select MIN(MiliSec) from "+tbl_add_news+")" ;

            Cursor csr = database.rawQuery(query, null);

            if(AppUtills.showLogs)Log.v("csr.length","csr.length   "+csr.getCount());

            if (csr.moveToFirst()) {
                do {

                    if(AppUtills.showLogs)Log.e("time is","time is   "+csr.getString(0));

                    long new_time = Long.parseLong(csr.getString(0))-1;

                    if(AppUtills.showLogs)Log.e("new_time is","new_time is   "+new_time);

                    int Ids = Integer.parseInt(id);
//
                 ContentValues contentValues = new ContentValues();
                 contentValues.put(MiliSec, ""+new_time);

                database.update(tbl_add_news, contentValues, "Id = " + Ids, null);

                } while (csr.moveToNext());
            }
            csr.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            database.close();
        }
    }

    public void update_Notification_Read_status(String notification_id)
    {
        database = this.getWritableDatabase();
        if (AppUtills.showLogs) Log.v("Dbhelper ", "update_Notification_Read_status called");
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(isRead, "Read");

            int i = database.update(tbl_add_notification, contentValues, "notification_id = '" +notification_id+"'", null);

            if(AppUtills.showLogs)Log.v("tbl_add_notification","News Read Row Updated Successfully"+i);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            database.close();
        }
    }


    public void Update_Media_Status(String id, String type,String new_id)
    {
        database = this.getWritableDatabase();

        try
        {
            int Ids = Integer.parseInt(id);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Status, "uploaded");

          int  i =  database.update(tbl_images, contentValues, "Id = " + Ids, null);

            if(AppUtills.showLogs)Log.v("Media Row Updated","Media Row Updated Successfully   "+i);
            if(AppUtills.showLogs)Log.e("Media type","Media Row Updated Successfully   "+type);
            if(AppUtills.showLogs)Log.e("Media new_id","Media Row Updated Successfully   "+new_id);

            if(i>0) {
                if(!new_id.equals("")){
                String media = "";

                if (type.equalsIgnoreCase("Image") || type.equalsIgnoreCase("Thumb"))
                    media = "Img_size";
                else if (type.equalsIgnoreCase("Audio"))
                    media = "Aud_size";
                else if (type.equalsIgnoreCase("Video"))
                    media = "Vid_size";

                if (AppUtills.showLogs)
                    Log.e("Media media", "Media Row Updated Successfully   " + media);
                if (AppUtills.showLogs)
                    Log.e("Media media", "media column Row Updated Successfully   " + media);

                String query = "select " + media + " from " + tbl_add_news + " where Id = '" + new_id + "'";
                SQLiteDatabase db = this.getWritableDatabase();
                Cursor csr = db.rawQuery(query, null);
                if (AppUtills.showLogs)
                    Log.e("Media csr.length", "csr.getCount()   " + csr.getCount());
                if (csr.moveToFirst()) {
                    if (AppUtills.showLogs)
                        Log.e("Media csr.getString(0)", "csr.getString(0) from cursor   " + csr.getString(0));
                    int counter = Integer.parseInt(csr.getString(0));
                    if (AppUtills.showLogs) Log.e("Media counter", "from cursor   " + counter);
                    counter += 1;
                    if (AppUtills.showLogs) Log.e("Media counter", "after increase   " + counter);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(media, "" + counter);
                    int news_id = Integer.parseInt(new_id);
                    int j = database.update(tbl_add_news, contentValues1, "Id = " + news_id, null);
                    if (AppUtills.showLogs)
                        Log.v("Media Row Updated", "Update_Media_Status news Row Updated Successfully   " + j);
                }
            }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            database.close();
        }
    }


    public ArrayList<HashMap<String,String>> getAll_Images(String Id,String Status_media)
    {
        if (AppUtills.showLogs) Log.v(TAG, "getAll_Images called ");
        if (AppUtills.showLogs) Log.v(TAG, "id is  "+Id);

        ArrayList<HashMap<String,String>> arrReceive1 = new ArrayList<HashMap<String, String>>();
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
                 if(Status_media.equals(""))
                   query = "SELECT * FROM " + tbl_images+" where Id_Ref = '"+Id+"'" ;
                else if(!Status_media.equals(""))
                  query = "SELECT * FROM " + tbl_images+" where Id_Ref = '"+Id+"' AND Status = '"+Status_media+"'";

            Cursor csr = db.rawQuery(query, null);
            if(AppUtills.showLogs)Log.v("Cursor length","cursor length of image is is  "+csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    HashMap<String,String> hm = new HashMap<>();
                    DBHandler dbHandler = new DBHandler();
                    hm.put("Id",csr.getString(0));
                    hm.put("Id_Ref",csr.getString(1));
                    hm.put("Type",csr.getString(2));
                    hm.put("Path",csr.getString(3));
                    hm.put("Status",csr.getString(4));
                    hm.put("Nid",csr.getString(5));
                    hm.put("Status_process",csr.getString(6));

                    Log.e("DBHElper","Status at a time "+csr.getString(4));
                    arrReceive1.add(hm);
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return arrReceive1;
    }

    public void insertDataInNotifiation_tbl(DBHandler db) {

        try {

            database = this.getWritableDatabase();
            if (AppUtills.showLogs) Log.v(TAG, "id in insertDataInNotifiation_tbl  " + Nid);

                ContentValues contentValues = new ContentValues();
                contentValues.put(Nid, "" + db.Nid);
                contentValues.put(Content_url, "" + db.Content_url);
                contentValues.put(Heading, "" + db.Heading);
                contentValues.put(Sub_heading, "" + db.Sub_heading);
                contentValues.put(Time, "" + db.Time);
                contentValues.put(PlaceName, "" + db.PlaceName);
                contentValues.put(Required_more, "" + db.Required_more);
                contentValues.put(isRead, "" + db.isRead);
                contentValues.put(Story_Type, "" + db.Story_Type);
                contentValues.put(notification_id, "" + db.notification_id);

                database.insert(tbl_add_notification, null, contentValues);

                if (AppUtills.showLogs) Log.v(TAG, "Data Inserted Successfully");

            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long insertDataInAdd_News(DBHandler db, String id) {

        String thumb = db.Thumbnail;
        long i = 0;
        try {
            if (AppUtills.showLogs)
                Log.v(TAG, "id in insertDataInAdd_Story_tbl  " + id);
            if (id.equals("")) {
                database = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(Status, "" + db.Status);
                contentValues.put(Status_process, "" + db.Status_process);
                contentValues.put(Story_Type, "" + db.Story_Type);
                contentValues.put(Heading, "" + db.Heading);
                contentValues.put(Sub_heading, "" + db.Sub_heading);
                contentValues.put(Body, db.Body);
                contentValues.put(Introduction, "" + db.Introduction);
                contentValues.put(Personality_topic, "" + db.Personality_topic);
                contentValues.put(Priority, "" + db.Priority);
                contentValues.put(Scope, "" + db.Scope);
                contentValues.put(Category, "" + db.Category);
                contentValues.put(Domain, "" + db.Domain);
                contentValues.put(Time, "" + db.Time);
                contentValues.put(PlaceName, "" + db.PlaceName);
                contentValues.put(Nid, "" + db.Nid);
                contentValues.put(MiliSec, db.MiliSec);
                contentValues.put(Thumbnail, "" + db.Thumbnail);
                contentValues.put(publish_status_print, "" + db.publish_status_print);
                contentValues.put(publish_status_tv, "" + db.publish_status_tv);
                contentValues.put(publish_status_web, "" + db.publish_status_web);
                contentValues.put(latitute, "" + db.latitute);
                contentValues.put(longitute, "" + db.longitute);
                contentValues.put(Img_size, "" + db.Img_size);
                contentValues.put(Vid_size, "" + db.Vid_size);
                contentValues.put(Aud_size, "" + db.Aud_size);

                database.insert(tbl_add_news, null, contentValues);

                String query = "SELECT "+Id+" from "+tbl_add_news+" order by "+Id+" DESC limit 1";
                Cursor c = database.rawQuery(query,null);
                if (c != null && c.moveToFirst()) {
                    i = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
                    if (AppUtills.showLogs) Log.v(TAG, "lastId inserted is  "+i);
                }

                if (AppUtills.showLogs) Log.v(TAG, "total inserted is  "+i);

                if (AppUtills.showLogs) Log.v(TAG, "Data Inserted Successfully");

                database.close();
            } else if (!id.equals("")) {
                database = this.getWritableDatabase();

                int Ids = Integer.parseInt(id);
                ContentValues contentValues = new ContentValues();
                if(!db.Nid.equals("")) {
                String query = "SELECT * from " + tbl_add_news + " where Nid = '" + db.Nid + "'";
                Cursor cursor = database.rawQuery(query, null);
                if (cursor.getCount() > 0)
                {
                    if (cursor.moveToFirst()) {
                        do {
                                if (db.Thumbnail(cursor.getString(17)).equals(""))
                                    contentValues.put(Thumbnail, "" + thumb);

                            if(!db.Thumbnail(cursor.getString(18)).equalsIgnoreCase("1"))
                                contentValues.put(publish_status_print, ""+db.publish_status_print);
                            if(!db.Thumbnail(cursor.getString(19)).equalsIgnoreCase("1"))
                                contentValues.put(publish_status_tv, ""+db.publish_status_tv);
                            if(!db.Thumbnail(cursor.getString(20)).equalsIgnoreCase("1"))
                                contentValues.put(publish_status_web, ""+db.publish_status_web);
                        } while (cursor.moveToNext());
                    }
                }else
                {
                    contentValues.put(Thumbnail, "" + db.Thumbnail);
                    contentValues.put(publish_status_print, ""+db.publish_status_print);
                    contentValues.put(publish_status_tv, ""+db.publish_status_tv);
                    contentValues.put(publish_status_web, ""+db.publish_status_web);
                }
                }else
                {
                    contentValues.put(Thumbnail, "" + db.Thumbnail);
                    contentValues.put(publish_status_print, ""+db.publish_status_print);
                    contentValues.put(publish_status_tv, ""+db.publish_status_tv);
                    contentValues.put(publish_status_web, ""+db.publish_status_web);
                }

                contentValues.put(Status, "" + db.Status);
                contentValues.put(Status_process, "" + db.Status_process);
                contentValues.put(Story_Type, "" + db.Story_Type);
                contentValues.put(Heading, "" + db.Heading);
                contentValues.put(Sub_heading, "" + db.Sub_heading);
                contentValues.put(Body, db.Body);
                contentValues.put(Introduction, "" + db.Introduction);
                contentValues.put(Personality_topic, "" + db.Personality_topic);
                contentValues.put(Priority, "" + db.Priority);
                contentValues.put(Scope, "" + db.Scope);
                contentValues.put(Category, "" + db.Category);
                contentValues.put(Domain, "" + db.Domain);
                contentValues.put(Time, "" + db.Time);
                contentValues.put(PlaceName, "" + db.PlaceName);
                contentValues.put(Nid, "" + db.Nid);
                contentValues.put(MiliSec, "" + db.MiliSec);
                contentValues.put(latitute, "" + db.latitute);
                contentValues.put(longitute, "" + db.longitute);
                contentValues.put(Img_size, "" + db.Img_size);
                contentValues.put(Vid_size, "" + db.Vid_size);
                contentValues.put(Aud_size, "" + db.Aud_size);

               database.update(tbl_add_news, contentValues, "Id = " + Ids, null);

                i = Ids;
                if (AppUtills.showLogs) Log.v(TAG, "Data updated Successfully   "+i);

                database.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public ArrayList<HashMap<String, String>> getAll_News(String Story_Type) {
        ArrayList<HashMap<String, String>> arrReceive1 = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
                    query = "SELECT * FROM " + tbl_add_news + " where Story_Type = '"+Story_Type+"' ORDER BY Time  DESC";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Id", dbHandler.Id(csr.getString(0)));
                    hashMap.put("Status", dbHandler.Status(csr.getString(1)));
                    hashMap.put("Status_process", dbHandler.Status_process(csr.getString(2)));
                    hashMap.put("Story_Type", dbHandler.Story_Type(csr.getString(3)));
                    hashMap.put("Heading", dbHandler.Heading(csr.getString(4)));
                    hashMap.put("Sub_heading", dbHandler.Sub_heading(csr.getString(5)));
                    hashMap.put("Body", dbHandler.Body(csr.getString(6)));
                    hashMap.put("Introduction", dbHandler.Introduction(csr.getString(7)));
                    hashMap.put("Personality_topic", dbHandler.Personality_topic(csr.getString(8)));
                    hashMap.put("Priority", dbHandler.Priority(csr.getString(9)));
                    hashMap.put("Scope", dbHandler.Scope(csr.getString(10)));
                    hashMap.put("Category", dbHandler.Category(csr.getString(11)));
                    hashMap.put("Domain", dbHandler.Domain(csr.getString(12)));
                    hashMap.put("Time", dbHandler.Time(csr.getString(13)));
                    hashMap.put("PlaceName", dbHandler.PlaceName(csr.getString(14)));
                    hashMap.put("Nid", dbHandler.Nid(csr.getString(15)));
                    hashMap.put("MiliSec", ""+dbHandler.MiliSec(csr.getLong(16)));
                    hashMap.put("Thumbnail", dbHandler.Thumbnail(csr.getString(17)));
                    hashMap.put("publish_status_print", dbHandler.Thumbnail(csr.getString(18)));
                    hashMap.put("publish_status_tv", dbHandler.Thumbnail(csr.getString(19)));
                    hashMap.put("publish_status_web", dbHandler.Thumbnail(csr.getString(20)));
                    hashMap.put("latitute", dbHandler.latitute(csr.getString(21)));
                    hashMap.put("longitute", dbHandler.longitute(csr.getString(22)));
                    hashMap.put("Img_size", dbHandler.Img_size(csr.getString(23)));
                    hashMap.put("Vid_size", dbHandler.Vid_size(csr.getString(24)));
                    hashMap.put("Aud_size", dbHandler.Aud_size(csr.getString(25)));

                    arrReceive1.add(hashMap);
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrReceive1;
    }

    public ArrayList<HashMap<String, String>> get_Published_news(String Story_Type) {
        ArrayList<HashMap<String, String>> arrReceive1 = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
            query = "SELECT * FROM " + tbl_add_news_dynamic + " where Story_Type = '"+Story_Type+"' order by Time DESC";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  of dynamic data " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Id", dbHandler.Id(csr.getString(0)));
                    hashMap.put("Story_Type", dbHandler.Story_Type(csr.getString(1)));
                    hashMap.put("Heading", dbHandler.Heading(csr.getString(2)));
                    hashMap.put("Sub_heading", dbHandler.Sub_heading(csr.getString(3)));
                    hashMap.put("Ftp_directory", dbHandler.Body(csr.getString(4)));
                    hashMap.put("User_name", dbHandler.Body(csr.getString(5)));
                    hashMap.put("Time", dbHandler.Time(csr.getString(6)));
                    hashMap.put("PlaceName", dbHandler.PlaceName(csr.getString(7)));
                    hashMap.put("Nid", dbHandler.Nid(csr.getString(8)));
                    hashMap.put("publish_status_print", dbHandler.publish_status_print(csr.getString(9)));
                    hashMap.put("publish_status_tv", dbHandler.publish_status_tv(csr.getString(10)));
                    hashMap.put("publish_status_web", dbHandler.publish_status_web(csr.getString(11)));
                    hashMap.put("Thumbnail", dbHandler.Thumbnail(csr.getString(12)));

                    Log.e(TAG,"publish_status_web"+dbHandler.publish_status_web(csr.getString(11)));

                    arrReceive1.add(hashMap);
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrReceive1;
    }


    public ArrayList<HashMap<String, String>> get_All_Notifications() {
        ArrayList<HashMap<String, String>> arrReceive1 = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
            query = "SELECT * FROM " + tbl_add_notification+" order by Time DESC";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  of dynamic data " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Id", dbHandler.Id(csr.getString(0)));
                    hashMap.put("Nid", dbHandler.Story_Type(csr.getString(1)));
                    hashMap.put("Content_url", dbHandler.Story_Type(csr.getString(2)));
                    hashMap.put("Heading", dbHandler.Heading(csr.getString(3)));
                    hashMap.put("Time", dbHandler.Sub_heading(csr.getString(4)));
                    hashMap.put("PlaceName", dbHandler.Body(csr.getString(5)));
                    hashMap.put("Required_more", dbHandler.Body(csr.getString(6)));
                    hashMap.put("isRead", dbHandler.Body(csr.getString(7)));
                    hashMap.put("Sub_heading", dbHandler.Heading(csr.getString(8)));
                    hashMap.put("Story_Type", dbHandler.Story_Type(csr.getString(9)));
                    hashMap.put("notification_id", dbHandler.notification_id(csr.getString(10)));
                    arrReceive1.add(hashMap);
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrReceive1;
    }


    public ArrayList<HashMap<String, String>> getAll_News_By_Type(String Status_process,String Story_Type) {
        ArrayList<HashMap<String, String>> arrReceive1 = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";

              query = "SELECT * FROM " + tbl_add_news + " where Status_process = '"+Status_process+"' AND Story_Type = '"+Story_Type+"' order by "+MiliSec+" ASC";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Id", dbHandler.Id(csr.getString(0)));
                    hashMap.put("Status", dbHandler.Status(csr.getString(1)));
                    hashMap.put("Status_process", dbHandler.Status_process(csr.getString(2)));
                    hashMap.put("Story_Type", dbHandler.Story_Type(csr.getString(3)));
                    hashMap.put("Heading", dbHandler.Heading(csr.getString(4)));
                    hashMap.put("Sub_heading", dbHandler.Sub_heading(csr.getString(5)));
                    hashMap.put("Body", dbHandler.Body(csr.getString(6)));
                    hashMap.put("Introduction", dbHandler.Introduction(csr.getString(7)));
                    hashMap.put("Personality_topic", dbHandler.Personality_topic(csr.getString(8)));
                    hashMap.put("Priority", dbHandler.Priority(csr.getString(9)));
                    hashMap.put("Scope", dbHandler.Scope(csr.getString(10)));
                    hashMap.put("Category", dbHandler.Category(csr.getString(11)));
                    hashMap.put("Domain", dbHandler.Domain(csr.getString(12)));
                    hashMap.put("Time", dbHandler.Time(csr.getString(13)));
                    hashMap.put("PlaceName", dbHandler.PlaceName(csr.getString(14)));
                    hashMap.put("Nid", dbHandler.Nid(csr.getString(15)));
                    hashMap.put("MiliSec", ""+dbHandler.MiliSec(csr.getLong(16)));
                    hashMap.put("Thumbnail", dbHandler.Thumbnail(csr.getString(17)));
                    hashMap.put("publish_status_print", dbHandler.Thumbnail(csr.getString(18)));
                    hashMap.put("publish_status_tv", dbHandler.Thumbnail(csr.getString(19)));
                    hashMap.put("publish_status_web", dbHandler.Thumbnail(csr.getString(20)));
                    hashMap.put("latitute", dbHandler.latitute(csr.getString(21)));
                    hashMap.put("longitute", dbHandler.longitute(csr.getString(22)));
                    hashMap.put("Img_size", dbHandler.Img_size(csr.getString(23)));
                    hashMap.put("Vid_size", dbHandler.Vid_size(csr.getString(24)));
                    hashMap.put("Aud_size", dbHandler.Aud_size(csr.getString(25)));


                    arrReceive1.add(hashMap);
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrReceive1;
    }

    public void insert_published_News(HashMap<String,String> News_List,String type,boolean flag) {

        try {

            database = this.getWritableDatabase();

            if (flag)
                database.delete(tbl_add_news_dynamic, "Story_Type = '" + type + "'", null);

                String query = "SELECT * from " + tbl_add_news_dynamic + " where Nid = '" + News_List.get("Nid") + "'";
                Cursor cursor = database.rawQuery(query, null);
                DBHandler dbHandler = new DBHandler();

                if (cursor.getCount() > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Ftp_directory, ""+News_List.get("Ftp_directory"));
                    contentValues.put(User_name, ""+News_List.get("User_name"));
                    contentValues.put(Story_Type, ""+News_List.get("Story_Type"));
                    contentValues.put(Heading, ""+News_List.get("Heading"));
                    contentValues.put(Sub_heading, ""+News_List.get("Sub_heading"));
                    contentValues.put(Time, ""+News_List.get("Time"));
                    contentValues.put(PlaceName, ""+News_List.get("PlaceName"));
                    contentValues.put(Nid, ""+News_List.get("Nid"));

                    if (cursor.moveToFirst()) {
                        do {
                            if(dbHandler.Thumbnail(cursor.getString(12)).equals(""))
                                contentValues.put(Thumbnail, ""+News_List.get("Thumbnail"));

                            if(dbHandler.publish_status_print(cursor.getString(9)).equals("1") || dbHandler.publish_status_print(cursor.getString(9)).equals("") || dbHandler.publish_status_print(cursor.getString(9)).equals("0"))
                                contentValues.put(publish_status_print, ""+News_List.get("publish_status_print"));
                            if(dbHandler.publish_status_tv(cursor.getString(10)).equals("1") || dbHandler.publish_status_tv(cursor.getString(10)).equals("") || dbHandler.publish_status_tv(cursor.getString(10)).equals("0"))
                                contentValues.put(publish_status_tv, ""+News_List.get("publish_status_tv"));
                            if(dbHandler.publish_status_web(cursor.getString(11)).equals("1") || dbHandler.publish_status_web(cursor.getString(11)).equals("") || dbHandler.publish_status_web(cursor.getString(11)).equals("0"))
                                contentValues.put(publish_status_web, ""+News_List.get("publish_status_web"));

                        } while (cursor.moveToNext());
                        }

                    if (AppUtills.showLogs) Log.v(TAG, "publish_status_web in db after insert   " +News_List.get("publish_status_web"));

                    database.update(tbl_add_news_dynamic, contentValues, "Nid = " + News_List.get("Nid"), null);

                    if (AppUtills.showLogs) Log.v(TAG, "Data updated Successfully   ");

                } else {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Ftp_directory, ""+News_List.get("Ftp_directory"));
                    contentValues.put(User_name, ""+News_List.get("User_name"));
                    contentValues.put(Story_Type, ""+News_List.get("Story_Type"));
                    contentValues.put(Heading, ""+News_List.get("Heading"));
                    contentValues.put(Sub_heading, ""+News_List.get("Sub_heading"));
                    contentValues.put(Time, ""+News_List.get("Time"));
                    contentValues.put(PlaceName, ""+News_List.get("PlaceName"));
                    contentValues.put(Nid, ""+News_List.get("Nid"));
                    contentValues.put(publish_status_print, ""+News_List.get("publish_status_print"));
                    contentValues.put(publish_status_tv, ""+News_List.get("publish_status_tv"));
                    contentValues.put(publish_status_web, ""+News_List.get("publish_status_web"));
                    contentValues.put(Thumbnail, ""+News_List.get("Thumbnail"));

                    database.insert(tbl_add_news_dynamic, null, contentValues);


                    if (AppUtills.showLogs) Log.v(TAG, "publish_status_web in db after insert   " +News_List.get("publish_status_web"));

                    if (AppUtills.showLogs) Log.v(TAG, "Data Insertesd Successfully & thumb is "+News_List.get("Thumbnail"));

                }

            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getListSelected_news(String Id,String Story_Type,String News_id) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";

           if(!Id.equals("")) {
               int id = Integer.parseInt(Id);
               query = "SELECT * FROM " + tbl_add_news + " where  Id = '" + id + "' AND Story_Type = '" + Story_Type + "'";
           }else if(!News_id.equals(""))
               query = "SELECT * FROM " + tbl_add_news + " where  Nid = '" + News_id + "' AND Story_Type = '"+ Story_Type +"'";


            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    hashMap.put("Id", dbHandler.Id(csr.getString(0)));
                    hashMap.put("Status", dbHandler.Status(csr.getString(1)));
                    hashMap.put("Status_process", dbHandler.Status_process(csr.getString(2)));
                    hashMap.put("Story_Type", dbHandler.Story_Type(csr.getString(3)));
                    hashMap.put("Heading", dbHandler.Heading(csr.getString(4)));
                    hashMap.put("Sub_heading", dbHandler.Sub_heading(csr.getString(5)));
                    hashMap.put("Body", dbHandler.Body(csr.getString(6)));
                    hashMap.put("Introduction", dbHandler.Introduction(csr.getString(7)));
                    hashMap.put("Personality_topic", dbHandler.Personality_topic(csr.getString(8)));
                    hashMap.put("Priority", dbHandler.Priority(csr.getString(9)));
                    hashMap.put("Scope", dbHandler.Scope(csr.getString(10)));
                    hashMap.put("Category", dbHandler.Category(csr.getString(11)));
                    hashMap.put("Domain", dbHandler.Domain(csr.getString(12)));
                    hashMap.put("Time", dbHandler.Time(csr.getString(13)));
                    hashMap.put("PlaceName", dbHandler.PlaceName(csr.getString(14)));
                    hashMap.put("Nid", dbHandler.Nid(csr.getString(15)));
                    hashMap.put("MiliSec", ""+dbHandler.MiliSec(csr.getLong(16)));
                    hashMap.put("Thumbnail", dbHandler.Thumbnail(csr.getString(17)));
                    hashMap.put("publish_status_print", dbHandler.Thumbnail(csr.getString(18)));
                    hashMap.put("publish_status_tv", dbHandler.Thumbnail(csr.getString(19)));
                    hashMap.put("publish_status_web", dbHandler.Thumbnail(csr.getString(20)));
                    hashMap.put("latitute", dbHandler.latitute(csr.getString(21)));
                    hashMap.put("longitute", dbHandler.longitute(csr.getString(22)));
                    hashMap.put("Img_size", dbHandler.Img_size(csr.getString(23)));
                    hashMap.put("Vid_size", dbHandler.Vid_size(csr.getString(24)));
                    hashMap.put("Aud_size", dbHandler.Aud_size(csr.getString(25)));
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public String discard_Stored_data(String id,String flag) {

        database = this.getWritableDatabase();
        String thumbnail = "";
        try {

            if(flag.equals("not_publish")) {
                int Ids = Integer.parseInt(id);
                database.delete(tbl_add_news, "Id = " + Ids, null);
                thumbnail = "";
            }else if(flag.equals("publish"))
            {
                String   query = "SELECT * FROM " + tbl_add_news+" where Nid = '"+id+"'";

                Cursor csr = database.rawQuery(query, null);
                if (AppUtills.showLogs) Log.v("Cursor length", "cursor length where published news is deleted " + csr.getCount());
                if (csr.moveToFirst()) {
                    do {
                        DBHandler dbHandler = new DBHandler();
                        thumbnail = dbHandler.Id(csr.getString(17));
                        if (AppUtills.showLogs) Log.v("thumbnail", "thumbnail   "+thumbnail);

                    } while (csr.moveToNext());
                }
                csr.close();

                database.delete(tbl_add_news, "Nid = '" + id+"'", null);
                if (AppUtills.showLogs) Log.v("Row Deleted", "Row Deleted Successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return thumbnail;
    }


    public String getAll_News_InProcessNews() {
        String id = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
            query = "SELECT Id FROM " + tbl_add_news + " where Status = '"+ AppController_Patrika.Story_Status_process+"' AND Story_Type = '"+AppController_Patrika.Story_Type_Detailed+"'";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is where process is inproess " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    id = dbHandler.Id(csr.getString(0));
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public String get_Thumbnail(String News_id) {
        String Thumbnail = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";
            query = "SELECT Thumbnail FROM " + tbl_add_news + " where Nid = '"+News_id+"'";

            if (AppUtills.showLogs) Log.v("News_id", "News_id " + News_id);
            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is where process is inproess " + csr.getCount());
            if (csr.moveToFirst()) {
                do {
                    DBHandler dbHandler = new DBHandler();
                    Thumbnail = dbHandler.Thumbnail(csr.getString(0));
                } while (csr.moveToNext());
            }
            csr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Thumbnail;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.delete(tbl_add_news, null, null);
            if (AppUtills.showLogs) Log.w(TAG, "In On UPgrade");
            if (AppUtills.showLogs)
                Log.w(TAG + DBHandler.class.getName(), "Upgrading database from version "
                        + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
            String buildSQL = "DROP TABLE IF EXISTS " + tbl_add_news;
            db.execSQL(buildSQL);
            String buildSQL1 = "DROP TABLE IF EXISTS " + tbl_images;
            db.execSQL(buildSQL1);
            onCreate(db);
            if (AppUtills.showLogs) Log.w(TAG, "In On UPgrade1");
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void discard_all_data() {
        database = this.getWritableDatabase();

        try {
            database.delete(tbl_images, null, null);
            database.delete(tbl_add_news, null, null);
            database.delete(tbl_add_news_dynamic, null, null);
            database.delete(tbl_add_notification, null, null);
            database.delete(tbl_drop_downs_values, null, null);
            if (AppUtills.showLogs) Log.v("Row Deleted", "Row Deleted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public HashMap<String,String> check_if_news_exist(String Nid,String news_type,String Story_typee) {
        HashMap<String,String> hashMap = new HashMap<>();
        String id = "";
        String type = "";
        String tbl_name = "";

        if(news_type.equals("publish"))
            tbl_name = tbl_add_news_dynamic;
        else if(news_type.equals("not_publish"))
            tbl_name = tbl_add_news;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "";

          if(Story_typee.equals(""))
            query  = "select Id, Story_Type FROM " + tbl_name + " where  Nid = '" + Nid + "'";
            else
              query  = "select Id, Story_Type FROM " + tbl_name + " where  Nid = '" + Nid + "' AND Story_Type = '" + Story_typee + "'";

            Cursor csr = db.rawQuery(query, null);
            if (AppUtills.showLogs) Log.v("Cursor length", "cursor length is  " + csr.getCount());

            if (csr.moveToFirst()) {
                do {
                    id = csr.getString(0);
                    type = csr.getString(1);
                    if (AppUtills.showLogs) Log.v("Cursor length", "Id is  " + id);
                    if (AppUtills.showLogs) Log.v("Cursor length", "Id is  " + type);
                    hashMap.put("id",id);
                    hashMap.put("type",type);
                    break;
                } while (csr.moveToNext());

                csr.close();
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }
}