package com.example.ucsc.SafetyPal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;

/*
 * @author Ali Hooman - alhooman@ucsc.edu
 *
 * Manage the adding of safety contacts.
 */
public class ManageContacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

    }

    public void onAddContactButtonClick() {
        SafetyContact contactToAdd = new SafetyContact();

        // input name
        EditText inputName = (EditText) findViewById(R.id.inputName);
        contactToAdd.setSafetyContactName(inputName.getText().toString());

        // input email
        EditText inputEmail = (EditText) findViewById(R.id.inputEmail);
        contactToAdd.setSafetyContactEmail(inputEmail.getText().toString());

        // input phone
        EditText inputPhone = (EditText) findViewById(R.id.inputPhone);
        contactToAdd.setSafetyContactPhone(inputPhone.getText().toString());

        SafetyContact.safetyContactsList.add(contactToAdd);

        inputName.clearComposingText();
        inputEmail.clearComposingText();
        inputPhone.clearComposingText();

    }
}
