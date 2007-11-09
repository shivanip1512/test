package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class FriendlyExceptionResolver {

    List<KnownExceptionType> knownExceptions = new ArrayList<KnownExceptionType>();
    Properties exceptionStrings;

    // getFriendlyExceptionMessage - default to US locale
    public String getFriendlyExceptionMessage(Throwable exception){
        return getFriendlyExceptionMessage(exception, Locale.US);
    }

    // getFriendlyExceptionMessage
    public String getFriendlyExceptionMessage(Throwable exception, Locale locale) {

        String friendlyExceptionMessage = null;

        KnownExceptionType knownExceptionType = getKnownExceptionType(exception);

        if (knownExceptionType != null) {
            String friendlyExceptionPropertyKey = knownExceptionType.getFriendlyExceptionPropertyKey();
            friendlyExceptionMessage = exceptionStrings.getProperty(friendlyExceptionPropertyKey);
        }
        if (friendlyExceptionMessage == null) {
            friendlyExceptionMessage = exception.getMessage();
        }

        return friendlyExceptionMessage;
    }

    // find matching KnownExceptionType
    private KnownExceptionType getKnownExceptionType(Throwable exception){

        KnownExceptionType matchingKnownExceptionType = null;

        for(KnownExceptionType k : knownExceptions){
            if(k.matchesException(exception)){
                matchingKnownExceptionType = k;
                break;
            }
        }

        return matchingKnownExceptionType;
    }


    // setter/getters
    public List<KnownExceptionType> getKnownExceptions() {
        return knownExceptions;
    }

    public void setKnownExceptions(List<KnownExceptionType> knownExceptions) {
        this.knownExceptions = knownExceptions;
    }

    public Properties getExceptionStrings() {
        return exceptionStrings;
    }

    public void setExceptionStrings(Properties exceptionStrings) {
        this.exceptionStrings = exceptionStrings;
    }

}
