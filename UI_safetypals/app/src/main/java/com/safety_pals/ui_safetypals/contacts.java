package com.safety_pals.ui_safetypals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class contacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }
    public void goHompagePage(View view){
        startActivity(new Intent(contacts.this, homepage.class));
    }
}
