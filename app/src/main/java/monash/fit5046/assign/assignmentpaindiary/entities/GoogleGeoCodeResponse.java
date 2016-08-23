package monash.fit5046.assign.assignmentpaindiary.entities;

/**
 * Created by Steven on 26/04/2016.
 */
public class GoogleGeoCodeResponse {

    public String status;
    public results[] results;

    public GoogleGeoCodeResponse() {
    }

    public class results {
        public address_component[] address_components;
        public String formatted_address;
        public geometry geometry;
        public String place_id;
        public String[] types;
    }

    public class address_component {
        public String long_name;
        public String short_name;
        public String[] types;
    }

    public class geometry {
        public String location_type;
        public location location;
        public bounds viewport;
        public bounds bounds;
    }

    public class bounds {
        public location northeast;
        public location southwest;
    }

    public class location {
        public String lat;
        public String lng;
    }


}
