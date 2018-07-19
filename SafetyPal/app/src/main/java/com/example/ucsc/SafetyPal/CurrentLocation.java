package com.example.ucsc.SafetyPal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 * @author Ali Hooman - alhooman@ucsc.edu
 *
 * Google map fragment activity for displaying device location.
 */
public class CurrentLocation extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        OnGlobalLayoutListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference dataRef;

    // Request code for location permission request.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // Flag indicating whether permission was denied.
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final View mapView;
    private final OnGlobalLayoutAndMapReadyListener devCallBack;
    private boolean isViewReady;
    private boolean isMapReady;
    private double lastKnownLong;
    private double lastKnownLat;
    private Location mLastKnownLocation;

    /*
     * Listener for location
     */
    public interface OnGlobalLayoutAndMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    /*
     * Current location constructor
     */
    public CurrentLocation(
            SupportMapFragment mapFragment, OnGlobalLayoutAndMapReadyListener devCallBack) {
        this.mapFragment = mapFragment;
        mapView = mapFragment.getView();
        this.devCallBack = devCallBack;
        isViewReady = false;
        isMapReady = false;
        mMap = null;

        registerListeners();
    }

    /*
     * Zero argument constructor.
     */
    public CurrentLocation() {
        this.mapFragment = SupportMapFragment.newInstance();
        mapView = mapFragment.getView();
        isViewReady = false;
        isMapReady = false;
        mMap = null;
        this.devCallBack = null;
    }

    /*
     * Registers active listeners.
     */
    private void registerListeners() {
        // View layout
        if((mapView.getWidth() != 0) && (mapView.getHeight() != 0)) {
            // Layout is complete, view is ready.
            isViewReady = true;
        } else {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        fireCallbackIfReady();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

        // Get last location
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), 5));
                        lastKnownLat = mLastKnownLocation.getLatitude();
                        lastKnownLong = mLastKnownLocation.getLongitude();

                        // Add to Firebase user
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        dataRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());

                        dataRef.child("locationLat").setValue(lastKnownLat);
                        dataRef.child("locationLong").setValue(lastKnownLong);

                    }
                }
        });
/*
        lastKnownLatLng = new LatLng(lastKnownLat, lastKnownLong);
        mMap.clear();
        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(lastKnownLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 5));
        */
    }

    /*
     * If fine location permission is granted, enable my location layer on map.
     */
    private void enableMyLocation() {
        // Check for fine location permissions using Google's PermissionUtils.java
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if(mMap != null) {
            // Access to location has been granted.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @SuppressWarnings("deprecation")    // Use new method when supported
    @SuppressLint("NewApi")             // Check which build is being used
    @Override
    public void onGlobalLayout() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        isViewReady = true;
        fireCallbackIfReady();
    }

    private void fireCallbackIfReady() {
        if(isViewReady && isMapReady) {
            devCallBack.onMapReady(mMap);
        }
    }
}
