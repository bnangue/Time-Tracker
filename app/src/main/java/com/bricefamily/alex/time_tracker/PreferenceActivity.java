package com.bricefamily.alex.time_tracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class PreferenceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ArrayList<Item> items = new ArrayList<Item>();

    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        listView =(ListView)findViewById(R.id.listpreference);
        items.add(new SectionItem("Your account"));
        items.add(new EntryItem("My account "));
        items.add(new EntryItem("Community settings"));
        items.add(new EntryItem("Notification"));
        items.add(new EntryItem("Privacy settings"));
        items.add(new EntryItem("Check for updates"));


        items.add(new SectionItem("Questions or problems?"));
        items.add(new EntryItem("Help Center"));
        items.add(new EntryItem("FAQ"));
        items.add(new EntryItem("Licenses"));
        items.add(new EntryItem("About us"));

        items.add(new SectionItem(""));



        PreferenceListAdapter adapter = new PreferenceListAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preference, menu);
        return true;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!items.get(position).isSection()){

            EntryItem item = (EntryItem)items.get(position);

            Toast.makeText(this, "You clicked " + item.title, Toast.LENGTH_SHORT).show();


        }
    }
}
