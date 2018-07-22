package com.example.ucsc.SafetyPal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/*
 * @author Ali Hooman - alhooman@ucsc.edu
 *
 * Manage the adding of safety contacts.
 */
public class ManageContacts extends AppCompatActivity implements View.OnClickListener {

    private ImageButton addContacts;
    private EditText nameTextField;
    private EditText emailTextField;
    private EditText numberTextField;

    private FirebaseAuth auth;
    private DatabaseReference dataRef;

    private List<contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        nameTextField = findViewById(R.id.inputName);
        emailTextField = findViewById(R.id.inputEmail);
        numberTextField = findViewById(R.id.inputPhone);

        addContacts = findViewById(R.id.addContact);
        addContacts.setOnClickListener(this);

        // Setup contact list
        com.example.ucsc.SafetyPal.Globals g = (com.example.ucsc.SafetyPal.Globals)getApplication();
        contactList = g.getContactList();

    }

    @Override
    public void onClick(View view) {
        String name = nameTextField.getText().toString();
        String email = emailTextField.getText().toString();
        String number = numberTextField.getText().toString();

        contact newFriend = new contact(name, email, number);
        auth = FirebaseAuth.getInstance();


        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, logIn.class));
        }

        /*
         * Duplicate contacts are being generated in Firebase realtime DB - Ali
         * //TODO Implement check to avoid duplicate contact creation.
         */
        //dataRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        FirebaseUser user = auth.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        DatabaseReference userContactList = dataRef.child("ContactList").push();
        userContactList.setValue(newFriend);

        finish();
        startActivity(new Intent(this, MainActivity.class));

        /*
         * Add contact to MainActivity.contactList
         * Ali Hooman (alhooman@ucsc.edu)
         *
         * //TODO This is hacky AF. contactList is public and doesn't survive application shutdown.
         */
        contact pal = new contact(name, email, number);
        contactList.add(pal);
        Context context = getApplicationContext();
        String addedContactMsg = pal.getPalPhonePlain() + " added!";
        Toast contactAddedToast = Toast.makeText(context, addedContactMsg, Toast.LENGTH_SHORT);
        contactAddedToast.show();
    }
}
