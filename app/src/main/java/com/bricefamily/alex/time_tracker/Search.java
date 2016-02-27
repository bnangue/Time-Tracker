package com.bricefamily.alex.time_tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by bricenangue on 24/02/16.
 */
public class Search extends Fragment implements CentralPageAdapter.OnEventSelected {

    public static final String SERVER_ADDRESS = "http://time-tracker.comlu.com/";

    View myFragmentView;
    SearchView search;
    ImageButton buttonBarcode;
    ImageButton buttonAudio;
    ListView searchResults;
    String found = "N";


    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<EventObject> productResults = new ArrayList<EventObject>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<EventObject> filteredProductResults = new ArrayList<EventObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the context of the HomeScreen Activity
        final NewUserTabsActivity activity = (NewUserTabsActivity) getActivity();

        //define a typeface for formatting text fields and listview.

        myFragmentView = inflater.inflate(R.layout.search_fragment, container, false);

        search = (SearchView) myFragmentView.findViewById(R.id.searchView1);
        search.setQueryHint("Start typing to search...");

        searchResults = (ListView) myFragmentView.findViewById(R.id.listview_search);
        buttonBarcode = (ImageButton) myFragmentView.findViewById(R.id.imageButton2);
        buttonAudio = (ImageButton) myFragmentView.findViewById(R.id.imageButton1);


        buttonAudio.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                // Specify free form input
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please start speaking");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);
                startActivityForResult(intent, 2);
            }
        });
        //this part of the code is to handle the situation when user enters any search criteria, how should the
        //application behave?

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                //Toast.makeText(activity, String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 1) {

                    searchResults.setVisibility(myFragmentView.VISIBLE);
                    myAsyncTask m = (myAsyncTask) new myAsyncTask().execute(newText);
                } else {

                    searchResults.setVisibility(myFragmentView.INVISIBLE);
                }


                return false;
            }

        });
        return myFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //this requestCode is for handling the barcode activity.
        if(requestCode==1)
        {
            String barcode=data.getStringExtra("BARCODE");
            if (barcode.equals("NULL"))
            {
                //that means barcode could not be identified or user pressed the back button
                //do nothing
            }
            else
            {
                search.setQuery(barcode, true);
                search.setIconifiedByDefault(false);
            }
        }

        //this requestCode is for speechRecognizer. Only this part of the code needs to be added for
        //the implementation of voice to text functionality.

        if (requestCode == 2) {
            ArrayList<String> results;
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //Toast.makeText(this, results.get(0), Toast.LENGTH_SHORT).show();

            //if the name has an ' then the SQL is failing. Hence replacing them.
            String text = results.get(0).replace("'","");
            search.setQuery(text, true);
            search.setIconifiedByDefault(false);
        }

    }

    //this filters products from productResults and copies to filteredProductResults based on search text

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

    @Override
    public void selected(int count, boolean[] events) {

    }

    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class myAsyncTask extends AsyncTask<String, Void, String> {
        JSONArray productList;
        String textSearch;
        ProgressDialog pd;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList = new JSONArray();
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
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
                Toast.makeText(getActivity(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

                pd.dismiss();
            } else {


                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);
                searchResults.setAdapter(new CentralPageAdapter(getActivity(), filteredProductResults, (CentralPageAdapter.OnEventSelected) this));
                pd.dismiss();
            }
        }


    }


}

