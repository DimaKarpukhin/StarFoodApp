package com.studymobile.moonlight.Services;

import android.util.Patterns;

import java.util.regex.Pattern;

public class InputValidation
{
    private static final int INPUT_PARAM = 2;
    private static final int ERROR_PARAM = 0;
    private static final int TOAST_PARAM = 1;
//    private static final int MAX_PARAMS = 3;


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +  "$");        //at least 6 characters



    public static boolean IsValidEmail(String[] i_Params)
    {
        if (i_Params[INPUT_PARAM].isEmpty())
        {
            i_Params[ERROR_PARAM] = "Field can't be empty";
            i_Params[TOAST_PARAM] = "Please enter an email";
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(i_Params[INPUT_PARAM]).matches())
        {
            i_Params[ERROR_PARAM] = "Invalid input";
            i_Params[TOAST_PARAM] = "Please enter a valid email address";
            return false;
        }
        else {
            i_Params[ERROR_PARAM] = null;
            i_Params[TOAST_PARAM] = null;
            return true;
        }
    }

    public static boolean IsValidPassword(String[] i_Params)
    {
        if (i_Params[INPUT_PARAM].isEmpty()) {
            i_Params[ERROR_PARAM] = "Field can't be empty";
            i_Params[TOAST_PARAM] = "Please enter a password";
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(i_Params[INPUT_PARAM]).matches())
        {
            i_Params[ERROR_PARAM] = "Password too weak";
            i_Params[TOAST_PARAM] =  "Password must contain at list 6 characters without white spaces";
            return false;
        } else {
            i_Params[ERROR_PARAM] = null;
            i_Params[TOAST_PARAM] = null;
            return true;
        }
    }
}
