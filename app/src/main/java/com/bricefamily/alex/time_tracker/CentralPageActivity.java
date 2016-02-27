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
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
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


public class CentralPageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,CentralPageAdapter.OnEventSelected,  ActionMode.Callback,DialogLogoutFragment.YesNoListenerDeleteAccount,SwipeRefreshLayout.OnRefreshListener {

    ListView mDrawerList;
    RelativeLayout mDrawerpane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();


    private CharSequence drawerTitle;
    private CharSequence title,mtitel;
    private TextView userName;
    private CircularImageView profilePicture;
    ListView evnetListView;
    CentralPageAdapter centralPageAdapter;
    private Menu menu;
    boolean hideOptions=false;
    boolean[] selectionevents;
    private ArrayList<EventObject> listEvent;
    FloatingActionButton fab;
    SwipeRefreshLayout refreshLayout;
    User loggedinUser;
   private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    private String username;

    int countevent = 0;
    private android.support.v7.view.ActionMode mactionMode;

    public static final String SERVER_ADDRESS = "http://time-tracker.comlu.com/";


    String found = "N";


    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<EventObject> productResults = new ArrayList<EventObject>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<EventObject> filteredProductResults = new ArrayList<EventObject>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);

        prepareView();
        mySQLiteHelper=new MySQLiteHelper(this);
         fab = (FloatingActionButton) findViewById(R.id.fab);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        refreshLayout.setOnRefreshListener(this);

        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cellSelected)));
        userLocalStore = new UserLocalStore(this);
        mtitel=drawerTitle = title = getTitle();
        mySQLiteHelper.updateIncomingMessage(1,2);


        listEvent = new ArrayList<EventObject>();
        Bundle extras = getIntent().getExtras();
        if (getIntent().getExtras()!=null || extras != null) {
            username = extras.getString("username");
            listEvent=extras.getParcelableArrayList("eventlist");
            loggedinUser=extras.getParcelable("loggedinUser");

        }
        UserProfilePicture u=new UserProfilePicture(username,null);
       // getUserPicture(u);
        prepareDrawerViews();

        if(savedInstanceState!=null){
            username=savedInstanceState.getString("user");
            hideOptions=savedInstanceState.getBoolean("hideOptions");
            selectionevents=savedInstanceState.getBooleanArray("selectedevents");
            listEvent=savedInstanceState.getParcelableArrayList("eventsArray");
            countevent = savedInstanceState.getInt("numberOfSelectedevents");
            loggedinUser=savedInstanceState.getParcelable("loggedinUser");

            prepareOrientationchange();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String formattedDate = df.format(c.getTime());

            prepareListview(listEvent);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

    }
    void prepareOrientationchange(){
        if( selectionevents.length!=0){
            prepareListview(listEvent);
            if(countevent !=0){
                centralPageAdapter.setEventSelection(selectionevents,countevent);
                mactionMode= startSupportActionMode(this);
                if(hideOptions){
                    hideOption(R.id.menu_delete);
                }
                mactionMode.setTitle(countevent + " selected");
            }

        }else{
            prepareListview(listEvent);
        }
    }

    public void openProfileOverviewClick(View view){
        Intent intent= new Intent(CentralPageActivity.this,ProfileOverviewActivity.class);
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
            Bitmap bitmap=loadImageFromStorage(path,uName);
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

    void prepareListview(ArrayList<EventObject> listEvent) {

        evnetListView = (ListView) findViewById(R.id.listviewdetails);
        centralPageAdapter = new CentralPageAdapter(this,listEvent,this);
        evnetListView.setAdapter(centralPageAdapter);
        centralPageAdapter.notifyDataSetChanged();

        evnetListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        evnetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onLongListItemClick(view, position, id);
            }
        });

        evnetListView.setOnItemClickListener(this);

        if(selectionevents==null){
            selectionevents=new boolean[listEvent.size()];
        }

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
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }

    void getUserPicture(final UserProfilePicture u){
        if(u.username!=null|| !u.username.isEmpty()){

            ServerRequestUser serverRequest=new ServerRequestUser(this);
            serverRequest.fetchUserPicture(u, new GetImageCallBacks() {
                @Override
                public void done(String reponse) {

                }

                @Override
                public void image(UserProfilePicture reponse) {
                    if (reponse != null) {
                        Bitmap bitmap = reponse.uProfilePicture;
                        profilePicture.setImageBitmap(bitmap);
                        storeimageLocaly(reponse.uProfilePicture);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Picture save for this user", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private boolean  storeimageLocaly(Bitmap picture) {


        FileOutputStream fos=null;
        try {
            fos=openFileOutput("profile.png", Context.MODE_PRIVATE);
            picture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
                refreshLayout.setRefreshing(true);
                getEventsFromDatabase(username);
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
                    prepareListview(listEvent);
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
        Intent intent= new Intent(CentralPageActivity.this, CreateNewEventActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("loggedinUser",loggedinUser);
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

    private void fetchuserlist(final User user){
        final ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.fetchallUserForGcm(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                    ArrayList<User> users = new ArrayList<User>();
                    users = reponse;
                    final ArrayList<User> finalUsers = users;
                    serverRequestUser.fetchallUsers(user,new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {

                        }

                        @Override
                        public void deleted(String reponse) {

                        }

                        @Override
                        public void userlist(ArrayList<User> reponse) {

                            if (reponse.size() != 0) {
                                Intent intent = new Intent(CentralPageActivity.this, UserListTabsActivity.class);
                                intent.putExtra("userlistforgcm", finalUsers);
                                intent.putExtra("userlist", reponse);
                                startActivity(intent);
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent=new Intent(CentralPageActivity.this,DetailsEventsActivity.class);
        intent.putExtra("titel", listEvent.get(position).titel);
        intent.putExtra("textinfo", listEvent.get(position).infotext);
        intent.putExtra("time", listEvent.get(position).creationTime);
        intent.putExtra("creator", listEvent.get(position).creator);
        intent.putExtra("day", listEvent.get(position).eDay);
        intent.putExtra("month", listEvent.get(position).eMonth);
        intent.putExtra("year", listEvent.get(position).eYear);
        intent.putExtra("hash", listEvent.get(position).eventHash);


        startActivity(intent);
    }



    //Für Portr/Landsc Wechsel, Activity wird gekillt und Daten werden gesichert
    @Override
    protected void onSaveInstanceState(Bundle state) {

        super.onSaveInstanceState(state);
         state.putBooleanArray("selectedevents", selectionevents);
            state.putInt("numberOfSelectedevents", countevent);
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


    public Bitmap getThumbnail(String filename) {

        //String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        Bitmap thumbnail = null;

// Look for the file on the external storage
        //try {
        //if (tools.isSdReadable() == true) {
        //thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
        // }
        // } catch (Exception e) {
        // Log.e("getThumbnail() on external storage", e.getMessage());
        // }

// If no file on external storage, look in internal storage
        // if (thumbnail == null) {

        Bitmap bitmap;
            try {

                File filePath = getFileStreamPath(filename);
                FileInputStream fi = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(fi);


               thumbnail=bitmap;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        return thumbnail;
    }

    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.menu=menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contxt_menu, menu);
        fab.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:




                int size=0;
                for (int i = selectionevents.length-1; i >=0; i--) {
                    if (selectionevents[i]) {
                        size++;
                    }
                }
                final int[] eventtodelete= new int[size];
                int j=0;
                for (int i = 0; i <selectionevents.length; i++){
                    if(selectionevents[i]){

                        eventtodelete[j]=i;
                        j++;
                    }
                }
                for(int k=0;k<eventtodelete.length;k++){
                    String hash=listEvent.get(eventtodelete[k]).eventHash;
                    ServerRequest serverRequest=new ServerRequest(this);
                    final int finalK = k;
                    serverRequest.deleteEvents(listEvent.get(eventtodelete[k]), new GetEventsCallbacks() {
                        @Override
                        public void done(ArrayList<EventObject> returnedeventobject) {

                        }

                        @Override
                        public void updated(String reponse) {

                            if (reponse.contains("Event successfully deleted")) {
                                listEvent.remove(eventtodelete[finalK]);
                                centralPageAdapter.notifyDataSetChanged();


                            }

                        }
                    },hash);
                }

                centralPageAdapter.notifyDataSetChanged();
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //centralPageAdapter.clearSelection();
        // centralPageAdapter.removeSelection();
        mactionMode=null;
        countevent =0;
        selectionevents=new boolean[listEvent.size()];
        centralPageAdapter.setEventSelection(selectionevents, countevent);
        fab.setVisibility(View.VISIBLE);
    }



    @Override
    public void selected(int count, boolean[] events) {
        countevent =count;
        selectionevents=events;

        if(mactionMode==null){
            mactionMode=startSupportActionMode(this);
            int size=0;
            for (int i = selectionevents.length-1; i >=0; i--) {
                if (selectionevents[i]) {
                    size++;
                }
            }
            final int[] eventtodelete= new int[size];
            int j=0;
            for (int i = 0; i <selectionevents.length; i++){
                if(selectionevents[i]){

                    eventtodelete[j]=i;
                    j++;
                }
            }
            for(int k=0;k<eventtodelete.length;k++){
                if(!listEvent.get(eventtodelete[k]).creator.equals(userLocalStore.getLoggedInUser().username)){
                    hideOption(R.id.menu_delete);
                    hideOptions=true;
                    break;
                }

            }

        }

        if(countevent!=0){
            mactionMode.setTitle(countevent + " selected");
            fab.setVisibility(View.INVISIBLE);

        }else {
            hideOptions=false;
            showOption(R.id.menu_delete);
            mactionMode.finish();
            fab.setVisibility(View.VISIBLE);
            mactionMode=null;
            countevent=0;
        }

        centralPageAdapter.setEventSelection(selectionevents, countevent);
    }

    void refresh(final String username){
        final android.os.Handler h=new android.os.Handler();
        final int delay=1000;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getEventsFromDatabase(username);
                h.postDelayed(this, delay);
            }
        }, delay);
    }


    void  getEventsFromDatabase(final String username){

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchAlleventscentralpage(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(CentralPageActivity.this, CentralPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                    refreshLayout.setRefreshing(false);

                } else {

                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
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

        updatestatus(us);
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);

    }

    @Override
    public void onNo() {

    }

    private Bitmap loadImageFromStorage(String path,String username)
    {
        Bitmap bitmap=null;
        try {
            File f=new File(path, username+".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CentralPageActivity.this, HomeScreenActivity.class);
        intent.putExtra("loggedinUser",loggedinUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {

        getEventsFromDatabase(userLocalStore.getLoggedInUser().username);
    }

    public void filterProductArray(String newText) {

        String pName;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).titel.toLowerCase();
            if (pName.contains(newText.toLowerCase()) ||
                    productResults.get(i).creator.contains(newText)) {
                filteredProductResults.add(productResults.get(i));

            }
        }

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

            EventObject object;
            String matchFound = "N";
            //productResults is an arraylist with all product details for the search criteria
            //productResults.clear();

            ArrayList<EventObject> returnedEvents = new ArrayList<>();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {

                url=new URL(SERVER_ADDRESS+ "FetchAllEvents.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                InputStream in = urlConnection.getInputStream();
                String respons = "";
                StringBuilder bi = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons = bi.toString();
                JSONArray jsonArray = new JSONArray(respons);

                productList = jsonArray;

                //parse date for dateList
                for (int i = 0; i < productList.length(); i++) {
                    object = null;

                    JSONObject jo_inside = productList.getJSONObject(i);

                    String titel = jo_inside.getString("eventTitel");
                    String infotext = jo_inside.getString("eventDetails");
                    String creator = jo_inside.getString("eventCreator");
                    String creationTime = jo_inside.getString("eventCreationtime");
                    String eDay = jo_inside.getString("eventDay");
                    String eMonth = jo_inside.getString("eventMonth");
                    String eYear = jo_inside.getString("eventYear");
                    String eventStatus = jo_inside.getString("eventStatus");
                    String eventHash = jo_inside.getString("eventHash");


                    String[] creationtime = creationTime.split(" ");
                    DateEventObject dateEventObject = new DateEventObject(eDay, eMonth, eYear);

                    object = new EventObject(titel, infotext, creator, creationtime[0],
                            dateEventObject, eventStatus, eventHash);

                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).titel.equals(object.titel)) {
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
                Toast.makeText(getApplicationContext(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

            } else {


                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);
                prepareListview(filteredProductResults);

            }
        }


    }


}