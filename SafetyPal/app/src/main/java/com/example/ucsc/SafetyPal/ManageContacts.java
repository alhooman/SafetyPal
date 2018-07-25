package com.example.ucsc.SafetyPal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore firestore;
    private DocumentReference usersRef;

    private List<contact> contactList;              // List of contact objects
    private ArrayAdapter<String> arrayAdapter;     // ArrayAdapter for ListView
    private ListView listView;                      // ListView for displaying contacts
    private String[] contactNamesListView;          // String array for filling ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        nameTextField = findViewById(R.id.inputName);
        emailTextField = findViewById(R.id.inputEmail);
        numberTextField = findViewById(R.id.inputPhone);

        addContacts = findViewById(R.id.addContact);
        addContacts.setOnClickListener(this);

        // Setup contact list from Globals class
        com.example.ucsc.SafetyPal.Globals g = (com.example.ucsc.SafetyPal.Globals)getApplication();
        contactList = g.getContactList();

        // Create contactNamesListView array based on contactList ArrayList.
        updateContactNamesListViewArray();

        // Create ArrayAdapter for filling listView
        arrayAdapter = new ArrayAdapter<>
                (this, R.layout.text_view_for_list,
                        R.id.textViewForList, contactNamesListView);

        // Create ListView and attach Array Adapter
        ListView listView = findViewById(R.id.Contact_List_Button);
        listView.setAdapter(arrayAdapter);

    }

    /*
     * This method updates the contactNamesListView array of Strings to stay up to date with the
     * List contactList.
     *
     * Call this method before and after making changes to contact list.
     *
     * This is required for now because the ArrayAdapter doesn't work well with contact objects.
     * There may be a better way of doing this but I didn't have time to figure it out. -Ali
     */
    public void updateContactNamesListViewArray() {
        String[] temp;
        if(contactList.isEmpty()) {
            temp = new String[]{"Add a contact using the above options."};
        }
        else {
            temp = new String[contactList.size()];
            for(int index = 0; index < contactList.size(); index++) {
                temp[index] = contactList.get(index).getPalName();
            }
        }
        contactNamesListView = temp;
    }

    @Override
    public void onClick(View view) {
        String name = nameTextField.getText().toString();
        String email = emailTextField.getText().toString();
        String number = numberTextField.getText().toString();

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
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection("Users").document(name);

        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        addContact();
                    }else{
                        userDoesNotExist();
                        Log.d("contact", "no such user");
                    }
                }
            }
        });

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

        // Update contactNameListView, force listView adapter to redraw, clear text fields
        updateContactNamesListViewArray();
        arrayAdapter.notifyDataSetChanged();
        nameTextField.clearComposingText();
        emailTextField.clearComposingText();
        numberTextField.clearComposingText();

        // Reload activity
        finish();
        startActivity(getIntent());
    }

    public void addContact(){

        Toast.makeText(this, "Contact Added", Toast.LENGTH_LONG).show();

        String name = nameTextField.getText().toString();
        String email = emailTextField.getText().toString();
        String number = numberTextField.getText().toString();

        contact newFriend = new contact(name, email, number);
        auth = FirebaseAuth.getInstance();


        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, logIn.class));
        }


        //dataRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        FirebaseUser user = auth.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        DatabaseReference userContactList = dataRef.child("ContactList");
        userContactList.child(name).setValue(newFriend);

        firestore = FirebaseFirestore.getInstance();
        //TODO change document path to global current username variable
        usersRef = firestore.collection("Users").document("Evan Blank Test").collection("ContactList").document(name);
        usersRef.set(newFriend);

        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void userDoesNotExist(){
        Toast.makeText(this, "User Does Not Exist", Toast.LENGTH_LONG).show();
    }

}
