package com.bricefamily.alex.time_tracker;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentAllEvents extends Fragment implements DialogDeleteEventFragment.OnDeleteEventListener,FragmentCommunicator,FragmentLife {

    private TextView eventpriode, creatorname, createdtime, notes, descriptionexpand;

    private MySQLiteHelper mySQLiteHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter.MyClickListener myClickListener;
    private static String LOG_TAG = "RecyclerViewActivity";
    private static String LOG_TAGT = "RecyclerDeleteActivity";
    private Fragment fragment = this;
    private ArrayList<CalendarCollection> newItems = new ArrayList<>();
    private ArrayList<CalendarCollection> collectionArrayList = new ArrayList<>();
    private UserLocalStore userLocalStore;

    private boolean isShown = false;
    private AlertDialog alertDialog;

    private void prepareRecyclerView(Context context, ArrayList<CalendarCollection> arrayList) {

        mAdapter = new MyRecyclerViewAdapter(context, arrayList, myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void prepareRecyclerView(ArrayList<CalendarCollection> arrayList) {

        mAdapter = new MyRecyclerViewAdapter(getContext(), arrayList, myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void passDataToFragment(ArrayList<CalendarCollection> someValue) {
        collectionArrayList = someValue;
    }


    private OnCalendarEventsChanged calendarEventsChanged;

    public FragmentAllEvents() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        calendarEventsChanged = (OnCalendarEventsChanged) getActivity();
        ((NewCalendarActivty) getActivity()).fragmentCommunicator = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        userLocalStore=new UserLocalStore(getContext());

        View rootView = inflater.inflate(R.layout.fragment_fragment_all_events, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        myClickListener = new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                setViews(v, position);
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.setTargetFragment(fragment, 1);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "DELETEALLEVENTFRAGMENT");


                        break;
                    case R.id.buttonsharecardview:

                        break;
                }
            }
        };

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);


        return rootView;

    }




    void showFriendstosharewith() {
        // Intialize  readable sequence of char values
        List<CharSequence> list = new ArrayList<CharSequence>();

        for (int i=0;i<7;i++){

            list.add("Friend  " + i);  // Add the item in the list
        }
        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Select Item");
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                if (stringBuilder.length() > 0) stringBuilder.append(",");
                                stringBuilder.append(list.getItemAtPosition(i));

                            }
                        }

                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                        if (stringBuilder.toString().trim().equals("")) {

                            stringBuilder.setLength(0);

                        } else {

                        }
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
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
    void showDialogListCalendarEvent(CalendarCollection collectionsevent){



        LayoutInflater inflater = getLayoutInflater(null);
        View convertView = (View) inflater.inflate(R.layout.calendar_event_details_show, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Added Events");

        TextView txt = (TextView) convertView.findViewById(R.id.titeleventcalendar);
        txt.setText(collectionsevent.description );
        Button btn = (Button) convertView.findViewById(R.id.buttonOkarlertCalendareventshow);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mySQLiteHelper=new MySQLiteHelper(getContext());
        mAdapter = new MyRecyclerViewAdapter(getContext(), collectionArrayList, myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();


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


        if(!isShown){


            collectionArrayList=getCalendarEvents(mySQLiteHelper.getAllIncomingNotification());
                prepareRecyclerView(getContext(),collectionArrayList);


        }


        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(myClickListener);


    }

    public void updateUi(Context context,ArrayList<CalendarCollection> arrayList){
        prepareRecyclerView(context,arrayList);
    }
    @Override
    public void onPause() {
        super.onPause();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(null);

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
                a.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return a;
    }



    @Override
    public void delete(int position) {
        ((MyRecyclerViewAdapter)mAdapter).deleteItem(position);

        calendarEventsChanged.eventsCahnged(true);

    }




    @Override
    public void onUpdateUi(ArrayList<CalendarCollection> arrayList,String uName) {
        prepareRecyclerView(arrayList);
    }

}
