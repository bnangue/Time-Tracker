package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by bricenangue on 08/03/16.
 */
public class DialogFragmentTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private boolean bol;

    public static DialogFragmentTimePicker newInstance(boolean isStart) {
        DialogFragmentTimePicker frag = new DialogFragmentTimePicker();
        Bundle args = new Bundle();
        args.putBoolean("isStart", isStart);

        frag.setArguments(args);
        return frag;
    }

    public interface OnTimeSet{
        public void timeSet(String time, boolean isstarttime);
    }

    private OnTimeSet onTimeSet;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onTimeSet=(OnTimeSet)activity;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
        cal.set(Calendar.MINUTE,minute);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String ddte=formatter.format(cal.getTimeInMillis());
        onTimeSet.timeSet(ddte,bol);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        bol=getArguments().getBoolean("isStart");
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),this,hour,min,true);
    }
}
