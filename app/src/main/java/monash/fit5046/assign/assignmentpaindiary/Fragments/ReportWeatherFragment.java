package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.FormatValidation;
import monash.fit5046.assign.assignmentpaindiary.MainMenuActivity;
import monash.fit5046.assign.assignmentpaindiary.R;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.entities.PainLevelResponse;

/**
 * Created by Steven on 23/04/2016.
 */
public class ReportWeatherFragment extends Fragment {
    View reportWeatherView;
    private MainMenuActivity mainMenuActivity;

    private EditText frw_et_startdate;
    private EditText frw_et_enddate;
    private Spinner frw_sp_weatherVar;
    private Button frw_btn_submit;
    private CombinedChart frw_combinedChart;
    private TextView tv_correlationValue;
    private TextView tv_significantValue;
    private static String loginUserId = "";
    private static String weatherVariable = "";

    private static FormatValidation formatValidation = new FormatValidation();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reportWeatherView = inflater.inflate(R.layout.fragment_report_weather, container, false);
        mainMenuActivity = (MainMenuActivity) getActivity();
        loginUserId = mainMenuActivity.loginUser.getPatientid().toString();
        initiateWidgets();

        frw_sp_weatherVar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weatherVariable = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        frw_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formatValidation.validateEditText(new EditText[]{frw_et_startdate, frw_et_enddate})) {
                    String startdate = frw_et_startdate.getText().toString();
                    String enddate = frw_et_enddate.getText().toString();

                    if (formatValidation.isValidDate(startdate) && formatValidation.isValidDate(enddate)) {
                        new AsyncTask<String, Void, List<PainLevelResponse>>() {
                            @Override
                            protected List<PainLevelResponse> doInBackground(String... params) {
                                return RestClient.reportPainLevel(params[0], params[1], params[2], params[3]);
                            }

                            @Override
                            protected void onPostExecute(List<PainLevelResponse> responses) {
                                buildCombinedChart(responses);
                                frw_combinedChart.setVisibility(View.VISIBLE);
                            }
                        }.execute(loginUserId, startdate, enddate, weatherVariable);

                        new AsyncTask<String, Void, String[]>() {
                            @Override
                            protected String[] doInBackground(String... params) {
                                return RestClient.reportCorrelation(params[0], params[1], params[2]);
                            }

                            @Override
                            protected void onPostExecute(String[] responses) {
                                tv_correlationValue.setText(responses[0]);
                                tv_significantValue.setText(responses[1]);
                            }
                        }.execute(startdate, enddate, weatherVariable);
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

        return reportWeatherView;
    }

    private void buildCombinedChart(List<PainLevelResponse> responses) {
        // draw order
        frw_combinedChart.setDrawOrder(new CombinedChart.DrawOrder[] {
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE
        });

        YAxis rightAxis = frw_combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = frw_combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = frw_combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        ArrayList<String> labels = new ArrayList<String>();
        for (PainLevelResponse response : responses) {
            labels.add(response.recorddate + response.recordtime);
        }

        CombinedData data = new CombinedData(labels);
        data.setData(generatePainLevelData(responses));
        data.setData(generateWeatherData(responses));
        frw_combinedChart.setData(data);
        frw_combinedChart.invalidate();
    }

    private LineData generatePainLevelData(List<PainLevelResponse> responses) {
        int index = 0;

        // 1- Set data
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (PainLevelResponse response : responses) {
            entries.add(new Entry(Float.parseFloat(response.painlevel), index));
            index++;
        }

        LineDataSet set = new LineDataSet(entries, "");
        LineData data = new LineData();
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        data.addDataSet(set);
        return data;
    }

    private LineData generateWeatherData(List<PainLevelResponse> responses) {
        int index = 0;

        // 1- Set data
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (PainLevelResponse response : responses) {
            switch (weatherVariable) {
                case "temperature":
                    entries.add(new Entry(Float.parseFloat(response.temperature), index));
                    break;
                case "humidity":
                    entries.add(new Entry(Float.parseFloat(response.humidity), index));
                    break;
                case "windspeed":
                    entries.add(new Entry(Float.parseFloat(response.windspeed), index));
                    break;
                case "atmosphericpressure":
                    entries.add(new Entry(Float.parseFloat(response.atmosphericpressure), index));
                    break;
                default:
                    break;
            }
            index++;
        }

        LineDataSet set = new LineDataSet(entries, "");
        LineData data = new LineData();
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        data.addDataSet(set);
        return data;
    }

    private void initiateWidgets() {
        frw_et_startdate = (EditText) reportWeatherView.findViewById(R.id.frw_et_startdate);
        frw_et_enddate = (EditText) reportWeatherView.findViewById(R.id.frw_et_enddate);
        frw_sp_weatherVar = (Spinner) reportWeatherView.findViewById(R.id.frw_sp_weatherVar);
        frw_btn_submit = (Button) reportWeatherView.findViewById(R.id.frw_btn_submit);
        frw_combinedChart = (CombinedChart) reportWeatherView.findViewById(R.id.frw_combinedChart);
        frw_combinedChart.setVisibility(View.GONE);
        tv_correlationValue = (TextView) reportWeatherView.findViewById(R.id.frw_tv_correlationValue);
        tv_significantValue = (TextView) reportWeatherView.findViewById(R.id.frw_tv_significantValue);
    }
}
