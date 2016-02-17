package com.bricefamily.alex.time_tracker;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
public class UserFriendsListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ArrayList<User> userArrayListforGcm;
    ArrayList<User> userArrayList;
    ProfileListAdapter profileListAdapter;
    UserLocalStore userLocalStore;

    int[] status ;
    private ListView listView;
    SwipeRefreshLayout refreshLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());

        View rootview =inflater.inflate(R.layout.activity_user_friend_list_tab, container, false);
        User u=userLocalStore.getLoggedInUser();
        listView=(ListView)rootview.findViewById(R.id.listfriend);
        refreshLayout=(SwipeRefreshLayout)rootview.findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        refreshLayout.setOnRefreshListener(this);
        fetchuserlist(u);

        return rootview;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fetchuserlist(User user){
        final ServerRequestUser serverRequestUser=new ServerRequestUser(getContext());
        serverRequestUser.fetchallUserForGcm(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                    ArrayList<User> users = new ArrayList<User>();
                    users = reponse;
                    final ArrayList<User> finalUsers = users;
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
                                User user=new User();
                                ArrayList<String> flist=user.getuserfriendlist(userLocalStore.getUserfriendliststring());
                                ArrayList<User> list=new ArrayList<User>();
                                for(int i=0; i<finalUsers.size();i++){
                                    for(int j=0;j<flist.size();j++){
                                        if(flist.get(j).equals(finalUsers.get(i).username)){
                                            list.add(finalUsers.get(i));
                                        }
                                    }
                                }
                                userArrayListforGcm=list;
                                status=setstatuslist(reponse,list);

                                prepareListview(list, status);
                                refreshLayout.setRefreshing(false);

                            }
                        }
                    });

                }
            }
        });
    }



    void prepareListview(ArrayList<User> list,int[] statsus){
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

    private int[] setstatuslist(ArrayList<User> list,ArrayList<User> gcmlist){
        int[] status=new int[gcmlist.size()];

        for(int i=0;i <gcmlist.size();i++){
            for (int j=0;j<list.size();j++)
            {
                if(list.get(j).username.equals(gcmlist.get(i).username)){

                    if(list.get(j).status==0){
                        status[i]=0;
                    }else {
                        status[i]=1;
                    }

                }
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
        PopupMenu popup = new PopupMenu(getContext(), v);
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
                        intent.putExtra("user", userArrayListforGcm.get(position));
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

        if(status[position]!=0){
            User reciever= userArrayListforGcm.get(position);
            String recievername=reciever.username;
            String reciverregId=reciever.regId;
            Intent intent =new Intent(getActivity(),LiveChatActivity.class);
            intent.putExtra("recieverName",recievername);
            intent.putExtra("recieverregId",reciverregId);
            intent.putExtra("message","");
            startActivity(intent);

        }else{
            Toast.makeText(getContext(),"User "+ userArrayListforGcm.get(position).username
                    +" currently offline",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        fetchuserlist(userLocalStore.getLoggedInUser());
    }
}
