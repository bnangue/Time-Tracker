package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by bricenangue on 04/02/16.
 */
public class DialogFragmentDatePicker1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    String ddte;
    boolean boli;


    public static DialogFragmentDatePicker1 newInstance(boolean isStart) {
        DialogFragmentDatePicker1 frag = new DialogFragmentDatePicker1();
        Bundle args = new Bundle();
        args.putBoolean("isStart", isStart);

        frag.setArguments(args);
        return frag;
    }

    public interface OnDateGet{
        public void dateSet(String date, boolean isstart);
    }

   private OnDateGet dateGet;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            dateGet=(OnDateGet)activity;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

        boli=getArguments().getBoolean("isStart");
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       ddte=formatter.format(c.getTimeInMillis());


        // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar cal=Calendar.getInstance();
            cal.set(Calendar.YEAR,year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH,day);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ddte=formatter.format(cal.getTimeInMillis());
            dateGet.dateSet(ddte,boli);
           // dateGet.dateSet(year,month,day);
        }

}
