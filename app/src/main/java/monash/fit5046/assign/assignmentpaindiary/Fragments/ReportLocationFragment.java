package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.FormatValidation;
import monash.fit5046.assign.assignmentpaindiary.MainMenuActivity;
import monash.fit5046.assign.assignmentpaindiary.R;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.entities.PainLocationResponse;

/**
 * Created by Steven on 23/04/2016.
 */
public class ReportLocationFragment extends Fragment {
    View reportLocationView;
    private MainMenuActivity mainMenuActivity;

    private EditText frl_et_startdate;
    private EditText frl_et_enddate;
    private Button frl_btn_submit;
    private PieChart pieChart;
    private static String loginUserId = "";

    private static FormatValidation formatValidation = new FormatValidation();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reportLocationView = inflater.inflate(R.layout.fragment_report_location, container, false);
        mainMenuActivity = (MainMenuActivity) getActivity();
        loginUserId = mainMenuActivity.loginUser.getPatientid().toString();
        initiateWidgets();

        frl_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formatValidation.validateEditText(new EditText[] {frl_et_startdate, frl_et_enddate})) {
                    String startdate = frl_et_startdate.getText().toString();
                    String enddate = frl_et_enddate.getText().toString();

                    if (formatValidation.isValidDate(startdate) && formatValidation.isValidDate(enddate)) {
                        new AsyncTask<String, Void, List<PainLocationResponse>>() {
                            @Override
                            protected List<PainLocationResponse> doInBackground(String... params) {
                                return RestClient.reportPainLocation(params[0], params[1], params[2]);
                            }

                            @Override
                            protected void onPostExecute(List<PainLocationResponse> responses) {
                                buildPirChart(responses);
                                pieChart.setVisibility(View.VISIBLE);
                            }
                        }.execute(loginUserId, startdate, enddate);
                    } else {
                        Toast.makeText(mainMenuActivity, "Error: Date in wrong format.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainMenuActivity, "Error: All input should be formed.", Toast.LENGTH_SHORT).show();
                }

                // Hide the soft keyboard
                InputMethodManager inputManager = (InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mainMenuActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        return reportLocationView;
    }

    private void buildPirChart(List<PainLocationResponse> responses) {
        int index = 0;

        // 1- Set data
        ArrayList<Entry> entries = new ArrayList<>();
        for (PainLocationResponse response : responses) {
            entries.add(new Entry(Float.parseFloat(response.frequency), index));
            index++;
        }
        PieDataSet dataset = new PieDataSet(entries, "");

         // 2- Set Label
        ArrayList<String> labels = new ArrayList<String>();

        for (PainLocationResponse response : responses) {
            labels.add(response.painlocation);
        }

        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data); //set data into chart
        pieChart.setDescription("Description");  // set the description
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); // set the colorUsr*/
    }

    private void initiateWidgets() {
        frl_et_startdate = (EditText) reportLocationView.findViewById(R.id.frl_et_startdate);
        frl_et_enddate = (EditText) reportLocationView.findViewById(R.id.frl_et_enddate);
        //frl_sp_wvar = (Spinner) reportLocationView.findViewById(R.id.frl_sp_wvar);
        frl_btn_submit = (Button) reportLocationView.findViewById(R.id.frl_btn_submit);
        pieChart = (PieChart) reportLocationView.findViewById(R.id.frl_pieChart);
        pieChart.setVisibility(View.GONE);
    }
}
