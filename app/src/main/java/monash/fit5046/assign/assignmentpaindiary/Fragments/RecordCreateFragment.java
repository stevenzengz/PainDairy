package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.DatabaseHepler;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.FormatValidation;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.WidgetsHepler;
import monash.fit5046.assign.assignmentpaindiary.MainMenuActivity;
import monash.fit5046.assign.assignmentpaindiary.R;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.entities.Record;

/**
 * Created by Steven on 23/04/2016.
 */
public class RecordCreateFragment extends Fragment {
    private static View recordCreateView;
    private static MainMenuActivity mainMenuActivity;

    private TextView tv_painlevel;
    private SeekBar sb_painlevel;
    private Spinner frc_sp_plocation;
    private LinearLayout frc_ll_addtrigger;
    private Spinner frc_sp_trigger;
    private EditText frc_et_trigger;
    private Button frc_btn_addtrigger;
    private TextView tv_mood;
    private SeekBar sb_mood;
    private Button frc_btn_submit;

    // SQLite database storing the list of pain trigger
    private static DatabaseHepler dbHepler;
    private static SQLiteDatabase db;
    private List<String> userPainTriggers = new ArrayList<String>();

    // Record attributes being assemble
    private String painLevel = "0";
    private String painLocation = "";
    private String painTrigger = "";
    private String moodValue = "Very Low";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recordCreateView = inflater.inflate(R.layout.fragment_record_create, container, false);
        mainMenuActivity = (MainMenuActivity) getActivity();
        dbHepler = new DatabaseHepler(recordCreateView.getContext(), "PainDairy.db", null, 2);
        db = dbHepler.getWritableDatabase();
        initWidgets();

        // Gain Record information
        // 1- Pain Level
        sb_painlevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                tv_painlevel.setText("Step 1 - Pain Level: " + Integer.toString(progressValue) + "/" + seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_painlevel.setText("Step 1 - Pain Level: " + Integer.toString(progressValue) + "/" + seekBar.getMax());
                painLevel = Integer.toString(progressValue);
            }
        });

        // 2- Pain Location
        frc_sp_plocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                painLocation = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 3- Pain Trigger
        // 3.1- get pain triggers of login user
        userPainTriggers = getTriggersByUser(getActivity().getApplicationContext(), mainMenuActivity.loginUserId, db);
        WidgetsHepler.populateSpinner(recordCreateView.getContext(), userPainTriggers, frc_sp_trigger);
        // 3.2- action listener: spinner selection
        frc_sp_trigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if the user select others, AddTrigger will appear for user to add new trigger
                if (parent.getItemAtPosition(position).toString().equals("Others")) {
                    frc_ll_addtrigger.setVisibility(View.VISIBLE);
                } else {
                    frc_ll_addtrigger.setVisibility(View.GONE);
                    painTrigger = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 3.2- action listener: add trigger
        frc_btn_addtrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FormatValidation.isValidString(frc_et_trigger)) {
                    // save the new trigger to database
                    String newTriggerStr = frc_et_trigger.getText().toString();
                    addTrigger(newTriggerStr, mainMenuActivity.loginUserId, db);
                    userPainTriggers.add(newTriggerStr);
                    // re-populate the pain trigger spinner
                    WidgetsHepler.populateSpinner(recordCreateView.getContext(), userPainTriggers, frc_sp_trigger);
                    // set the current location of spinner to the new pain trigger
                    setTrigger(frc_sp_trigger, newTriggerStr);
                } else {
                    Toast.makeText(mainMenuActivity, FormatValidation.errorNotice(11), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4- Mood
        sb_mood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_mood.setText("Step 4 - Mood Level: " + getMood(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                moodValue = getMood(seekBar.getProgress());
            }
        });

        // 5- Submit
        frc_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecord(painLevel, painLocation, painTrigger, moodValue);
            }
        });

        return recordCreateView;
    }

    /**
     * Get pain triggers by user id from SQLite
     * @param userId
     * @return
     */
    public static List<String> getTriggersByUser(Context context, String userId, SQLiteDatabase db) {
        List<String> painTriggers = new ArrayList<String>();
        String[] selectColumn = new String[] {"activity"};
        String whereClause = "userid = ?";
        String[] whereArgs = new String[] {userId};
        Cursor cursor = db.query("Paintrigger", selectColumn, whereClause, whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                painTriggers.add(cursor.getString(cursor.getColumnIndex("activity")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return painTriggers;
    }

    /**
     * Add a pain trigger to SQLite
     *
     * @param painTrigger
     */
    public static void addTrigger(String painTrigger, String userId, SQLiteDatabase db) {
        ContentValues newTrigger = new ContentValues();
        newTrigger.put("userid", userId);
        newTrigger.put("activity", painTrigger);
        db.insert("Paintrigger", null, newTrigger);
    }

    /**
     * Set the pain trigger to a spinner
     *
     * @param spinner
     * @param painTrigger
     */
    public static void setTrigger(Spinner spinner, String painTrigger) {
        ArrayAdapter triggerAdapter = (ArrayAdapter) spinner.getAdapter();
        int triggerPos = triggerAdapter.getPosition(painTrigger);
        spinner.setSelection(triggerPos);
    }

    /**
     * Get the pain mood from seek bar
     *
     * @param progress
     * @return
     */
    public static String getMood(int progress) {
        String moodValue = "";
        switch (progress) {
            case 0:
                moodValue = "Very Low";
                break;
            case 1:
                moodValue = "Low";
                break;
            case 2:
                moodValue = "Average";
                break;
            case 3:
                moodValue = "Good";
                break;
            case 4:
                moodValue = "Very Good";
                break;
        }
        return moodValue;
    }

    /**
     * Set the progress of the mood seek bar by mood value
     *
     * @param moodValue
     * @return
     */
    public static int setMood(String moodValue) {
        int progress = 0;
        switch (moodValue) {
            case "Very Low":
                progress = 0;
                break;
            case "Low":
                progress = 1;
                break;
            case "Average":
                progress = 2;
                break;
            case "Good":
                progress = 3;
                break;
            case "Very Good":
                progress = 4;
                break;
        }
        return progress;
    }

    /**
     * Create a pain record
     *
     * @param painLevel
     * @param painLocation
     * @param painTrigger
     * @param moodValue
     */
    public static void createRecord(String painLevel, String painLocation, String painTrigger, String moodValue) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                // 1- get the latitude and longitude from SQLite
                String latitude = "";
                String longitude = "";
                String[] selectColumn = new String[]{"latitude", "longitude"};
                String whereClause = "userid = ?";
                String[] whereArgs = new String[]{mainMenuActivity.loginUserId};
                Cursor cursor = db.query("Record", selectColumn, whereClause, whereArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                        longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                // 2- Call RestClient.getWeatherInfo to get the weather infomation
                List<String> weatherInfo = RestClient.getWeatherInfo(latitude, longitude);
                // 3- Assemble a Record entity
                String[] currentDate = FormatValidation.getCurrentDate();
                Record record = new Record(Double.parseDouble(weatherInfo.get(3)),
                        Integer.parseInt(weatherInfo.get(1)),
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        params[3],
                        Integer.parseInt(params[0]),
                        params[1],
                        params[2],
                        currentDate[0],
                        currentDate[1],
                        Double.parseDouble(weatherInfo.get(0)),
                        Double.parseDouble(weatherInfo.get(2)));
                RestClient.addRecord(record, mainMenuActivity.loginUser);
                return null;
            }

            @Override
            protected void onPostExecute(Void responses) {
                Toast.makeText(mainMenuActivity, "Record Create Successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(painLevel, painLocation, painTrigger, moodValue);
    }

    /**
     * Initiate all widgets
     */
    private void initWidgets() {
        tv_painlevel = (TextView) recordCreateView.findViewById(R.id.frc_tv_painlevel);
        sb_painlevel = (SeekBar) recordCreateView.findViewById(R.id.frc_sb_painlevel);
        tv_painlevel.setText("Step 1 - Pain Level: 0/10");
        frc_sp_plocation = (Spinner) recordCreateView.findViewById(R.id.frc_sp_plocation);
        frc_ll_addtrigger = (LinearLayout) recordCreateView.findViewById(R.id.frc_ll_addtrigger);
        frc_ll_addtrigger.setVisibility(View.GONE);
        frc_sp_trigger = (Spinner) recordCreateView.findViewById(R.id.frc_sp_trigger);
        frc_et_trigger = (EditText) recordCreateView.findViewById(R.id.frc_et_trigger);
        frc_btn_addtrigger = (Button) recordCreateView.findViewById(R.id.frc_btn_addtrigger);
        tv_mood = (TextView) recordCreateView.findViewById(R.id.frc_tv_mood);
        sb_mood = (SeekBar) recordCreateView.findViewById(R.id.frc_sb_mood);
        tv_mood.setText("Step 4 - Mood Level: Very Low");
        frc_btn_submit = (Button) recordCreateView.findViewById(R.id.frc_btn_submit);
    }

}
