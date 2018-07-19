package com.example.ucsc.SafetyPal;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private DatabaseReference dataRef;
    private Button logOutButton;
    private Button alarmStart;

    /*
     * Location provider objects
     * Ali Hooman (alhooman@ucsc.edu)
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location lastDeviceLocation;
    private TextView latView;
    private TextView longView;

    /*
     * SMS Manager objects
     * Ali Hooman (alhooman@ucsc.edu)
     */
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.signOut);
        logOutButton.setOnClickListener(this);

        alarmStart = findViewById(R.id.alarmChange);
        alarmStart.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();


        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, logIn.class));
        }

        FirebaseUser user = auth.getCurrentUser();

        dataRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        DatabaseReference alarmStatus = dataRef.child("isAlarmActivated");
        alarmStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString() == "true"){
                    System.out.print("i changed value");
                    Intent myIntent = new Intent(MainActivity.this, MyAlarmServices.class);

                    PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);



                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);



                    Calendar calendar = Calendar.getInstance();

                    calendar.setTimeInMillis(System.currentTimeMillis());

                    calendar.add(Calendar.SECOND, 2);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);



                    Toast.makeText(MainActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Changed To False", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
         * Fused Location provider updates for device last known location
         *
         * Ali Hooman - alhooman@ucsc.edu
         */
        // Create location services client.
        latView = (TextView) findViewById(R.id.mainLatView);
        longView = (TextView) findViewById(R.id.mainLongView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize Location object to coordinate: 0,0 to avoid nullPointerException
        lastDeviceLocation = new Location("");
        lastDeviceLocation.setLatitude(0.5d);
        lastDeviceLocation.setLongitude(5.0d);
        // Find last location and update.
        callGetLastLocation();
        setLatAndLongTextViews(lastDeviceLocation);

    }

    /*
     * Retrieves lastDeviceLocation Location object.
     * Ali Hooman (alhooman@ucsc.edu)
     */
    public Location getLastDeviceLocation() {
        return this.lastDeviceLocation;
    }

    /*
     * Calls fused location provider's getLastLocation() method and updates lastDeviceLocation;
     * Ali Hooman (alhooman@ucsc.edu)
     */
    public void callGetLastLocation() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get last location
            Task<Location> locationResult = mFusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastDeviceLocation = task.getResult();
                        setLatAndLongTextViews(lastDeviceLocation);
                        // Add to Firebase user
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        dataRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
                        dataRef.child("locationLat").setValue(lastDeviceLocation.getLatitude());
                        dataRef.child("locationLong").setValue(lastDeviceLocation.getLongitude());
                    }
                }
            });
        }
    }

    /*
     * Sets latitude and longitude text view objects on main activity.
     * Ali Hooman (alhooman@ucsc.edu)
     *
     * Parameters: Location location - location object for last know device location.
     */
    public void setLatAndLongTextViews(Location location) {
        // Get coordinates from location
        double latCoord = 0.0;
        double longCoord = 0.0;
        latCoord = location.getLatitude();
        longCoord = location.getLongitude();
        // Convert to strings
        String latString = String.valueOf(latCoord);
        String longString = String.valueOf(longCoord);
        // Set text views
        latView.setText(latString);
        longView.setText(longString);
    }

    /*
     * Opens default SMS application to send a request for help.
     * Ali Hooman (alhooman@ucsc.edu)
     */
    public void sendRequestHelpSMS() {
        // Create intent
        String intentTypeSMS = "vnd.android-dir/mms-sms";
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        // Fill in phone number and message body
        sendIntent.putExtra("sms_body", "safetyPal test");
        sendIntent.putExtra("address", new String("4087612025"));
        // Start intent
        sendIntent.setType(intentTypeSMS);
        startActivity(sendIntent);
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

    @Override
    public void onClick(View view) {
        if(view == logOutButton){
            // log out
            auth.signOut();
            finish();
            startActivity(new Intent(this, logIn.class));
        }

        if(view == alarmStart){
            dataRef.child("isAlarmActivated").setValue(true);
            callGetLastLocation();

            sendRequestHelpSMS(); // Text for help from contacts
        }


    }
}
