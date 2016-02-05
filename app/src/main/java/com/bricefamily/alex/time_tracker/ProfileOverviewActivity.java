package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class ProfileOverviewActivity extends AppCompatActivity {

    private UserLocalStore userLocalStore;

    private User user;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLocalStore=new UserLocalStore(this);
        user=userLocalStore.getLoggedInUser();

        setContentView(R.layout.activity_profile_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    public void buttonOpencompleteProfilePressed(View view){
        Intent intent=new Intent(ProfileOverviewActivity.this,CompleteProfileActivity.class);
        intent.putExtra("username",user.username);
        startActivity(intent);
    }
}
