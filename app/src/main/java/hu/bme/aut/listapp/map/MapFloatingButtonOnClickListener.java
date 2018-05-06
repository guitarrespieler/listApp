package hu.bme.aut.listapp.map;

import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.SettingsActivity;
import noman.googleplaces.NRPlaces;

public class MapFloatingButtonOnClickListener implements View.OnClickListener {

    MapsActivity activity;

    public MapFloatingButtonOnClickListener(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Location lastLocation = activity.getLastLocation();
        GoogleMap mMap = activity.getmMap();

        if (lastLocation == null || mMap == null) {
            Snackbar.make(v, R.string.tryAgainLaterMsg,
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        Snackbar.make(v, R.string.searchingNearbyPlacesInProgress, Snackbar.LENGTH_LONG).show();

        refreshUserMarkerAndCameraPosition(lastLocation, mMap);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getApplicationContext());

        //get nearby places
        buildPlacesQueryRequest(lastLocation, sharedPreferences, v)
                .execute();
    }

    /**
     * Gathers every information to build request to ask for nearby places.
     *
     * @param lastLocation
     * @param sharedPreferences
     * @return
     */
    private NRPlaces buildPlacesQueryRequest(Location lastLocation, SharedPreferences sharedPreferences, View v) {
        boolean openNow = sharedPreferences.getBoolean(SettingsActivity.KEY_IS_OPEN, false);
        String type = sharedPreferences.getString(SettingsActivity.KEY_PLACE_TYPE, "");

        int radius = 50000;
        try {
            radius = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_RADIUS, String.valueOf(radius)));
        } catch (Exception e) {
        }

        final NRPlaces.Builder builder = new NRPlaces.Builder();

        if (!type.isEmpty() && !type.equals("all"))
            builder.type(type);

        return builder.opennow(openNow)
                .listener(activity)
                .radius(radius)
                .latlng(lastLocation.getLatitude(), lastLocation.getLongitude())
                .key(activity.getString(R.string.google_maps_key))
                .build();
    }

    /**
     * Refreshes the marker of user's location and translates the camera to the current location.
     *
     * @param lastLocation
     * @param mMap
     */
    private void refreshUserMarkerAndCameraPosition(Location lastLocation, GoogleMap mMap) {
        activity.animateCameraToThisPosition(lastLocation);
        mMap.clear();
        LatLng pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        activity.createMarker(pos, activity.getString(R.string.user_position), null, BitmapDescriptorFactory.HUE_GREEN);
    }
}
