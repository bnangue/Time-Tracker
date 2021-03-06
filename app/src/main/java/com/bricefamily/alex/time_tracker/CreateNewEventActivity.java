package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bricefamily.alex.time_tracker.com.bricefamily.alex.app.gcm.server.App;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateNewEventActivity extends ActionBarActivity implements DatePickerFragment.OnDateGetActivity, TextView.OnEditorActionListener {

    private TextView currenttime;
    private EditText titeled,detailed,dateed,noteed,creatornameed;
    private String titelstr,detailsstr,notestr,creatornamestr,datestr,status,currenttimestr;
    EventObject eventObject;
    DateEventObject dateEventObject;
    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        prepareView();

        userLocalStore=new UserLocalStore(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        currenttimestr=formattedDate;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            creatornamestr = extras.getString("username");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        currenttime=(TextView)findViewById(R.id.textVieweventcurrenttime);
        currenttime.setText(currenttimestr);
        titeled= (EditText)findViewById(R.id.editTexteventtitel);
        detailed= (EditText)findViewById(R.id.editTexteventdetails);
        dateed= (EditText)findViewById(R.id.editTexteventdate);
        noteed= (EditText)findViewById(R.id.editTexteventnote);
        noteed.setOnEditorActionListener(this);
        creatornameed= (EditText)findViewById(R.id.editTexteventcreator);
        creatornameed.setText(creatornamestr);
    }

    public void prepareView() {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.actionbarbackground, null);


        //center des ActionBar Titles
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

        try {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setCustomView(view, params);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cellSelected)));
        } catch (NullPointerException e) {
            Log.w("ActionBar Error", e.getMessage());
        }
        try {
            //ab Android 5.0
            ab.setElevation(0);
        } catch (NullPointerException e) {
            Log.w("ActionBar Error", e.getMessage());
        }

    }

    public void buttonCreateEventListener(View view){
        titelstr=titeled.getText().toString();
        detailsstr=detailed.getText().toString();
        notestr=noteed.getText().toString();
        datestr=dateed.getText().toString();
        creatornamestr=creatornameed.getText().toString();

        if(titelstr.isEmpty() || datestr.isEmpty()|| creatornamestr.isEmpty()||notestr.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please fill empty filed",Toast.LENGTH_SHORT).show();
        }else{
            String[] date=datestr.split("[.]");
            dateEventObject=new DateEventObject(date[0],date[1],date[2]);
            status="1";
            int eventHashcode=(titelstr+creatornamestr+currenttimestr).hashCode();

            eventObject=new EventObject(titelstr,detailsstr,creatornamestr,currenttimestr
                    ,dateEventObject,status,String.valueOf(eventHashcode));

            createEvents(eventObject);
        }
    }
    private void createEvents(EventObject eve){
        ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.createEventinBackground(eve, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                showdialg(creatornamestr);
            }

            @Override
            public void updated(String reponse) {

                if(reponse.contains("Event added successfully")){
                    User user=userLocalStore.getLoggedInUser();
                    String registrationId=userLocalStore.getUserRegistrationId();

                    String[] args={registrationId,user.username,user.email,"New Event","new Event added by "+user.username+" .Have a look!"};
                    App.main(args);
                }

            }
        });
    }
    public void onDatePickerclicked(View view){
        DialogFragment fragmentDatePicker=new DatePickerFragment();
        fragmentDatePicker.show(getSupportFragmentManager(),"datePickerActivty");
    }
    void startCentralPage(String username){
        getEventsFromDatabase(creatornamestr);
    }
    private void showdialg(String username){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setMessage("New Event successfully created by "+ username);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                titeled.setText("");
                detailed.setText("");
                dateed.setText("");
                noteed.setText("");
            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {
       getEventsFromDatabase(creatornamestr);
    }
    void  getEventsFromDatabase(final String username){

        ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(CreateNewEventActivity.this, CentralPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username",username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    @Override
    public void dateSetactivity(int year, int month, int day) {
        dateed.setText(new StringBuilder().append(day).append(".")
        .append(month +1).append(".").append(year));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            titelstr=titeled.getText().toString();
            detailsstr=detailed.getText().toString();
            notestr=noteed.getText().toString();
            datestr=dateed.getText().toString();
            creatornamestr=creatornameed.getText().toString();

            if(titelstr.isEmpty() || datestr.isEmpty()|| creatornamestr.isEmpty()||notestr.isEmpty()){
                Toast.makeText(getApplicationContext(),"Please fill empty filed",Toast.LENGTH_SHORT).show();
            }else{
                String[] date=datestr.split("[.]");
                dateEventObject=new DateEventObject(date[0],date[1],date[2]);
                status="1";
                int eventHashcode=(titelstr+creatornamestr+currenttimestr).hashCode();

                eventObject=new EventObject(titelstr,detailsstr,creatornamestr,currenttimestr
                        ,dateEventObject,status,String.valueOf(eventHashcode));

                createEvents(eventObject);
            }
        }
        return false;
    }
}
