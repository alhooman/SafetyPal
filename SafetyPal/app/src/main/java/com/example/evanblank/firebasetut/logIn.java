package com.example.evanblank.firebasetut;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class logIn extends AppCompatActivity {
    boolean logedIn = false;
    Button loginButton;
    String totalUser;
    //Button registerButton;
    EditText userName;
    EditText password;
    EditText emailField;
    private DatabaseReference dataBase;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        loginButton = findViewById(R.id.loginButton);
        emailField= findViewById(R.id.email);
        userName = findViewById(R.id.loginUser);
        //registerButton = findViewById(R.id.register);
        password = findViewById(R.id.loginPass);
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        final Map<String, String> userMap = new HashMap<>();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userName.getText().toString();
                String pass = password.getText().toString();
                String email = emailField.getText().toString();
                userMap.put("email", email);
                userMap.put("password", pass);

               // DatabaseReference existingUser = dataBase.child("users").child(user).child(email);
              /*  ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            //fireStore.collection("users").add(userMap);
                            //dataBase.child('Users').setValue(user);
                            //dataBase.collection("users").setValue(user);
                            logedIn = false;

                        }else{
                            logedIn = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                if(logedIn){
                    fireStore.collection("users").document(user).set(userMap);
                }else{
                    totalUser = user;
                }*/
                fireStore.collection("users").document(user).set(userMap);
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkUser(View v){

    }
    /*@Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null){
            //handle already log in user
        }
    }
    public void register(final String User, String pass){
        auth.createUserWithEmailAndPassword(User, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //store addition of user
                    user user1 = new user(User);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user1);
                }else{
                    Toast.makeText(logIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }
*/
}
