/**
 * Fragment - Record Modify
 *
 * Relationship:
 *   Parent Activity - MainMenuActicity
 *   Next Activity   -
 *
 * Prerequisite:
 *   1 - user login
 *   2 - record list has been retrieved
 *
 * Action:
 *   Click - Button - Retrieve - retrieve a record by date
 *   Click - Button - Delete - delete the selected record
 *   Click - Button - Modify - display the modify mode
 *
 * Logical Processes
 *   1 - retrieve a record by date
 *   2 - display this record.
 *   3 - delete a record id user clicks delete button
 *   4 - if user chooses to modify this record, a record form will display for changing. save changes by submitting
 *   5 - display again this record
 */
package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
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
public class RecordModifyFragment extends Fragment {
    private static View recordModifyView;
    private static MainMenuActivity mainMenuActivity;

    // SQLite database storing the list of pain trigger
    private static DatabaseHepler dbHepler;
    private static SQLiteDatabase db;
    private List<String> userPainTriggers = new ArrayList<String>();

    private Spinner frm_sp_recordDate;
    private String selectRecordDate = "";
    private Button frm_btn_submit;

    private Record selectedRecord;

    private LinearLayout frm_ll_displayRecord;
    private TextView frm_tv_painlevel;
    private TextView frm_tv_painlocation;
    private TextView frm_tv_paintrigger;
    private TextView frm_tv_moodlevel;
    private Button frm_btn_premodify;
    private Button frm_btn_delete;

    private LinearLayout frm_ll_modifyrecord;
    private TextView frm_tv_mpainlevel;
    private SeekBar frm_sb_mpainlevel;
    private Spinner frm_sp_mplocation;
    private Spinner frm_sp_mtrigger;
    private LinearLayout frm_ll_maddtrigger;
    private EditText frm_et_mtrigger;
    private Button frm_btn_maddtrigger;
    private TextView frm_tv_mmood;
    private SeekBar frm_sb_mmood;
    private Button frm_btn_msubmit;

    // Record attributes being assemble
    private String painLevel = "0";
    private String painLocation = "";
    private String painTrigger = "";
    private String moodValue = "Very Low";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recordModifyView = inflater.inflate(R.layout.fragment_record_modify, container, false);
        mainMenuActivity = (MainMenuActivity) getActivity();
        dbHepler = new DatabaseHepler(recordModifyView.getContext(), "PainDairy.db", null, 2);
        db = dbHepler.getWritableDatabase();
        initWidgets();

        // 1 - retrieve a record by date
        populateRecordSpinner(mainMenuActivity.loginUserId);
        // 2 - display selected record
        // 2.1 - get selected date
        frm_sp_recordDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectRecordDate = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 2.2 - find record by selected date
        frm_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readRecord(selectRecordDate);
            }
        });
        // 3 - delete record
        frm_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord(selectedRecord);
            }
        });
        // 4 - modify mode
        // 4.1 - display modify form
        frm_btn_premodify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hides record display and shows the modify panel
                frm_ll_displayRecord.setVisibility(View.GONE);
                frm_ll_modifyrecord.setVisibility(View.VISIBLE);
                initModifyPanel(selectedRecord);
            }
        });

        // 5 - Modify - gain record information
        // 5.1- pain level
        frm_sb_mpainlevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frm_tv_mpainlevel.setText("Step 1 - Pain Level: " + Integer.toString(progress) + "/" + seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                painLevel = Integer.toString(seekBar.getProgress());
            }
        });
        // 5.2- pain location
        frm_sp_mplocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                painLocation = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // 5.3- pain trigger
        frm_sp_mtrigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Others")) {
                    frm_ll_maddtrigger.setVisibility(View.VISIBLE);
                } else {
                    frm_ll_maddtrigger.setVisibility(View.GONE);
                    painTrigger = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        frm_btn_maddtrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FormatValidation.isValidString(frm_et_mtrigger)) {
                    String newTriggerStr = frm_et_mtrigger.getText().toString();
                    RecordCreateFragment.addTrigger(newTriggerStr, mainMenuActivity.loginUserId, db);
                    userPainTriggers.add(newTriggerStr);
                    WidgetsHepler.populateSpinner(recordModifyView.getContext(), userPainTriggers, frm_sp_mtrigger);
                    RecordCreateFragment.setTrigger(frm_sp_mtrigger, newTriggerStr);
                }
            }
        });
        // 5.4- mood level
        frm_sb_mmood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frm_tv_mmood.setText("Step 4 - Mood Level: " + RecordCreateFragment.getMood(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                moodValue = RecordCreateFragment.getMood(seekBar.getProgress());
            }
        });

        // Hand in update
        frm_btn_msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord(painLevel, painLocation, painTrigger, moodValue);
                frm_ll_modifyrecord.setVisibility(View.GONE);
            }
        });

        return recordModifyView;
    }

    /**
     * Populates the spinner with user's records
     *
     * @param userId
     */
    private void populateRecordSpinner(String userId) {
        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(String... params) {
                return RestClient.readRecordDateList(params[0]);
            }

            @Override
            protected void onPostExecute(List<String> responses) {
                WidgetsHepler.populateSpinner(recordModifyView.getContext(), responses, frm_sp_recordDate);
            }
        }.execute(userId);
    }

    /**
     * Read the record in specific date
     *
     * @param selectedDate
     */
    private void readRecord(String selectedDate) {
        String date = selectedDate.substring(0, 10);
        String time = selectedDate.substring(14, 22);

        new AsyncTask<String, Void, Record>() {
            @Override
            protected Record doInBackground(String... params) {
                return RestClient.findRecordByDate(params[0], params[1], params[2]);
            }

            @Override
            protected void onPostExecute(Record responses) {
                displayRecord(responses);
                selectedRecord = responses;
            }
        }.execute(mainMenuActivity.loginUserId, date, time);
    }

    /**
     * Display a record
     *
     * @param record
     */
    private void displayRecord(Record record) {
        frm_ll_displayRecord.setVisibility(View.VISIBLE);
        frm_tv_painlevel.setText("Pain Level:" + record.getPainlevel().toString());
        frm_tv_painlocation.setText("Pain Location: " + record.getPainlocation().toString());
        frm_tv_paintrigger.setText("Pain Trigger: " + record.getPaintrigger().toString());
        frm_tv_moodlevel.setText("Mood Level: " + record.getMoodlevel().toString());
    }

    /**
     * Delete a record
     *
     * @param record
     */
    private void deleteRecord(Record record) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                RestClient.deleteRecord(params[0]);
                populateRecordSpinner(params[1]);
                return null;
            }

            @Override
            protected void onPostExecute(Void responses) {
                frm_ll_displayRecord.setVisibility(View.GONE);
                frm_tv_painlevel.setText("Pain Level: ");
                frm_tv_painlocation.setText("Pain Location: ");
                frm_tv_paintrigger.setText("Pain Trigger: ");
                frm_tv_moodlevel.setText("Mood Level: ");
                Toast.makeText(mainMenuActivity, "Delete Successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(record.getRecordid().toString(), mainMenuActivity.loginUserId);
    }

    /**
     *
     * @param record
     */
    private void initModifyPanel(Record record) {
        painLevel = record.getPainlevel().toString();
        painLocation = record.getPainlocation();
        painTrigger = record.getPaintrigger();
        moodValue = record.getMoodlevel();
        // set this specified record value to four widgets
        // 1 - pain level
        frm_sb_mpainlevel.setProgress(record.getPainlevel());
        // 2 - pain location
        ArrayAdapter sp_mplocation_adapter = (ArrayAdapter) frm_sp_mplocation.getAdapter();
        int locationSp = sp_mplocation_adapter.getPosition(painLocation);
        frm_sp_mplocation.setSelection(locationSp);
        // 3 - pain trigger
        userPainTriggers = RecordCreateFragment.getTriggersByUser(getActivity().getApplicationContext(), mainMenuActivity.loginUserId, db);
        WidgetsHepler.populateSpinner(recordModifyView.getContext(), userPainTriggers, frm_sp_mtrigger);
        RecordCreateFragment.setTrigger(frm_sp_mtrigger, painTrigger);
        // 4 - mood level
        frm_sb_mmood.setProgress(RecordCreateFragment.setMood(moodValue));
    }

    /**
     * Update a record
     *
     * @param painLevel
     * @param painLocation
     * @param painTrigger
     * @param moodValue
     */
    private void updateRecord(String painLevel, String painLocation, String painTrigger, String moodValue) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                selectedRecord.setPainlevel(Integer.parseInt(params[0]));
                selectedRecord.setPainlocation(params[1]);
                selectedRecord.setPaintrigger(params[2]);
                selectedRecord.setMoodlevel(params[3]);
                RestClient.updateRecord(selectedRecord);
                return null;
            }

            @Override
            protected void onPostExecute(Void responses) {
                Toast.makeText(mainMenuActivity, "Update Sucssfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(painLevel, painLocation, painTrigger, moodValue);
    }

    /**
     * Initiate all widgets
     */
    private void initWidgets() {
        frm_sp_recordDate = (Spinner) recordModifyView.findViewById(R.id.frm_sp_recordDate);
        frm_btn_submit = (Button) recordModifyView.findViewById(R.id.frm_btn_submit);

        frm_ll_displayRecord = (LinearLayout) recordModifyView.findViewById(R.id.frm_ll_displayRecord);
        frm_ll_displayRecord.setVisibility(View.GONE);
        frm_tv_painlevel = (TextView) recordModifyView.findViewById(R.id.frm_tv_painlevel);
        frm_tv_painlocation = (TextView) recordModifyView.findViewById(R.id.frm_tv_painlocation);
        frm_tv_paintrigger = (TextView) recordModifyView.findViewById(R.id.frm_tv_paintrigger);
        frm_tv_moodlevel = (TextView) recordModifyView.findViewById(R.id.frm_tv_moodlevel);
        frm_btn_premodify = (Button) recordModifyView.findViewById(R.id.frm_btn_premodify);
        frm_btn_delete = (Button) recordModifyView.findViewById(R.id.frm_btn_delete);

        frm_ll_modifyrecord = (LinearLayout) recordModifyView.findViewById(R.id.frm_ll_modifyrecord);
        frm_ll_modifyrecord.setVisibility(View.GONE);
        frm_tv_mpainlevel = (TextView) recordModifyView.findViewById(R.id.frm_tv_mpainlevel);
        frm_sb_mpainlevel = (SeekBar) recordModifyView.findViewById(R.id.frm_sb_mpainlevel);
        frm_sb_mpainlevel.setProgress(2);
        frm_sp_mplocation = (Spinner) recordModifyView.findViewById(R.id.frm_sp_mplocation);
        frm_sp_mtrigger = (Spinner) recordModifyView.findViewById(R.id.frm_sp_mtrigger);
        frm_ll_maddtrigger = (LinearLayout) recordModifyView.findViewById(R.id.frm_ll_maddtrigger);
        frm_et_mtrigger = (EditText) recordModifyView.findViewById(R.id.frm_et_mtrigger);
        frm_btn_maddtrigger = (Button) recordModifyView.findViewById(R.id.frm_btn_maddtrigger);
        frm_tv_mmood = (TextView) recordModifyView.findViewById(R.id.frm_tv_mmood);
        frm_sb_mmood = (SeekBar) recordModifyView.findViewById(R.id.frm_sb_mmood);
        frm_btn_msubmit = (Button) recordModifyView.findViewById(R.id.frm_btn_msubmit);
    }
}
