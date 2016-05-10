package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by bricenangue on 04/03/16.
 */
public class AlertDialogChangeNotSaved extends DialogFragment {
    public interface OnChangesCancel{
        void changescanceled(boolean canceled);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangesCancel)) {
            throw new ClassCastException(activity.toString() + " must implement OnChangesCancel");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialoglog = new AlertDialog.Builder(getContext()).create();

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.layout_error_warning, null);

        dialoglog.setView(vw);
        final TextView title=(TextView)vw.findViewById(R.id.titelwarningerror);
        final TextView message=(TextView)vw.findViewById(R.id.messagewarningerror);

        Button btnOK=(Button)vw.findViewById(R.id.buttonchangeerrorOk);
        Button btnCancl=(Button)vw.findViewById(R.id.buttonchangeerroCancel);
        title.setText("You have unsaved changes");
        message.setText("Exit?  Any changes will not be saved");

        btnCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnChangesCancel) getActivity()).changescanceled(false);
                dialoglog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnChangesCancel) getActivity()).changescanceled(true);
                dialoglog.dismiss();
            }
        });

        dialoglog.setCancelable(false);
        return dialoglog;
    }
}
