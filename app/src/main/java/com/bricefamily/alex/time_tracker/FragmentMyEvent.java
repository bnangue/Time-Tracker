package com.bricefamily.alex.time_tracker;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyEvent extends Fragment implements DialogDeleteEventFragment.OnDeleteEventListener, FragmentCommunicator ,FragmentLife{


    private TextView eventpriode,creatorname,createdtime,notes,descriptionexpand;

    private MyRecyclerViewAdapter.MyClickListener myClickListener;
    private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "RecyclerViewActivity";
    private Fragment fragment=this;
    private ArrayList<CalendarCollection> newItems = new ArrayList<>();
    private ArrayList<CalendarCollection> collectionArrayList = new ArrayList<>();

    private boolean isShown=false;
    private OnCalendarEventsChanged calendarEventsChanged;


    private void prepareRecyclerView(Context context,ArrayList<CalendarCollection> arrayList){

        mAdapter = new MyRecyclerViewAdapter(context,arrayList,myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void prepareRecyclerView(ArrayList<CalendarCollection> arrayList){

        mAdapter = new MyRecyclerViewAdapter(getContext(),arrayList,myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }
    public FragmentMyEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


      //  getEvents(mySQLiteHelper.getAllIncomingNotification());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_my_event, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        myClickListener=new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                setViews(v,position);
                Log.i(LOG_TAG, " MY event Clicked on Item " + position);
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview:

                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.setTargetFragment(fragment, 1);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "DELETEMYEVENTEVENTFRAGMENT");

                        break;
                    case R.id.buttonsharecardview:
                        break;
                }
            }
        };


        return rootView;
    }

    private void setViews(View v, int position){
        creatorname = (TextView) v.findViewById(R.id.textViewexpandcreator);
        createdtime = (TextView) v.findViewById(R.id.textViewexpandcreationtime);
        eventpriode = (TextView) v.findViewById(R.id.textViewexpandperiode);
        descriptionexpand = (TextView) v.findViewById(R.id.textViewexpanddescription);
        notes = (TextView) v.findViewById(R.id.textViewexpandnote);



        if(collectionArrayList.size()!=0){
            CalendarCollection ecollection=collectionArrayList.get(position);
            creatorname.setText(ecollection.creator);
            createdtime.setText(ecollection.creationdatetime);
            descriptionexpand.setText(ecollection.description);

            String[] sttime=ecollection.startingtime.split(" ");
            String[] edtime=ecollection.endingtime.split(" ");

            eventpriode.setText(sttime[0]+"  -  "+edtime[0]);
            StringBuilder builder=new StringBuilder();
            if(ecollection.alldayevent.equals("1")){
                builder.append("All day");
            }
            if(ecollection.alldayevent.equals("1")){
                builder.append(",").append(" repeat every month");
            }
            if(builder.toString().isEmpty()){
                notes.setText("");
            }else{
                notes.setText(builder.toString());
            }

        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        userLocalStore=new UserLocalStore(getContext());
        mySQLiteHelper=new MySQLiteHelper(getContext());

        mAdapter = new MyRecyclerViewAdapter(getContext(),collectionArrayList,myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        calendarEventsChanged =(OnCalendarEventsChanged)getActivity();
        ((NewCalendarActivty)getActivity()).fragmentCommunicator = this;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

        } else {
            isShown = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //collectionArrayList=CalendarCollection.date_collection_arr;
        if(!isShown){


                prepareRecyclerView(getContext(),getCalendarEvents(mySQLiteHelper.getAllIncomingNotification()));


        }
                ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(myClickListener);



    }


    @Override
    public void onPause() {
        super.onPause();

        ((MyRecyclerViewAdapter)mAdapter).setOnItemClickListener(null);
    }

    @Override
    public void delete(int position) {
       // deleteFromSQLITEAndSERver(position);
        ((MyRecyclerViewAdapter)mAdapter).deleteItem(position);
        //((MyRecyclerViewAdapter)mAdapter).notifyDataSetChanged();
        calendarEventsChanged.eventsCahnged(true);
    }

    private void deleteFromSQLITEAndSERver(final int index){
        ServerRequests serverRequests= new ServerRequests(getContext());
        serverRequests.deleteCalenderEventInBackgroung(collectionArrayList.get(index), new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {

            }

            @Override
            public void donec(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void updated(String reponse) {
                if (reponse.contains("Event successfully deleted")) {
                    mySQLiteHelper.deleteIncomingNotificationcal(collectionArrayList.get(index).incomingnotifictionid);
                    // getEvents(mySQLiteHelper.getAllIncomingNotification());

                }
            }
        });
    }
    public void updateUi(ArrayList<CalendarCollection> arrayList,String uName){
        ArrayList<CalendarCollection> a=new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).creator.contains(uName)){
                a.add(arrayList.get(i));
            }
        }
        prepareRecyclerView(a);
    }
    private ArrayList<CalendarCollection> getCalendarEvents(ArrayList<IncomingNotification> incomingNotifications){

        ArrayList<CalendarCollection> a =new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            try {
                jo_inside = new JSONObject(incomingNotifications.get(i).body);

                String titel = jo_inside.getString("title");
                String infotext = jo_inside.getString("description");
                String creator = jo_inside.getString("creator");
                String creationTime = jo_inside.getString("datetime");
                String category = jo_inside.getString("category");
                String startingtime = jo_inside.getString("startingtime");
                String endingtime = jo_inside.getString("endingtime");
                String alldayevent = jo_inside.getString("alldayevent");
                String eventHash = jo_inside.getString("hashid");
                String everymonth = jo_inside.getString("everymonth");
                String creationdatetime = jo_inside.getString("defaulttime");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);
                object.incomingnotifictionid = incomingNotifications.get(i).id;
                if(object.creator.contains(userLocalStore.getLoggedInUser().getfullname())){
                    a.add(object);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return a;
    }


    @Override
    public void passDataToFragment(ArrayList<CalendarCollection> someValue) {
        collectionArrayList=someValue;
    }

    @Override
    public void onUpdateUi(ArrayList<CalendarCollection> arrayList,String uName) {
        updateUi(arrayList,uName);
    }
}
