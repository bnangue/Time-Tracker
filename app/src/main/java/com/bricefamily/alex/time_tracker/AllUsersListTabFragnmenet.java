package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class AllUsersListTabFragnmenet extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,
        AllUserTabAdapter.OnbuttonAddFriendPressed,DialogRequestAddFriendFragment.OnRequestconfirm,SwipeRefreshLayout.OnRefreshListener {
    ArrayList<User> userArrayList;
    ArrayList<String> userfriendsArrayList;
    AllUserTabAdapter allUserTabAdapter;
    UserLocalStore userLocalStore;

    int[] status ;
    private ListView listView;
    SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());
        User user=new User();
        userfriendsArrayList=user.getuserfriendlist(userLocalStore.getUserfriendliststring());

        View rootview=inflater.inflate(R.layout.activity_all_users_lists_tab,container,false);
        listView=(ListView)rootview.findViewById(R.id.listalluser);
        refreshLayout=(SwipeRefreshLayout)rootview.findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        refreshLayout.setOnRefreshListener(this);

        fetchuserlist();
        return rootview;
    }

    private ArrayList<User> fetchuserlist(){
        final ArrayList<User> staus = null;
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
                    userArrayList = reponse;
                    status = setfriendstatuslist(reponse, userfriendsArrayList);
                    prepareListview(reponse, setfriendstatuslist(reponse, userfriendsArrayList));
                    refreshLayout.setRefreshing(false);

                }
            }
        });
        return staus;
    }



    void prepareListview(ArrayList<User> list,int[] statsus){

        allUserTabAdapter=new AllUserTabAdapter(getContext(),list,this);

        listView.setAdapter(allUserTabAdapter);
        allUserTabAdapter.setRequeststatus(statsus);
        allUserTabAdapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        if(status==null){
            status=new int[list.size()];
        }
    }


    private int[] setfriendstatuslist(ArrayList<User> list,ArrayList<String> friendlist){
        int[] status=new int[list.size()];
        for(int i=0;i <list.size();i++){
            for (int j=0;j<friendlist.size();j++)
            {
                if(friendlist.get(j).equals(list.get(i).username)){
                    status[i]=1;
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
        showFilterPopup(v, pos);
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
                        ServerRequestUser serverRequestUser=new ServerRequestUser(getActivity());
                        String f=userLocalStore.getUserfriendliststring();
                        StringBuilder fadd=null;
                        String finalfriendlist=null;
                        if(f.equals("noFrineds")){
                            userLocalStore.setUserUserfriendliststring(userArrayList.get(position).username);
                            finalfriendlist=userArrayList.get(position).username;
                        }else{
                             fadd=new StringBuilder(f).append(",").append(userArrayList.get(position).username);
                            userLocalStore.setUserUserfriendliststring(fadd.toString());
                            finalfriendlist=fadd.toString();
                        }


                        String password=userLocalStore.getLoggedInUser().password;
                        String email=userLocalStore.getLoggedInUser().email;
                        String uname=userLocalStore.getLoggedInUser().username;
                        User user=new User(uname,email,password,finalfriendlist,1);
                        serverRequestUser.updateFriendList(user, new GetUserCallbacks() {
                            @Override
                            public void done(User returneduser) {

                            }

                            @Override
                            public void deleted(String reponse) {
                                if(reponse.contains("Friendlist successfully updated")){
                                    Toast.makeText(getContext(),userArrayList.get(position).username+" added to your friend list", Toast.LENGTH_SHORT).show();
                                    status[position]=1;
                                    allUserTabAdapter.setRequeststatus(status);
                                }

                            }

                            @Override
                            public void userlist(ArrayList<User> reponse) {

                            }
                        });
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

    private void removeuserinfriendlist(final int position) {

        ServerRequestUser serverRequestUser=new ServerRequestUser(getActivity());
        String f=userLocalStore.getUserfriendliststring();
        StringBuilder fadd=new StringBuilder();
        String finalfriendlist=null;
        if(f.equals("noFrineds")){
            Toast.makeText(getContext(),"You currently have no friends",Toast.LENGTH_SHORT).show();
        }else{
            String[] fls=f.split(",");
            for (int i =0;i<fls.length;i++){
                if(!userArrayList.get(position).username.equals(fls[i])){
                    if(i==fls.length-1){
                        fadd.append(fls[i]);
                    }else {
                        fadd.append(fls[i]).append(",");
                    }

                }
            }

            userLocalStore.setUserUserfriendliststring(fadd.toString());
            finalfriendlist=fadd.toString();
        }


        String password=userLocalStore.getLoggedInUser().password;
        String email=userLocalStore.getLoggedInUser().email;
        String uname=userLocalStore.getLoggedInUser().username;
        User user=new User(uname,email,password,finalfriendlist,1);
        serverRequestUser.updateFriendList(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {
                    Toast.makeText(getContext(), userArrayList.get(position).username + " removed from your friend list", Toast.LENGTH_SHORT).show();
                    status[position]=0;
                    allUserTabAdapter.setRequeststatus(status);
                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    private  void adduserinfriendList(final int position){
        ServerRequestUser serverRequestUser=new ServerRequestUser(getActivity());
        String f=userLocalStore.getUserfriendliststring();
        StringBuilder fadd=null;
        String finalfriendlist=null;
        if(f.equals("noFrineds")){
            userLocalStore.setUserUserfriendliststring(userArrayList.get(position).username);
            finalfriendlist=userArrayList.get(position).username;
        }else{
            fadd=new StringBuilder(f).append(",").append(userArrayList.get(position).username);
            userLocalStore.setUserUserfriendliststring(fadd.toString());
            finalfriendlist=fadd.toString();
        }


        String password=userLocalStore.getLoggedInUser().password;
        String email=userLocalStore.getLoggedInUser().email;
        String uname=userLocalStore.getLoggedInUser().username;
        User user=new User(uname,email,password,finalfriendlist,1);
        serverRequestUser.updateFriendList(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {
                    Toast.makeText(getContext(), userArrayList.get(position).username + " added to your friend list", Toast.LENGTH_SHORT).show();
                    status[position]=1;
                    allUserTabAdapter.setRequeststatus(status);
                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    @Override
    public void onbuttonAddPressed(int position) {
        if(status[position]==1){
            DialogFragment alertDialogFragment = DialogRequestAddFriendFragment.newInstance(position,true);
            alertDialogFragment.setTargetFragment(this,getTargetRequestCode());
            alertDialogFragment.setCancelable(false);
            alertDialogFragment.show(getFragmentManager(), "fragmentalluser");
        }else{
            DialogFragment alertDialogFragment = DialogRequestAddFriendFragment.newInstance(position,false);
            alertDialogFragment.setTargetFragment(this,getTargetRequestCode());
            alertDialogFragment.setCancelable(false);
            alertDialogFragment.show(getFragmentManager(), "fragmentalluser");
        }

    }

    @Override
    public void onAdd(int position) {
        adduserinfriendList(position);
    }

    @Override
    public void onRemove(int position) {
        removeuserinfriendlist(position);
    }



    @Override
    public void onRefresh() {
        fetchuserlist();
    }
}
