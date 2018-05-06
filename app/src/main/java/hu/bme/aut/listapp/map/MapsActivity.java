package hu.bme.aut.listapp.map;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.LinkedList;
import java.util.List;

import hu.bme.aut.listapp.R;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlacesListener {

    public static final LatLng budapestPos = new LatLng(47.4813602, 18.9902208);

    private Location lastLocation;

    private GoogleMap mMap;

    private FloatingActionButton fab;

    private ProgressBar progressBar;

    private List<Place> places = new LinkedList<>();

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

            if (lastLocation != null) {
                refreshMarkers();
                animateCameraToThisPosition(lastLocation);
            }
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

        progressBar = findViewById(R.id.mapProgressBar);

        fab = findViewById(R.id.mapFab);
        fab.setOnClickListener(new MapFloatingButtonOnClickListener(this));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null)
            progressBar.setVisibility(View.VISIBLE);

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

        progressBar.setVisibility(View.GONE);

        animateCameraToThisPosition(lastLocation);
        refreshMarkers();
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        Toast.makeText(this, R.string.somethingWentWrongMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlacesStart() {
        places.clear();
    }

    @Override
    public void onPlacesSuccess(List<Place> placesList) {
        runOnUiThread(() -> {
            places.addAll(placesList);
            refreshMarkers();
        });
    }

    /**
     * Refeshes the marker of the user's location
     * and every places' location.
     */
    public void refreshMarkers() {
        mMap.clear();

        createPlaceMarkers(places);

        LatLng pos = null;
        if (lastLocation != null) {
            pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        } else {
            pos = budapestPos;
        }
        createMarker(pos, getString(R.string.user_position), null, BitmapDescriptorFactory.HUE_GREEN);
    }

    /**
     * Translates the camera to the given location. If location is null,
     * animates it to the default location, which is Budapest, Hungary.
     *
     * @param location
     */
    public void animateCameraToThisPosition(Location location) {
        LatLng pos = null;
        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();

        if (location == null) {
            pos = budapestPos; //Budapest
        } else {
            pos = new LatLng(location.getLatitude(),
                    location.getLongitude());
            cameraPositionBuilder.bearing(location.getBearing());
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)                // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void createPlaceMarkers(List<Place> placeList) {
        if (placeList == null || placeList.isEmpty())
            return;

        for (Place place : placeList) {
            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
            createMarker(latLng, place.getName(), place.getVicinity(), BitmapDescriptorFactory.HUE_RED);
        }
    }

    /**
     * Creates marker with the given parameters.
     *
     * @param position    latitude and longitude position of marker
     * @param title       title of marker
     * @param snippet     snippet of marker
     * @param markerColor color of the marker. Use BitmapDescriptorFactory.
     */
    public void createMarker(LatLng position, String title, String snippet, float markerColor) {
        if (title == null)
            title = "";

        if (snippet == null)
            snippet = "";

        mMap.addMarker(new MarkerOptions().position(position)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
    }

    @Override
    public void onPlacesFinished() {
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
