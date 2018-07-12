package com.example.ucsc.SafetyPal;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;
    SmsManager smsManager = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference();
    }

    /*
     * Opens CurrentLocation activity on locationButton click.
     */
    public void openCurrentLocation(View view) {
        Intent locationViewIntent = new Intent(this, CurrentLocation.class);
        startActivity(locationViewIntent);
    }

    /*
     * Open manage contact list view.
     */
    public void openManageContacts(View view) {
        Intent manageContactsViewIntent = new Intent(this, ManageContacts.class);
        startActivity(manageContactsViewIntent);
    }

    /*
     * Send SMS and location on SOS button click.
     */
    public void sendSMSOnClick(View View) {
        for(SafetyContact contact : SafetyContact.safetyContactsList) {
            String number = contact.getSafetyPhoneParsed();
            String help = "Safety Pal alert: I need help!";
            String SENT = "SMS Sent";
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            smsManager.sendTextMessage(number, null, help, sentPI, null);
            smsManager.sendTextMessage("4087612025", null, help, null, null);
        }
    }
}
