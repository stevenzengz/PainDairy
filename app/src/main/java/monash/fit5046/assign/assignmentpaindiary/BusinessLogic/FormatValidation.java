package monash.fit5046.assign.assignmentpaindiary.BusinessLogic;

import android.os.AsyncTask;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Steven on 27/04/2016.
 */
public class FormatValidation {

    public FormatValidation() {

    }

    /*********************************** 1 - String Validation ************************************/
    /**
     * Check if any EditText is an empty string
     *
     * @param fields
     * @return if any EditText is empty, return false.
     */
    public static boolean validateEditText(EditText[] fields) {
        for (int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(currentField.getText().toString().trim().equals(""))
                return false;
        }

        return true;
    }

    /**
     * New version by using varargs.
     * 11 - Check if any EditText is an empty string.
     *
     * @param editTexts
     * @return
     */
    public static boolean isValidString(EditText... editTexts) {
        EditText editText;
        String inputValue;

        for (int i = 0; i < editTexts.length; i++){
            editText = editTexts[i];
            inputValue =  editText.getText().toString();
            if(inputValue.trim().equals("") || inputValue.startsWith(" ") || inputValue.endsWith(" "))
                return false;
        }

        return true;
    }

    /**************************************** 2 - Valid format ************************************/
    /**
     * 21 - Check if the date is valid
     *
     * @param dateString
     * @return
     */
    public static boolean isValidDate(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            df.parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 22 - Check if the string is all numeric
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
            return true;
        } catch(NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Check if email is valid
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_REGEX);
    }

    /**************************************** 3 - Unify format ************************************/
    /**
     * Return current date and time in specified format
     *
     * @return
     */
    public static String[] getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String[] formattedDate = {(dateFormat.format(currentDate) + "T00:00:00+10:00"), ("1970-01-01T" + timeFormat.format(currentDate) + "+10:00")};
        return formattedDate;
    }

    /**************************************** Error Notice ****************************************/
    /**
     * Error messages when fail in validation
     *
     * @param errorCode
     * @return
     */
    public static String errorNotice(int errorCode) {
        String errorMessage = "Error: ";

        switch(errorCode) {
            case 11: errorMessage += "Detect invalid space.";
                break;
            case 21: errorMessage += "Invalid date format.";
                break;
            case 22: errorMessage += "Numeric value should not be alphabetic.";
                break;
            case 31: errorMessage += "Invalid address.";
                break;
            default: errorMessage += "Unexpected invalid values.";
                break;
        }

        return errorMessage;
    }
}
