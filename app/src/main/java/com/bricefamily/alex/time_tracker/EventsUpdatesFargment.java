package com.bricefamily.alex.time_tracker;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/02/16.
 */
public class EventsUpdatesFargment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private MySQLiteHelper db;
    private UserLocalStore userLocalStore;
    InboxEventAdapter inboxEventAdapter;
    ArrayList<IncomingNotification> incominglist;
    private  SQLPictureHelper sqlPictureHelper;

    int[] status ;
    private ListView listView;
    SwipeRefreshLayout refreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         db = new MySQLiteHelper(getContext());

        sqlPictureHelper=new SQLPictureHelper(getContext());
        View rootview =inflater.inflate(R.layout.new_update_event_notification,container,false);
        listView=(ListView)rootview.findViewById(R.id.listfriend);
        refreshLayout=(SwipeRefreshLayout)rootview.findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        refreshLayout.setOnRefreshListener(this);
        fetchuserlist();
        return rootview;
    }

    private void fetchuserlist() {
        incominglist=db.getAllIncomingNotification();
        prepareListview(incominglist);
        refreshLayout.setRefreshing(false);
    }

    void prepareListview(ArrayList<IncomingNotification> list){
        inboxEventAdapter=new InboxEventAdapter(getContext(),list);

        listView.setAdapter(inboxEventAdapter);
        inboxEventAdapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);


    }
    @Override
    public void onRefresh() {
        fetchuserlist();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showFilterPopup(view, position);
        return true;
    }

    private void showFilterPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.incomingpopup, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_follow:
                        db.deleteIncomingNotification(incominglist.get(position));
                        onRefresh();
                        Toast.makeText(getActivity(), "deleted", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_profile:
                        toupdate(position);
                        Toast.makeText(getActivity(), "marked as read", Toast.LENGTH_SHORT).show();
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

    void toupdate(int position){
        IncomingNotification incomingNotification=new IncomingNotification(incominglist.get(position).type,1 ,incominglist.get(position).body,incominglist.get(position).creationDate);
        incomingNotification.id=incominglist.get(position).id;
        int i= db.updateIncomingNotification(incomingNotification);
        if(i==0){
            Toast.makeText(getContext(),"ERror sql",Toast.LENGTH_SHORT).show();
        }else{
            onRefresh();
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int type=incominglist.get(position).type;
        int status=incominglist.get(position).readStatus;
        String jsodata=incominglist.get(position).body;

        switch (type){
            case 1:
                if(status==0){
                    try {
                        IncomingNotification incomingNotification=new IncomingNotification(incominglist.get(position).type,1 ,incominglist.get(position).body,incominglist.get(position).creationDate);
                        incomingNotification.id=incominglist.get(position).id;
                        JSONObject jsonObject=new JSONObject(jsodata);
                        Intent intent = new Intent(getActivity(),RequestHandlerActivity.class);

                        intent.putExtra("recieverName", jsonObject.getString("sender"));
                        intent.putExtra("receiver", jsonObject.getString("receiver"));
                        intent.putExtra("recieverregId", jsonObject.getString("recieverregId"));
                        intent.putExtra("messagefromgcm", jsonObject.getString("message"));
                        intent.putExtra("request", true);
                        intent.putExtra("myemail", jsonObject.getString("myemail"));
                        intent.putExtra("mypassword", jsonObject.getString("mypassword"));
                        intent.putExtra("notification", incomingNotification);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(getContext(),"You already reply this message",Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:
                getAllEventsFromDatabase(userLocalStore.getLoggedInUser().username);
                break;
            case 3:
                try {
                    JSONObject jsonObject=new JSONObject(jsodata);
                    Intent intent = new Intent(getActivity(),MessageForShowActivity.class);

                    intent.putExtra("recieverName", jsonObject.getString("sender"));
                    intent.putExtra("recieverregId", jsonObject.getString("recieverregId"));
                    intent.putExtra("messagefromgcm", jsonObject.getString("message"));
                    intent.putExtra("friendPicture",sqlPictureHelper.getfriendPicture(jsonObject.getString("sender")));

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 4:
                if(status==0){
                    try {
                        IncomingNotification incomingNotification=new IncomingNotification(incominglist.get(position).type,1 ,incominglist.get(position).body,incominglist.get(position).creationDate);
                        incomingNotification.id=incominglist.get(position).id;
                        JSONObject jsonObject=new JSONObject(jsodata);
                        Intent intent = new Intent(getActivity(),RemovedAsFriendActivity.class);

                        intent.putExtra("recieverName", jsonObject.getString("sender"));
                        intent.putExtra("receiver", jsonObject.getString("receiver"));
                        intent.putExtra("recieverregId", jsonObject.getString("recieverregId"));
                        intent.putExtra("messagefromgcm", jsonObject.getString("message"));
                        intent.putExtra("notification", incomingNotification);

                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getContext(),"You already reply this message",Toast.LENGTH_SHORT).show();
                }

                break;
            case 5:
                if(status==0){
                    try {
                        IncomingNotification incomingNotification=new IncomingNotification(incominglist.get(position).type,1 ,incominglist.get(position).body,incominglist.get(position).creationDate);
                        incomingNotification.id=incominglist.get(position).id;
                        JSONObject jsonObject=new JSONObject(jsodata);
                        Intent intent = new Intent(getActivity(),RemovedAsFriendActivity.class);

                        intent.putExtra("recieverName", jsonObject.getString("sender"));
                        intent.putExtra("receiver", jsonObject.getString("receiver"));
                        intent.putExtra("recieverregId", jsonObject.getString("recieverregId"));
                        intent.putExtra("messagefromgcm", jsonObject.getString("message"));
                        intent.putExtra("notification", incomingNotification);

                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(getContext(),"You already reply this message",Toast.LENGTH_SHORT).show();
                }

                break;
        }
        toupdate(position);
    }

    void getAllEventsFromDatabase(final String username) {

        ServerRequest serverRequest = new ServerRequest(getContext());
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(getActivity(), CentralPageActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("eventlist", returnedeventobject);

                    startActivity(intent);

                } else {
                    showdialg2("Uneable to fetch data form DataBase");
                }
            }

            @Override
            public void donec(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    private void showdialg2(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
