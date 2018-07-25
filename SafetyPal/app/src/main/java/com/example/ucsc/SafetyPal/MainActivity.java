package com.example.ucsc.SafetyPal;

import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private DatabaseReference dataRef;
    private ImageButton logOutButton;
    private Button alarmStart;      // Currently invisble, same function as imageView
    private ImageView imageView;    // Make giant yellow thing clickable
    private EditText customMessage; // Custom message input box

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

    /*
     * Contact list
     * Ali Hooman (alhooman@ucsc.edu)
     *
     * This list is public at the moment and does not survive application shutdown.
     * //TODO   Save list to Firebase
     * //TODO   Make list not public
     *
     * Initial public declaration of contactList within MainActivity.java does not work. Activities
     * are transient and simply attempting to access an object of one activity, despite it being
     * public and static, will not work from another activity.
     */
     public List<contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.signOut);
        logOutButton.setOnClickListener(this);

        alarmStart = findViewById(R.id.alarmChange);
        alarmStart.setOnClickListener(this);

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);

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
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                Intent myIntent = new Intent(MainActivity.this, MyAlarmServices.class);
                if(dataSnapshot.getValue().toString() == "true"){



                    PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);




                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 2);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);



                    Toast.makeText(MainActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
                }else{

                    PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pIntent);
                   // Toast.makeText(MainActivity.this, "Changed To False", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
         * Fused Location provider updates for device last known location
         *
         * Ali Hooman (alhooman@ucsc.edu)
         */
        // Create location services client.
        latView = (TextView) findViewById(R.id.mainLatView);
        longView = (TextView) findViewById(R.id.mainLongView);
        // Create location provider client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize Location object to coordinate: 0,0 to avoid nullPointerException
        lastDeviceLocation = new Location("");
        lastDeviceLocation.setLatitude(0.5d);
        lastDeviceLocation.setLongitude(5.0d);
        // Find last location and update.
        callGetLastLocation();
        setLatAndLongTextViews(lastDeviceLocation);

        /*
         * Setup contact list
         * Ali Hooman (alhooman@ucsc.edu)
         */
        //contactList = new ArrayList<contact>();
        com.example.ucsc.SafetyPal.Globals g = (com.example.ucsc.SafetyPal.Globals)getApplication();
        contactList = g.getContactList();

    }

    /*
     * Calls fused location provider's getLastLocation() method and updates lastDeviceLocation;
     * Ali Hooman (alhooman@ucsc.edu)
     */
    public void callGetLastLocation() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get last location
            final Task<Location> locationResult = mFusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastDeviceLocation = task.getResult();
                        // Make sure location object exists, emulators have issues with this.
                        if(lastDeviceLocation != null) {
                            setLatAndLongTextViews(lastDeviceLocation);
                            // Add to Firebase user
                            auth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            dataRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
                            dataRef.child("locationLat").setValue(lastDeviceLocation.getLatitude());
                            dataRef.child("locationLong").setValue(lastDeviceLocation.getLongitude());
                        }
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
        if(location != null) {
            latCoord = location.getLatitude();
            longCoord = location.getLongitude();
        }
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
     *
     * SMS is used since most modern devices, as long as some connection is maintained to a cell
     * tower, can send out an SMS.
     */
    public void sendRequestHelpSMS() {

        // Update location
        callGetLastLocation();

        /*
         * Create maps cross-platform url using device latitude and longitude coordinates.
         * Example: https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393
         *
         * String commonMapsUrl is the initial part of the maps URL
         * Additional URL components (included in commonMapsUrl):
         *      ?api=1  -- This is required and must be set to 1. No other option is valid.
         *      &query= -- This is required to set the map to a specific location.
         */
        String commonMapsUrl = "https://www.google.com/maps/search/?api=1&query=";
        String currentLocMapUrl;    // Store current latitude and longitude as string
        String latString = String.valueOf(lastDeviceLocation.getLatitude());    // Latitude
        String longString = String.valueOf(lastDeviceLocation.getLongitude());   // Longitude
        currentLocMapUrl = commonMapsUrl + latString + "," + longString;    // Build map url

        /*
         * Build a Distress message to be sent
         *
         * SMS message length limits are as follows:
         *      160 characters for 8 bit alphabet
         *      140 characters for 7 bit alphabet
         *
         * Out message lengths:
         *      currentLocMapURL: 70
         *      helpMeAlert:      17
         *      white space:      3     // This is for reader clarity
         *      Remaining space:  50
         *
         * Custom message must be limited to 50 characters to ensure the message is not split by
         * the carrier resulting in the URL being divided and no longer parsed by recipient SMS
         * application. This has been done by limiting the TextEdit input box's maxLength to 50 and
         * truncating the custom message before being sent.
         */
        String helpMeAlert = "SafetyPal alert: ";       // Initial part of message

        // If a custom message is set, get it and add it.
        customMessage = findViewById(R.id.message_Text_Box);
        if(customMessage.getText().toString().trim().equals("")) {
            // No custom message, build final SMS message without custom message.
            helpMeAlert = helpMeAlert + currentLocMapUrl;   // Add location url to message
        }
        else {
            // Custom message exists, build final SMS with custom message.
            String msgCustom = customMessage.getText().toString().trim();
            if(msgCustom.length() > 50) {
                // If greater than 50 char, trim to 50 char limit, index 0 to 49.
                msgCustom.substring(0, 49);
            }
            // Build final SMS message with custom message and URL.
            helpMeAlert = helpMeAlert + msgCustom + ": " + currentLocMapUrl;
        }

        /*
         * Recipients
         * Set smsRecipients to empty string, default SMS application will load Recipient Selection
         * Screen by default if this string is empty.
         */
        String smsRecipients = "";

        /*
         * Get recipients if there are any.
         * For multiple recipients, traverse contactList and add phone numbers separated by
         * semicolons.
         */
        if(contactList.size() < 1) {
            // Avoid out of bounds error
        }
        else if(contactList.size() == 1) {
            smsRecipients = contactList.get(0).getPalPhonePlain();
        }
        else {
            smsRecipients = contactList.get(0).getPalPhonePlain();
            for(int index = 1; index < contactList.size(); index++) {
                smsRecipients = smsRecipients + ";" + contactList.get(index).getPalPhonePlain();
            }
        }

        // Create intent
        String intentTypeSMS = "vnd.android-dir/mms-sms";
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        // Fill in phone number and message body
        sendIntent.putExtra("sms_body", helpMeAlert);
        sendIntent.putExtra("address", new String(smsRecipients));
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

        if(view == alarmStart || view == imageView){
            //dataRef.child("isAlarmActivated").setValue(true);
            callGetLastLocation();

            sendRequestHelpSMS(); // Text for help from contacts
        }


    }
}
