package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bricenangue on 15/02/16.
 */
public class AllUsersListTabFragnmenet extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    ArrayList<User> userArrayList;
    ProfileListAdapter profileListAdapter;
    UserLocalStore userLocalStore;
    int[] status ;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());


        View rootview=inflater.inflate(R.layout.activity_all_users_lists_tab,container,false);
        fetchuserlist(rootview);
        return rootview;
    }

    private int[] fetchuserlist(final View view){
        final int[][] status = new int[1][1];
        ServerRequestUser serverRequestUser=new ServerRequestUser(getContext());
        serverRequestUser.fetchallUsers(new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                    prepareListview(view,reponse, setstatuslist(reponse));
                    status[0] =setstatuslist(reponse);
                }
            }
        });
        return status[0];
    }



    void prepareListview(View v,ArrayList<User> list,int[] statsus){
        listView=(ListView)v.findViewById(R.id.listalluser);
        profileListAdapter=new ProfileListAdapter(getContext(),list);

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
        showFilterPopup(v,pos);
        return true;
    }
    private void showFilterPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_follow:
                        Toast.makeText(getActivity(), "share", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_profile:
                        Intent intent=new Intent(getActivity(),ViewFriendActivity.class);
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

        Toast.makeText(getActivity(),"User currently offline",Toast.LENGTH_SHORT).show();

    }
}
