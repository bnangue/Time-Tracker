package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchResultActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    public static final String SERVER_ADDRESS = "http://time-tracker.comlu.com/";


    ListView searchResults;
    String found = "N";

    ProfileListAdapter profileListAdapter;
    UserLocalStore userLocalStore;

    int[] status ;


    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<User> productResults = new ArrayList<User>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<User> filteredProductResults = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchResults=(ListView)findViewById(R.id.listview_search);
        userLocalStore=new UserLocalStore(this);

        if(filteredProductResults.size()!=0){
            prepareListviewall(filteredProductResults,setstatuslist(filteredProductResults));
        }
        prepareView();

    }
    public void prepareView() {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.actionbar_backgroud_resultactivity, null);
        SearchView search=(SearchView)view.findViewById(R.id.actionbarsearchactivity);
        search.setQueryHint("Start typing to search...");
        search.setFocusable(true);
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

                if (newText.length() > 0) {

                    searchResults.setVisibility(View.VISIBLE);
                    myAsyncTask m = (myAsyncTask) new myAsyncTask().execute(newText);
                } else {
                    searchResults.setVisibility(View.INVISIBLE);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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


                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);
                status=setstatuslist(filteredProductResults);
                prepareListviewall(filteredProductResults, status);

            }
        }


    }
    void prepareListviewall(ArrayList<User> list,int[] statsus){
        profileListAdapter=new ProfileListAdapter(this,list,userLocalStore.getLoggedInUser().username);

        searchResults.setAdapter(profileListAdapter);
        profileListAdapter.setUserStatus(statsus);
        profileListAdapter.notifyDataSetChanged();

        searchResults.setOnItemClickListener(this);

        if(status==null){
            status=new int[list.size()];
        }
    }

    private int[] setstatuslist(ArrayList<User> list){

        int[] status=new int[list.size()];
        for(int i=0;i <list.size();i++) {
            if (list.get(i).status == 0) {
                status[i] = 0;
            } else {
                status[i] = 1;
            }



        }

        return status;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
