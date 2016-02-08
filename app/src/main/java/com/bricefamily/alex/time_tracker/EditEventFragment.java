package com.bricefamily.alex.time_tracker;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditEventFragment extends Fragment implements View.OnClickListener ,DialogFragmentDatePicker.OnDateGet, TextView.OnEditorActionListener {


    private TextView currenttime;
    private EditText titeled,detailed,dateed,noteed,creatornameed;
    private String titelstr,detailsstr,notestr,datestr,status,currenttimestr;
    DateEventObject dateEventObject;
    private EventObject eventObject;
    UserLocalStore userLocalStore;

    private String eventHash;

    public static EditEventFragment newInstance(String title,String details,String note,String date,String hash){
        EditEventFragment fragment= new EditEventFragment();
        Bundle args=new Bundle();
        args.putString("title",title);
        args.putString("details",details);
        args.putString("note",note);
        args.putString("date",date);
        args.putString("hash",hash);
        fragment.setArguments(args);
        return fragment;
    }
    public EditEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String tit=getArguments().getString("title");
        final String det=getArguments().getString("details");
        final String not=getArguments().getString("note");
        final String dat=getArguments().getString("date");
        final String hsh=getArguments().getString("hash");



        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        currenttimestr=formattedDate;

        userLocalStore=new UserLocalStore(getContext());
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit_event, container, false);
        currenttime=(TextView)v.findViewById(R.id.textVieweventcurrenttime);
        currenttime.setText(currenttimestr);
        titeled= (EditText)v.findViewById(R.id.editTexteventtitel);
        titeled.setText(tit);
        detailed= (EditText)v.findViewById(R.id.editTexteventdetails);
        detailed.setText(det);
        dateed= (EditText)v.findViewById(R.id.editTexteventdate);
        dateed.setText(dat);
        noteed= (EditText)v.findViewById(R.id.editTexteventnote);
        noteed.setText(not);
        noteed.setOnEditorActionListener(this);
        creatornameed= (EditText)v.findViewById(R.id.editTexteventcreator);
        creatornameed.setText(userLocalStore.getLoggedInUser().username);
        creatornameed.setEnabled(false);
        ImageView datepicker=(ImageView)v.findViewById(R.id.imageButton);
        datepicker.setOnClickListener(this);

        eventHash=hsh;
        Button update=(Button)v.findViewById(R.id.button);
        Button cancel=(Button)v.findViewById(R.id.buttoncancel);
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);


        return v;

    }

    public void onDatePickercliced(){
        android.support.v4.app.FragmentManager manager=getFragmentManager();
        DialogFragment fragmentDatePicker=new DialogFragmentDatePicker();
        fragmentDatePicker.setTargetFragment(this,0);

        fragmentDatePicker.show(manager,"datePickerfr");
    }
  void uevent( ){
      titelstr=titeled.getText().toString();
      detailsstr=detailed.getText().toString();
      notestr=noteed.getText().toString();
      datestr=dateed.getText().toString();
       String creatornamestr=userLocalStore.getLoggedInUser().username;

      if(titelstr.isEmpty() || datestr.isEmpty()||notestr.isEmpty()){
          Toast.makeText(getContext(), "Please fill empty filed", Toast.LENGTH_SHORT).show();
      }else{
          String[] date=datestr.split("[.]");
          dateEventObject=new DateEventObject(date[0],date[1],date[2]);
          status="1";
          int eventHashcode=(titelstr+creatornamestr+currenttimestr).hashCode();
          eventObject=new EventObject(titelstr,detailsstr,creatornamestr,currenttimestr
                  ,dateEventObject,status,String.valueOf(eventHashcode));

          updateEvent(eventObject,eventHash);
      }
  }

    void updateEvent(EventObject eventObject,String hash){
        ServerRequest serverRequest=new ServerRequest(getContext());
        serverRequest.updateEvents(eventObject, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {

            }

            @Override
            public void updated(String reponse) {
                if(reponse.contains("Event successfully updated")){
                    getActivity().onBackPressed();
                    getEventsFromDatabase(userLocalStore.getLoggedInUser().username);

                }else{

                }
            }
        },hash);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                uevent();
                break;
            case R.id.buttoncancel:
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.imageButton:
                onDatePickercliced();
                break;
        }
    }

    void  getEventsFromDatabase(final String username){

        ServerRequest serverRequest=new ServerRequest(getContext());
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(getActivity(), CentralPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }


    @Override
    public void dateSet(int year, int month, int day) {
        dateed.setText(new StringBuilder().append(day).append(".")
                .append(month +1).append(".").append(year));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
            uevent();
        }

            return false;
    }
}
