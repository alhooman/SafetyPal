package com.example.ucsc.SafetyPal;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

public class user {
    public String Name;
    public String EmailAddress;
    public String PhoneNumber;
    public boolean isAlarmActivated;
    public List<contact> ContactList;
    public double locationLong;
    public double locationLat;

    public user(String name, String email, String number){
        this.Name = name;
        this.EmailAddress = email;
        this.PhoneNumber = number;
        //contact friend = new contact("joe", "joe@test.com", "55555555555");
        //this.ContactList.add(friend);
        this.ContactList = new ArrayList<contact>();
        this.isAlarmActivated = false;
        this.locationLong = 0.0;
        this.locationLat = 0.0;
    }
}
