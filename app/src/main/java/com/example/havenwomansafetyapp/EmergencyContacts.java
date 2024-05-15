package com.example.havenwomansafetyapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class EmergencyContacts extends Activity {
    static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    Button sendSMSBtn;
    Button addSMSContactBtn;
    Button sendEmailBtn;
    Button addEmailContactBtn;
    EditText txtMessage;
    EditText etSMSName;
    EditText etSMSNumber;
    EditText etEmailName;
    EditText etEmailAddress;
    String message;
    static ArrayList<Contact> staticSmsContactsList = new ArrayList<>();
    ArrayList<Contact> emailContactsList = new ArrayList<>();
    ContactAdapter smsContactAdapter;
    ContactAdapter emailContactAdapter;
    FusedLocationProviderClient fusedLocationClient;
    // Static method to access the contacts list
    public static ArrayList<Contact> getStaticSmsContactList() {
        return staticSmsContactsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        setContentView(R.layout.activity_emergency_contacts);
        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sendSMSBtn = findViewById(R.id.btnSendSMS);
        addSMSContactBtn = findViewById(R.id.btnAddSMSContact);
        sendEmailBtn = findViewById(R.id.btnSendEmail);
        addEmailContactBtn = findViewById(R.id.btnAddEmailContact);
        txtMessage = findViewById(R.id.etSMSNumber);
        etSMSName = findViewById(R.id.etSMSName);
        etSMSNumber = findViewById(R.id.etSMSNumber);
        etEmailName = findViewById(R.id.etEmailName);
        etEmailAddress = findViewById(R.id.etEmailAddress);

        // Initialize the static list
        if (staticSmsContactsList.isEmpty()) {
            staticSmsContactsList.add(new Contact("Steve", "1234567890", ""));
            staticSmsContactsList.add(new Contact("Emily", "0987654321", ""));
        }

        // Create adapter for SMS contacts list with the static list
        smsContactAdapter = new ContactAdapter(this, staticSmsContactsList);
        ListView listViewSMSContacts = findViewById(R.id.listViewSMSContacts);
        listViewSMSContacts.setAdapter(smsContactAdapter);

        ListView listViewEmailContacts = findViewById(R.id.listViewEmailContacts);
        emailContactAdapter = new ContactAdapter(this, emailContactsList);
        listViewEmailContacts.setAdapter(emailContactAdapter);

        listViewSMSContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = staticSmsContactsList.get(position).getNumber();
                initiatePhoneCall(phoneNumber);
            }
        });

        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                chooseSMSRecipient();
            }
        });

        addSMSContactBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addSMSContact();
            }
        });

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                chooseEmailRecipient();
            }
        });

        addEmailContactBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addEmailContact();
            }
        });

        // Example email contacts
        emailContactsList.add(new Contact("", "", "example@example.com"));
    }
    protected void chooseSMSRecipient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose SMS Recipient");

        String[] contactsArray = new String[staticSmsContactsList.size()];
        for (int i = 0; i < staticSmsContactsList.size(); i++) {
            contactsArray[i] = staticSmsContactsList.get(i).getName();
        }
        builder.setItems(contactsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedNumber = staticSmsContactsList.get(which).getNumber();
                sendSMSMessage(selectedNumber);
                // Send SMS to the selected contact's number
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void chooseEmailRecipient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Email Recipient");
        String[] contactsArray = new String[emailContactsList.size()];
        for (int i = 0; i < emailContactsList.size(); i++) {
            contactsArray[i] = emailContactsList.get(i).getEmail();
        }
        builder.setItems(contactsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedEmail = emailContactsList.get(which).getEmail();
                sendEmailMessage(selectedEmail); // Send email to the selected contact's email address
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void sendEmailMessage(String emailAddress) {

        String liveLocationUrl = "https://www.google.com/https:/www.google.com/maps?q=";
        String sosMessage = "SOS Alert! I need help! My current location is: " + liveLocationUrl;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc82");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress}); // Recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SOS Alert");
        emailIntent.putExtra(Intent.EXTRA_TEXT, sosMessage);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "No email clients.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void addSMSContact() {
        String name = etSMSName.getText().toString();
        String contact = etSMSNumber.getText().toString();
        staticSmsContactsList.add(new Contact(name, contact, ""));
        smsContactAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "SMS Contact added successfully", Toast.LENGTH_SHORT).show();
    }

    private void addEmailContact() {
        String name = etEmailName.getText().toString();
        String email = etEmailAddress.getText().toString();
        emailContactsList.add(new Contact(name, "", email));
        emailContactAdapter.notifyDataSetChanged(); // Update ListView
        Toast.makeText(getApplicationContext(), "Email Contact added successfully", Toast.LENGTH_SHORT).show();
    }

    protected void sendSMSMessage(String phoneNumber) {
        // Construct SOS message with live location URL
        String liveLocationUrl = "https://www.google.com/maps?q=";
        String sosMessage = "SOS Alert! I need help! My current location is: " + liveLocationUrl;

        // Check permission for sending SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            // Send SMS
            sendSMS(phoneNumber, sosMessage); // Pass the SOS message as the SMS body
        }
    }


    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SOS SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SOS SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void initiatePhoneCall(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Phone number is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber.trim()));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No app to handle phone calls", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // handles SMS permission result
                break;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    chooseEmailRecipient();
                } else {
                    // permission denied, boo!
                    Toast.makeText(getApplicationContext(), "Location permission denied, cannot fetch location.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }}
