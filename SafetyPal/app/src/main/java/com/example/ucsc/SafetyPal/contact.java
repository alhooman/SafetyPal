package com.example.ucsc.SafetyPal;

public class contact {
    public String Name;
    public String EmailAddress;
    public String PhoneNumber;
    public boolean isSelected;

    /*
     * Firebase has its own getters for public fields it seems, this is causing compile errors.
     * Adding additional fields for local contactList.
     * //TODO Move contactList completely to firebase.
     *
     * Ali Hooman (alhooman@ucsc.edu)
     */
    private String palName;
    private String palEmailAddress;
    private String palPhoneNumber;
    private boolean palIsSelected;

    public contact(String name, String email, String number){
        this.Name = name;
        this.EmailAddress = email;
        this.PhoneNumber = number;
        this.isSelected = false;

        /*
         * Private field initialization
         * Ali Hooman (alhooman@ucsc.edu)
         */
        this.palName = name;
        this.palEmailAddress = email;
        this.palPhoneNumber = number;
        this.palIsSelected = false;

        /*
         * Format number into 10 digit string without special characters.
         * Ali Hooman (alhooman@ucsc.edu)
         */
        //Strip input
        this.PhoneNumber = number.replaceAll("[^0-9]", "");
        this.palPhoneNumber = number.replaceAll("[^0-9]", "");
    }

    /*
     * Setters for each field.
     * Ali Hooman (alhooman@ucsc.edu)
     *
     * We should make fields private after getting core functionality working.
     */
    public void setPalName(String name) {
        this.palName = name;
    }
    public void setPalEmailAddress(String address) {
        this.palEmailAddress = address;
    }
    public void setPalPhoneNumber(String phone) {
        this.palPhoneNumber = phone;
    }
    public void setPalIsSelected(boolean selectFlag) {
        this.palIsSelected = selectFlag;
    }

    /*
     * Getters for each field.
     * Ali Hooman (alhooman@ucsc.edu)
     *
     * We should make fields private after getting core functionality working.
     */
    public String getPalName() {
        return this.palName;
    }
    public String getPalEmailAddress() {
        return this.palEmailAddress;
    }
    public boolean getPalIsSelected() {
        return palIsSelected;
    }
    public String getPalPhonePlain() {
        return palPhoneNumber;
    }
    public String getPalPhoneFormatted() {
        // Add ( ) and - to PhoneNumber String and return
        //TODO
        return this.palPhoneNumber;
    }

}
