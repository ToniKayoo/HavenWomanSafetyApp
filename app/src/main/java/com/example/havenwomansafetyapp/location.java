package com.example.havenwomansafetyapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class location extends AppCompatActivity implements OnMapReadyCallback {
    private LocationRequest locationRequest;
    private double latitude, longitude;
    private ProgressBar loadingPB;
    private GoogleMap mMap;
    private LocationDatabaseHelper databaseHelper;
    private DatabaseReference locationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        databaseHelper = new LocationDatabaseHelper(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        locationRef = database.getReference("user_location");
        loadingPB = findViewById(R.id.idPBLoading);
        Button locationButton = findViewById(R.id.locationButton);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                getCurrentLocation();
            }
        });
    }

    private void showLocationDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Details");

        String message = "Latitude: " + latitude + "\nLongitude: " + longitude + "\nAddress: " + getAddress(latitude, longitude);

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, request location updates
                requestLocationUpdates();
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            requestLocationUpdates();
        }
    }

    // Request location updates
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(location.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                String address = getAddress(latitude, longitude);
                                String city = "Sample City";
                                String country = "Sample Country";
                                updateDatabase(latitude, longitude, address, city, country);
                                updateMap(latitude, longitude);
                                loadingPB.setVisibility(View.GONE);
                                showLocationDetailsDialog(); // Show location details in a dialog
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void updateMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void updateDatabase(double latitude, double longitude, String address, String city, String country) {
        // Data inserted into SQLite database
        long rowId = databaseHelper.insertLocation(latitude, longitude, address);

        // Check if insertion was successful
        if (rowId != -1) {
            // Create a new LocationData object
            LocationData locationData = new LocationData(latitude, longitude, address, city, country);

            // Save location data to Firebase
            locationRef.child(String.valueOf(rowId)).setValue(locationData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(location.this, "Location data saved to Firebase", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(location.this, "Failed to save location data to Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(location.this, "Failed to insert location data into SQLite database", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }
}
