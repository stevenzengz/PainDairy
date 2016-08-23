package monash.fit5046.assign.assignmentpaindiary.entities;

/**
 * Created by Steven on 26/04/2016.
 */
public class OpenWeatherMaoResponse {

    public coord coord;
    public weather[] weather;
    public String base;
    public main main;
    public wind wind;
    public clouds clouds;
    public String dt;
    public sys sys;
    public String id;
    public String name;
    public String cod;

    public OpenWeatherMaoResponse() {
    }

    public class coord {
        public String lon;
        public String lat;
    }

    public class weather {
        public String id;
        public String main;
        public String description;
        public String icon;
    }

    public class main {
        public String temp;
        public String pressure;
        public String humidity;
        public String temp_min;
        public String temp_max;
    }

    public class wind {
        public String speed;
        public String deg;
    }

    public class clouds {
        public String all;
    }

    public class sys {
        public String type;
        public String id;
        public String message;
        public String country;
        public String sunrise;
        public String sunset;
    }
}
