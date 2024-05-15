package com.example.havenwomansafetyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;
    private EmergencyContacts emergencyContacts;

    private CardView contactCard, locationCard, recCard, resCard, memoCard, sosCard;
    private CountDownTimer countdownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactCard = findViewById(R.id.contactCard);
        locationCard = findViewById(R.id.locationCard);
        recCard = findViewById(R.id.recCard);
        resCard = findViewById(R.id.resCard);
        memoCard = findViewById(R.id.memoCard);
        sosCard = findViewById(R.id.sosCard);

        emergencyContacts = new EmergencyContacts();

        contactCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EmergencyContacts.class)));
        sosCard.setOnClickListener(v -> {
            startEmergencyCallCountdown();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                sendLocationToAllContacts();
            }
        });
        locationCard.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                startActivity(new Intent(MainActivity.this, location.class));
            }
        });

        recCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Record.class)));
        resCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Resources.class)));
        memoCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Memo.class)));

    }

    private void sendLocationToAllContacts() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String liveLocationUrl = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                        String sosMessage = "SOS Alert! I need help! My current location is: " + liveLocationUrl;
                        ArrayList<Contact> smsContactsList = EmergencyContacts.getStaticSmsContactList();
                        for (Contact contact : smsContactsList) {
                            sendSMS(contact.getNumber(), sosMessage);
                        }
                        Toast.makeText(MainActivity.this, "Location sent to all contacts.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Failed to get current location.", Toast.LENGTH_SHORT).show());
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SMS", "SMS sent successfully.");
        } catch (Exception e) {
            Log.e("SMS", "SMS sending failed.", e);
            Toast.makeText(getApplicationContext(), "SMS sending failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startEmergencyCallCountdown() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Emergency call will be made in 3 seconds.")
                .setCancelable(false)
                .setPositiveButton("Cancel", (dialog, id) -> {
                    if (countdownTimer != null) {
                        countdownTimer.cancel();
                    }
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();

        countdownTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                alert.setMessage("You will be prompted to an Emergency call in " + (millisUntilFinished / 1000) + " seconds.");
            }

            public void onFinish() {
                alert.dismiss();
                initiateEmergencyCall();
            }
        }.start();
    }

    private void initiateEmergencyCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:999"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocationToAllContacts();
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(MainActivity.this, location.class));
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateEmergencyCall();
            } else {
                Toast.makeText(this, "Call permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}