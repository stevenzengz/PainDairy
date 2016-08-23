package monash.fit5046.assign.assignmentpaindiary.BusinessLogic;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import monash.fit5046.assign.assignmentpaindiary.entities.Doctor;
import monash.fit5046.assign.assignmentpaindiary.entities.GoogleGeoCodeResponse;
import monash.fit5046.assign.assignmentpaindiary.entities.Hospital;
import monash.fit5046.assign.assignmentpaindiary.entities.OpenWeatherMaoResponse;
import monash.fit5046.assign.assignmentpaindiary.entities.PainLevelResponse;
import monash.fit5046.assign.assignmentpaindiary.entities.PainLocationResponse;
import monash.fit5046.assign.assignmentpaindiary.entities.Patient;
import monash.fit5046.assign.assignmentpaindiary.entities.Record;
import monash.fit5046.assign.assignmentpaindiary.entities.Registration;

/**
 * Processes of consuming RESTful WS.
 *
 * Created by Steven on 22/04/2016.
 */
public class RestClient {

    /**
     * URL Constants
     */
    // Root URI of each entity
    private static final String ROOT_URI_DOCTOR = "http://10.0.2.2:48123/fit5046AssignV5/webresources/fit5046.assign.entities.doctor";
    private static final String ROOT_URI_PATIENT = "http://10.0.2.2:48123/fit5046AssignV5/webresources/fit5046.assign.entities.patient";
    private static final String ROOT_URI_REGISTER = "http://10.0.2.2:48123/fit5046AssignV5/webresources/fit5046.assign.entities.registration";
    private static final String ROOT_URI_RECORD = "http://10.0.2.2:48123/fit5046AssignV5/webresources/fit5046.assign.entities.record";
    // Open Weather Map API
    private static final String ROOT_URI_OWM = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String ROOT_KEY_OWM = "&appid=a78c252a2f13e0c757b55b8e70d987e2";

    /********************************** Entity: Doctor **************************************/
    /**
     * Read all clinics from the server
     *
     * @return a list of all clinics
     */
    public static List<String> readAllClinic() {
        String methodPath = "/findAllClinics";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<String> clinicsList = new ArrayList<String>();

        try {
            url = new URL(ROOT_URI_DOCTOR + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            JSONArray responseList = new JSONArray(textResult);
            for (int i = 0; i < responseList.length(); i++) {
                JSONObject clinic = responseList.getJSONObject(i);
                clinicsList.add(clinic.getString("address"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return clinicsList;
    }

    /**
     * Get doctors from a specified clinic
     *
     * @param clinicAddress
     * @return all doctors from a specific clinic
     */
    public static List<Doctor> findDoctorsByClinic(String clinicAddress) {
        final String methodPath = "/findByClinicAddress/" + clinicAddress.replace(" ", "%20");
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<Doctor> doctors = new ArrayList<Doctor>();

        try {
            url = new URL(ROOT_URI_DOCTOR + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            doctors = gson.fromJson(textResult, new TypeToken<List<Doctor>>() {}.getType());
        }
         catch (Exception e) {
             e.printStackTrace();
         } finally {
            conn.disconnect();
        }

        return doctors;
    }

    /********************************** Entity: Patient *******************************************/
    /**
     * Get specified patient by id
     *
     * @param patientid
     * @return
     */
    public static Patient findPatientById (String patientid) {
        Patient patient = null;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            url = new URL(ROOT_URI_PATIENT + "/" + patientid);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            patient = gson.fromJson(textResult, Patient.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return patient;
    }

    /**
     * Add a new Patient
     *
     * @param patient
     * @param doctor
     * @return
     */
    public static String addPatient(Patient patient, Doctor doctor) {
        final String methodPath = "/createWithResponse";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        patient.setDoctorid(doctor);

        try {
            Gson gson = new Gson();
            String stringPatientJson = gson.toJson(patient);
            url = new URL(ROOT_URI_PATIENT + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringPatientJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            // send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringPatientJson);
            out.close();

            // Read the reponse
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return textResult;
    }

    /********************************** Entity: Registration **************************************/
    /**
     * Find a user by user and password when logging in
     *
     * @param username
     * @param password
     * @return
     */
    public static Registration findUser(String username, String password) {
        Registration register = null;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        String encyptedPassword = encryptInput(password);
        final String methodPath = "/findByRegisterDetail/" + username + "/" + encyptedPassword;

        try {
            // Set connection
            url = new URL(ROOT_URI_REGISTER + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            if (conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
                Gson gson = new Gson();
                register = gson.fromJson(textResult, Registration.class);
            } else {
                Log.i("HTTP Error Status", "No such user exist: " + conn.getErrorStream().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return register;
    }

    /**
     * Find specified registration by patient id
     *
     * @param patientid
     * @return
     */
    public static Registration findRegisterById (String patientid) {
        Registration register = null;
        final String methodPath = "/findByPatientId/" + patientid;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            url = new URL(ROOT_URI_REGISTER + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            register = gson.fromJson(textResult, Registration.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return register;
    }

    /**
     * Find user by username
     *
     * @param username
     * @return
     */
    public static Registration findRegisterByUsername (String username) {
        Registration register = null;
        final String methodPath = "/findByUserName/" + username;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            url = new URL(ROOT_URI_REGISTER + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            if (conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
                Gson gson = new Gson();
                register = gson.fromJson(textResult, Registration.class);
            } else {
                Log.i("HTTP Error Status", "No such user exist: " + conn.getErrorStream().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return register;
    }

    /**
     * Add a new registration.
     *
     * @param register
     * @return
     */
    public static String addRegister(Registration register) {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            Gson gson = new Gson();
            // Convert entity to string json
            String stringRegistrationJson = gson.toJson(register);
            url = new URL(ROOT_URI_REGISTER);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringRegistrationJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            // send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringRegistrationJson);
            out.close();

            // Read the reponse
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    /********************************** Entity: Record ********************************************/
    /**
     * Add a new Record
     *
     * @param record
     * @return
     */
    public static String addRecord(Record record, Registration register) {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        record.setPatientid(register);

        try {
            Gson gson = new Gson();
            String stringRecordJson = gson.toJson(record);
            url = new URL(ROOT_URI_RECORD);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringRecordJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            // send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringRecordJson);
            out.close();

            // Read the reponse
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    /**
     * Read the date and time of all records of a user
     *
     * @param userId
     * @return
     */
    public static List<String> readRecordDateList(String userId) {
        final String methodPath = "/readRecordDate/" + userId;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        String recordDate = "";
        List<String> recordDateList = new ArrayList<String>();

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            if (conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }

                JSONObject obj = new JSONObject(textResult);
                if (obj.getString("status").equals("OK")) {
                    JSONArray arr = obj.getJSONArray("results");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject eachObj = arr.getJSONObject(i);
                        recordDate = eachObj.getString("date") + " at " + eachObj.getString("time");
                        recordDateList.add(recordDate);
                    }
                } else {
                    Log.i("Empty Value", "This user has no records");
                }
            } else {
                Log.i("HTTP Error Status", "No such user exist: " + conn.getErrorStream().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return recordDateList;
    }

    /**
     *
     * @param patientId
     * @param recordDate
     * @param recordTime
     * @return
     */
    public static Record findRecordByDate (String patientId, String recordDate, String recordTime) {
        final String methodPath = "/findByDateAndTime/" + patientId + "/" + recordDate + "/" + recordTime;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        Record record = null;

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            record = gson.fromJson(textResult, Record.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return record;
    }

    /**
     * Delete a record
     *
     * @param recordId
     */
    public static void deleteRecord (String recordId) {
        final String methodPath = "/" + recordId;
        URL url = null;
        HttpURLConnection conn = null;
        //String textResult = "";

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.getInputStream();

            // Get response
            /*
            Scanner inStream = new Scanner();
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        //return textResult;
    }

    /**
     * Update a record
     *
     * @param record
     * @return
     */
    public static void updateRecord (Record record) {
        final String methodPath = "/" + record.getRecordid();
        URL url = null;
        HttpURLConnection conn = null;
        //String textResult = "";

        try {
            Gson gson = new Gson();
            String stringRecordJson = gson.toJson(record);
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringRecordJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            // send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringRecordJson);
            out.close();
            conn.getInputStream();
            /*
            // Read the reponse
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        //return textResult;
    }

    /********************************** Report: Pain Location *************************************/
    /**
     * Report the frequency
     *
     * @param userid
     * @param startdate
     * @param enddate
     * @return
     */
    public static List<PainLocationResponse> reportPainLocation(String userid, String startdate, String enddate) {
        final String methodPath = "/reportPainLocationAndFrequency/" + userid + "/" + startdate + "/" + enddate;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<PainLocationResponse> responses = new ArrayList<PainLocationResponse>();

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            responses = gson.fromJson(textResult, new TypeToken<List<PainLocationResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return responses;
    }

    /********************************** Report: Pain Weather **************************************/
    public static List<PainLevelResponse> reportPainLevel(String userid, String startdate, String enddate, String weatherVar) {
        final String methodPath = "/reportPainLevel/" + userid + "/" + startdate + "/" + enddate + "/" + weatherVar;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<PainLevelResponse> responses = new ArrayList<PainLevelResponse>();

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            responses = gson.fromJson(textResult, new TypeToken<List<PainLevelResponse>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return responses;
    }

    /********************************** Report: Pain Correlation **********************************/
    public static String[] reportCorrelation(String startdate, String enddate, String weatherVar) {
        final String methodPath = "/reportCorrelation/" + startdate + "/" + enddate + "/" + weatherVar;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        String[] realationResults = new String[2];

        try {
            url = new URL(ROOT_URI_RECORD + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            JSONObject jsonObject = new JSONObject(textResult);
            realationResults[0] = jsonObject.getString("correlationValue");
            realationResults[1] = jsonObject.getString("significantValue");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return realationResults;
    }

    /********************************** Others: Google Geocoding API ******************************/
    /**
     * Get the latitude and longitude of specific address
     *
     * @param address
     * @return
     */
    public static List<String> getAddressGeoInfo(String address) {
        final String methodPath = "/getGeoInfo/" + address.replace(" ", "+");
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<String> geoInfo = null;

        try {
            url = new URL(ROOT_URI_REGISTER + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            if (conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }

                JSONObject obj = new JSONObject(textResult);
                if (obj.getString("status").equals("OK")) {
                    geoInfo = new ArrayList<String>();
                    geoInfo.add(Double.toString(obj.getJSONObject("results").getDouble("lat")));
                    geoInfo.add(Double.toString(obj.getJSONObject("results").getDouble("lng")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return geoInfo;
    }

    /**
     * Get a list of hospitals around specific location
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    public static List<Hospital> getHospitalGeoInfo(String latitude, String longitude
            , String radius) {
        final String methodPath = "/getCategoryGeoInfo/" + latitude + "/" + longitude + "/"
                + radius + "/hospital";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<Hospital> hospitals = null;

        try {
            url = new URL(ROOT_URI_REGISTER + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }

                JSONObject obj = new JSONObject(textResult);
                if (obj.getString("status").equals("OK")) {
                    Gson gson = new Gson();
                    hospitals = gson.fromJson(obj.getJSONArray("results").toString(), new TypeToken<List<Hospital>>(){}.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return hospitals;
    }

    /********************************** Others: Open Weather Map API ******************************/
    /**
     * Get current weather information from OWM api
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static List<String> getWeatherInfo (String latitude, String longitude) {
        String latitudePath = "lat=" + latitude;
        String longitudePath = "lon=" + longitude;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        List<String> weatherInfo = new ArrayList<String>();

        try {
            url = new URL(ROOT_URI_OWM + latitudePath + "&" + longitudePath + ROOT_KEY_OWM);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Get response
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            Gson gson = new Gson();
            OpenWeatherMaoResponse result = gson.fromJson(textResult, OpenWeatherMaoResponse.class);
            weatherInfo.add(result.main.temp);
            weatherInfo.add(result.main.humidity);
            weatherInfo.add(result.wind.speed);
            weatherInfo.add(result.main.pressure);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return weatherInfo;
    }

    /********************************** Others: Encrypt password **********************************/
    /**
     * Encrypt a string value by MD5 and return encrypted value
     *
     * @param inputValue
     * @return
     */
    public static String encryptInput(String inputValue) {
        try {
            // Encrypt by MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputValue.getBytes());
            byte[] byteData = md.digest();

            // Format encrypted string into hexadecimal
            StringBuffer hexData = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1)
                    hexData.append('0');
                hexData.append(hex);
            }

            return hexData.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
