package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.Logger;


public class CentralPageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,CentralPageAdapter.OnEventSelected,  android.support.v7.view.ActionMode.Callback {

    ListView mDrawerList;
    RelativeLayout mDrawerpane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private UserLocalStore userLocalStore;
    private String username;
    private CharSequence drawerTitle;
    private CharSequence title,mtitel;
    private TextView userName;
    private CircularImageView profilePicture;
    ListView evnetListView;
    CentralPageAdapter centralPageAdapter;
    boolean[] selectionevents;
    private ArrayList<EventObject> listEvent;
    FloatingActionButton fab;

    int countevent = 0;
    private android.support.v7.view.ActionMode mactionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);

         fab = (FloatingActionButton) findViewById(R.id.fab);

        userLocalStore = new UserLocalStore(this);
        mtitel=drawerTitle = title = getTitle();



        listEvent = new ArrayList<EventObject>();
        Bundle extras = getIntent().getExtras();
        if (getIntent().getExtras()!=null || extras != null) {
            username = extras.getString("username");
            listEvent=extras.getParcelableArrayList("eventlist");
        }
        UserProfilePicture u=new UserProfilePicture(username,null);
       // getUserPicture(u);
        prepareDrawerViews();

        if(savedInstanceState!=null){
            username=savedInstanceState.getString("user");
            selectionevents=savedInstanceState.getBooleanArray("selectedevents");
            listEvent=savedInstanceState.getParcelableArrayList("eventsArray");
            countevent = savedInstanceState.getInt("numberOfSelectedevents");

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
                mactionMode.setTitle(countevent + " selected");
            }

        }else{
            prepareListview(listEvent);
        }
    }

    public void openProfileOverviewClick(View view){
        Intent intent= new Intent(CentralPageActivity.this,ProfileOverviewActivity.class);
        startActivity(intent);
    }
    void prepareDrawerViews(){
        fillList();

        Bitmap bitmap=getThumbnail("profile.png");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerpane = (RelativeLayout) findViewById(R.id.drawerpane);
        mDrawerList = (ListView) findViewById(R.id.navlist);
        userName = (TextView) findViewById(R.id.username);
        profilePicture = (CircularImageView) findViewById(R.id.avatar);
        if(bitmap!=null){

            profilePicture.setImageBitmap(bitmap);
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

        evnetListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        evnetListView.setOnItemClickListener(this);
       // evnetListView.setMultiChoiceModeListener(this);

        if(selectionevents==null){
            selectionevents=new boolean[listEvent.size()];
        }

    }

    private void fillList() {
        mNavItems.add(new NavItem("Home", "MeetUp Destination", R.drawable.colorhome));
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
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                Intent intent = new Intent(CentralPageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                getEventsFromDatabase(username);
                break;
        }

        return super.onOptionsItemSelected(item);
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
        startActivity(intent);

    }
    private void selecItemFromDrawer(int position) {
        startActivity(new Intent(CentralPageActivity.this, PreferenceActivity.class));
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitel);

        mDrawerLayout.closeDrawer(mDrawerpane);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),"item "+ listEvent.get(position).titel,Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CentralPageActivity.this,DetailsEventsActivity.class);
        intent.putExtra("titel", listEvent.get(position).titel);
        intent.putExtra("textinfo", listEvent.get(position).infotext);
        intent.putExtra("time", listEvent.get(position).creationTime);
        intent.putExtra("creator", listEvent.get(position).creator);
        intent.putExtra("day",listEvent.get(position).eDay);
        intent.putExtra("month", listEvent.get(position).eMonth);
        intent.putExtra("year",listEvent.get(position).eYear);
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


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
        }

        if(countevent!=0){
            mactionMode.setTitle(countevent + " selected");
            fab.setVisibility(View.INVISIBLE);

        }else {
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

        ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(CentralPageActivity.this, CentralPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", username);
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


}