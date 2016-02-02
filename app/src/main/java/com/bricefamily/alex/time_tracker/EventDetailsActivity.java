package com.bricefamily.alex.time_tracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class EventDetailsActivity extends AppCompatActivity {

    String titel,infotext,time,creator;
    TextView titeltv,infotexttv,timetv,creatortv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            time=bundle.getString("time");
            titel=bundle.getString("titel");
            infotext=bundle.getString("textinfo");
            creator=bundle.getString("creator");

        }

        titeltv=(TextView)findViewById(R.id.textViewdetailstitel);
        infotexttv=(TextView)findViewById(R.id.textViewdetailstextinfo);
        timetv=(TextView)findViewById(R.id.textViewdetailstime);
        creatortv=(TextView)findViewById(R.id.textViewdetailscreator);
        setDetails();
    }


    private void setDetails(){
        timetv.setText(time);
        creatortv.setText(creator);

        titeltv.setText(titel);
        infotexttv.setText(infotext);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_share:
                //do something to share
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
