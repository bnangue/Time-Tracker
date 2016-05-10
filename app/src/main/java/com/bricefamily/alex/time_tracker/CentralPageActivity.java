package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CentralPageActivity extends ActionBarActivity implements DialogLogoutFragment.YesNoListenerDeleteAccount,DialogDeleteEventFragment.OnDeleteEventListener, ShareWithFriendAdapter.OnEventSelected {

    ListView mDrawerList;
    RelativeLayout mDrawerpane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private TextView eventpriode, creatorname, createdtime, notes, descriptionexpand;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter.MyClickListener myClickListener;

    private CharSequence drawerTitle;
    private CharSequence title,mtitel;
    private TextView userName;
    private CircularImageView profilePicture;
    private Menu menu;
    boolean hideOptions=false;
    private ArrayList<CalendarCollection> listEvent;
    FloatingActionButton fab;
    User loggedinUser;
   private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    private String username;

    private android.support.v7.view.ActionMode mactionMode;

    public static final String SERVER_ADDRESS = "http://time-tracker.comlu.com/";


    String found = "N";
    private AlertDialog alertDialog;

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



    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<CalendarCollection> productResults = new ArrayList<CalendarCollection>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<CalendarCollection> filteredProductResults = new ArrayList<CalendarCollection>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);

        prepareView();
        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        mySQLiteHelper=new MySQLiteHelper(this);
         fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cellSelected)));
        userLocalStore = new UserLocalStore(this);
        mtitel=drawerTitle = title = getTitle();
        mySQLiteHelper.updateIncomingMessage(1,2);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_viewc);



        listEvent = new ArrayList<CalendarCollection>();
        Bundle extras = getIntent().getExtras();
        if (getIntent().getExtras()!=null || extras != null) {
            username = extras.getString("username");
            loggedinUser=extras.getParcelable("loggedinUser");

        }
        UserProfilePicture u=new UserProfilePicture(username,null);
       // getUserPicture(u);
        prepareDrawerViews();

        if(savedInstanceState!=null){
            username=savedInstanceState.getString("user");
            hideOptions=savedInstanceState.getBoolean("hideOptions");
            listEvent=savedInstanceState.getParcelableArrayList("eventsArray");
            loggedinUser=savedInstanceState.getParcelable("loggedinUser");

            prepareOrientationchange();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String formattedDate = df.format(c.getTime());
            listEvent=getCalendarEvents(mySQLiteHelper.getAllIncomingNotificationEvent());
            prepareRecyclerView(listEvent);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        myClickListener=new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                setViews(v,position);
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.show(getSupportFragmentManager(), "DELETEALLEVENTFRAGMENT");


                        break;
                    case R.id.buttonsharecardview:
                        ArrayList<User> arrayList=getUsers(mySQLiteHelper.getAllIncomingNotificationUsers());
                        showDialogsharewithfriend(arrayList);
                        break;
                }
            }
        };

    }


    void showDialogsharewithfriend(ArrayList<User> users){



        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.share_friend_layout, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Share with ");
        ListView listView=(ListView)convertView.findViewById(R.id.listviewsharefriend);
        ShareWithFriendAdapter friendAdapter=new ShareWithFriendAdapter(this, users,this);
        Button btncancel = (Button) convertView.findViewById(R.id.buttonCancelsharewithfriend);

        Button btnok = (Button) convertView.findViewById(R.id.buttonOKsharewithfriend);
        listView.setAdapter(friendAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //pass data
            }
        });


        alertDialog.show();
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

    private void setViews(View v, int position){
        creatorname = (TextView) v.findViewById(R.id.textViewexpandcreator);
        createdtime = (TextView) v.findViewById(R.id.textViewexpandcreationtime);
        eventpriode = (TextView) v.findViewById(R.id.textViewexpandperiode);
        descriptionexpand = (TextView) v.findViewById(R.id.textViewexpanddescription);
        notes = (TextView) v.findViewById(R.id.textViewexpandnote);


        if(listEvent.size()!=0){
            CalendarCollection ecollection=listEvent.get(position);
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


    void prepareOrientationchange(){
            prepareRecyclerView(listEvent);
    }

    public void openProfileOverviewClick(View view){
        Intent intent= new Intent(CentralPageActivity.this,OpenUserProfileActivity.class);
        intent.putExtra("loggedinUser",loggedinUser);
        startActivity(intent);
    }
    void prepareDrawerViews(){
        fillList();

        String path=userLocalStore.getUserPicturePath();
        String uName=userLocalStore.getLoggedInUser().username;


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerpane = (RelativeLayout) findViewById(R.id.drawerpane);
        mDrawerList = (ListView) findViewById(R.id.navlist);
        userName = (TextView) findViewById(R.id.username);
        TextView luserName = (TextView) findViewById(R.id.usernamemainpage);
        luserName.setText(uName);
        profilePicture = (CircularImageView) findViewById(R.id.avatarfriend);
        if(path!=null){
            Bitmap bitmap=userLocalStore.loadImageFromStorage(path);
            if(bitmap!=null){

                profilePicture.setImageBitmap(bitmap);
            }
        }

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecItemFromDrawer(position);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawerOpen, R.string.drawerClose);

        userName.setText(username);
        mDrawerToggle = new ActionBarDrawerToggle(this, // Host Activity
                mDrawerLayout, // layout container for navigation drawer
                // Application Icon
                R.string.drawerOpen, // Open Drawer Description
                R.string.drawerClose) // Close Drawer Description
        {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void prepareRecyclerView(ArrayList<CalendarCollection> arrayList) {

        mAdapter = new MyRecyclerViewAdapter(this, arrayList, myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(myClickListener);

    }

    private void updatestatus(final User user){
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.updtaestatus(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

                if (reponse.contains("Status successfully updated")) {
                    Intent intent = new Intent(CentralPageActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }




    protected boolean onLongListItemClick(View v, int pos, long id) {

        showFilterPopup(v);
        return true;
    }


    private void fillList() {
        mNavItems.add(new NavItem("Home", "MeetUp Destination", R.drawable.colorhome));
        mNavItems.add(new NavItem("Group", "meet your friends", R.drawable.groupuser));
        mNavItems.add(new NavItem("Timesheet", "plan your life", R.drawable.sundaycalendar));
        mNavItems.add(new NavItem("GOOGLE MAP", "explore new places", R.drawable.mapgooglebig));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.colorsettings));
        mNavItems.add(new NavItem("About", "learn more about time-tracker", R.drawable.colornfo));

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_central_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                break;
            case R.id.action_logout:
                DialogLogoutFragment alertDialogFragmentTwobtn=new DialogLogoutFragment();
                alertDialogFragmentTwobtn.setCancelable(false);
                alertDialogFragmentTwobtn.show(getSupportFragmentManager(), "tag");

                break;
            case R.id.action_refresh:
                if(haveNetworkConnection()){
                    getEventsFromDatabase();

                }else {
                    Toast.makeText(getApplicationContext(), "No internet connection, please try again later", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.action_view_in_calendar:
                startActivity(new Intent(CentralPageActivity.this,BaseActivity.class));

                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void prepareView() {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.actionbackgroundsearch, null);
        SearchView search=(SearchView)view.findViewById(R.id.actionbarsearch);
        search.setQueryHint("Start typing to search...");
        if(search.hasFocus()){
            search.setBackgroundColor(getResources().getColor(R.color.white));
        }
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                //Toast.makeText(activity, String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 1) {

                    myAsyncTask m = (myAsyncTask) new myAsyncTask().execute(newText);
                } else {
                    prepareRecyclerView(getCalendarEvents(mySQLiteHelper.getAllIncomingNotificationEvent()));
                }


                return false;
            }

        });



        //center des ActionBar Titles
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);

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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen =mDrawerLayout.isDrawerOpen(mDrawerpane);
        menu.findItem(R.id.action_settings).setVisible(drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public void buttonCreateNewEventPressed(View view){
        Intent intent= new Intent(CentralPageActivity.this, AddNewEventActivity.class);
        startActivity(intent);

    }
    private void selecItemFromDrawer(int position) {
        String titel=mNavItems.get(position).mTitel;

        switch (titel){
            case "Home":


                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Preferences":
                startActivity(new Intent(CentralPageActivity.this, PreferenceAppActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "About":
                startActivity(new Intent(CentralPageActivity.this,AboutApplicationActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Group":
                Intent intent = new Intent(CentralPageActivity.this, NewUserTabsActivity.class);

                startActivity(intent);

                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "GOOGLE MAP":
                startActivity(new Intent(CentralPageActivity.this,MAPActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Timesheet":
                startActivity(new Intent(CentralPageActivity.this,NewCalendarActivty.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            default: return;
        }

    }





    //Für Portr/Landsc Wechsel, Activity wird gekillt und Daten werden gesichert
    @Override
    protected void onSaveInstanceState(Bundle state) {

        super.onSaveInstanceState(state);
            state.putParcelableArrayList("eventsArray", listEvent);
            state.putString("user", username);
        state.putBoolean("hideOptions", hideOptions);
        state.putParcelable("loggedinUser", loggedinUser);


    }



    public boolean isSdReadable() {

        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
            mExternalStorageAvailable = true;
            Log.i("isSdReadable", "External storage card is readable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = true;
        } else {
// Something else is wrong. It may be one of many other
// states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }

        return mExternalStorageAvailable;
    }


    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(true);
    }



    void  getEventsFromDatabase(){

        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.getCalenderEventInBackgroung(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {

            }

            @Override
            public void donec(ArrayList<CalendarCollection> returnedeventobject) {
                if (returnedeventobject != null) {
                    mySQLiteHelper.reInitializeSqliteTable();
                    saveeventtoSQl(returnedeventobject);
                    Intent intent = new Intent(CentralPageActivity.this, CentralPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to connect to server,please try again later", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }
    private void saveeventtoSQl(ArrayList<CalendarCollection> calendarCollections) {
        IncomingNotification incomingNotification;

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


    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_follow:
                        Toast.makeText(CentralPageActivity.this, "follow user", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_profile:
                        Toast.makeText(CentralPageActivity.this, "open profile!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onYes() {
        User us=new User(userLocalStore.getLoggedInUser().username,userLocalStore.getLoggedInUser().email,userLocalStore.getLoggedInUser().password,0,userLocalStore.getUserRegistrationId());

        if(haveNetworkConnection()){
            updatestatus(us);
            userLocalStore.clearUserData();
            userLocalStore.setUserLoggedIn(false);
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection, please try again later", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onNo() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CentralPageActivity.this, HomeScreenActivity.class);
        intent.putExtra("loggedinUser",loggedinUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    public void filterProductArray(String newText) {

        String pName;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).title.toLowerCase();
            if (pName.contains(newText.toLowerCase()) || productResults.get(i).creator.contains(newText.toLowerCase()) ||
                    productResults.get(i).category.contains(newText.toLowerCase()) || productResults.get(i).datetime.contains(newText.toLowerCase())) {
                filteredProductResults.add(productResults.get(i));

            }
        }

    }

    @Override
    public void delete(int position) {
        ((MyRecyclerViewAdapter)mAdapter).deleteItem(position);

    }

    @Override
    public void selected(int count, boolean[] events, int position) {

    }


    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class myAsyncTask extends AsyncTask<String, Void, String> {
        JSONArray productList;
        String textSearch;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList = new JSONArray();
        }

        @Override
        protected String doInBackground(String... sText) {

            // url="http://lawgo.in/lawgo/products/user/1/search/"+sText[0];
            String returnResult = getProductList();
            this.textSearch = sText[0];
            return returnResult;

        }

        public String getProductList() {

            CalendarCollection object;
            String matchFound = "N";
            //productResults is an arraylist with all product details for the search criteria
            //productResults.clear();

            ArrayList<CalendarCollection> returnedEvents = new ArrayList<>();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {

                url=new URL(ServerRequest.SERVER_ADDRESS + "FetchEventCalendar.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);

                productList = jsonArray;

                //parse date for dateList
                for (int i = 0; i < productList.length(); i++) {
                    object = null;

                    JSONObject jo_inside = productList.getJSONObject(i);


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


                    String[] creationtime=creationTime.split(" ");

                      object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);

                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).title.equals(object.title)) {
                            matchFound = "Y";
                        }
                    }

                    if (matchFound == "N") {
                        productResults.add(object);
                    }

                }

                return ("OK");

            } catch (Exception e) {
                e.printStackTrace();
                return ("Exception Caught");
            }




        }

        @Override
        protected void onPostExecute (String result){

            super.onPostExecute(result);

            if (result.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getApplicationContext(), "Unable to connect to server,please try again later", Toast.LENGTH_LONG).show();

            } else {


                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);
                prepareRecyclerView(filteredProductResults);

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



}