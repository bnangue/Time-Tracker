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
    Fragment fragment;

    int[] status ;
    boolean[]friendstatus;
    private ListView listView;
    SwipeRefreshLayout refreshLayout;
    User u;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());
        fragment=this;
        User user=new User();
        userfriendsArrayList=user.getuserfriendlist(userLocalStore.getUserfriendliststring());

        View rootview=inflater.inflate(R.layout.activity_all_users_lists_tab,container,false);
        listView=(ListView)rootview.findViewById(R.id.listalluser);
        refreshLayout=(SwipeRefreshLayout)rootview.findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        refreshLayout.setOnRefreshListener(this);

         u=userLocalStore.getLoggedInUser();
        fetchuserlist(u);

        return rootview;
    }

    private ArrayList<User> fetchuserlist(User user){
        final ArrayList<User> staus = null;
        ServerRequestUser serverRequestUser=new ServerRequestUser(getContext());
        serverRequestUser.fetchallUsers(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                    ArrayList<User> list = new ArrayList<User>();
                    for(int i=0;i<reponse.size();i++){
                        if(userLocalStore.getLoggedInUser().username.equals(reponse.get(i).username)){
                            userLocalStore.setUserUserfriendliststring(reponse.get(i).friendlist);
                        }else{
                            list.add(reponse.get(i));
                        }
                    }
                    userArrayList = list;
                    status = setfriendstatuslist(list, userfriendsArrayList);

                    prepareListview(list, setfriendstatuslist(list, userfriendsArrayList));

                    friendstatus=setFriendstatus(list,userfriendsArrayList);
                    allUserTabAdapter.setfriendstatus(friendstatus);
                    refreshLayout.setRefreshing(false);

                }
            }
        });
        return staus;
    }



    boolean[] setFriendstatus(ArrayList<User> userArrayList,ArrayList<String> userfriendsArrayList){
        boolean[]frieds=new boolean[userArrayList.size()];
        for(int i=0; i<userArrayList.size();i++){
            for(int j=0;j<userfriendsArrayList.size();j++){
                if(userfriendsArrayList.get(j).equals(userArrayList.get(i).username)){
                    frieds[i]=true;
                }
            }
        }

        return frieds;
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
        if(friendstatus==null){
            friendstatus=new boolean[list.size()];
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
                        if(status[position]==1){
                            DialogFragment alertDialogFragment = DialogRequestAddFriendFragment.newInstance(position,true);
                            alertDialogFragment.setTargetFragment(fragment,getTargetRequestCode());
                            alertDialogFragment.setCancelable(false);
                            alertDialogFragment.show(getFragmentManager(), "fragmentalluser");
                        }else{
                            DialogFragment alertDialogFragment = DialogRequestAddFriendFragment.newInstance(position,false);
                            alertDialogFragment.setTargetFragment(fragment, getTargetRequestCode());
                            alertDialogFragment.setCancelable(false);
                            alertDialogFragment.show(getFragmentManager(), "fragmentalluser");
                        }
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

        Intent intent=new Intent(getActivity(),ViewFriendActivity.class);
        intent.putExtra("user", userArrayList.get(position));
        startActivity(intent);

    }

    public void removeuserinfriendlist(final int position) {

        User currentUser=userLocalStore.getLoggedInUser();
        ServerRequestUser serverRequestUser=new ServerRequestUser(getActivity());

        String f=userArrayList.get(position).friendlist;

        StringBuilder fadd=new StringBuilder();
        StringBuilder fre=new StringBuilder();

        String finalfriendlist=null;
        String finalhisfriendlist=null;

        User cuser=null;

        if(f.equals("noFrineds")|| f.isEmpty()||f==null){
            Toast.makeText(getContext(),userArrayList.get(position).username+" is not in friend list",Toast.LENGTH_SHORT).show();
        }else{
            String fcurrentuser=userLocalStore.getUserfriendliststring();



            String[] fl=fcurrentuser.split(",");

            for (int i =0;i<fl.length;i++){
                if(!userArrayList.get(position).username.equals(fl[i])){
                    if(i==fl.length-1){
                        fre.append(fl[i]);
                    }else {
                        fre.append(fl[i]).append(",");
                    }

                }
            }

            finalfriendlist=fre.toString();
            userLocalStore.setUserUserfriendliststring(finalfriendlist);
             cuser=new User(currentUser.username,currentUser.email,currentUser.password,finalfriendlist,1);

            String[] fls=f.split(",");
            String cu=currentUser.username;
                for (int i =0;i<fls.length;i++){
                    if(!cu.equals(fls[i])){
                        if(i==fls.length-1){
                            fadd.append(fls[i]);
                        }else {
                            fadd.append(fls[i]).append(",");
                        }

                    }
                }

                finalhisfriendlist=fadd.toString();


        }


        String password=userArrayList.get(position).password;
        String email=userArrayList.get(position).email;
        String uname=userArrayList.get(position).username;
        User user=new User(uname,email,password,finalhisfriendlist,1);

        new FriendRequest(getContext(),cuser).uddateuserinfriendList(cuser);

        serverRequestUser.updatefriendFriendList(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {
                    Toast.makeText(getContext(), userArrayList.get(position).username + " removed from your friend list", Toast.LENGTH_SHORT).show();
                    FriendRequest friendRequest=new FriendRequest(getContext(),userArrayList.get(position));
                    friendRequest.sendFriendremove();
                    status[position] = 0;
                    friendstatus[position] = false;
                    allUserTabAdapter.setRequeststatus(status);
                    allUserTabAdapter.setfriendstatus(friendstatus);
                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    public   void adduserinfriendList(final int position){
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
        if(userArrayList.get(position).regId==null){
            Toast.makeText(getContext(),"This user has not registered",Toast.LENGTH_SHORT).show();
        }else {
            FriendRequest friendRequest=new FriendRequest(getContext(),userArrayList.get(position));
            friendRequest.sendFriendresquest(true,null,null);
            status[position]=1;
            allUserTabAdapter.setRequeststatus(status);
        }

    }

    @Override
    public void onRemove(int position) {
        removeuserinfriendlist(position);
    }



    @Override
    public void onRefresh() {
        fetchuserlist(u);
    }
}
