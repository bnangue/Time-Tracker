package com.bricefamily.alex.time_tracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CompleteProfileActivity extends AppCompatActivity {

    private EditText firtsnameed,lastnameed,ageed,postalcodeed,phonenumbed;
    private Button save ,cancel;
    private  String firstname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setViews();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    void setViews(){
        save= (Button)findViewById(R.id.buttonsaveCompleteprofile);
        save= (Button)findViewById(R.id.buttoncancelCompleteprofile);

        firtsnameed= (EditText)findViewById(R.id.editTextfirstnameCompletprofile);
        lastnameed= (EditText)findViewById(R.id.editTextlastnameCompleteprofile);
        ageed= (EditText)findViewById(R.id.editTextageCompleteprofile);
        postalcodeed= (EditText)findViewById(R.id.editTextAddress);
        phonenumbed= (EditText)findViewById(R.id.editTextphonenumbCompleteprofile);
    }
    public  void buttonSaveCompleteprofile(View view){

    }
    public  void buttonCancelCompleteprofile(View view){

    }

}
