package com.cannontech.web.dr.model;

import org.joda.time.LocalTime;

public class EcobeeSettings {

    private boolean checkErrors;
    private LocalTime checkErrorsTime;
    private boolean dataCollection;
    private LocalTime dataCollectionTime;

    public boolean isCheckErrors() {
        return checkErrors;
    }

    public void setCheckErrors(boolean checkErrors) {
        this.checkErrors = checkErrors;
    }

    public boolean isDataCollection() {
        return dataCollection;
    }

    public LocalTime getCheckErrorsTime() {
        return checkErrorsTime;
    }

    public void setCheckErrorsTime(LocalTime checkErrorsTime) {
        this.checkErrorsTime = checkErrorsTime;
    }

    public LocalTime getDataCollectionTime() {
        return dataCollectionTime;
    }

    public void setDataCollectionTime(LocalTime dataCollectionTime) {
        this.dataCollectionTime = dataCollectionTime;
    }

    public void setDataCollection(boolean dataCollection) {
        this.dataCollection = dataCollection;
    }

    @Override
    public String toString() {
        return String.format("EcobeeSettings [checkErrors=%s, errorCheckTime=%s, dataCollection=%s, downloadTime=%s]",
                      checkErrors, checkErrorsTime, dataCollection, dataCollectionTime);
    }
    
}