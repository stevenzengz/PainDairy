/**
 * Fragment - Nearby Hospital Map
 * Steven Zeng 8/7/2016
 *
 * Relationship:
 *   Parent Activity - MainMenuActicity
 *
 * Prerequisite:
 *
 * User Case:
 *   1 - user inputs an address and click search button
 *   2 - the map shows some hospitals around this specific place
 *   3 - user can pick up a hospital for further information on the map by Google Place API
 *
 * Functions Sequence:
 *   1 - Action - Click - search an address
 *   2 - getPlaceGeoInfo() - return the latitude and longitude of this specific place by Google
 *                           GeoInfo API
 *   3 - getNearbyHospitals() - return a list of hospital objects with geographic information around
 *                              a place by Google Place Web Service
 *   4 - displayHospitalsOnMap() - based on a list of hospital, displays them on the Google map
 */
package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.R;
import monash.fit5046.assign.assignmentpaindiary.entities.Hospital;

public class HospitalLocFragment extends Fragment implements OnMapReadyCallback{

    private static View mapView;

    // Google Maps
    private GoogleMap mMap;
    private final static LatLng homeLoc = new LatLng(-37.8749181, 145.041461);
    private static String radius = "2000";
    private LatLng requestPlaceLoc;

    // Widgets
    private EditText wg_place;
    private Button wg_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.fragment_hospital_location, container, false);
        initWidgets();

        // Google Maps Configuration
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.fhl_fra_map);
        mapFragment.getMapAsync(this);

        // Action - Button
        wg_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeAddress = wg_place.getText().toString();
                searchNearbyHospitals(placeAddress, radius);
            }
        });

        return mapView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(homeLoc).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLoc, 20));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

    /**
     * Search hospitals nearby a specific place in a radius scale
     *
     * @param placeAddress
     * @param radius
     */
    private void searchNearbyHospitals(String placeAddress, String radius) {
        new AsyncTask<String, Void, List<Hospital>>() {
            @Override
            protected List<Hospital> doInBackground(String... params) {
                List<String> requestPlace = getPlaceGeoInfo(params[0]);
                String requestPlaceLat = requestPlace.get(0);
                String requestPlaceLon = requestPlace.get(1);
                requestPlaceLoc = new LatLng(Double.valueOf(requestPlaceLat), Double.valueOf(requestPlaceLon));
                return getNearbyHospitals(requestPlaceLat, requestPlaceLon, params[1]);
            }

            @Override
            protected void onPostExecute(List<Hospital> response) {
                if (response != null) {
                    displayHospitalsOnMap(response);
                } else {
                    Toast.makeText(mapView.getContext(), "No Hospitals Around", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(placeAddress, radius);
    }

    /**
     * Return the latitude and longitude of a specific place by Google GeoInfo API
     *
     * @return
     */
    private List<String> getPlaceGeoInfo(String placeAddress) {
        return RestClient.getAddressGeoInfo(placeAddress);
    }

    /**
     * Return a list of hospital objects with geographic information around a place by Google Place
     * Web Service
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private List<Hospital> getNearbyHospitals(String latitude, String longitude, String radius) {
        return RestClient.getHospitalGeoInfo(latitude, longitude, radius);
    }

    /**
     * Displays a list of hospitals on the Google map
     *
     * @param hospitals
     */
    private void displayHospitalsOnMap(List<Hospital> hospitals) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(requestPlaceLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        for (Hospital hospital : hospitals) {
            LatLng hospitalGeoInfo = new LatLng(hospital.getLatitude(), hospital.getLongitude());
            String hospitalInfo = hospital.getName() + " " + hospital.getVicinity();
            mMap.addMarker(new MarkerOptions().position(hospitalGeoInfo).title(hospitalInfo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(requestPlaceLoc, 13.5f));
        }
    }

    /**
     * Initiate all widgets
     */
    private void initWidgets() {
        wg_place = (EditText) mapView.findViewById(R.id.fhl_et_place);
        wg_search = (Button) mapView.findViewById(R.id.fhl_bt_search);
    }

}
