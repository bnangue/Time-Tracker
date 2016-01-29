package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
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


public class CentralPageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener,CentralPageAdapter.OndeleteFromList {

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
    List<EventObject> listEvent = new ArrayList<EventObject>();

    SparseBooleanArray sparseBooleanArray;
    public  android.support.v7.view.ActionMode mactionMode;
    int count = 0;
    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_page);
        userLocalStore = new UserLocalStore(this);
        drawerTitle = title = getTitle();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        fillList(formattedDate);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerpane = (RelativeLayout) findViewById(R.id.drawerpane);
        mDrawerList = (ListView) findViewById(R.id.navlist);
        userName = (TextView) findViewById(R.id.username);
        profilePicture = (ImageView) findViewById(R.id.avatar);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        prepareListview(listEvent);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecItemFromDrawer(position);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawerOpen, R.string.drawerClose);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    void prepareListview(List<EventObject> listEvent) {

        evnetListView = (ListView) findViewById(R.id.listviewdetails);
        centralPageAdapter = new CentralPageAdapter(this, R.layout.central_page_item,listEvent);
        evnetListView.setAdapter(centralPageAdapter);
        evnetListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        evnetListView.setOnItemClickListener(this);
        evnetListView.setMultiChoiceModeListener(this);

    }

    private void fillList(String formattedDate) {
        mNavItems.add(new NavItem("Home", "MeetUp Destination", R.drawable.colorhome));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.colorsettings));
        mNavItems.add(new NavItem("About", "learn more about time-tracker", R.drawable.colornfo));
        listEvent.add(new EventObject("Einkaufen", "wir müssen noch den Tag festlesgen", "Jacqueline", formattedDate));
        listEvent.add(new EventObject("Tanken", "wir müssen noch den Tag festlesgen", "Brice", formattedDate));
        listEvent.add(new EventObject("Kino Besuch", "wir müssen noch den Tag festlesgen", "Jacqueline", formattedDate));
        listEvent.add(new EventObject("Urlaub", "wir müssen noch den Tag festlesgen", "Brice", formattedDate));
        listEvent.add(new EventObject("Valentinestag", "wir müssen noch den Tag festlesgen", "Brice", formattedDate));
        listEvent.add(new EventObject("Romantic Dinner", "wir müssen noch den Tag festlesgen", "Brice & Jacqueline", formattedDate));
        listEvent.add(new EventObject("Abendessen", "wir müssen noch den Tag festlesgen", "Jacqueline", formattedDate));
        listEvent.add(new EventObject("Taken", "wir müssen noch den Tag festlesgen", "Brice", formattedDate));
        listEvent.add(new EventObject("Einkaufen", "wir müssen noch den Tag festlesgen", "Jacqueline", formattedDate));
        listEvent.add(new EventObject("Geschirrspülen", "wir müssen noch den Tag festlesgen", "Jacqueline", formattedDate));
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
    }


//    void showActionMode(){
//        mactionMode=startSupportActionMode(new ActionModeStarter());
//    }
//    void closeActionMode(){
//        mactionMode.finish();
//    }

    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {


        centralPageAdapter.toggleSelection(position);

        if(checked){
            count++;
            centralPageAdapter.setNewSelection(position,checked);

        }else {
            centralPageAdapter.removeSelection(position);
            if (count!=0){
                count--;
            }else{
                mode.finish();
            }


        }
        mode.setTitle(count+ " dselected");
    }
    public void removeEvent(int position){
        centralPageAdapter.remove(listEvent.get(position));

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

                    SparseBooleanArray selected = centralPageAdapter.getSelectedIds();
                    int po=centralPageAdapter.getCount();

                    for (int i = po-1; i >=0; i--){
                        if (selected.get(i)) {
                          centralPageAdapter.remove(listEvent.get(i));
                        }
                    }
//                    for (int i=0; i<listEvent.size();i++){
//                        if(centralPageAdapter.isPositionChecked(i)){
//                           removeEvent(i);
//                        }
//
//                    }
//
                    centralPageAdapter.notifyDataSetChanged();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {

        centralPageAdapter.clearSelection();
        centralPageAdapter.removeSelection();

    }

    @Override
    public void delete(int position) {
       listEvent.remove(position);


    }

//    private final class ActionModeStarter implements android.support.v7.view.ActionMode.Callback {
//
//
//        @Override
//        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.contxt_menu, menu);
//
//            mactionMode = mode;
//
//            return true;
//
//        }
//
//        @Override
//        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_delete:
//
//                    for (Object Item : selectedItems) {
//                        EventObject e = (EventObject) Item;
//                        centralPageAdapter.removeEvent(e);
//
//                    }
//                    centralPageAdapter.notifyDataSetChanged();
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }
//
//        }
//
//        @Override
//        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
//
//          mactionMode=null;
//            selectedItems = new boolean[centralPageAdapter.getCount()];
//            count = 0;
//            centralPageAdapter.notifyDataSetChanged();
//        }
//
//
//    }

}