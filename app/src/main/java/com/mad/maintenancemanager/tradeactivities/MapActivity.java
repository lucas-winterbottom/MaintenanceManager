package com.mad.maintenancemanager.tradeactivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.maintenancemanager.LoginActivity;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.api.GPSTracker;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSION = 2;
    private String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private GPSTracker mGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.task_near_you);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission},
                    REQUEST_CODE_PERMISSION);
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLoc;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mGPS = new GPSTracker(MapActivity.this);

        // check if GPS enabled
        if (mGPS.canGetLocation()) {

            double latitude = mGPS.getLatitude();
            double longitude = mGPS.getLongitude();
            currentLoc = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            mGPS.showSettingsAlert();
        }
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
    }

}
