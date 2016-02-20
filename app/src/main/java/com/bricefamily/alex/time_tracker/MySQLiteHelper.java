package com.bricefamily.alex.time_tracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bricenangue on 19/02/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "IncomingNotificationDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_INCOMING_TABLE = "CREATE TABLE IncomingNotification ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER NOT NULL, readStatus INTEGER NOT NULL, " + "body TEXT NOT NULL, "+ "creationDate TEXT NOT NULL )";

        // create books table
        db.execSQL(CREATE_INCOMING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS IncomingNotification");

        // create fresh books table
        onCreate(db);
    }

    // Books table name
    private static final String TABLE_BOOKS = "IncomingNotification";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_STATUS = "readStatus";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATE = "creationDate";

    private static final String[] COLUMNS = {KEY_ID,KEY_TYPE, KEY_STATUS,  KEY_BODY, KEY_DATE};


    public void addIncomingNotification(IncomingNotification notification){
        //for logging
        Log.d("addNotificationIncome", notification.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, notification.type);
        values.put(KEY_STATUS, notification.readStatus);
        values.put(KEY_BODY, notification.body); // get title
        values.put(KEY_DATE, notification.creationDate); // get author

        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ArrayList<IncomingNotification> getAllIncomingNotification() {
        ArrayList<IncomingNotification> incomingNotifications = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOOKS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        IncomingNotification incomingNotification = null;
        if (cursor.moveToFirst()) {
            do {
                incomingNotification = new IncomingNotification();
                int iD=Integer.parseInt(cursor.getString(0));
                int tyPe=Integer.parseInt(cursor.getString(1));
                int readSttatus=Integer.parseInt(cursor.getString(2));
                incomingNotification.id=iD;
                incomingNotification.type=tyPe;
                incomingNotification.readStatus=readSttatus;
                incomingNotification.body=cursor.getString(3);
                incomingNotification.creationDate=cursor.getString(4);

                // Add book to books
                incomingNotifications.add(incomingNotification);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", incomingNotifications.toString());

        // return books
        return incomingNotifications;
    }

    public int updateIncomingNotification(IncomingNotification incoming) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, incoming.id);
        values.put(KEY_TYPE, incoming.type);
        values.put(KEY_STATUS, incoming.readStatus);
        values.put(KEY_BODY, incoming.body); // get title
        values.put(KEY_DATE, incoming.creationDate); // get author// get title

        // 3. updating row
        int i = db.update(TABLE_BOOKS, //table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(incoming.id) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public int updateIncomingMessage(int readStatus,int type) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, readStatus);

        // 3. updating row
        int i = db.update(TABLE_BOOKS, //table
                values, // column/value
                "type = ?", // selections
                new String[] { String.valueOf(type) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteIncomingNotification(IncomingNotification incomingNotification) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(incomingNotification.id) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteBook", incomingNotification.toString());

    }


}
