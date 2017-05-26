package org.mule.modules.dropbox.model.version2;

public class Name {

    /*
    {
        "given_name": "Franz",
        "surname": "Ferdinand",
        "familiar_name": "Franz",
        "display_name": "Franz Ferdinand (Personal)",
        "abbreviated_name": "FF"
    }
     */

    private String givenName;
    private String surname;
    private String familiarName;
    private String displayName;
    private String abbreviatedName;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFamiliarName() {
        return familiarName;
    }

    public void setFamiliarName(String familiarName) {
        this.familiarName = familiarName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    public void setAbbreviatedName(String abbreviatedName) {
        this.abbreviatedName = abbreviatedName;
    }
}
