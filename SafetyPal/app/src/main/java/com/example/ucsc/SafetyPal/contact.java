package com.example.ucsc.SafetyPal;

public class contact {
    public String Name;
    public String EmailAddress;
    public String PhoneNumber;
    public boolean isSelected;

    public contact(String name, String email, String number){
        this.Name = name;
        this.EmailAddress = email;
        this.PhoneNumber = number;
        this.isSelected = false;
    }
}
