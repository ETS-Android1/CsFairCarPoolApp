package com.mkrolak;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;


public class RequestActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    private GoogleMap mMap;

    private LatLng otherPerson;
    private LatLng currentLocation;
    private LatLng laneTech;

    private PickUpRequest pickUpRequest;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        key = getIntent().getStringExtra(HomeActivity.REQUEST_REFERENCE);


        laneTech = new LatLng(Double.parseDouble(getString(R.string.LaneLatLong).split(",")[0]),Double.parseDouble(getString(R.string.LaneLatLong).split(",")[1]));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(RequestActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                database.getReference(HomeActivity.RIDER_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                            if(dataSnapshot1.getKey().equals(key)){
                                                pickUpRequest = dataSnapshot1.getValue(PickUpRequest.class);
                                                findViewById(R.id.acceptButton).setVisibility((pickUpRequest.name.equals(mAuth.getCurrentUser().getDisplayName()))?View.INVISIBLE:View.VISIBLE);
                                                otherPerson = new LatLng(pickUpRequest.latOfPerson,pickUpRequest.lonOfPerson);
                                                SupportMapFragment mapFragment = (SupportMapFragment) RequestActivity.this.getSupportFragmentManager().findFragmentById(R.id.mapView);
                                                mapFragment.getMapAsync(RequestActivity.this);

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
        }




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.addMarker(new MarkerOptions().position(otherPerson));
            mMap.addMarker(new MarkerOptions().position(laneTech));
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));


    }
}
