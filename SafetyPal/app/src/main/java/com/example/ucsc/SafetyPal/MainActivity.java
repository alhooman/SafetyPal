package com.example.ucsc.SafetyPal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference();
    }

    /*
        Opens CurrentLocation activity on locationButton click.
     */
    public void openCurrentLocation(View view) {
        Intent locationViewIntent = new Intent(this, CurrentLocation.class);
        startActivity(locationViewIntent);
    }
}
