package com.cannontech.web.dr.model;

public class EcobeeSettings {
    
    private boolean checkErrors;
    private int errorCheckTime;
    private boolean dataCollection;
    private int downloadTime;
    
    public boolean isCheckErrors() {
        return checkErrors;
    }
    
    public void setCheckErrors(boolean checkErrors) {
        this.checkErrors = checkErrors;
    }
    
    public int getErrorCheckTime() {
        return errorCheckTime;
    }
    
    public void setErrorCheckTime(int errorCheckTime) {
        this.errorCheckTime = errorCheckTime;
    }
    
    public boolean isDataCollection() {
        return dataCollection;
    }
    
    public void setDataCollection(boolean dataCollection) {
        this.dataCollection = dataCollection;
    }

    public void setDownloadTime(int downloadTime) {
        this.downloadTime = downloadTime;
    }

    public int getDownloadTime() {
        return downloadTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (checkErrors ? 1231 : 1237);
        result = prime * result + (dataCollection ? 1231 : 1237);
        result = prime * result + errorCheckTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EcobeeSettings other = (EcobeeSettings) obj;
        if (checkErrors != other.checkErrors)
            return false;
        if (dataCollection != other.dataCollection)
            return false;
        if (errorCheckTime != other.errorCheckTime)
            return false;
        if (downloadTime != other.downloadTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("EcobeeSettings [checkErrors=%s, errorCheckTime=%s, dataCollection=%s, downloadTime=%s]",
                        checkErrors, errorCheckTime, dataCollection, downloadTime);
    }
    
}