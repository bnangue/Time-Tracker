package com.bricefamily.alex.time_tracker;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;

public class UserListTabsActivity extends TabActivity {

    ArrayList<User> userArrayListforGcm;
    ArrayList<User> userArrayList;
    UserLocalStore userLocalStore;
    private static final String INBOX_SPEC = "My Friends";
    private static final String OUTBOX_SPEC = "All Users";


    private ViewPager mViewPager;

    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_tabs);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            userArrayListforGcm =extras.getParcelableArrayList("userlistforgcm");
            userArrayList =extras.getParcelableArrayList("userlist");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cellSelected)));
        TabHost tabHost = getTabHost();

        if(savedInstanceState!=null){
            userArrayList=savedInstanceState.getParcelableArrayList("list");
            userArrayListforGcm=savedInstanceState.getParcelableArrayList("listforgcm");
        }
        // Inbox Tab
        TabHost.TabSpec userlisttabSpec = tabHost.newTabSpec(INBOX_SPEC);
        // Tab Icon
        userlisttabSpec.setIndicator(INBOX_SPEC, getResources().getDrawable(R.drawable.colorhome));
        Intent inboxIntent = new Intent(this, UserFriendListTabActivity.class);
        inboxIntent.putExtra("userlistforgcm",userArrayListforGcm);
        inboxIntent.putExtra("userlist",userArrayList);

        // Tab Content
        userlisttabSpec.setContent(inboxIntent);

        // Outbox Tab
        TabHost.TabSpec userfriendlisttabSpec = tabHost.newTabSpec(OUTBOX_SPEC);
        userfriendlisttabSpec.setIndicator(OUTBOX_SPEC, getResources().getDrawable(R.drawable.groupuser));
        Intent outboxIntent = new Intent(this, AllUsersListsTabActivity.class);
        inboxIntent.putExtra("userlist",userArrayList);

        userfriendlisttabSpec.setContent(outboxIntent);

        // Profile Tab


        // Adding all TabSpec to TabHost
        tabHost.addTab(userlisttabSpec); // Adding Inbox tab
        tabHost.addTab(userfriendlisttabSpec); // Adding Outbox tab
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList("listforgcm", userArrayListforGcm);
        outState.putParcelableArrayList("list",userArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(UserListTabsActivity.this,HomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }
}
