package com.bricefamily.alex.time_tracker;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
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
public class UserFriendsListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,DialogRequestAddFriendFragment.OnRequestconfirm {

    ArrayList<User> userArrayListforGcm;
    ProfileListAdapter profileListAdapter;
    UserLocalStore userLocalStore;
    private SQLPictureHelper sqlPictureHelper;

    int[] status ;
    private ListView listView;
    SwipeRefreshLayout refreshLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());

        sqlPictureHelper=new SQLPictureHelper(getContext());
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
        serverRequestUser.fetchallUsers(user, new GetUserCallbacks() {
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
                    User user = new User();
                    ArrayList<User> listw = new ArrayList<User>();
                    for (int i = 0; i < reponse.size(); i++) {
                        String u = userLocalStore.getLoggedInUser().username;
                        if (u.equals(reponse.get(i).username)) {
                            userLocalStore.setUserUserfriendliststring(reponse.get(i).friendlist);
                        } else {
                            listw.add(reponse.get(i));
                        }
                    }
                    ArrayList<String> flist = user.getuserfriendlist(userLocalStore.getUserfriendliststring());
                    ArrayList<User> list = new ArrayList<User>();
                    for (int i = 0; i < listw.size(); i++) {
                        for (int j = 0; j < flist.size(); j++) {
                            if (flist.get(j).equals(listw.get(i).username)) {
                                list.add(listw.get(i));
                                if(listw.get(i).picture!=null){
                                    Bitmap b=sqlPictureHelper.getfriendPicture(listw.get(i).username);
                                    if(b!=null){
                                        sqlPictureHelper.updatefriendPicture(listw.get(i).username,listw.get(i).picture);
                                    }else {
                                        sqlPictureHelper.addfriendPicture(listw.get(i).username,listw.get(i).picture);
                                    }

                                }



                            }
                        }
                    }
                    userArrayListforGcm = list;
                    status = setstatuslist(list);

                    prepareListview(list, status);
                    refreshLayout.setRefreshing(false);

                }
            }
        });

    }



    void prepareListview(ArrayList<User> list,int[] statsus){
        profileListAdapter=new ProfileListAdapter(getContext(),list,userLocalStore.getLoggedInUser().username);

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
        showFilterPopup(v, pos);
        return true;
    }
    private void showFilterPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.friendpopup_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_follow:
                        unfollowFriend(position);
                        Toast.makeText(getActivity(), "removed as friend", Toast.LENGTH_SHORT).show();
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
    void unfollowFriend(int position){
        DialogFragment alertDialogFragment = DialogRequestAddFriendFragment.newInstance(position,true);
        alertDialogFragment.setTargetFragment(this, getTargetRequestCode());
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getFragmentManager(), "fragmentalluser");
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
            intent.putExtra("friendPicture",reciever.picture);
            intent.putExtra("notifreceiver",userLocalStore.getLoggedInUser().username);

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

    @Override
    public void onAdd(int position) {

    }

    @Override
    public void onRemove(int position) {
        removeuserinfriendlist(position);
    }
    public void removeuserinfriendlist(final int position) {

        User currentUser=userLocalStore.getLoggedInUser();
        ServerRequestUser serverRequestUser=new ServerRequestUser(getActivity());

        String f=userArrayListforGcm.get(position).friendlist;

        StringBuilder fadd=new StringBuilder();
        StringBuilder fre=new StringBuilder();

        String finalfriendlist=null;
        String finalhisfriendlist=null;

        User cuser=null;

        if(f.equals("noFrineds")|| f.isEmpty()||f==null){
            Toast.makeText(getContext(),userArrayListforGcm.get(position).username+" is not in friend list",Toast.LENGTH_SHORT).show();
        }else{
            String fcurrentuser=userLocalStore.getUserfriendliststring();



            String[] fl=fcurrentuser.split(",");

            for (int i =0;i<fl.length;i++){
                if(!userArrayListforGcm.get(position).username.equals(fl[i])|| fl[i].isEmpty()){
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
                if(!cu.equals(fls[i])|| fls[i].isEmpty()){
                    if(i==fls.length-1){
                        fadd.append(fls[i]);
                    }else {
                        fadd.append(fls[i]).append(",");
                    }

                }
            }

            finalhisfriendlist=fadd.toString();


        }


        String password=userArrayListforGcm.get(position).password;
        String email=userArrayListforGcm.get(position).email;
        String uname=userArrayListforGcm.get(position).username;
        User user=new User(uname,email,password,finalhisfriendlist,1);

        new FriendRequest(getContext(),cuser).uddateuserinfriendList(cuser);

        serverRequestUser.updatefriendFriendList(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {
                    Toast.makeText(getContext(), userArrayListforGcm.get(position).username + " removed from your friend list", Toast.LENGTH_SHORT).show();
                    FriendRequest friendRequest=new FriendRequest(getContext(),userArrayListforGcm.get(position));
                    friendRequest.sendFriendremove();
                    status[position] = 0;
                    userArrayListforGcm.remove(position);
                    profileListAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
}
