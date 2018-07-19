package com.example.ucsc.SafetyPal;

import android.app.Application;
import com.example.ucsc.SafetyPal.contact;

import java.util.ArrayList;
import java.util.List;

/*
 * Global variables class to create local contactList object.
 *
 * Ali Hooman (alhooman.ucsc.edu)
 *
 * This is not the best way of doing this at all, but I have 1.5 hours and need to get
 * some stuff working.
 *
 * //TODO   Find a better way to maintain a contact list -> store in Firebase or on disk.
 */
public class Globals extends Application {

    // Initialize list
    private List<contact> contactList = new ArrayList<contact>();

    // Public getter for contactList.
    public List getContactList() {
        return contactList;
    }

}
