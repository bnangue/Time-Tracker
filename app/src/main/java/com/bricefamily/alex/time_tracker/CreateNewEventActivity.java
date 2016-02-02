package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateNewEventActivity extends AppCompatActivity {

    private TextView currenttime;
    private EditText titeled,detailed,dateed,noteed,creatornameed;
    private String titelstr,detailsstr,notestr,creatornamestr,datestr,status,currenttimestr;
    EventObject eventObject;
    DateEventObject dateEventObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        creatornameed= (EditText)findViewById(R.id.editTexteventcreator);
        creatornameed.setText(creatornamestr);
    }

    public void buttonCreateEventListener(View view){
        titelstr=titeled.getText().toString();
        detailsstr=detailed.getText().toString();
        notestr=noteed.getText().toString();
        datestr=dateed.getText().toString();
        creatornamestr=creatornameed.getText().toString();

        String[] date=datestr.split("[.]");
        dateEventObject=new DateEventObject(date[0],date[1],date[2]);
        status="1";
        eventObject=new EventObject(titelstr,detailsstr,creatornamestr,currenttimestr
                ,dateEventObject,status);

        createEvents(eventObject);
    }
    private void createEvents(EventObject eve){
        ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.createEventinBackground(eve, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                finish();
                Intent  intent=new Intent(CreateNewEventActivity.this,CentralPageActivity.class);
                intent.putExtra("username",creatornamestr);
                startActivity(intent);
            }
        });
        Toast.makeText(getApplicationContext(),"Event created",Toast.LENGTH_SHORT).show();
    }
}
