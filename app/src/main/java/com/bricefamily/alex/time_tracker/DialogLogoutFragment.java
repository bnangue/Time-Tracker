package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by bricenangue on 15/02/16.
 */
public class DialogLogoutFragment  extends DialogFragment {


    public interface YesNoListenerDeleteAccount {
        void onYes();

        void onNo();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof YesNoListenerDeleteAccount)) {
            throw new ClassCastException(activity.toString() + " must implement YesNoListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.dialog_warning_logout, null);

        alertDialog.setView(vw);


        Button delete= (Button)vw.findViewById(R.id.buttonDeleteaccount);
        Button cancel= (Button)vw.findViewById(R.id.buttonCancelaccount);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((YesNoListenerDeleteAccount)getActivity()).onYes();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

        return alertDialog;
    }
}
