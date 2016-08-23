package monash.fit5046.assign.assignmentpaindiary.entities;

/**
 * Created by Steven on 19/07/2016.
 */
public class Hospital {

    private String name;
    private Double latitude;
    private Double longitude;
    private String vicinity;

    public Hospital() {
    }

    public Hospital(String name, Double latitude, Double longitude, String vicinity) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.vicinity = vicinity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
