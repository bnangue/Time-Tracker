package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
    private ProgressBar loadprogressBar;
    public static boolean eventsareloaded=false;

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);

        loadprogressBar=(ProgressBar)findViewById(R.id.prbar);
        loadprogressBar.setVisibility(View.VISIBLE);

        mySQLiteHelper=new MySQLiteHelper(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutmainactivity);
        startapp();



    }
    void startapp(){
        if (haveNetworkConnection()){
            mySQLiteHelper.reInitializeSqliteTable();
            mySQLiteHelper.reInitializeSqliteUsers();
            getEventsfromMySQL();

        }else{
            showSnackBar();
            startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void getEventsfromMySQL() {
        final ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.getCalenderEventInBackgroung(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {

            }

            @Override
            public void donec(final ArrayList<CalendarCollection> returnedeventobject) {
                if(returnedeventobject.size()!=0){

                    serverRequests.fetchallUserstoSQL(new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {

                        }

                        @Override
                        public void deleted(String reponse) {

                        }

                        @Override
                        public void serverReponse(String reponse) {

                        }

                        @Override
                        public void userlist(ArrayList<User> reponse) {
                            if(reponse.size()!=0){
                                saveUserstoSQl(reponse);
                                saveeventtoSQl(returnedeventobject);
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                eventsareloaded=true;
                            }else {
                                showSnackBar();
                                eventsareloaded=false;
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }

                        }
                    });


                }else {
                    showSnackBar();
                    eventsareloaded=false;
                    loadprogressBar.setIndeterminate(false);
                    loadprogressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    private void saveeventtoSQl(ArrayList<CalendarCollection> calendarCollections) {

        if(calendarCollections.size()!=0){

        }
        for(int i=0;i<calendarCollections.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("title",calendarCollections.get(i).title);
                jsonObject.put("description",calendarCollections.get(i).description);
                jsonObject.put("datetime",calendarCollections.get(i).datetime);
                jsonObject.put("creator",calendarCollections.get(i).creator);
                jsonObject.put("category",calendarCollections.get(i).category);
                jsonObject.put("startingtime",calendarCollections.get(i).startingtime);
                jsonObject.put("endingtime",calendarCollections.get(i).endingtime);
                jsonObject.put("hashid",calendarCollections.get(i).hashid);
                jsonObject.put("alldayevent",calendarCollections.get(i).alldayevent);
                jsonObject.put("everymonth",calendarCollections.get(i).everymonth);
                jsonObject.put("defaulttime",calendarCollections.get(i).creationdatetime);

                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                incomingNotification=new IncomingNotification(0,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }

    private void saveUserstoSQl(ArrayList<User> userArrayList) {

        for(int i=0;i<userArrayList.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("username",userArrayList.get(i).username);
                jsonObject.put("email",userArrayList.get(i).email);
                jsonObject.put("password",userArrayList.get(i).password);
                jsonObject.put("firstname",userArrayList.get(i).firstname);
                jsonObject.put("lastname",userArrayList.get(i).lastname);
                jsonObject.put("regId",userArrayList.get(i).regId);
                jsonObject.put("friendlist",userArrayList.get(i).friendlist);
                jsonObject.put("picture",getStringImage(userArrayList.get(i).picture));
                jsonObject.put("status",userArrayList.get(i).status);


                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                incomingNotification=new IncomingNotification(6,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }

    public String getStringImage(Bitmap bmp){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String temp= Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return temp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, "No connection internet detected.", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
