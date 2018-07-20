package com.safety_pals.ui_safetypals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class homepage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }
    public void goContactsPage(View view){
        startActivity(new Intent(homepage.this, contacts.class));
    }
    public void goLoginPage(View view){
        startActivity(new Intent(homepage.this, login.class));
    }

}
