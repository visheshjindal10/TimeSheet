package com.hartleylab.app.timesheet.utilities;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hartleylab.app.timesheet.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static <E> boolean isEmpty(final Collection<E> list) {
        return list == null || list.isEmpty();
    }

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateFirstName(String firstName) {
        return firstName.matches("[A-Z][a-zA-Z]*");
    }

    /**
     * Function to hide keyboard implicitly
     *
     * @param context Context
     * @param view    View
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * Function To create md5 encrypted string
     *
     * @param employeeID Employee ID
     * @return key
     */
    public static String getKey(String employeeID) {

        try {
            // Create MD5 Hash
            String s = employeeID + Constants.API_KEY;
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb);
            for (byte aMessageDigest : messageDigest){
                hexString.append(formatter.format("%02x", aMessageDigest));
                sb.setLength(0);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

}
