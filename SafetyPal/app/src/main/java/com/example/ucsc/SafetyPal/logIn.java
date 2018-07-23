package com.example.ucsc.SafetyPal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class  logIn extends AppCompatActivity implements View.OnClickListener{
    EditText userName;
    EditText password;
    EditText emailField;
    EditText phoneNumber;

    Button login;
    Button registerButton;
    private ProgressDialog progress;

    private FirebaseAuth auth;
    private DatabaseReference dataRef;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        progress = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        //check if user is already logged in
        if(auth.getCurrentUser() != null){
            //start activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        dataRef = FirebaseDatabase.getInstance().getReference();

        emailField= findViewById(R.id.email);
        userName = findViewById(R.id.loginUser);
        password = findViewById(R.id.loginPass);
        phoneNumber = findViewById(R.id.phoneNumber);

        login = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.register);

        login.setOnClickListener(this);
        registerButton.setOnClickListener(this);


    }

    private void registerUser(){
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        String email = emailField.getText().toString();
        String number = phoneNumber.getText().toString();

        if(TextUtils.isEmpty(user)){
            //user field is empty
            Toast.makeText(this, "Please Enter a Username.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(number)){
            //user field is empty
            Toast.makeText(this, "Please Enter a Phone Number.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(pass)){
            //password field empty
            Toast.makeText(this, "Please Enter a Password.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(email)){
            //email field is empty
            Toast.makeText(this, "Please Enter a Valid Email Address.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        //validations are fine
        //first show progress
        progress.setMessage("Registering New User...");
        progress.show();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();
                        if(task.isSuccessful()){
                            //task is successful
                            //start activity
                            saveUserInfo();
                            Toast.makeText(logIn.this, "Registered Succesfully!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(logIn.this, "Not Registered, try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    //create a user object and save it to the real time database
    private void saveUserInfo(){
        String userN = userName.getText().toString();
        String number = phoneNumber.getText().toString();
        String email = emailField.getText().toString();

        user userInfo = new user(userN, email, number);

        FirebaseUser currentUser = auth.getCurrentUser();

        dataRef.child(currentUser.getUid()).setValue(userInfo);

        Toast.makeText(this, "Information Saved!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        if(view == login){
            //log in
            userLogin();
        }

        if(view == registerButton){
            registerUser();
        }
    }

    private void userLogin() {
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        String email = emailField.getText().toString();
        String number = phoneNumber.getText().toString();

        if(TextUtils.isEmpty(user)){
            //user field is empty
            Toast.makeText(this, "Please Enter a Username", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(number)){
            //user field is empty
            Toast.makeText(this, "Please Enter a Valid Phone Number.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(pass)){
            //password field empty
            Toast.makeText(this, "Please Enter the Correct Password.", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        if(TextUtils.isEmpty(email)){
            //email field is empty
            Toast.makeText(this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
            //stopping the function from continuing
            return;
        }

        //validations are fine
        //first show progress
        progress.setMessage("Logging In...");
        progress.show();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();
                        if(task.isSuccessful()) {
                            //start activity
                            saveUserInfo();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(logIn.this, "Log In failed, try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
