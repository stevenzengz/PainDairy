package monash.fit5046.assign.assignmentpaindiary.entities;

import java.util.Date;

/**
 * Created by Steven on 23/04/2016.
 */
public class Record {

    private Double atmosphericpressure;
    private Integer humidity;
    private Double latitude;
    private Double longitude;
    private String moodlevel;
    private Integer painlevel;
    private String painlocation;
    private String paintrigger;
    private Registration patientid;
    private String recorddate;
    private Integer recordid;
    private String recordtime;
    private Double temperature;
    private Double windspeed;

    public Record() {
    }

    public Record(Double atmosphericpressure, Integer humidity, Double latitude, Double longitude, String moodlevel, Integer painlevel, String painlocation, String paintrigger, String recorddate, String recordtime, Double temperature, Double windspeed) {
        this.recorddate = recorddate;
        this.recordtime = recordtime;
        this.painlevel = painlevel;
        this.painlocation = painlocation;
        this.moodlevel = moodlevel;
        this.paintrigger = paintrigger;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.atmosphericpressure = atmosphericpressure;
    }

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(String recorddate) {
        this.recorddate = recorddate;
    }

    public String getRecordtime() {
        return recordtime;
    }

    public void setRecordtime(String recordtime) {
        this.recordtime = recordtime;
    }

    public Integer getPainlevel() {
        return painlevel;
    }

    public void setPainlevel(Integer painlevel) {
        this.painlevel = painlevel;
    }

    public String getPainlocation() {
        return painlocation;
    }

    public void setPainlocation(String painlocation) {
        this.painlocation = painlocation;
    }

    public String getMoodlevel() {
        return moodlevel;
    }

    public void setMoodlevel(String moodlevel) {
        this.moodlevel = moodlevel;
    }

    public String getPaintrigger() {
        return paintrigger;
    }

    public void setPaintrigger(String paintrigger) {
        this.paintrigger = paintrigger;
    }

    public Registration getPatientid() {
        return patientid;
    }

    public void setPatientid(Registration patientid) {
        this.patientid = patientid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(Double windspeed) {
        this.windspeed = windspeed;
    }

    public Double getAtmosphericpressure() {
        return atmosphericpressure;
    }

    public void setAtmosphericpressure(Double atmosphericpressure) {
        this.atmosphericpressure = atmosphericpressure;
    }

}
