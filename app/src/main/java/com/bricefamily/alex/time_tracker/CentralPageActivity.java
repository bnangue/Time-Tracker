package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Handler;


public class CentralPageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,CentralPageAdapter.OnEventSelected,  android.support.v7.view.ActionMode.Callback {

    ListView mDrawerList;
    RelativeLayout mDrawerpane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private UserLocalStore userLocalStore;
    private String username;
    private CharSequence drawerTitle;
    private CharSequence title;
    private TextView userName;
    private ImageView profilePicture;
    ListView evnetListView;
    CentralPageAdapter centralPageAdapter;
    boolean[] selectionevents;
    private ArrayList<EventObject> listEvent;

    int countevent = 0;
    private android.support.v7.view.ActionMode mactionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);
        userLocalStore = new UserLocalStore(this);
        drawerTitle = title = getTitle();



        listEvent = new ArrayList<EventObject>();
        Bundle extras = getIntent().getExtras();
        if (getIntent().getExtras()!=null || extras != null) {
            username = extras.getString("username");
            listEvent=extras.getParcelableArrayList("eventlist");
        }
        UserProfilePicture u=new UserProfilePicture(username,null);
        getUserPicture(u);
        prepareDrawerViews();

        if(savedInstanceState!=null){
            username=savedInstanceState.getString("user");
            selectionevents=savedInstanceState.getBooleanArray("selectedevents");
            listEvent=savedInstanceState.getParcelableArrayList("eventsArray");
            countevent = savedInstanceState.getInt("numberOfSelectedevents");

          //  selectedevent = savedInstanceState.getIntArray("selectedevent");

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
    void prepareDrawerViews(){
        fillList();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerpane = (RelativeLayout) findViewById(R.id.drawerpane);
        mDrawerList = (ListView) findViewById(R.id.navlist);
        userName = (TextView) findViewById(R.id.username);
        profilePicture = (ImageView) findViewById(R.id.avatar);
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
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
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
        Intent intent=new Intent(CentralPageActivity.this,EventDetailsActivity.class);
        intent.putExtra("titel",listEvent.get(position).titel);
        intent.putExtra("textinfo",listEvent.get(position).infotext);
        intent.putExtra("time",listEvent.get(position).creationTime);
        intent.putExtra("creator",listEvent.get(position).creator);
        startActivity(intent);
    }


//    void showActionMode(){
//        mactionMode=startSupportActionMode(new ActionModeStarter());
//    }
//    void closeActionMode(){
//        mactionMode.finish();
//    }


    //Für Portr/Landsc Wechsel, Activity wird gekillt und Daten werden gesichert
    @Override
    protected void onSaveInstanceState(Bundle state) {

        super.onSaveInstanceState(state);
         state.putBooleanArray("selectedevents",selectionevents);
            state.putInt("numberOfSelectedevents", countevent);
            state.putParcelableArrayList("eventsArray", listEvent);
            state.putString("user", username);


    }


    void getUserPicture(final UserProfilePicture u){
        if(u.username!=null|| !u.username.isEmpty()){

            ServerRequest serverRequest=new ServerRequest(this);
            serverRequest.fetchUserPicture(u, new GetImageCallBacks() {
                @Override
                public void done(String reponse) {

                }

                @Override
                public void image(UserProfilePicture reponse) {
                    if(reponse!=null){
                        Bitmap bitmap=reponse.uProfilePicture;
                        profilePicture.setImageBitmap(bitmap);
                        storeimageLocaly(reponse.uProfilePicture);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Picture save for this user",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }
    private String  storeimageLocaly(Bitmap picture) {

        ContextWrapper cw=new ContextWrapper(getApplicationContext());
        File directory=cw.getDir("ProfilePictures", Context.MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file=new File(directory,"profile.jpg");

        FileOutputStream fos=null;

        if(file.exists()){
            file.delete();
            file=new File(directory,"profile.jpg");
        }
        try {
            fos=new FileOutputStream(file);
            picture=BitmapFactory.decodeFile(file.getName());
            picture.compress(Bitmap.CompressFormat.JPEG, 100, fos);


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contxt_menu, menu);
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

                int po=centralPageAdapter.getCount();

                for (int i = po-1; i >=0; i--){
                    if (listEvent.get(i)==null) {
                    }
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
        }else {
            mactionMode.finish();
            mactionMode=null;
            countevent=0;
        }

        centralPageAdapter.setEventSelection(selectionevents,countevent);
    }

    void refresh(final String username){
        final android.os.Handler h=new android.os.Handler();
        final int delay=1000;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getEventsFromDatabase(username);
                h.postDelayed(this,delay);
            }
        },delay);
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
        });
    }
}