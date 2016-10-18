package com.xrdev.musicastmaterial.models;

import com.wrapper.spotify.models.AuthorizationCodeCredentials;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by Guilherme on 03/08/2014.
 */
public class Token {

    String accessString;
    String refreshString;
    DateTime expirationDt;

    public Token(String accessString, String refreshString, String expirationString) {
        this.accessString = accessString;
        this.refreshString = refreshString;

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        this.expirationDt = fmt.parseDateTime(expirationString);

    }

    public Token(String accessString, String refreshString, int expiresIn) {
        this.accessString = accessString;
        this.refreshString = refreshString;
        this.expirationDt = (new DateTime()).plusSeconds(expiresIn);
    }

    public Token(AuthorizationCodeCredentials authCredentials) {
        this.accessString = authCredentials.getAccessToken();
        this.refreshString = authCredentials.getRefreshToken();
        this.expirationDt = (new DateTime()).plusSeconds(authCredentials.getExpiresIn());
    }

    public DateTime getExpirationDt() {
        return expirationDt;
    }

    public void setExpirationDt(DateTime expirationDt) {
        this.expirationDt = expirationDt;
    }

    public void setExpirationDt(int expiresIn) {
        this.expirationDt = (new DateTime()).plusSeconds(expiresIn);
    }

    public String getRefreshString() {
        return refreshString;
    }

    public void setRefreshString(String refreshString) {
        this.refreshString = refreshString;
    }

    public String getAccessString() {
        return accessString;
    }

    public void setAccessString(String accessString) {
        this.accessString = accessString;
    }

    public boolean isValid() {
        if (expirationDt.isAfterNow())
            return true;
        else
            return false;
    }
}
