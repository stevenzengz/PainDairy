package monash.fit5046.assign.assignmentpaindiary.entities;

import java.util.Date;

/**
 * Created by Steven on 23/04/2016.
 */
public class Patient {

    private String address;
    private String dob;
    private Doctor doctorid;
    private String firstname;
    private String gender;
    private Integer height;
    private String lastname;
    private String occupation;
    private Integer patientid;
    private Integer weight;

    public Patient() {
    }

    public Patient(String address, String dob, String firstname, String gender, Integer height, String lastname, String occupation, Integer weight) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.occupation = occupation;
        this.address = address;
    }

    public Patient(String address, String dob, Doctor doctorid, String firstname, String gender, Integer height, String lastname, String occupation, Integer patientid, Integer weight) {
        this.patientid = patientid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.occupation = occupation;
        this.address = address;
        this.doctorid = doctorid;
    }

    public Integer getPatientid() {
        return patientid;
    }

    public void setPatientid(Integer patientid) {
        this.patientid = patientid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Doctor getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(Doctor doctorid) {
        this.doctorid = doctorid;
    }

}
