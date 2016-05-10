package com.bricefamily.alex.time_tracker;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.bricefamily.alex.time_tracker.com.bricefamily.alex.app.gcm.server.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewEventFragment extends AppCompatActivity implements DialogFragmentDatePicker1.OnDateGet,DialogFragmentTimePicker.OnTimeSet, View.OnClickListener {
    private String dateSet="", startdatetime,enddatetime;
    private CalendarCollection calendarCollection;
    private  EditText dateed,titleed;
    private Button btnstartdate,btnstarttime, btnenddate,btnendtime;
    private CheckBox checkboxadd,checkBoxeverymonth;
    private Spinner spinner;
    private boolean fromCalendar=false;
    private UserLocalStore userLocalStore;
    int categoryname,hour,minute,day,month,year;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_events);


        mySQLiteHelper=new MySQLiteHelper(this);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            categoryname=extras.getInt("category");
            if(extras.containsKey("fromCalendar")){
                fromCalendar=extras.getBoolean("fromCalendar");
                day=extras.getInt("day");
                month=extras.getInt("month");
                year=extras.getInt("year");
                hour=extras.getInt("hour");
                minute=extras.getInt("minute");
            }
        }

        userLocalStore=new UserLocalStore(this);
        final EditText descriptioned=(EditText)findViewById(R.id.eddescription);
        titleed=(EditText)findViewById(R.id.edtitle);
        btnstartdate=(Button)findViewById(R.id.dateaddeventstart);
        btnstarttime=(Button)findViewById(R.id.timeaddeventstart);
        btnenddate=(Button)findViewById(R.id.dateaddeventend);
        btnendtime=(Button)findViewById(R.id.timeaddeventend);
        checkboxadd=(CheckBox)findViewById(R.id.checkboxalldayevent);
        checkBoxeverymonth=(CheckBox)findViewById(R.id.checkboxeverymonthevent);
        spinner=(Spinner)findViewById(R.id.edspinnercategory);
        btnstartdate.setOnClickListener(this);
        btnstarttime.setOnClickListener(this);
        btnenddate.setOnClickListener(this);
        btnendtime.setOnClickListener(this);
        checkboxadd.setOnClickListener(this);

         final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        String dgdh[]=curTime.split(",");
        String [] userAccArray={"Normal","Business","Birthdays","Grocery","Work Plans"};
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);
        spinner.setSelection(categoryname);

        if(fromCalendar){
            StringBuilder builder=new StringBuilder();
            builder.append(year).append("-");
            if(String.valueOf(month).length()==1){
                builder.append("0").append(month).append("-");
            }else {
                builder.append(month).append("-");
            }
            if(String.valueOf(day).length()==1){
                builder.append("0").append(day);
            }else {
                builder.append(day);
            }
            btnstartdate.setText(builder.toString());
            btnenddate.setText(builder.toString());

            builder=new StringBuilder();
            builder.append(hour).append(":").append(minute);
            btnstarttime.setText(builder.toString());
            btnendtime.setText(builder.toString());

        }else {
            btnstartdate.setText(dgdh[0]);
            btnstarttime.setText(dgdh[1]);
            btnenddate.setText(dgdh[0]);
            btnendtime.setText(dgdh[1]);
        }

        if(!btnstartdate.getText().toString().equals(btnenddate.getText().toString())){
            checkboxadd.setEnabled(false);
        }else {
            checkboxadd.setEnabled(true);
        }

        Button savebtn=(Button)findViewById(R.id.buttonsavenewevent);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String allday,everymonth;
                if(checkboxadd.isChecked()){
                    allday="1";
                    btnstartdate.setEnabled(false);
                    btnstarttime.setEnabled(false);
                    btnenddate.setEnabled(false);
                    btnendtime.setEnabled(false);


                }else {
                    allday="0";
                    btnstartdate.setEnabled(true);
                    btnstarttime.setEnabled(true);
                    btnenddate.setEnabled(true);
                    btnendtime.setEnabled(true);
                }
                if(checkBoxeverymonth.isChecked()){
                    everymonth="1";
                }else {
                    everymonth="0";
                }
                Calendar c = Calendar.getInstance();

                Date currentDate = new Date();
                String currentTime = formatter.format(currentDate);

                int eventHashcode=(dateSet+userLocalStore.getUserfullname()+formatter.format(c.getTime())).hashCode();
                startdatetime=btnstartdate.getText().toString() +" "+btnstarttime.getText().toString();
                enddatetime=btnenddate.getText().toString() +" "+btnendtime.getText().toString();
                String fullname=userLocalStore.getUserfullname();

                dateSet=titleed.getText().toString();
                calendarCollection=new CalendarCollection(dateSet,descriptioned.getText().toString(),
                       fullname,btnstartdate.getText().toString(),startdatetime,
                        enddatetime,String.valueOf(eventHashcode),spinner.getSelectedItem().toString(),allday,everymonth,currentTime);
                saveEvent(calendarCollection);

            }
        });
    }


    private  ArrayList<User> getUsers(ArrayList<IncomingNotification> incomingNotifications){

        ArrayList<User> arrayList=new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            try {
                jo_inside = new JSONObject(incomingNotifications.get(i).body);

                String username = jo_inside.getString("username");
                String email = jo_inside.getString("email");
                String password = jo_inside.getString("password");
                String firstname = jo_inside.getString("firstname");
                String lastname = jo_inside.getString("lastname");
                String regId = jo_inside.getString("regId");
                String friendlist = jo_inside.getString("friendlist");
                String picture = jo_inside.getString("picture");
                Bitmap bitmap=ServerRequests.decodeBase64(picture);

                int status = jo_inside.getInt("status");

                User  object =new User(username,email,password,firstname,lastname,status,regId,bitmap,friendlist);
                arrayList.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return arrayList;
    }


    void saveEvent(final CalendarCollection collection){
       ServerRequests serverRequests=new ServerRequests(this);
       serverRequests.saveCalenderEventInBackgroung(collection, new GetEventsCallbacks() {
           @Override
           public void done(ArrayList<EventObject> returnedeventobject) {

           }

           @Override
           public void donec(ArrayList<CalendarCollection> returnedeventobject) {

           }

           @Override
           public void updated(String reponse) {
               if (reponse.contains("Event added successfully")) {

                   ArrayList<User> arrayList=getUsers(mySQLiteHelper.getAllIncomingNotificationUsers());
                   StringBuilder builder=new StringBuilder();
                   User user=userLocalStore.getLoggedInUser();
                   for(int i=0;i<arrayList.size();i++){
                       builder.append(arrayList.get(i).regId).append(" ");
                   }
                   String registrationIds=builder.toString();

                   String[] args={registrationIds,user.username,user.email,"New Event","new Event added by "+user.username+" .Have a look!"};
                   App.main(args);

                   Intent intent=new Intent(AddNewEventFragment.this,AddNewEventActivity.class);
                   intent.putExtra("savedEvent",collection);
                   intent.putExtra("fromcalender",fromCalendar);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
               }
           }
       });

   }

    @Override
    public void dateSet(String date,boolean isstart) {

        if(isstart){
            btnstartdate.setText(date);
            btnenddate.setText(date);
            checkboxadd.setEnabled(true);

        }else {
            btnenddate.setText(date);
            if(date.equals(btnstartdate.getText().toString())){
                checkboxadd.setEnabled(true);
            }else{
                checkboxadd.setEnabled(false);
            }

        }
    }

    @Override
    public void timeSet(String time,boolean isstarttime) {
        if(isstarttime){
            btnstarttime.setText(time);
        }else {
            btnendtime.setText(time);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dateaddeventstart:
                onDatePickercliced(true);
                break;
            case R.id.timeaddeventstart:
                onTimePickercliced(true);
                break;
            case R.id.dateaddeventend:
                onDatePickercliced(false);
                break;
            case R.id.timeaddeventend:
                onTimePickercliced(false);
                break;
            case R.id.checkboxalldayevent:
                if(checkboxadd.isChecked()){

                    btnstartdate.setEnabled(false);
                    btnstarttime.setEnabled(false);
                    btnenddate.setEnabled(false);
                    btnendtime.setEnabled(false);
                    btnenddate.setText(btnstartdate.getText().toString());
                    btnstarttime.setText("00:00");
                    btnendtime.setText("23:59");

                }else {

                    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                    formatter.setLenient(false);

                    Date curDate = new Date();
                    String curTime = formatter.format(curDate);
                    String dgdh[]=curTime.split(",");

                    btnstartdate.setText(dgdh[0]);
                    btnstarttime.setText(dgdh[1]);
                    btnenddate.setText(dgdh[0]);
                    btnendtime.setText(dgdh[1]);
                    btnstartdate.setEnabled(true);
                    btnstarttime.setEnabled(true);
                    btnenddate.setEnabled(true);
                    btnendtime.setEnabled(true);
                }
                break;
        }
    }


    public void onTimePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentTimePicker.newInstance(bol);
        fragmentDatePicker.show(manager,"timePickerfr");
    }

    public void onDatePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker1.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }



}
