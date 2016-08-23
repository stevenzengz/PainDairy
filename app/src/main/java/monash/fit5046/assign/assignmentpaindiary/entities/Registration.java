package monash.fit5046.assign.assignmentpaindiary.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Steven on 23/04/2016.
 */
public class Registration implements Serializable {

    private Integer patientid;
    private String username;
    private String password;
    private String registerdate;
    private String registertime;

    public Registration() {
    }

    public Registration(Integer patientid, String username, String password, String registerdate, String registertime) {
        this.patientid = patientid;
        this.username = username;
        this.password = password;
        this.registerdate = registerdate;
        this.registertime = registertime;
    }

    public Integer getPatientid() {
        return patientid;
    }

    public void setPatientid(Integer patientid) {
        this.patientid = patientid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(String registerdate) {
        this.registerdate = registerdate;
    }

    public String getRegistertime() {
        return registertime;
    }

    public void setRegistertime(String registertime) {
        this.registertime = registertime;
    }

}
