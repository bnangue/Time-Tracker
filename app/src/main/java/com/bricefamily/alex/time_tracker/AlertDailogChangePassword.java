package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

/**
 * Created by bricenangue on 04/03/16.
 */
public class AlertDailogChangePassword extends DialogFragment {

    private UserLocalStore userLocalStore;
    public interface OnPasswordChanged{
        void passwordchange(String newPassword);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnPasswordChanged)) {
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
        View vw = inflater.inflate(R.layout.layout_change_password, null);

        dialoglog.setView(vw);
        final EditText editTextoldPw=(EditText)vw.findViewById(R.id.editTextoldPassword);
        final EditText editTextnewPW=(EditText)vw.findViewById(R.id.editTextnewPaword);
        final EditText editTextConPW=(EditText)vw.findViewById(R.id.editTextconfirmNewpassowrd);
        Button btnOKemail=(Button)vw.findViewById(R.id.buttonchangepwOk);
        Button btnCancleemail=(Button)vw.findViewById(R.id.buttonchangepwCancel);

        btnCancleemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglog.dismiss();
            }
        });
        btnOKemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpassword= String.valueOf(editTextoldPw.getText().toString().hashCode());
                String newpassword= String.valueOf(editTextnewPW.getText().toString().hashCode());
                String conpassword= String.valueOf(editTextConPW.getText().toString().hashCode());


                if(TextUtils.isEmpty(editTextoldPw.getText().toString())){
                    editTextoldPw.setError("this field cannot be empty");
                }else if(TextUtils.isEmpty(editTextnewPW.getText().toString())){
                    editTextnewPW.setError("this field cannot be empty");

                }else if(TextUtils.isEmpty(editTextConPW.getText().toString())){
                    editTextConPW.setError("this field cannot be empty");
                }else if(!TextUtils.isEmpty(editTextoldPw.getText().toString()) && !TextUtils.isEmpty(editTextnewPW.getText().toString()) && !TextUtils.isEmpty(editTextConPW.getText().toString())){

                    if(userLocalStore.getLoggedInUser().password.equals(oldpassword)){
                        if(newpassword.equals(conpassword)){
                            ((OnPasswordChanged) getActivity()).passwordchange(newpassword);
                            dialoglog.dismiss();
                        }else {
                            editTextConPW.setError("Password does not match");
                        }
                    }else {
                        editTextoldPw.setError("Wrong password");
                    }
                }


            }
        });

        dialoglog.setCancelable(false);


        return dialoglog;
    }
}
