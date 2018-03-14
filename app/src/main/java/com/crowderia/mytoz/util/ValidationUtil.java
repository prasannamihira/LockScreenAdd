package com.crowderia.mytoz.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Crowderia on 11/17/2016.
 */

public class ValidationUtil {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final String PHONE_NUMBER_GARBAGE_REGEX = "[()\\s-]+";
    private static final String PHONE_NUMBER_REGEX = "(([(]?(\\d{2,4})[)]?)|(\\d{2,4})|([+1-9]+\\d{1,2}))?[-\\s]?(\\d{2,3})?[-\\s]?((\\d{7,8})|(\\d{3,4}[-\\s]\\d{3,4}))$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    public static boolean validEmail(String emailStr) {
        /*Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();*/
        if (emailStr == null) {
            return false;
        } else {
            return VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr).matches();
//            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches();
        }
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() <= 16 && PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(pDateString);
        return new Date().after(date);
    }
}
