package com.xrdev.musicastmaterial.models;

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

    // Token gerado a partir de SharedPreferences
    public Token(String accessString, String expirationString) {
        this.accessString = accessString;

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        this.expirationDt = fmt.parseDateTime(expirationString);
    }

    // Token gerado a partir de refresh da API.
    public Token(String accessString, int expiresIn) {
        this.accessString = accessString;
        this.expirationDt = (new DateTime()).plusSeconds(expiresIn);
    }

    public DateTime getExpirationDt() {
        return expirationDt;
    }

    public String getRefreshString() {
        return refreshString;
    }

    public String getAccessString() {
        return accessString;
    }

    public boolean isValid() {
        return expirationDt.isAfterNow();
    }
}
