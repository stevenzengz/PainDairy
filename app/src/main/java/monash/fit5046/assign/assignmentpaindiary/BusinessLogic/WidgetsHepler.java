package monash.fit5046.assign.assignmentpaindiary.BusinessLogic;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Collections;
import java.util.List;

/**
 * Created by Steven on 5/07/2016.
 */
public class WidgetsHepler {

    public static void populateSpinner(Context context, List<String> values, Spinner spinner) {
        Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }
}
