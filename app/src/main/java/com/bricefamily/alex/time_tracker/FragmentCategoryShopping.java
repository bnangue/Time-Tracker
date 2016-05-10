package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCategoryShopping.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCategoryShopping#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategoryShopping extends Fragment implements DialogDeleteEventFragment.OnDeleteEventListener, FragmentCommunicator,FragmentLife {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView eventpriode,creatorname,createdtime,notes,descriptionexpand;


    private MyRecyclerViewAdapter.MyClickListener myClickListener;
    private ListView lv_android;
    private MySQLiteHelper mySQLiteHelper;
    private OnFragmentInteractionListener mListener;
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

    public FragmentCategoryShopping() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategoryShopping.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategoryShopping newInstance(String param1, String param2) {
        FragmentCategoryShopping fragment = new FragmentCategoryShopping();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



      //  getEvents(mySQLiteHelper.getAllIncomingNotification());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_category_shopping, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        myClickListener=new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                setViews(v,position);
                Log.i(LOG_TAG, " shopping Clicked on Item " + position);
            }

            @Override
            public void onButtonClick(int position, View v) {
                int iD = v.getId();
                switch (iD) {
                    case R.id.buttondeletecardview:
                        DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(position);
                        dialogFragment.setCancelable(false);
                        dialogFragment.setTargetFragment(fragment, 1);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "DELETESHOPPINGEVENTFRAGMENT");

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
        mySQLiteHelper=new MySQLiteHelper(getContext());
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getContext(),collectionArrayList,myClickListener);


        mRecyclerView.setAdapter(mAdapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void passDataToFragment(ArrayList<CalendarCollection> someValue) {
        collectionArrayList=someValue;
    }

    @Override
    public void onUpdateUi(ArrayList<CalendarCollection> arrayList,String uName) {
        updateUi(arrayList);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void delete(int position) {
        //deleteFromSQLITEAndSERver(position);
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
                    //  getEvents(mySQLiteHelper.getAllIncomingNotification());

                }
            }
        });
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

        if(!isShown){

                prepareRecyclerView(getContext(), getCalendarEvents(mySQLiteHelper.getAllIncomingNotification()));

        }
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(myClickListener);


    }

    @Override
    public void onPause() {
        super.onPause();
        ((MyRecyclerViewAdapter)mAdapter).setOnItemClickListener(null);
    }

    public void updateUi(ArrayList<CalendarCollection> arrayList){
        ArrayList<CalendarCollection> a=new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).category.contains("Grocery")){
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
                if(object.category.contains("Grocery")){
                    a.add(object);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return a;
    }

}
