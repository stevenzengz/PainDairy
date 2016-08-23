/**
 * Activity - Registration to use application
 *
 * Relationship:
 *   Parent Activity - LoginActivity
 *   Next Activity   - MainMenuActicity (if register successes)
 *
 * Action:
 *   Click - Button - Submit Registration
 *
 * Logical Processes
 *   1 - check validation of input value (numeric, string, email, DOB, address)
 *   2 - check if username is duplicate
 *   3 - encrypt password
 *   4 - assemble to an Registration object
 *   5 - pass object to background function and to server
 *   6 - switch to MainMenuActicity and finish this acticity
 */
package monash.fit5046.assign.assignmentpaindiary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.DatabaseHepler;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.FormatValidation;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.entities.Doctor;
import monash.fit5046.assign.assignmentpaindiary.entities.Patient;
import monash.fit5046.assign.assignmentpaindiary.entities.Registration;

public class RegistActivity extends AppCompatActivity {

    private EditText ra_et_address;
    private EditText ra_et_dob;
    private EditText ra_et_firstname;
    private EditText ra_et_height;
    private EditText ra_et_lastname;
    private EditText ra_et_occupation;
    private EditText ra_et_weight;
    private Spinner ra_sp_gender;
    private Spinner ra_sp_clinic;
    private Spinner ra_sp_doctors;
    private EditText ra_et_username;
    private EditText ra_et_password;
    private Button ra_et_regist;
    private DatabaseHepler databaseHepler;
    private SQLiteDatabase db;

    private String gender;
    private List<Doctor> doctorRepository = new ArrayList<Doctor>();
    private Doctor selectedDoctor = new Doctor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        initWidgets();

        // SQLite database operation
        // 1- create an object of database helper
        // the rest database creation are work in "ra_et_regist.setOnClickListener"
        databaseHepler = new DatabaseHepler(this, "PainDairy.db", null, 2);

        // Actions
        // 1 - Spinner (gender, clinics, doctors)
        // Spinner of gender
        ra_sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "Male";
            }
        });

        // Spinner of clinics and doctors
        populateClinicSpinner();
        ra_sp_clinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateDoctorSpinner(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ra_sp_doctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctor = doctorRepository.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 2 - Button (submit registration)
        ra_et_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText[] editTexts = {ra_et_address, ra_et_dob, ra_et_firstname, ra_et_height,
                        ra_et_lastname, ra_et_occupation, ra_et_weight, ra_et_username, ra_et_password};
                if (FormatValidation.isValidString(editTexts)) {
                    // Registration
                    String username = ra_et_username.getText().toString();
                    String password = ra_et_password.getText().toString();
                    // Patient
                    String firstname = ra_et_firstname.getText().toString();
                    String lastname = ra_et_lastname.getText().toString();
                    String address = ra_et_address.getText().toString();
                    String dob = ra_et_dob.getText().toString() + "T00:00:00+10:00";
                    String height = ra_et_height.getText().toString();
                    String weight = ra_et_weight.getText().toString();
                    String occupation = ra_et_occupation.getText().toString();

                    if (FormatValidation.isValidDate(dob)) {
                        if (FormatValidation.isNumeric(height) && FormatValidation.isNumeric(weight)) {
                            // Format date
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
                            String registerDate = dateFormater.format(currentDate) + "T00:00:00+10:00";
                            String registerTime = "1970-01-01T" + timeFormater.format(currentDate) + "+10:00";
                            // Encrypt password
                            String encryptedPassword = RestClient.encryptInput(password);
                            // Add user to server
                            registerNewUser(address, dob, firstname, gender, height, lastname, occupation, weight,username, encryptedPassword, registerDate, registerTime);
                        } else {
                            Toast.makeText(RegistActivity.this, FormatValidation.errorNotice(22), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistActivity.this, FormatValidation.errorNotice(21), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistActivity.this, FormatValidation.errorNotice(11), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Remove floating mail icon
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /**
     * Populate clinic spinner from background server
     */
    private void populateClinicSpinner() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                return RestClient.readAllClinic();
            }

            @Override
            protected void onPostExecute(List<String> response) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistActivity.this, android.R.layout.simple_spinner_item, response);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ra_sp_clinic.setAdapter(arrayAdapter);
            }
        }.execute();
    }

    /**
     * Populate doctor spinner based on the chosen clinic
     *
     * @param clinicAddress
     */
    private void populateDoctorSpinner(String clinicAddress) {
        new AsyncTask<String, Void, List<Doctor>>() {
            @Override
            protected List<Doctor> doInBackground(String... params) {
                return RestClient.findDoctorsByClinic(params[0]);
            }

            @Override
            protected void onPostExecute(List<Doctor> response) {
                doctorRepository = response;
                String fullName = "";
                List<String> doctorsName = new ArrayList<String>();
                for (Doctor doctor : response) {
                    fullName = doctor.getFirstname() + " " + doctor.getLastname();
                    doctorsName.add(fullName);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistActivity.this, android.R.layout.simple_spinner_item, doctorsName);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ra_sp_doctors.setAdapter(arrayAdapter);
            }
        }.execute(clinicAddress);
    }

    /**
     * Register new user
     *
     * @param address
     * @param dob
     * @param firstname
     * @param gender
     * @param height
     * @param lastname
     * @param occupation
     * @param weight
     * @param username
     * @param encryptedPassword
     * @param registerDate
     * @param registerTime
     */
    private void registerNewUser(String address, String dob, String firstname, String gender, String height, String lastname,
                                 String occupation, String weight, String username, String encryptedPassword, String registerDate,
                                 String registerTime) {
        new AsyncTask<String, Void, Registration>() {
            @Override
            protected Registration doInBackground(String... params) {
                if (RestClient.findRegisterByUsername(params[8]) == null) {

                    List<String> addressGeoInfo = RestClient.getAddressGeoInfo(params[0]);
                    if (addressGeoInfo != null) {
                        // To Server
                        // Assemble new Patient and Registration objects
                        Patient newPatient = new Patient(params[0], params[1], params[2], params[3], Integer.parseInt(params[4]),
                                params[5], params[6], Integer.parseInt(params[7]));
                        String newPatientIdStr = RestClient.addPatient(newPatient, selectedDoctor);
                        Integer newPatientId = Integer.parseInt(newPatientIdStr);
                        Registration newRegister = new Registration(newPatientId, params[8], params[9], params[10], params[11]);
                        RestClient.addRegister(newRegister);

                        // To SQLite
                        // If Record table not exist, create table
                        db = databaseHepler.getWritableDatabase();
                        // Add partly information to Record table after a new user is registered
                        ContentValues userRecord = new ContentValues();
                        userRecord.put("latitude", addressGeoInfo.get(0));
                        userRecord.put("longitude", addressGeoInfo.get(1));
                        userRecord.put("userid", newPatientIdStr);
                        userRecord.put("address", params[0]);
                        userRecord.put("date", params[10]);
                        userRecord.put("time", params[11]);
                        db.insert("Record", null, userRecord);
                        // Add initial data to pain trigger
                        ContentValues painTriggers = new ContentValues();
                        painTriggers.put("userid", newPatientIdStr);
                        painTriggers.put("activity", "heavy lifting");
                        painTriggers.put("activity", "gardening");
                        painTriggers.put("activity", "walking");
                        painTriggers.put("activity", "others");
                        db.insert("Paintrigger", null, painTriggers);
                        return newRegister;
                    } else
                        return null;
                } else
                    return null;
            }

            @Override
            protected void onPostExecute(Registration responses) {
                if (responses == null) {
                    Toast.makeText(RegistActivity.this, "Registration Fail: Same username has existed./Address is not valid", Toast.LENGTH_SHORT).show();
                } else {
                    // Switch to MainMenuActivity after registration
                    Toast.makeText(RegistActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistActivity.this, MainMenuActivity.class);
                    intent.putExtra("login_user", responses);
                    startActivity(intent);
                    // Once user register successfully, finish RegistActivity so that user can not roll back.
                    finish();
                }
            }
        }.execute(address, dob, firstname, gender, height, lastname, occupation, weight,
                username, encryptedPassword, registerDate, registerTime);
    }

    /**
     * Initiate all widgets
     */
    private void initWidgets() {
        ra_et_address = (EditText) findViewById(R.id.ra_et_address);
        ra_et_dob = (EditText) findViewById(R.id.ra_et_dob);
        ra_et_firstname = (EditText) findViewById(R.id.ra_et_firstname);
        ra_et_height = (EditText) findViewById(R.id.ra_et_height);
        ra_et_lastname = (EditText) findViewById(R.id.ra_et_lastname);
        ra_et_occupation = (EditText) findViewById(R.id.ra_et_occupation);
        ra_et_weight = (EditText) findViewById(R.id.ra_et_weight);
        ra_sp_gender = (Spinner) findViewById(R.id.ra_sp_gender);
        ra_sp_clinic = (Spinner) findViewById(R.id.ra_sp_clinic);
        ra_sp_doctors = (Spinner) findViewById(R.id.ra_sp_doctors);
        ra_et_username = (EditText) findViewById(R.id.ra_et_username);
        ra_et_password = (EditText) findViewById(R.id.ra_et_password);
        ra_et_regist = (Button) findViewById(R.id.ra_et_regist);
    }

}
