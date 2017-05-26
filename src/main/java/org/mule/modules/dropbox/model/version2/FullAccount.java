package org.mule.modules.dropbox.model.version2;

import java.util.Map;

public class FullAccount {

    /*
    This is the response to https://api.dropboxapi.com/2/users/get_current_account for V2 of Dropbox API
    See dropbox docs at https://www.dropbox.com/developers/documentation/http/documentation#users-get_current_account

    {
        "account_id": "dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc",
        "name": {
            "given_name": "Franz",
            "surname": "Ferdinand",
            "familiar_name": "Franz",
            "display_name": "Franz Ferdinand (Personal)",
            "abbreviated_name": "FF"
        },
        "email": "franz@dropbox.com",
        "email_verified": true,
        "disabled": false,
        "locale": "en",
        "referral_link": "https://db.tt/ZITNuhtI",
        "is_paired": true,
        "account_type": {
            ".tag": "business"
        },
        "country": "US",
        "team": {
            "id": "dbtid:AAFdgehTzw7WlXhZJsbGCLePe8RvQGYDr-I",
            "name": "Acme, Inc.",
            "sharing_policies": {
                "shared_folder_member_policy": {
                    ".tag": "team"
                },
                "shared_folder_join_policy": {
                    ".tag": "from_anyone"
                },
                "shared_link_create_policy": {
                    ".tag": "team_only"
                }
            },
            "office_addin_policy": {
                ".tag": "disabled"
            }
        },
        "team_member_id": "dbmid:AAHhy7WsR0x-u4ZCqiDl5Fz5zvuL3kmspwU"
    }
     */

    private String accountId;
    private Name name;
    private String email;
    private String emailVerified;
    private Boolean disabled;
    private String locale;
    private String referralLink;
    private Boolean isPaired;
    private Map<String, String> accountType;
    private String country;
    private Team team;
    private String teamMemberId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getReferralLink() {
        return referralLink;
    }

    public void setReferralLink(String referralLink) {
        this.referralLink = referralLink;
    }

    public Boolean getPaired() {
        return isPaired;
    }

    public void setPaired(Boolean paired) {
        isPaired = paired;
    }

    public Map<String, String> getAccountType() {
        return accountType;
    }

    public void setAccountType(Map<String, String> accountType) {
        this.accountType = accountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(String teamMemberId) {
        this.teamMemberId = teamMemberId;
    }
}
