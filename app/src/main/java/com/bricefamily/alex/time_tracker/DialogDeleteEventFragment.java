package com.bricefamily.alex.time_tracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by bricenangue on 15/02/16.
 */
public class DialogDeleteEventFragment extends DialogFragment {


    public interface OnDeleteEventListener {
        void delete(int position);
    }

    OnDeleteEventListener onDeleteEventListener;
    public static DialogDeleteEventFragment newInstance(int position) {
        DialogDeleteEventFragment frag = new DialogDeleteEventFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            onDeleteEventListener=(OnDeleteEventListener)getActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onDeleteEventListener = (OnDeleteEventListener) activity;

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int position =getArguments().getInt("position");
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.dialog_warning_delete_event, null);

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
                onDeleteEventListener.delete(position);
                alertDialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

        return alertDialog;
    }
}
