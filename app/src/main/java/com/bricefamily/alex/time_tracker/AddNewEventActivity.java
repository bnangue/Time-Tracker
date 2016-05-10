package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewEventActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdaterAdddNewEventListButton.OnButtonString, View.OnClickListener {

    private Button button;
    private AdaterAdddNewEventListButton adapter;
    private ListView listView;
    private ArrayList<String> categorylist;
    private String categoryname;
    private TextView eventpriode,creatorname,createdtime,notes,descriptionexpand;
    public static ArrayList<CalendarCollection> collections=new ArrayList<>();
    private CalendarCollection collection;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean fromCalendar=false;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "RecyclerViewActivity";

    private android.support.v7.app.AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        mySQLiteHelper=new MySQLiteHelper(this);
        alertDialog = new android.support.v7.app.AlertDialog.Builder(AddNewEventActivity.this).create();
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("savedEvent")){
                collection=extras.getParcelable("savedEvent");
                collections.add(collection);
                fromCalendar=extras.getBoolean("fromcalender");
            }
        }

        categorylist=new ArrayList<>();
        String [] userAccArray={"Normal","Business","Birthdays","Grocery","Work Plans"};
        for(int i=0;i<userAccArray.length;i++){
            categorylist.add(userAccArray[i]);
        }
        button=(Button)findViewById(R.id.buttoncountaddedEventactivity);
        button.setText(collections.size()+ " added");
        button.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.addeventbuttonlistview);
        adapter=new AdaterAdddNewEventListButton(this,categorylist,this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void returnedCalenderEvent(ArrayList<CalendarCollection> calendarCollections) {
        if(fromCalendar){
            Intent intent=new Intent(AddNewEventActivity.this,BaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            collections.clear();
            finish();
        }else {
            super.onBackPressed();
            collections.clear();
            finish();
        }

    }

    @Override
    public void buttonString(int position) {
        Intent intent=new Intent(AddNewEventActivity.this,AddNewEventFragment.class);
        intent.putExtra("category", position);
        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        saveeventtoSQl(collections);
        returnedCalenderEvent(collections);
    }

    void showDialogListCalendarEvent(ArrayList<CalendarCollection> collectionsevent){



        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_layout_dailog_calendar_event, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Added Events");
        mRecyclerView = (RecyclerView) convertView.findViewById(R.id.my_recycler_view);
        Button btn = (Button) convertView.findViewById(R.id.buttonOkarlertCalendarevent);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                button.setText(collections.size()+ " added");

            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(this, collections, new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                setViews(v,position);
            }

            @Override
            public void onButtonClick(int position, View v) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        showDialogListCalendarEvent(collections);


    }

    private void setViews(View v, int position){
        creatorname = (TextView) v.findViewById(R.id.textViewexpandcreator);
        createdtime = (TextView) v.findViewById(R.id.textViewexpandcreationtime);
        eventpriode = (TextView) v.findViewById(R.id.textViewexpandperiode);
        descriptionexpand = (TextView) v.findViewById(R.id.textViewexpanddescription);
        notes = (TextView) v.findViewById(R.id.textViewexpandnote);



        if(collections.size()!=0){
            CalendarCollection ecollection=collections.get(position);
            creatorname.setText(ecollection.creator);
            createdtime.setText(ecollection.creationdatetime);
            descriptionexpand.setText(ecollection.description);

            String[] sttime=ecollection.startingtime.split(" ");
            String[] edtime=ecollection.endingtime.split(" ");

            eventpriode.setText(sttime[0]+"  -  "+edtime[0]);
            StringBuilder builder=new StringBuilder();
            if(ecollection.alldayevent.equals("1")){
                builder.append("All day");
            }
            if(ecollection.alldayevent.equals("1")){
                builder.append(",").append(" repeat every month");
            }
            if(builder.toString().isEmpty()){
                notes.setText("");
            }else{
                notes.setText(builder.toString());
            }

        }
    }
}
