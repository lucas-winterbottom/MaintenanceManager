package com.mad.maintenancemanager.tradeactivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.LoginActivity;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.api.GPSTracker;
import com.mad.maintenancemanager.api.PermissionUtils;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.TempPlace;
import com.mad.maintenancemanager.presenter.PlacesPresenter;

import java.util.List;

/**
 * Activity that shows a Google Map with pins on it for task locations that are near your
 * current location
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener{

    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private GPSTracker mGPS;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private TempPlace mTempPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.task_near_you);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission},
                    REQUEST_CODE_PERMISSION);
        } else {
            createGoogleApiClient();
            prepMap();
        }
    }

    /**
     * Creates Google API Clients to provide to PlacePresenter
     */
    private void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSION) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            prepMap();
        } else {
            Toast.makeText(getApplicationContext(), R.string.locations_acess_toast,Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Sets up map fragment
     */
    private void prepMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                startActivity(intent);
                DatabaseHelper.getInstance().userLogout();
                finish();
                break;

        }


        return true;

    }

    /**
     * Moves map to current locationa and adds pins for task locations
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng currentLoc;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mGPS = new GPSTracker(MapActivity.this);
        PlacesPresenter presenter = new PlacesPresenter(getApplicationContext(), mGoogleApiClient, mGoogleMap);
        presenter.setTaskPlaces();

        // check if GPS enabled
        if (mGPS.canGetLocation()) {

            double latitude = mGPS.getLatitude();
            double longitude = mGPS.getLongitude();
            currentLoc = new LatLng(latitude, longitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            mGPS.showSettingsAlert();
        }

        //// TODO: 4/6/17 Make this its own method when everything else works
        try {
            googleMap.setMyLocationEnabled(true);

        } catch (SecurityException e) {
            Log.d(Constants.SECURITY, e.toString());
        }
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * makes dialog fragment for loactions showing available tasks
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        TempPlace place = (TempPlace) marker.getTag();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TASK_LOCATION, place.getID());
        bundle.putString(Constants.LOCATION_NAME,place.getName());
        FragmentManager fm = getSupportFragmentManager();
        LocationTasksFragment fragment = new LocationTasksFragment();
        fragment.setArguments(bundle);
        fragment.show(fm, Constants.PLACE_STUFF);
        return false;

    }

}
