package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by bricenangue on 16/02/16.
 */
public class DialogRequestAddFriendFragment  extends DialogFragment {


    public interface OnRequestconfirm {
        void onAdd(int position);
        void onRemove(int position);
    }

    public static DialogRequestAddFriendFragment newInstance(int position,boolean remove) {

        DialogRequestAddFriendFragment frag = new DialogRequestAddFriendFragment();
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putBoolean("remove", remove);

        frag.setArguments(args);
        return frag;
    }
    OnRequestconfirm dateGet;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dateGet=(OnRequestconfirm)getTargetFragment();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int position=getArguments().getInt("position");
        final boolean remove=getArguments().getBoolean("remove");
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.dialog_warning_logout, null);

        alertDialog.setView(vw);


         Button delete= (Button)vw.findViewById(R.id.buttonDeleteaccount);
        Button cancel= (Button)vw.findViewById(R.id.buttonCancelaccount);

        TextView titel= (TextView)vw.findViewById(R.id.textViewTitelinfo);
         TextView message= (TextView)vw.findViewById(R.id.textViewMessageinfo);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remove) {

                    dateGet.onRemove(position);
                } else {

                    dateGet.onAdd(position);
                }

                alertDialog.dismiss();
            }
        });
        if(remove){
            titel.setText("Cancel request");
            message.setText("Do you want to cancel this friend request");
            delete.setText("CONFIRM");
        }else {
            titel.setText("Confirm request");
            message.setText("Do you want to send this friend request");
            delete.setText("CONFIRM");
        }
        alertDialog.setCancelable(false);
        alertDialog.show();

        return alertDialog;
    }
}
