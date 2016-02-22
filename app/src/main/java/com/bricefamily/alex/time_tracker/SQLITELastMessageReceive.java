package com.bricefamily.alex.time_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by bricenangue on 22/02/16.
 */
public class SQLITELastMessageReceive extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "FriendsLastMessageDB";


    public SQLITELastMessageReceive(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INCOMING_TABLE = "CREATE TABLE FriendsLastMessage ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR NOT NULL, lastMessage TEXT NOT NULL, status VARCHAR NOT NULL, registrationId TEXT NOT NULL)";

        // create books table
        db.execSQL(CREATE_INCOMING_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS FriendsLastMessage");

        // create fresh books table
        onCreate(db);
    }

    // Books table name
    private static final String FRIENDS_LAST_MESSAGE = "FriendsLastMessage";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String LAST_MESSAGE = "lastMessage";
    private static final String KEY_STATUS = "status";
    private static final String KEY_REGID = "registrationId";


    private static final String[] COLUMNS = {KEY_ID, KEY_USERNAME, LAST_MESSAGE};


    public int addfriendLastMessage(String friendusername,String lastmsg,String status,String regId){

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, friendusername);
        values.put(LAST_MESSAGE, lastmsg);
        values.put(KEY_STATUS, status);
        values.put(KEY_REGID, regId);

        // 3. insert
        int i= (int) db.insert(FRIENDS_LAST_MESSAGE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return i;
    }


    public int updatefriendLastMessage(String regId,String friendusername,String lastmsg,String status) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, friendusername);
        values.put(LAST_MESSAGE, lastmsg);
        values.put(KEY_STATUS, status);


        // 3. updating row
        int i = db.update(FRIENDS_LAST_MESSAGE, //table
                values, // column/value
                "registrationId = ?", // selections
                new String[] { regId }); //selection args

        // 4. close
        db.close();

        return i;

    }


    public LastMessage getfriendLastMessage(String regId){

        String u = "";
        String m = "";
        String s = "";
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT username, lastMessage, status FROM " + FRIENDS_LAST_MESSAGE +" WHERE registrationId = ? ORDER BY id DESC LIMIT 1";
        // 2. build query
        Cursor cursor =db.rawQuery(query, new String[]{regId});

        LastMessage lastMessage=null;
        // 3. if we got results get the first one
        if (cursor != null && cursor.moveToFirst()){
            u=cursor.getString(0);
             m=cursor.getString(1);
             s=cursor.getString(2);

            lastMessage=new LastMessage(u,m,s);

            cursor.close();
        }


        return lastMessage;
    }


    public void deletelastmessage(String regId) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(FRIENDS_LAST_MESSAGE, //table name
                KEY_REGID+" = ?",  // selections
                new String[] { regId }); //selections args

        // 3. close
        db.close();

    }
}
