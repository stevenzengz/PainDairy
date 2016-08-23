package monash.fit5046.assign.assignmentpaindiary.entities;

/**
 * Created by Steven on 23/04/2016.
 */
public class Doctor {

    private String clinicaddress;
    private String clinicphoneno;
    private Integer doctorid;
    private String firstname;
    private String lastname;
    private String phoneno;

    public Doctor() {
    }

    public Doctor(String clinicaddress, String clinicphoneno, Integer doctorid, String firstname, String lastname, String phoneno) {
        this.doctorid = doctorid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneno = phoneno;
        this.clinicaddress = clinicaddress;
        this.clinicphoneno = clinicphoneno;
    }

    public Integer getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(Integer doctorid) {
        this.doctorid = doctorid;
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

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getClinicaddress() {
        return clinicaddress;
    }

    public void setClinicaddress(String clinicaddress) {
        this.clinicaddress = clinicaddress;
    }

    public String getClinicphoneno() {
        return clinicphoneno;
    }

    public void setClinicphoneno(String clinicphoneno) {
        this.clinicphoneno = clinicphoneno;
    }

}
