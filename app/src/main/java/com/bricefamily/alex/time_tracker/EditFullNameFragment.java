package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by bricenangue on 04/03/16.
 */
public class EditFullNameFragment extends DialogFragment {

    private UserLocalStore userLocalStore;
    public interface OnFullNameChanged{
        void fulnamechanged(String firstname, String lastname);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnFullNameChanged)) {
            throw new ClassCastException(activity.toString() + " must implement OnPasswordChanged");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLocalStore=new UserLocalStore(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog dialoglog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.layout_save_full_name, null);

        dialoglog.setView(vw);
        final EditText editTextfirstname=(EditText)vw.findViewById(R.id.editTextfirstname);
        final EditText editTextlastname=(EditText)vw.findViewById(R.id.editTextlastname);
        ImageView picture=(ImageView)vw.findViewById(R.id.avatar);
        Bitmap bitmap=userLocalStore.loadImageFromStorage(userLocalStore.getUserPicturePath());
        if(bitmap!=null){
            picture.setImageBitmap(bitmap);
        }


        Button btnOKemail=(Button)vw.findViewById(R.id.buttonchangefirstlastname);
        Button btnCancleemail=(Button)vw.findViewById(R.id.buttonchangeCancelfirstlastname);

        btnCancleemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglog.dismiss();
            }
        });
        btnOKemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname= editTextfirstname.getText().toString();
                String lastname= editTextlastname.getText().toString();


                if(TextUtils.isEmpty(editTextfirstname.getText().toString())){
                    editTextfirstname.setError("this field cannot be empty");
                }else if(TextUtils.isEmpty(editTextlastname.getText().toString())){
                    editTextlastname.setError("this field cannot be empty");

                }else if(!TextUtils.isEmpty(editTextfirstname.getText().toString()) && !TextUtils.isEmpty(editTextlastname.getText().toString())){

                    ((OnFullNameChanged) getActivity()).fulnamechanged(firstname,lastname);
                    dialoglog.dismiss();

                }


            }
        });

        dialoglog.setCancelable(false);


        return dialoglog;
    }
}
