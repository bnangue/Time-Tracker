package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bricenangue on 27/02/16.
 */
public class NewCalendarActivty extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener,
        FragmentCategoryShopping.OnFragmentInteractionListener,DialogLogoutFragment.YesNoListenerDeleteAccount,OnCalendarEventsChanged{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */




    ViewPager viewPager;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private AppBarLayout mAppBarLayout;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    public static boolean eventHaschanged=false;
    private float mCurrentRotation = 360.0f;
    private String datestr;
    private FloatingActionButton fab;
    private ListView lv_android;
    String d;
   private ArrayList<CalendarCollection> allnewEvents=new ArrayList<>();
    ArrayList<CalendarCollection> arrayListcollections=new ArrayList<>();
    private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    public static ArrayList<CalendarCollection> calendarCollectionArrayList;
    public FragmentCommunicator fragmentCommunicator;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int pagePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_sheet_act_test);

        mySQLiteHelper=new MySQLiteHelper(this);
        userLocalStore=new UserLocalStore(this);



        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("events")){
                allnewEvents=extras.getParcelableArrayList("events");


            }
        }



        ArrayList<IncomingNotification> arrayList=new ArrayList<>();
        arrayList=mySQLiteHelper.getAllIncomingNotification();

        getEvents(arrayList);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.ttoolbar);
        setSupportActionBar(toolbar);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.drawSmallIndicatorForEvents(false);

        // Force English
        mCompactCalendarView.setLocale(/*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
                String d = dateFormat.format(dateClicked);
                Toast.makeText(getApplicationContext(), "You have event on this date: " + d, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });

        if(allnewEvents.size()!=0){
            for (int i=0; i<allnewEvents.size();i++){

                setEventinCalendar(allnewEvents.get(i));

            }
        }
        if(arrayListcollections.size()!=0){
            for (int i=0; i<arrayListcollections.size();i++){

                setEventinCalendar(arrayListcollections.get(i));


            }

        }


        initViewPagerAndTabs();
        // Set current date to today
        datestr=setCurrentDate(new Date());
        d =setCurrentDate(new Date());

        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation + 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation + 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    arrow.startAnimation(anim);
                    mAppBarLayout.setExpanded(false, true);
                    isExpanded = false;
                } else {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation - 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation - 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    arrow.startAnimation(anim);
                    mAppBarLayout.setExpanded(true, true);
                    isExpanded = true;
                }
            }
        });


    }



    private void initViewPagerAndTabs() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);

        final ViewPagerAdapter1 pagerAdapter=new ViewPagerAdapter1(getSupportFragmentManager());

        pagerAdapter.addFragment(new FragmentAllEvents(),"All Events");
        pagerAdapter.addFragment(new FragmentMyEvent(),"MY Events");
        pagerAdapter.addFragment(new FragmentCategoryBusiness(),"Business meetings");
        pagerAdapter.addFragment(new FragmentCategoryBirthdays(),"Birthdays");
        pagerAdapter.addFragment(new FragmentCategoryShopping(),"Shopping");
        pagerAdapter.addFragment(new FragmentCategoryWorkPlan(), "Work Plans");

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentposition =0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                switch (position){
                    case 2:
                        return getResources().getColor(R.color.business);

                    case 3:
                        return getResources().getColor(R.color.birthdays);

                    case 4:
                        return getResources().getColor(R.color.grocery);
                    case 5:
                        return getResources().getColor(R.color.workPlans);
                    default:return getResources().getColor(R.color.tabsScrollColor);

                }

            }
        });
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void eventsCahnged(boolean haschanged) {
    }

    public interface YourFragmentInterface {
        void fragmentBecameVisible();
    }

    public String setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }
        return dateFormat.format(date);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        TextView datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments


        switch (position){
            case 0:

                break;
            case 1:
                startActivity(new Intent(NewCalendarActivty.this, BaseActivity.class));
                break;
            case 2:
                break;
            case 3:
                break;

        }


    }

    @Override
    public void onNavigationDrawersubItemSelected(int position) {
        switch (position){
            case 0:
                viewPager.setCurrentItem(0,true);
                break;
            case 1:
                viewPager.setCurrentItem(2,true);
                break;
            case 2:
                viewPager.setCurrentItem(3,true);
                break;
            case 3:
                viewPager.setCurrentItem(4, true);
                break;
            case 4:
                viewPager.setCurrentItem(5,true);
                break;
            default:viewPager.setCurrentItem(0,true);

        }

    }




    private void getEvents(ArrayList<IncomingNotification> incomingNotifications){

        arrayListcollections=new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
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
                arrayListcollections.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private ArrayList<CalendarCollection> getCalendarEvents(ArrayList<IncomingNotification> incomingNotifications){

        ArrayList<CalendarCollection> a =new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
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
                a.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return a;
    }

    void setEventinCalendar(CalendarCollection calendarCollection){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date curDate = new Date();
        long curMillis = curDate.getTime();
        String curTime = formatter.format(curDate);
        String dgdh[]=curTime.split(",");


        String oldTime = calendarCollection.datetime +","+dgdh[1];
        Date oldDate = null;
        try {
            oldDate = formatter.parse(oldTime);
            long oldMillis = oldDate.getTime();
            switch (calendarCollection.category){
                case "Normal":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.normal)),false);

                    break;
                case "Business":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.business)),false);

                    break;
                case "Birthdays":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.birthdays)),false);

                    break;
                case "Grocery":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.grocery)),false);

                    break;
                case "Work Plans":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.workPlans)),false);

                    break;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       // d =setCurrentDate(new Date());
       // FragmentManager fragmentManager = getSupportFragmentManager();
       // fragmentManager.beginTransaction()
        //        .replace(R.id.container, new AddNewEventFragment())
        //        .commit();

        startActivity(new Intent(NewCalendarActivty.this, AddNewEventActivity.class));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onYes() {
        User us=new User(userLocalStore.getLoggedInUser().email,userLocalStore.getLoggedInUser().password,"0",userLocalStore.getUserRegistrationId());

        updatestatus(us);
    }

    @Override
    public void onNo() {

    }

    private void updatestatus(User us) {

        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.logginguserOutInBackgroung(us, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void serverReponse(String reponse) {
                if (reponse.contains("Status successfully updated")) {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLoggedIn(false);
                    Intent intent = new Intent(NewCalendarActivty.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    showErrordialog("Error : You cannot be logged out at the moment please try again later");
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });


    }
    private void showErrordialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

}
