package hu.bme.aut.listapp.map;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.SettingsActivity;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlacesListener {

    private Location lastLocation;

    private GoogleMap mMap;

    private FloatingActionButton fab;

    private PlacesListener listener;

    private List<Place> places;

    private ServiceLocation.BinderServiceLocation binderServiceLocation = null;

    private ServiceConnection servConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binderServiceLocation = ((ServiceLocation.BinderServiceLocation) iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            lastLocation = intent.getParcelableExtra(ServiceLocation.KEY_LOCATION);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listener = this;

        fab = findViewById(R.id.mapFab);
        fab.setOnClickListener((view) -> {
            if (lastLocation == null || mMap == null) {
                Snackbar.make(view, R.string.tryAgainLaterMsg,
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(
                            new LatLng(lastLocation.getLatitude(),
                                    lastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(14)                   // Sets the zoom
                    .bearing(lastLocation.getBearing())
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(lastLocation.getLatitude(),
                            lastLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(getString(R.string.user_position)));

            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());

            boolean openNow = sharedPreferences.getBoolean(SettingsActivity.KEY_IS_OPEN, false);
            String type = sharedPreferences.getString(SettingsActivity.KEY_PLACE_TYPE, "");

            int radius = 0;
            try {
                radius = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_RADIUS, "50000"));
            } catch (Exception e) {
            }

            final NRPlaces.Builder builder = new NRPlaces.Builder();

            if (!type.isEmpty() && !type.equals("all"))
                builder.type(type);

            builder.opennow(openNow)
                    .listener(listener)
                    .radius(radius)
                    .latlng(lastLocation.getLatitude(), lastLocation.getLongitude())
                    .key(getString(R.string.google_maps_key))
                    .build()
                    .execute();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = new Intent(this, ServiceLocation.class);
        this.bindService(i, servConn, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_LOCATION));
    }

    @Override
    public void onPause() {
        if (binderServiceLocation != null) {
            this.unbindService(servConn);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);

        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Budapest, Hungary.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        // Add a marker in Budapest and move the camera
        LatLng budapest = new LatLng(47.4813602, 18.9902208);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(budapest));
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        e.printStackTrace();
    }

    @Override
    public void onPlacesStart() {
    }

    @Override
    public void onPlacesSuccess(List<Place> placesList) {
        runOnUiThread(() -> {
            places.clear();
            places = placesList;

            for (Place place : places) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(place.getName())
                        .snippet(place.getVicinity())
                        .icon(BitmapDescriptorFactory.
                                defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
    }

    @Override
    public void onPlacesFinished() {
    }
}
