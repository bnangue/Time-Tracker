package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class NewUserTabsActivity extends ActionBarActivity {


    public static final String SERVER_ADDRESS = "http://time-tracker.comlu.com/";


    String found = "N";


    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<User> productResults = new ArrayList<User>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<User> filteredProductResults = new ArrayList<User>();

   // private Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"My Friends","All Users","Events"};
    int Numboftabs =3;


    ListView mDrawerList;
    RelativeLayout mDrawerpane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();

    private TextView userName;
    private CircularImageView profilePicture;
    private UserLocalStore userLocalStore;
    FloatingActionButton fab;
    private String username;
    private CharSequence drawerTitle;
    private CharSequence title,mtitel;

    private int position;
    User loggedinUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_tabs);
        userLocalStore=new UserLocalStore(this);
        prepareView();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cellSelected)));

        Bundle extras = getIntent().getExtras();
        if (getIntent().getExtras()!=null || extras != null) {
            username = extras.getString("username");
            loggedinUser=extras.getParcelable("loggedinUser");

        }


      //  toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimary));
        }
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        position= pager.getCurrentItem();
        prepareDrawerViews();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_users_tabs, menu);

        return true;
    }


    public void prepareView() {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.actionbarbackground, null);

        //center des ActionBar Titles
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

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

    void prepareDrawerViews(){
        fillList();

        String path=userLocalStore.getUserPicturePath();
        String uName=userLocalStore.getLoggedInUser().username;


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerpane = (RelativeLayout) findViewById(R.id.drawerpane);
        mDrawerList = (ListView) findViewById(R.id.navlist);
        userName = (TextView) findViewById(R.id.username);

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

            case R.id.action_search:
                startActivity(new Intent(NewUserTabsActivity.this,SearchResultActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen =mDrawerLayout.isDrawerOpen(mDrawerpane);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void selecItemFromDrawer(int position) {
        String titel=mNavItems.get(position).mTitel;

        switch (titel){
            case "Home":


                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Preferences":
                startActivity(new Intent(NewUserTabsActivity.this, PreferenceAppActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "About":
                startActivity(new Intent(NewUserTabsActivity.this,AboutApplicationActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Group":
                Intent intent = new Intent(NewUserTabsActivity.this, NewUserTabsActivity.class);

                startActivity(intent);

                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "GOOGLE MAP":
                startActivity(new Intent(NewUserTabsActivity.this,MAPActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            case "Timesheet":
                startActivity(new Intent(NewUserTabsActivity.this,TimeSheetActivity.class));
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerpane);
                break;
            default: return;
        }

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


    public void filterProductArray(String newText) {

        String pName;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).username.toLowerCase();
            if (pName.contains(newText.toLowerCase()) ||
                    productResults.get(i).email.contains(newText)) {
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

            User object;
            String matchFound = "N";
            //productResults is an arraylist with all product details for the search criteria
            //productResults.clear();

            URL url = null;
            HttpURLConnection urlConnection = null;
            try {

                url=new URL(SERVER_ADDRESS + "FetchAllUserAndPictures.php");
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

                    String username = jo_inside.getString("username");
                    String email = jo_inside.getString("email");
                    String password = jo_inside.getString("password");
                    String firstname = jo_inside.getString("firstname");
                    String lastname = jo_inside.getString("lastname");
                    int  status = jo_inside.getInt("onlineStatus");
                    String regId = jo_inside.getString("gcm_regid");
                    String imgString=jo_inside.getString("Image");
                    Bitmap bitmap=decodeBase64(imgString);
                    String friendlist=jo_inside.getString("friendList");


                      object =new User(username, email, password, firstname,
                            lastname,status,regId,bitmap,friendlist);


                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).username.equals(object.username)) {
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

                filterProductArray(textSearch);
            }
        }


    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
