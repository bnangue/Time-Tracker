package com.bricefamily.alex.time_tracker;

import android.widget.EditText;

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
        boolean hasnumberNR1 = false;
        boolean hasnumberNR2 = false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasSpecial = false;
        boolean max2timesSame = true;
        if (pwString.length() >= 10)
        {
            for (char actChar: pwString.toCharArray())
            {
                if (Character.isDigit(actChar)) {
                    if (hasnumberNR1) {hasnumberNR2 = true;}

                    else {hasnumberNR1 = true;}}

                else if (Character.isUpperCase(actChar)) {hasUpper = true;}

                else if (Character.isLowerCase(actChar)) {hasLower = true;}

                else {if (!Character.isSpaceChar(actChar)) {hasSpecial = true;}}

                int counter = 0;
                for( int i=0; i<pwString.length(); i++ ) {
                    if( pwString.charAt(i) == actChar ) {
                        counter++;
                    }
                }

                if (counter > 3)
                {
                    max2timesSame = false;
                    break;
                }
            }
        }

        return (hasnumberNR1 && hasnumberNR2 && hasLower && hasUpper && hasSpecial && max2timesSame);

    }
}
