package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener{
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private ArrayList<WeekViewEvent> mNewEvents;
    private android.support.v7.app.AlertDialog alertDialog;
    private ArrayList<String> optionlist;


    private ArrayList<CalendarCollection> mEvents =new ArrayList<>();
    private MySQLiteHelper mySQLiteHelper;
    int size=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mySQLiteHelper=new MySQLiteHelper(this);

        alertDialog = new android.support.v7.app.AlertDialog.Builder(BaseActivity.this).create();

        CalendarCollection.date_collection_arr=new ArrayList<>();
        getEvents(mySQLiteHelper.getAllIncomingNotification());

        CalendarCollection.date_collection_arr=mEvents;
        size=CalendarCollection.date_collection_arr.size();
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getEvents(ArrayList<IncomingNotification> incomingNotifications){

        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            if(incomingNotifications.get(i).type==0){
                try {
                    jo_inside = new JSONObject(incomingNotifications.get(i).body);

                    String titel = jo_inside.getString("title");
                    String infotext = jo_inside.getString("description");
                    String creator = jo_inside.getString("creator");
                    String creationTime = jo_inside.getString("datetime");
                    String category = jo_inside.getString("category");
                    String startingtime = jo_inside.getString("startingtime");
                    String endingtime = jo_inside.getString("endingtime");
                    String alldayevent = jo_inside.getString("alldayevent");
                    String eventHash = jo_inside.getString("hashid");
                    String everymonth = jo_inside.getString("everymonth");
                    String creationdatetime = jo_inside.getString("defaulttime");

                    CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);
                    object.incomingnotifictionid = incomingNotifications.get(i).id;

                    mEvents.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                String time =hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
                if(time.equals("0 PM")){
                    time = "12 PM";
                }
                return time;
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event at %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        mNewEvents.remove(event);
        mWeekView.notifyDatasetChanged();
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        showPopmenu(event,new String[]{"Edit", "Delete"},null);
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        showPopmenu(null,new String[]{"Add"}, time);
        //showFilterPopup(mWeekView,time);
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {// newMonth 1-based Month Calender-API 0base==> january =0
        // Populate the week view with some events.


       // List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        mNewEvents=new ArrayList<>();

        Calendar startTime;

        Calendar endTime ;

        WeekViewEvent ev;


        CalendarCollection collection;


        for (int i=0;i<mEvents.size();i++){

            collection=collectionForWeekview(mEvents.get(i));

            if(collection.everymonth.equals("1")){
                startTime = Calendar.getInstance();
                startTime.set(Calendar.DAY_OF_MONTH, collection.startday);
                startTime.set(Calendar.HOUR_OF_DAY, collection.starthour);
                startTime.set(Calendar.MINUTE, collection.startminute);
                startTime.set(Calendar.SECOND, 0);
                startTime.set(Calendar.MILLISECOND, 0);
                startTime.set(Calendar.MONTH, newMonth-1);
                startTime.set(Calendar.YEAR, collection.startyear);

                endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.DAY_OF_MONTH, collection.endday);
                endTime.set(Calendar.HOUR_OF_DAY, collection.endhour);
                endTime.set(Calendar.MONTH, newMonth-1);



                ev = new WeekViewEvent(collection.incomingnotifictionid, collection.title, startTime, endTime);
                switch (collection.category){
                    case "Normal":
                        ev.setColor(getResources().getColor(R.color.event_color_01));
                        mNewEvents.add(ev);
                        break;
                    case "Business":
                        ev.setColor(getResources().getColor(R.color.event_color_02));
                        mNewEvents.add(ev);
                        break;
                    case "Birthdays":
                        ev.setColor(getResources().getColor(R.color.event_color_03));
                        mNewEvents.add(ev);
                        break;
                    case "Grocery":
                        ev.setColor(getResources().getColor(R.color.event_color_04));
                        mNewEvents.add(ev);

                        break;
                    case "Work Plans":
                        ev.setColor(getResources().getColor(R.color.event_color_05));
                        mNewEvents.add(ev);

                        break;
                }
            }else {
                if(newMonth == collection.startmonth){

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, collection.startday);
                    startTime.set(Calendar.HOUR_OF_DAY, collection.starthour);
                    startTime.set(Calendar.MINUTE, collection.startminute);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);
                    startTime.set(Calendar.MONTH, collection.startmonth-1);
                    startTime.set(Calendar.YEAR, collection.startyear);

                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.DAY_OF_MONTH, collection.endday);
                    endTime.set(Calendar.HOUR_OF_DAY, collection.endhour);
                    endTime.set(Calendar.MINUTE, collection.endminute);
                    endTime.set(Calendar.MONTH, collection.endmonth-1);
                    endTime.set(Calendar.YEAR, collection.endyear);


                    ev = new WeekViewEvent(collection.incomingnotifictionid, collection.title, null, startTime, endTime);
                    switch (collection.category){
                        case "Normal":
                            ev.setColor(getResources().getColor(R.color.event_color_01));
                            mNewEvents.add(ev);
                            break;
                        case "Business":
                            ev.setColor(getResources().getColor(R.color.event_color_02));
                            mNewEvents.add(ev);
                            break;
                        case "Birthdays":
                            ev.setColor(getResources().getColor(R.color.event_color_03));
                            mNewEvents.add(ev);
                            break;
                        case "Grocery":
                            ev.setColor(getResources().getColor(R.color.event_color_04));
                            mNewEvents.add(ev);

                            break;
                        case "Work Plans":
                            ev.setColor(getResources().getColor(R.color.event_color_05));
                            mNewEvents.add(ev);

                            break;
                    }

                }
            }

        }
        // All day event until 00:00 next day





        return mNewEvents;
    }

    private void showFilterPopup(View v, final Calendar time) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.calendar_popup_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_create_new:
                        Intent intent=new Intent(BaseActivity.this,AddNewEventFragment.class);
                        intent.putExtra("hour", time.get(Calendar.HOUR_OF_DAY));
                        intent.putExtra("minute", time.get(Calendar.MINUTE));
                        intent.putExtra("month", time.get(Calendar.MONTH)+1);
                        intent.putExtra("day", time.get(Calendar.DAY_OF_MONTH));
                        intent.putExtra("year", time.get(Calendar.YEAR));
                        intent.putExtra("fromCalendar", true);
                        startActivity(intent);
                        return true;
                    case R.id.menu_delete:
                        Toast.makeText(getApplicationContext(), "removed as friend", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }



    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }


    private CalendarCollection collectionForWeekview(CalendarCollection calendarCollection){
        CalendarCollection collection=calendarCollection;

        String[]startdatetime=collection.startingtime.split(" ");

        String[] sdatet=startdatetime[0].split("-");

        String sdday=sdatet[2];
        String sdmounth=sdatet[1];
        String sdyear=sdatet[0];

        if(sdmounth.startsWith("0")){
            sdmounth=sdmounth.substring(1);
        }
        if(sdday.startsWith("0")){
            sdday=sdday.substring(1);
        }
        int sm=Integer.parseInt(sdmounth);

        int sy=Integer.parseInt(sdyear);
        int sd=Integer.parseInt(sdday);


        String[] stimet;

        if(startdatetime[1].isEmpty()){
            stimet=startdatetime[2].split(":");
        }else {
            stimet=startdatetime[1].split(":");
        }


        String sthour=stimet[0];
        String sminute=stimet[1];

        if(sthour.startsWith("0")){
            sthour=sthour.substring(1);
        }
        if(sminute.startsWith("0")){
            sminute=sminute.substring(1);
        }

        int sh=Integer.parseInt(sthour);
        int smin=Integer.parseInt(sminute);



        String[]enddatetime=collection.endingtime.split(" ");

        String[] edatet=enddatetime[0].split("-");

        String edday=edatet[2];
        String edmounth=edatet[1];
        String edyear=edatet[0];

        if(edmounth.startsWith("0")){
            edmounth=edmounth.substring(1);
        }
        if(edday.startsWith("0")){
            edday=edday.substring(1);
        }
        int em=Integer.parseInt(edmounth);

        int ey=Integer.parseInt(edyear);
        int ed=Integer.parseInt(edday);


        String[] etimet;

        if(enddatetime[1].isEmpty()){
            etimet=enddatetime[2].split(":");
        }else {
            etimet=enddatetime[1].split(":");
        }


        String ethour=etimet[0];
        String eminute=etimet[1];

        if(ethour.startsWith("0")){
            ethour=ethour.substring(1);
        }
        if(eminute.startsWith("0")){
            eminute=eminute.substring(1);
        }

        int eh=Integer.parseInt(ethour);
        int emin=Integer.parseInt(eminute);

        collection=new CalendarCollection(calendarCollection.title,calendarCollection.description,calendarCollection.creator,
                calendarCollection.category,calendarCollection.alldayevent,calendarCollection.everymonth,sh,smin,sd,sm,sy,eh,emin,ed,em,ey);

        collection.incomingnotifictionid=calendarCollection.incomingnotifictionid;
        return collection;
    }

    public void d(){
        // Return only the events that matches newYear and newMonth.
        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : matchedEvents) {
            if (eventMatches(event, 1, 2016)) {
                matchedEvents.add(event);
            }
        }
    }
    private ArrayList<WeekViewEvent> getNewEvents(int year, int month) {

        // Get the starting point and ending point of the given month. We need this to find the
        // events of the given month.
        Calendar startOfMonth = Calendar.getInstance();
        startOfMonth.set(Calendar.YEAR, year);
        startOfMonth.set(Calendar.MONTH, month - 1);
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);
        Calendar endOfMonth = (Calendar) startOfMonth.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);

        // Find the events that were added by tapping on empty view and that occurs in the given
        // time frame.
        ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : mNewEvents) {
            if (event.getEndTime().getTimeInMillis() > startOfMonth.getTimeInMillis() &&
                    event.getStartTime().getTimeInMillis() < endOfMonth.getTimeInMillis()) {
                events.add(event);
            }
        }
        return events;
    }


    void showPopmenu( WeekViewEvent events, final String [] options, final Calendar time){



        final WeekViewEvent event=events;
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.popup_menu_layout, null);
        alertDialog.setView(convertView);
        if(event !=null){
            alertDialog.setTitle("Event: "+event.getName()+event.getId());
        }else {
            alertDialog.setTitle("Add "+getEventTitle(time) +" ?");
        }

        ListView  lis = (ListView) convertView.findViewById(R.id.popupoptionen);
        optionlist=new ArrayList<>();
        optionlist.add(options[0]);
        if(options.length>1){
            optionlist.add(options[1]);
        }

        lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 && options.length>1) {

                    Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_SHORT).show();

                } else if (position==0 && options.length==1) {
                    Intent intent=new Intent(BaseActivity.this,AddNewEventFragment.class);
                    intent.putExtra("hour", time.get(Calendar.HOUR_OF_DAY));
                    intent.putExtra("minute", time.get(Calendar.MINUTE));
                    intent.putExtra("month", time.get(Calendar.MONTH)+1);
                    intent.putExtra("day", time.get(Calendar.DAY_OF_MONTH));
                    intent.putExtra("year", time.get(Calendar.YEAR));
                    intent.putExtra("fromCalendar", true);
                    startActivity(intent);
                    alertDialog.dismiss();
                } else {
                    //delete
                    if(event!=null){
                        mNewEvents.remove(event);
                        mySQLiteHelper.deleteIncomingNotificationcal((int) event.getId());
                        mWeekView.notifyDatasetChanged();
                        alertDialog.dismiss();

                    }

                    Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();

                }
            }
        });
        ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,optionlist);
        lis.setAdapter(adapter);
        alertDialog.show();
    }



}
