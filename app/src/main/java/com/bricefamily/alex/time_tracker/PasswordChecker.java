package com.bricefamily.alex.time_tracker;

import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by alex on 16.01.2016.
 */
public class PasswordChecker {

    public PasswordChecker(){

    }

    boolean checkIfValid (String password, String repeatPassword)
    {

        boolean inputValid = false;


        if (password.equals(repeatPassword))
        {
            if (checkIfPWIsSafe(password))
            {
                inputValid = true;
            }
        }



        return inputValid;
    }

    boolean checkIfPWIsSafe (String pwString)
    {
        boolean isSafe= false;
        if(isnotSequential(pwString) ){
            if(ifSame(pwString)){
                isSafe =true;
            }
        }
        return isSafe;

    }
    //check if character are range sequential
    boolean isnotSequential(String pw){
        boolean isnotSequence= false;
        Character[] vl;
        vl = toCharacterArray(pw);
//        Arrays.sort(vl);
        for(int i = 0; i<pw.length()-1; i++){
            if(vl[i]+1 != vl[i+1] ){

                isnotSequence = true;
            }
        }


        return isnotSequence;
    }

    // check if all character are same in password
    boolean ifSame(String pw){
        boolean issame = false;
        Character [] vl = toCharacterArray(pw);
        for(int i = 1; i < vl.length; i++) {
            if(vl[i-1]!=vl[i]) {
                issame=true;
            }
        }
        return issame;
    }

    public Character[] toCharacterArray( String s ) {

        if ( s == null ) {
            return null;
        }

        int len = s.length();
        Character[] array = new Character[len];
        for (int i = 0; i < len ; i++) {
            array[i] = new Character(s.charAt(i));
        }

        return array;
    }
}
