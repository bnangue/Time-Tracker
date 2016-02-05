package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by bricenangue on 04/02/16.
 */
public class DialogFragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface OnDateGet{
        public void dateSet(int year, int month, int day);
    }

   private OnDateGet dateGet;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dateGet=(OnDateGet)getTargetFragment();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            dateGet.dateSet(year,month,day);
        }

}
