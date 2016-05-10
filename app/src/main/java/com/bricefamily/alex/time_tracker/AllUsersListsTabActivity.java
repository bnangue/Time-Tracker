package com.bricefamily.alex.time_tracker;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AllUsersListsTabActivity extends ActionBarActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

        ArrayList<User> userArrayList;
        ProfileListAdapter profileListAdapter;
        UserLocalStore userLocalStore;

        int[] status ;
private ListView listView;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_lists_tab);
        userLocalStore=new UserLocalStore(this);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
        userArrayList =extras.getParcelableArrayList("userlist");
        }
        else{
        User u=userLocalStore.getLoggedInUser();
        fetchuserlist(u);

        }


        }

private int[] fetchuserlist(User user){
        final int[][] status = new int[1][1];
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.fetchallUsers(user,new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

                @Override
                public void serverReponse(String reponse) {

                }

                @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                        prepareListview(reponse, setstatuslist(reponse));
                   status[0] =setstatuslist(reponse);
                }
            }
        });
        return status[0];
        }



        void prepareListview(ArrayList<User> list,int[] statsus){
        listView=(ListView)findViewById(R.id.listalluser);
        profileListAdapter=new ProfileListAdapter(this,list,"");

        listView.setAdapter(profileListAdapter);
        profileListAdapter.setUserStatus(statsus);
        profileListAdapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

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


@Override
public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        onLongListItemClick(view, position,id);
        return true;
        }
protected boolean onLongListItemClick(View v, int pos, long id) {
        //selectionevents[pos]=true;
        // countevent=countevent+1;
        // selected(countevent, selectionevents);
        // centralPageAdapter.notifyDataSetChanged();
        showFilterPopup(v, pos);
        return true;
        }
private void showFilterPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_follow:
        Toast.makeText(AllUsersListsTabActivity.this, "share", Toast.LENGTH_SHORT).show();
        return true;
        case R.id.menu_profile:
        Intent intent=new Intent(AllUsersListsTabActivity.this,ViewFriendActivity.class);
        intent.putExtra("user", userArrayList.get(position));
        startActivity(intent);
        return true;
default:
        return false;
        }
        }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
        }

@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getApplicationContext(),"User currently offline",Toast.LENGTH_SHORT).show();

        }


}
