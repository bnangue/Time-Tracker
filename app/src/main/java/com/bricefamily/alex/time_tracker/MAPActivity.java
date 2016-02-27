package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MAPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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
        View view = inflator.inflate(R.layout.actionbarbackground, null);
        TextView titel=(TextView)view.findViewById(R.id.recievername);
        titel.setText("MAP");


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

}
