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

}
