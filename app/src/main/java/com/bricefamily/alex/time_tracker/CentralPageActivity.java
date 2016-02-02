package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CentralPageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener,CentralPageAdapter.OndeleteFromList,  android.support.v7.view.ActionMode.Callback {

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
    SparseBooleanArray selected;
    int[]selectedevent;
    boolean[] selectionevents;
    private ArrayList<EventObject> listEvent;

    int count = 0;
    private android.support.v7.view.ActionMode mactionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);
        userLocalStore = new UserLocalStore(this);
        drawerTitle = title = getTitle();



        listEvent = new ArrayList<EventObject>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            listEvent=extras.getParcelableArrayList("eventlist");
        }
        prepareDrawerViews();
        if(savedInstanceState!=null){
            username=savedInstanceState.getString("user");
            selectionevents=savedInstanceState.getBooleanArray("selectedevents");
            listEvent=savedInstanceState.getParcelableArrayList("eventsArray");
            count = savedInstanceState.getInt("numberOfSelectedevents");

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
            if(count!=0){
                centralPageAdapter.setEventSelection(selectionevents);
                mactionMode= startSupportActionMode(this);
                mactionMode.setTitle(count + " selected");
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
        centralPageAdapter = new CentralPageAdapter(this,listEvent);
        evnetListView.setAdapter(centralPageAdapter);
        centralPageAdapter.notifyDataSetChanged();

        evnetListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        evnetListView.setOnItemClickListener(this);
        evnetListView.setMultiChoiceModeListener(this);

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

                startActivity(new Intent(CentralPageActivity.this, LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {


       // centralPageAdapter.toggleSelection(position);

        if(checked){
            count++;
            selectionevents[position]=true;

        }else {
            selectionevents[position]=false;

            if (count!=0){
                count--;
            }else{
                mode.finish();
            }


        }
        mode.setTitle(count + " selected");
        centralPageAdapter.setEventSelection(selectionevents);
    }

    public void buttonCreateNewEventPressed(View view){
        Intent intent= new Intent(CentralPageActivity.this, CreateNewEventActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);

    }

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        count = 0;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contxt_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
                case R.id.menu_delete:

                    int po=centralPageAdapter.getCount();

                    for (int i = po-1; i >=0; i--){
                        if (selected.get(i)) {
                            selectionevents[i]=false;
                            centralPageAdapter.setEventSelection(selectionevents);
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
    public void onDestroyActionMode(android.view.ActionMode mode) {

        selectionevents=new boolean[listEvent.size()];
        centralPageAdapter.setEventSelection(selectionevents);
        count=0;

    }

    @Override
    public void delete(int position) {
       listEvent.remove(position);


    }
    //Für Portr/Landsc Wechsel, Activity wird gekillt und Daten werden gesichert
    @Override
    protected void onSaveInstanceState(Bundle state) {

        super.onSaveInstanceState(state);
         state.putBooleanArray("selectedevents",selectionevents);
            state.putInt("numberOfSelectedevents", count);
            state.putParcelableArrayList("eventsArray", listEvent);
            state.putString("user", username);


    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        count = 0;
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
                    if (selected.get(i)) {
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
        selectionevents=new boolean[listEvent.size()];
        centralPageAdapter.setEventSelection(selectionevents);
        count=0;
    }
}