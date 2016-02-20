package com.bricefamily.alex.time_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by bricenangue on 20/02/16.
 */
public class SQLPictureHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "FriendsPictureDB";


    public SQLPictureHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INCOMING_TABLE = "CREATE TABLE FriendsPictures ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR NOT NULL, userPicture BLOB NOT NULL)";

        // create books table
        db.execSQL(CREATE_INCOMING_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS FriendsPictures");

        // create fresh books table
        onCreate(db);
    }

    // Books table name
    private static final String FRIENDS_PICTURES = "FriendsPictures";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PICTURE = "userPicture";


    private static final String[] COLUMNS = {KEY_ID, KEY_USERNAME, KEY_PICTURE};


    public int addfriendPicture(String friendusername,Bitmap picture){
        //for logging
       // Log.d("addNotificationIncome", notification.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        byte[] friendpicture=getBytes(picture);

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, friendusername);
        values.put(KEY_PICTURE, friendpicture);

        // 3. insert
        int i= (int) db.insert(FRIENDS_PICTURES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return i;
    }


    public int updatefriendPicture(String friendusername,Bitmap picture) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        byte[] friendsPicture=getBytes(picture);
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PICTURE, friendsPicture);


        // 3. updating row
        int i = db.update(FRIENDS_PICTURES, //table
                values, // column/value
                "username = ?", // selections
                new String[] { friendusername }); //selection args

        // 4. close
        db.close();

        return i;

    }


    public Bitmap getfriendPicture(String friendusername){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT userPicture FROM " + FRIENDS_PICTURES +" WHERE username = ?";
        // 2. build query
        Cursor cursor =db.rawQuery(query, new String[]{friendusername});

        Bitmap bitmap=null;
        // 3. if we got results get the first one
        if (cursor != null&&cursor.moveToFirst()){
            byte[] bytes= cursor.getBlob(0);
            bitmap= getImage(bytes);
            cursor.close();
        }

        return bitmap;
    }


    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();

    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void deleteIncomingNotification(String friendUsername) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(FRIENDS_PICTURES, //table name
                KEY_USERNAME+" = ?",  // selections
                new String[] { friendUsername }); //selections args

        // 3. close
        db.close();

    }
}
