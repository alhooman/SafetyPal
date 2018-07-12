package com.example.ucsc.SafetyPal;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * @author Ali Hooman - alhooman@ucsc.edu
 *
 * SafetyContact object for sending SOS
 */
public class SafetyContact {
    private String safetyContactName;
    private String safetyContactEmail;
    private String safetyContactPhone;
    public static ArrayList<SafetyContact> safetyContactsList = new ArrayList<>();

    SafetyContact() {
        this.safetyContactName = "";
        this.safetyContactEmail = "";
        this.safetyContactPhone = "";
    }

    SafetyContact(String name, String email, String phone) {
        this.safetyContactName = name;
        this.safetyContactEmail = email;
        this.safetyContactPhone = phone;
    }

    public String getSafetyName() {
        return this.safetyContactName;
    }
    public String getSafetyEmail() {
        return this.safetyContactEmail;
    }
    // Returns 4085551234 instead of (408)555-1234
    public String getSafetyPhoneParsed() {
        String result;
        result = this.safetyContactPhone.replace("[^0-9]", "");
        return result;
    }
    // Returns phone number as it is stored with () and -.
    public String getSafetyPhoneNotParsed() {
        return this.safetyContactPhone;
    }

    public void setSafetyContactName(String name) {
        this.safetyContactName = name;
    }
    public void setSafetyContactEmail(String email) {
        this.safetyContactEmail = email;
    }
    public void setSafetyContactPhone(String phone) {
        this.safetyContactPhone = phone;
    }

}
