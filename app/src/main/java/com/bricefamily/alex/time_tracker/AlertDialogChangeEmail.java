package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by bricenangue on 04/03/16.
 */
public class AlertDialogChangeEmail extends DialogFragment {


    public interface OnEmailChanged{
       void emailchange(String newEmail);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnEmailChanged)) {
            throw new ClassCastException(activity.toString() + " must implement OnEmailChanged");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog dialoglog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.layout_change_email, null);

        dialoglog.setView(vw);
        final EditText editTextemail=(EditText)vw.findViewById(R.id.editTextenterNewemail);
        Button btnOKemail=(Button)vw.findViewById(R.id.buttonchangeemailOk);
        Button btnCancleemail=(Button)vw.findViewById(R.id.buttonchangeemailCancel);

        btnCancleemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglog.dismiss();
            }
        });
        btnOKemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editTextemail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    editTextemail.setError("this field cannot be empty");
                }else if(!TextUtils.isEmpty(email)){
                    ((OnEmailChanged) getActivity()).emailchange(email);
                    dialoglog.dismiss();

                }

            }
        });

        dialoglog.setCancelable(false);


        return dialoglog;
    }

}
